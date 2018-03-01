package com.sbt.component.table;

import com.sbt.component.UiInputFieldsBinder;
import com.sun.javafx.scene.control.skin.TableHeaderRow;
import com.sun.javafx.scene.control.skin.TableViewSkin;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.TableColumn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;

public abstract class SbtTableCacheMng {

    private static final Logger logger = LoggerFactory.getLogger(SbtTableCacheMng.class);
    private static final String CacheRoot;
    private static SoftReference<Set<SbtTableInterface<?>>> savableTable;
    private static SoftReference<Set<SbtTableInterface<?>>> columnConfigChangedTableSetRef = new SoftReference<>(null);

    static {
        CacheRoot = System.getProperty("java.io.tmpdir")+ File.pathSeparator+"_SBT"+File.pathSeparator+"_TableCache";
//        SbtApplication.addShutDownTask( ()-> {SbtTableCacheMng.saveAll();});
    }
    public static void saveAll() {
        saveTableColumnConfigForAllTables();
        saveDataForAllTables();
    }
    public static synchronized void addColumnConfigChangedTable(SbtTableInterface<?> table) {
        Set<SbtTableInterface<?>> columnConfigChangedTableSet = columnConfigChangedTableSetRef.get();
        if ( null == columnConfigChangedTableSet) {
            columnConfigChangedTableSet = new HashSet<>();
            columnConfigChangedTableSetRef = new SoftReference<>(columnConfigChangedTableSet);
        }
        columnConfigChangedTableSet.add(table);
    }
    public static synchronized void saveTableColumnConfigForAllTables() {
        Set<SbtTableInterface<?>> columnConfigChangedTableSet = columnConfigChangedTableSetRef.get();
        if ( null != columnConfigChangedTableSet) {
            columnConfigChangedTableSet.stream().forEach((tableView)-> {
                saveTableColumnConfig(tableView);
            });
        }
    }
    public static synchronized void saveDataForAllTables() {

        if ( null == savableTable || null == savableTable.get()) {
            return;
        }
        Set<SbtTableInterface<?>> tableViewSet  = savableTable.get();
        tableViewSet.stream().forEach((tableView) -> {
            if ( tableView.isCacheable()) {
                logger.info("save data for table "+tableView.getId());
                saveTableData(tableView);
                saveQueryConditions(tableView);
            }
        });
    }
    public static synchronized void fireTableDataChanged(SbtTableInterface<?> tableViewInterface) {
        if ( null == savableTable || null == savableTable.get()) {
            savableTable = new SoftReference<>(new HashSet<SbtTableInterface<?>>());
        }
        Set<SbtTableInterface<?>> tableViewSet = savableTable.get();
        tableViewSet.add(tableViewInterface);
    }
    public static synchronized void fireTableDataSaved(SbtTableInterface<?> tableViewInterface) {
        if ( null == savableTable || null == savableTable.get()) {
            return;
        }
        savableTable.get().remove(tableViewInterface);
    }
    public static void restoreTableColumnConfig(SbtTableInterface<?> tableView) {
        Properties props = new Properties();
        InputStream in = null;
        try {

            String cacheFileName = getCacheFileName(tableView,TableCacheType.ColVisibleConfig);
            if ( null == cacheFileName) {
                return;
            }
            File f = new File(cacheFileName);
            if ( ! f.exists()) {
                return;
            }
            in = new FileInputStream(f);
            props.load(in);
        }catch(IOException e) {

            logger.error("Table Cache:",e);

        } finally {
            if ( null != in) {
                try {
                    in.close();
                }catch(IOException e) {
                    logger.error("Table Cache:",e);
                }
            }
        }

        tableView.getTableColumns().stream().forEach((tableColumn)-> {
            setTableColumnVisibility(props,tableColumn);
        });
    }
    private static void setTableColumnVisibility(Properties props, TableColumn<?,?> tableColumn) {
        String columnText;
        boolean hasChildColumns = !(null == tableColumn.getColumns() || 0 == tableColumn.getColumns().size());
        if ( !hasChildColumns) {
            columnText = tableColumn.getText();
        } else {
            if ( 1 == tableColumn.getColumns().size()) {

                columnText = tableColumn.getColumns().get(0).getText().equals("")?tableColumn.getText():"";

            } else {

                tableColumn.getColumns().stream().forEach( (tc) -> {
                    setTableColumnVisibility(props,tc);
                });
                return;
            }
        }

        String visible = props.getProperty(columnText);
        if ( null != visible) {
            tableColumn.visibleProperty().set(Boolean.parseBoolean(visible));
        }
    }
    public static void saveTableColumnConfig(SbtTableInterface<?> tableView) {
        final Properties props = new Properties();
        TableHeaderRow tableHeaderRow = TableUtil.getTableHeaderRow((TableViewSkin<?>)tableView.getSkin());
        ContextMenu tableRowHeaderContextMenu = TableUtil.getContextMenu(tableHeaderRow);
        tableRowHeaderContextMenu.getItems().stream().forEach( ( menuItem ) -> {
            if ( menuItem instanceof CheckMenuItem) {
                CheckMenuItem cki = (CheckMenuItem)menuItem;
                String columnText = cki.getText().trim().replace(", ","");
                boolean isVisible = cki.isSelected();
                props.setProperty(columnText,String.valueOf(isVisible));
            }
        });

        OutputStream out = null;
        try {
            File f = new File(getCacheFileName(tableView,TableCacheType.ColVisibleConfig));
            out = new FileOutputStream(f);
            props.store(out,"TableView "+tableView.getId()+" Column Visibility Configuration.");
        }catch(IOException e) {
            logger.error("Table Cache:",e);
        } finally {

            if ( null != out) {
                try {
                    out.close();
                }catch(IOException e) {
                    logger.error("Table Cache:",e);
                }
            }
        }
    }
    private static<T> void saveTableColumnVisibleConfig(Properties props, TableColumn<T,?> tc) {

        List<TableColumn<T,?>> columns = tc.getColumns();
        if ( columns.isEmpty()) {

            props.setProperty(tc.getText(),String.valueOf(tc.isVisible()));

        } else {

            columns.stream().forEach( (tableColumn) -> {
                saveTableColumnVisibleConfig(props,tableColumn);
            });
        }
    }
    public static <T extends SbtTableRowData> void restoreTable(SbtTableInterface<?> tableView) {
        restoreTableData(tableView);
        restoreQueryConditions(tableView);
    }
    public static <T extends SbtTableRowData> void restoreTableData(SbtTableInterface<T> tableView) {
        InputStream in = null;
        ObjectInputStream ois;
        File cacheFile = null;
        try {
            String cacheFileName = getCacheFileName(tableView,TableCacheType.Data);
            if ( null == cacheFileName){
                return;
            }
            cacheFile = new File(cacheFileName);
            if ( ! cacheFile.exists()){
                return;
            }
            in = new FileInputStream(cacheFile);
            ois = new ObjectInputStream(in);
            SerializableTableItemData serializableTableItemData = (SerializableTableItemData)ois.readObject();
            ObservableList<T> items = serializableTableItemData.observableArrayList();
            tableView.setItems(items);
        }catch(Exception e) {

            logger.error("Table Cache:",e);
            if ( null != cacheFile) {
                if ( ! cacheFile.delete()) {
                    cacheFile.deleteOnExit();
                }
            }
        } finally {
            if ( null != in ) {
                try {
                    in.close();
                }catch(IOException e) {
                    logger.error("Table Cache:",e);
                }
            }
        }
    }
    public static void restoreQueryConditions(SbtTableInterface<?> tableView) {
        UiInputFieldsBinder queryConditionPropBinder = tableView.getQueryConditionPropBinder();
        if ( null == queryConditionPropBinder) {
            return;
        }
        Properties props = new Properties();
        InputStream in = null;
        try {
            String cacheFileName = getCacheFileName(tableView,TableCacheType.QueryCondConfig);
            if ( null == cacheFileName){
                return;
            }
            File f = new File(cacheFileName);
            if ( ! f.exists()) {
                return;
            }
            in = new FileInputStream(f);
            props.load(in);
        }catch ( IOException e) {
            logger.error("Table Cache:",e);
        }finally {
            if ( null != in ) {
                try{
                    in.close();
                }catch ( IOException e) {
                    logger.error("Table Cache:",e);
                }
            }
        }
        Iterator<String> condFieldNames = queryConditionPropBinder.getElementNames();
        while ( condFieldNames.hasNext()) {
            String condFileName = condFieldNames.next();
            if ( null != condFileName) {
                String value = props.getProperty(condFileName);
                queryConditionPropBinder.setValue(condFileName,value);
            }
        }
    }
    public static <T extends SbtTableRowData> void saveQueryConditions(SbtTableInterface<T> tableView) {

        UiInputFieldsBinder inputFieldsBinder = tableView.getQueryConditionPropBinder();
        if ( null == inputFieldsBinder) {
            return;
        }
        Properties props = inputFieldsBinder.toProps();

        OutputStream out = null;
        try {

            File f = new File(getCacheFileName(tableView,TableCacheType.QueryCondConfig));
            out = new FileOutputStream(f);
            props.store(out," TableView "+tableView.getId()+" Query Conditions");
        }catch ( IOException e) {
            logger.error("Table Cache:"+e);
        } finally {
            if ( null != out ) {
                try {
                    out.close();
                }catch ( IOException e) {
                    logger.error("Table Cache:"+e);
                }
            }
        }
    }
    public static <T extends SbtTableRowData> void saveTableData(SbtTableInterface<T> tableView) {

        SerializableTableItemData serializableTableItemData = new SerializableTableItemData(tableView);
        ObjectOutputStream oos = null;
        try {

            File f = new File(getCacheFileName(tableView,TableCacheType.Data));
            OutputStream outFile = new FileOutputStream(f);
            oos = new ObjectOutputStream(outFile);
            oos.writeObject(serializableTableItemData);
        }catch ( IOException e) {
            logger.error("Table Cache: ",e);
        }finally {
            if ( null != oos ) {
                try {
                    oos.close();
                }catch ( IOException e) {
                    logger.error("Table Cache: ",e);
                }
            }
        }
    }
    private static String getCacheFileName(SbtTableInterface<?> tableView, TableCacheType type) {
        String tblId = tableView.getId();
        if ( null == tblId) {
            Exception e = new Exception("table view id is not defined");
            logger.error("Table Cache:",e);
            return null;
        }

        if ( "".equals(tblId)) {
            return null;
        }

        StringBuilder sb = new StringBuilder(CacheRoot).append(File.pathSeparator).append("currrentUserId").
                append("_").append("TblCache_").append(tblId).append("_").append(type).append(type.getFileExtention());

        return sb.toString();
    }
  /////////////////////////////////////////////////////////////////////
  private enum TableCacheType {
        QueryCondConfig(".conf"),
        ColVisibleConfig(".conf"),
        Data("");

        private TableCacheType(String fileExtention) {
            this.fileExtention = fileExtention;
        }
        public String getFileExtention() {
            return fileExtention;
        }
        private final String fileExtention;
  }
  //////////////////////////////////////////////////////////////////////
    private static class SerializableTableItemData<T extends SbtTableRowData> implements Serializable {

        private static final long serialVersionUID = 2018022201l;
        private final List<T> serializableList;

        private SerializableTableItemData(SbtTableInterface<T> tableView) {

            List<T> tableDataItem = tableView.getItems();
            serializableList = new ArrayList<>(tableDataItem.size());
            tableDataItem.stream().forEach( (row) -> {
                serializableList.add(row);
            });
        }
        public List<T> getItems() {
            return serializableList;
        }
        public ObservableList<T> observableArrayList() {

            ObservableList<T> items = FXCollections.observableArrayList();
            serializableList.stream().forEach( (row) -> {
                items.add(row);
            });
            return items;
        }
    }
}
