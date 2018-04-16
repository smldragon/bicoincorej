package com.sbt.component.table;

import com.sbt.component.AsynEventExecutor;
import com.sbt.component.ComponentConstants;
import com.sbt.component.ComponentUtil;
import com.sbt.component.NodeDataLoader;
import com.sbt.component.UiInputFieldsBinder;
import com.sun.javafx.scene.control.skin.TableColumnHeader;
import com.sun.javafx.scene.control.skin.TableHeaderRow;
import com.sun.javafx.scene.control.skin.TableViewSkin;
import com.sun.javafx.scene.control.skin.VirtualFlow;
import javafx.beans.NamedArg;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumnBase;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.util.Callback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.function.Function;

public class SbtTableView<S extends SbtTableRowData> extends TableView<S> implements NodeDataLoader,SbtTableInterface<S> {

    private final ObjectProperty<SecondHeaderLineProp> secondHeaderLineProperty = new SimpleObjectProperty<>();
    private boolean hasRowNumberColumn = true;
    private boolean rowNumberColumnInstalled = false;
    private boolean listenersIntalled = false;
    private boolean initStatus = false;
    private boolean isRestoredFromCache = false;
    private TablePropertyInitializer<S> tablePropertyInitializer;
    private List<String> columnHeaderStyleClasses;
    private Double defaultRowHeight;
    private UiInputFieldsBinder queryConditionPropBinder;
    private TableDataProvider<S> tableDataProvider;
    private Function<ExpandableTableRow<S>, Region> detailRegionCreater;
    private boolean isCacheable = true;
    private TableRowMouseEventListener<S> tableRowMouseEventListener;
    private String dataLoadingPrompt = ComponentConstants.LoadingPrompt;
    private String noDataPrompt = ComponentConstants.NoDataPrompt;
    private ChangeListener<Number> scrollToSelectedRowListener;
    private boolean headSwappedStatus = false;
    private TableViewClipboardCopy<S> tableViewClipboardCopy;
    private TableCellsSelector tableCellSelector;
    private boolean toFillTableWithBlankRows = false;
    public static final String RowNoColTitle = "ÐòºÅ";
    private boolean rowNumbeColumnInstalled = false;
    private TableCellsSelector tableCellsSelector;
    private boolean inRestoredFromCache = false;
    private boolean rowNumberColStatus = false;
    private Callback<MouseEvent,Void> rowSelectCallback;
    private Label promptPlaceHolder;
    private static final Logger logger = LoggerFactory.getLogger(SbtTableView.class);
    private static final String editableColumnHeaderStyle = " -fx-background-color: lightGrey;";

    public SbtTableView() {
//        this(true, null);
        hasRowNumberColumn = true;
        secondHeaderLineProperty.set(null);

    }
    public SbtTableView(@NamedArg("hasRowNumberColumn")boolean hasRowNumberColumn, @NamedArg("secondHeaderLineProperty")SecondHeaderLineProp secondHeaderLineValue){
        this.hasRowNumberColumn = hasRowNumberColumn;
        this.secondHeaderLineProperty.set(secondHeaderLineValue);
    }
    public void setHasRowNumberColumn(boolean hasRowNumberColumn) {
        this.hasRowNumberColumn = hasRowNumberColumn;
    }
    public void prepareLoading() {
        prepareLoading(null);
    }
    public void startDataItem() {
        startDataItem(null);
    }
    public void startDataItem(String noDataPromptTxt) {
        if ( null == noDataPromptTxt) {
            noDataPromptTxt = noDataPrompt;
        }
        showPlaceHolderText(noDataPromptTxt);
    }
    public void setToFillTableWithBlankRows(boolean toFillTableWithBlankRows) {
        this.toFillTableWithBlankRows = toFillTableWithBlankRows;
    }
    public boolean toFillTableWithBlankRows() {
        return toFillTableWithBlankRows;
    }
    private void showPlaceHolderText(String text) {

        if ( null == text) {
            text = noDataPrompt;
        }
        getPromptPlaceHolder().setText(text);
    }
    public void setTableRowMouseEventListener(TableRowMouseEventListener<S> tableRowMouseEventListener) {
        this.tableRowMouseEventListener = tableRowMouseEventListener;
    }
    public TableColumn<S,?> getFirstColumnDeep() {
        TableColumn<S,?> firstParentCol = this.getColumns().get(0);
        List<TableColumn<S,?>> subColumns = firstParentCol.getColumns();
        if ( null == subColumns || 0 == subColumns.size() ) {
            return firstParentCol;
        }else {
            return subColumns.get(0);
        }
    }
    public TableColumn<S,?> getLastColumnDeep() {

        TableColumn<S,?> lastParentCol = this.getColumns().get(getColumns().size()-1);
        List<TableColumn<S,?>> subColumns = lastParentCol.getColumns();
        if ( null == subColumns || 0 == subColumns.size() ) {
            return lastParentCol;
        }else {
            return subColumns.get(subColumns.size()-1);
        }
    }
    public int getColumnIndex(TableColumn<S,?> tc) {
        int index = getColumns().indexOf(tc);
        if ( 0 > index ) {
            index = TableUtil.getTableColumnIndexDeep(getColumns(),tc);
        }
        return index;
    }
    public void restoreNoDataPrompt() {
        getPromptPlaceHolder().setStyle(" -fx-font-size: 20px; -fx-font-weight: bold; -fx-opacity: 0.35;");
        showPlaceHolderText(noDataPrompt);
    }
    public void setIsCacheable( boolean isCacheable) {
        this.isCacheable = isCacheable;
    }
    public boolean isDefaultRowHeightSet() {
        return null != defaultRowHeight;
    }
    public double getDefaultRowHeight() {
        return  null == defaultRowHeight?0:defaultRowHeight.doubleValue();
    }
    public void setRowSelectCallBack(Callback<MouseEvent,Void> rowSelectCallback) {
        this.rowSelectCallback = rowSelectCallback;
    }
    public void setDefaultRowHeight( double defaultRowHeight) {
        this.defaultRowHeight = defaultRowHeight;
    }
    public void setQueryConditionPropBinder(UiInputFieldsBinder queryConditionPropBinder) {
        this.queryConditionPropBinder = queryConditionPropBinder;
    }
    public TableDataProvider<S> getTableDataProvider() {
        return tableDataProvider;
    }
    public void setTableDataProvider(TableDataProvider<S> tableDataProvider) {
        this.tableDataProvider = tableDataProvider;
    }
    @Override
    public void layoutChildren() {
        List<TableColumn<S,?>> columns = getColumns();
        if ( !initStatus) {
            initComponents();
        }

        installRowNumberColumn();

        super.layoutChildren();

        if ( !inRestoredFromCache) {
            inRestoredFromCache = true;
            restoreFromCache();
        }

        if ( ! initStatus ) {
            TableViewWorkAround.patchContextMenuLeak(this);
        }
        initStatus = true;

    }
    protected void restoreFromCache() {
        SbtTableCacheMng.restoreTableColumnConfig(this);
        if ( isCacheable) {
            SbtTableCacheMng.restoreTable(this);
        }
    }
    public TablePropertyInitializer getTablePropertyInitializer() {
        return tablePropertyInitializer;
    }

    public void setTablePropertyInitializer(TablePropertyInitializer tablePropertyInitializer) {
        this.tablePropertyInitializer = tablePropertyInitializer;
    }
    public Function<ExpandableTableRow<S>, Region> getDetailRegionCreater() {
        return this.detailRegionCreater;
    }
    public void setDetailRegionCreater(Function<ExpandableTableRow<S>, Region> detailRegionCreater) {
        this.detailRegionCreater = detailRegionCreater;
    }
    public void setEditableComboBoxColumn(String columnName, String ... comboboxValues) {
        TableColumn<S,String> theColumn = (TableColumn<S,String>)getTableColumn(columnName);
        if ( null == comboboxValues) {
            comboboxValues = new String [] {""};
        }
        ComboBoxTableCellFactory<S> theColumnCellFactory = new ComboBoxTableCellFactory<>(comboboxValues);
        theColumn.setCellFactory(theColumnCellFactory);

        TableColumnHeader tch = getColumnHeader(theColumn);
        tch.setStyle(editableColumnHeaderStyle);
        theColumn.setOnEditCommit( new ColumnEditingEventHandler());
    }
    public <T> void setColumnEditable(String columnName,FormattedTableCellFactory<S,T> theColumnCellFactory) {
        TableColumn<S,T> theColumn = (TableColumn<S,T>)getTableColumn(columnName);
        theColumnCellFactory.setEditable(true);
        theColumn.setCellFactory(theColumnCellFactory);

        ColumnEditingEventHandler<S,T> editHandler = new ColumnEditingEventHandler<>();
        theColumn.setOnEditCommit(editHandler);
        theColumn.setOnEditStart(editHandler);

        TableColumnHeader tch = getColumnHeader(theColumn);
        tch.setStyle(editableColumnHeaderStyle);
    }
    @Override
    public List<TableColumn<S,?>> getTableColumns() {
        return getColumns();
    }
    @Override
    public void scrollToRow(int rowIndex, TableColumn<S,?> tc) {

        scrollToSelectedRowListener = getScrollToSelectedRowListener();
        getSelectionModel().selectedIndexProperty().addListener(scrollToSelectedRowListener);
        getSelectionModel().clearAndSelect(rowIndex,tc);
        getSelectionModel().selectedIndexProperty().removeListener(scrollToSelectedRowListener);
    }
    public void addSelectionChangedListener(ChangeListener<Number> listener) {
        getSelectionModel().selectedIndexProperty().addListener(listener);
    }
    private ChangeListener<Number> getScrollToSelectedRowListener() {

        if ( null == scrollToSelectedRowListener) {

            scrollToSelectedRowListener = (observable, oldValue,newValue) -> {

                TableViewSkin<?> ts = (TableViewSkin<?>)getSkin();
                VirtualFlow<?> vf = (VirtualFlow<?>)ts.getChildren().get(1);

                int first = vf.getFirstVisibleCellWithinViewPort().getIndex();
                int last  = vf.getLastVisibleCellWithinViewPort().getIndex();

                int middleIndex = (last-first)/2;
                int dist = newValue.intValue() - middleIndex;
                if ( 0<= dist) {
                    vf.scrollTo(dist);
                } else {
                    vf.scrollTo(0);
                }
            };
        }
        return scrollToSelectedRowListener;
    }
    public TableCellsSelector getTableCellsSelector() {
        return tableCellsSelector;
    }
    public void setColumnSubHeaders() {
        setColumnSubHeaders(new HashMap<String,String>());
    }
    public void setColumnSubHeaders(Map<String,String> subHeaderMap) {
        int colSize = getTableColumns().size();
        List<String> subHeaders = new Vector<>(colSize);
        for(int i=0;i<colSize;i++) {
            subHeaders.add("");
        }
        subHeaderMap.keySet().stream().forEach( (colText)-> {
            int colIndex = getTableColumnIndexByText(colText);
            subHeaders.set(colIndex,subHeaderMap.get(colText));
        });

        setColumnSubHeaders(subHeaders);
    }
    public void setColumnSubHeaders(List<String> subHeaders) {
        if ( ! headSwappedStatus) {
            swapHeaders();
            headSwappedStatus = true;
        }
        for( int i=0;i<subHeaders.size();i++) {
            TableColumn<S,?> tc = getColumns().get(i);
            if ( ! isRowNumbercolumn(i)) {
                String subHeader = subHeaders.get(i);
                if ( null == subHeader || "".equals(subHeader.trim())) {
                    subHeader = " "; // "" is not allowed, but " " is allowed
                }
                tc.setText(subHeader);
                tc.setId(String.valueOf(i));
            }
            List<TableColumn<S,?>> subTblCols = tc.getColumns();
            for( TableColumn<S,?> subTc: subTblCols) {
                TableColumnHeader tch = getColumnHeader(subTc);
                if ( null != tch ) {
                    installHeaderMouseEventDelegator(tch);
                }
            }
            applyStyle(tc);
        }
    }
    private void applyStyle(TableColumnBase<S,?> tc) {
        TableColumnHeader tch = getColumnHeader(tc);
        if ( null != tch ) {
            Label label = getColumnHeaderLabel(tch);
            label.setStyle(secondHeaderLineProperty.get().getStyle(tc)+ SecondHeaderLineProp.BorderStyle);
            tch.setStyle(secondHeaderLineProperty.get().getStyle(tc)+ SecondHeaderLineProp.BorderStyle);
        }
    }
    private void swapHeaders() {
        List<TableColumn<S,?>> columns = getTableColumns();
        for(int i=0;i<columns.size();i++) {
            TableColumn<S,?> tc = columns.get(i);
            if ( isRowNumbercolumn(i)) {
                continue;
            }
            TableColumn<S,?> subTblCol = tc.getColumns().get(0);
            subTblCol.setText(tc.getText());
        }
    }
    private boolean isRowNumbercolumn(int colIndex) {
        return hasRowNumberColumn && 0 == colIndex;
    }
    private void checkColumnFactories() {

    }
    private void installHeaderMouseEventDelegator(TableColumnHeader tch ) {
        tch.setOnMouseDragged( new HeaderMouseEventDelegator(null));
        EventHandler<? super MouseEvent> defaultMousePressedHandler = tch.getOnMousePressed();
        tch.setOnMousePressed(new HeaderMouseEventDelegator(defaultMousePressedHandler));
        EventHandler<? super MouseEvent> defaultMouseReleasedHandler = tch.getOnMouseReleased();
        tch.setOnMouseReleased(new HeaderMouseEventDelegator(defaultMouseReleasedHandler));
    }
    private void installRowNumberColumn() {
        if ( hasRowNumberColumn && ! rowNumberColumnInstalled ) {
            rowNumberColumnInstalled = true;
            RowNumberColumn<S> rowNumberColumn = new RowNumberColumn<>(this,secondHeaderLineProperty.get());
            rowNumberColumn.install();
        }
    }
    private Label getPromptPlaceHolder() {
        if ( null == promptPlaceHolder) {
            promptPlaceHolder = new Label();
            setPlaceholder(promptPlaceHolder);
        }
        return promptPlaceHolder;
    }
    protected void initComponents() {

        tableCellsSelector = new TableCellsSelector(this);

        if ( null != tablePropertyInitializer) {
            tablePropertyInitializer.initTableProperties(this);
        }

        setTableMenuButtonVisible(true);

        setRowFactory( new ExpandableTableRowFactory<>());

        if ( null == promptPlaceHolder ) {
            showPlaceHolderText(null);
        }

        getColumns().addListener((ListChangeListener.Change<? extends TableColumn<S, ?>> c)-> {
            List<?> columns = c.getList();
            if ( 0 == columns.size()) {
                rowNumbeColumnInstalled = false;
            }
        });

        TableHeaderRow thr = TableUtil.getTableHeaderRow((TableViewSkin<?>)getSkin());
        if ( null != thr) {
            ContextMenu tableRowHeaderContextMenu = TableUtil.getContextMenu(thr);
            ObservableList<MenuItem> selectionItems = tableRowHeaderContextMenu.getItems();
            tableRowHeaderContextMenu.setOnShown( (event) -> {
                for(MenuItem item: selectionItems)  {
                    standarizeCheckMenuItemText(item,item.getText());
                    if ( RowNoColTitle.equals(item.getText().trim())) {
                        item.setDisable(true);
                    }
                }
                SbtTableCacheMng.addColumnConfigChangedTable(SbtTableView.this);
            });
        }

        installKeyListeners();

        checkTableCellFactory();
    }
    private void checkTableCellFactory() {

        List<TableColumn<S,?>> columns = getColumns();
        for(TableColumn<S,?> tc: columns) {

            if ( null == tc.getCellFactory() || TableColumn.DEFAULT_CELL_FACTORY.equals(tc.getCellFactory())){
                tc.setCellFactory(new FormattedTableCellFactory());
            }
            if ( null == tc.getCellValueFactory()) {
                String fieldName = tc.getId();
                if ( null == fieldName) {
                    fieldName = ComponentUtil.convertToCamelString(tc.getText());
                }
                if ( null == fieldName) {
                    Exception e = new Exception("Without FormattedTableCellFactory assigned to the TableColumn, " +
                            "id of TableColumn has to be set to field name of S");
                    logger.error("",e);
                }
                tc.setCellValueFactory(new DefaultTableRowValueFactory(fieldName));
            }
        }
    }
    private void installKeyListeners() {

        tableViewClipboardCopy = new TableViewClipboardCopy<>(this);
        addEventFilter(KeyEvent.KEY_PRESSED, (event) -> {
            tableViewClipboardCopy.handle(event);
        });
    }
    private void standarizeCheckMenuItemText(MenuItem checkMenuItem,String originalText) {
        originalText = originalText.trim();
        String [] parts = originalText.split(",");
        String leafText = parts[parts.length -1];
        checkMenuItem.setText(leafText);
    }
    public void addContextMenuItems(MenuItem ... items) {

        ContextMenu contextMenu = initContextMenu();
        for(MenuItem item: items) {
            contextMenu.getItems().add(item);
            if ( contextMenu instanceof SbtTableRowContextMenu && item instanceof SbtTableRowContextMenu.RowIndexChangeListener) {
                ((SbtTableRowContextMenu)contextMenu).addRowIndexChangedListener((SbtTableRowContextMenu.RowIndexChangeListener)item);
            }
        }
    }
    private ContextMenu initContextMenu() {
        ContextMenu contextMenu = getContextMenu();
        if ( null != contextMenu) {
            return contextMenu;
        }
        //contextMenu is invoked by mouse action at ExpandableTableRowFactory
        contextMenu = new SbtTableRowContextMenu<>(this);
        contextMenu.setStyle(" -fx-background-color: #f2f2f2; ");
        contextMenu.setOnShowing( (windowEvent) -> {

        });
        contextMenu.setOnShown( (windowEvent) -> {

        });
        this.setContextMenu(contextMenu);
        return contextMenu;
    }
    private void installTableMenuListner() {

        getColumns().stream().forEach( (tc) -> {
            addListenerToColumn(tc);
        });
    }
    private void addListenerToColumn(TableColumn<S,?> tc) {
        List<TableColumn<S,?>> columns = tc.getColumns();
        if ( columns.isEmpty()) {
            addListenerToLeafColumn(tc);
        } else {
            columns.stream().forEach( (column) -> {
                addListenerToColumn(column);
            });
        }
    }
    private void addListenerToLeafColumn(TableColumn<S,?> leafTableColumn) {
        leafTableColumn.visibleProperty().addListener( (observable,oldValue,newValue)-> {
            TableColumnBase<S,?> parentTableColumn = leafTableColumn.getParentColumn();
            if ( null != parentTableColumn) {
                parentTableColumn.setVisible(newValue);
                SbtTableView.this.layout();
                if ( newValue ) {
                    applyStyle(parentTableColumn);
                    TableColumnHeader tch = SbtTableView.this.getColumnHeader(leafTableColumn);
                    installHeaderMouseEventDelegator(tch);
                }
            } else {
                SbtTableView.this.layout();
            }

            if ( newValue ) {
                TableViewWorkAround.patchContextMenuLeak(SbtTableView.this);
            }

            SbtTableCacheMng.fireTableDataChanged(SbtTableView.this);
        });
    }
    private void installItemchangedListener () {
        itemsProperty().addListener((observable,oldValue,newValue) -> {
            SbtTableCacheMng.fireTableDataChanged(SbtTableView.this);
        });
    }
    @Override
    public void loadData() {
        if ( 0 > getItems().size()) {
            ActionEvent event = new ActionEvent(this,null);
            loadData(event);
        }
    }
    public void loadData(ActionEvent event) {
        if ( null != tableDataProvider) {
            try {
                AsynEventExecutor.execute(tableDataProvider, event);
            }catch ( Exception e) {
                logger.error("",e);
            }
        }
    }
    @Override
    protected TableViewSkin<S> createDefaultSkin() {
        return (TableViewSkin<S>)super.createDefaultSkin();
    }
    @Override
    public void refresh() {
        super.refresh();
    }
    public void reload() {
        getItems().clear();
        loadData();
    }
    @Override
    public void showPrompt(String prompt) {
        showPlaceHolderText(prompt);
    }
    public TableColumn<S,?> getTableColumn(String colId) {
        return getColumns().stream().filter( (tableColumn) -> {
            return null == tableColumn.getId()?false:tableColumn.getId().equalsIgnoreCase(colId);
        }).findFirst().get();
    }
    public int getTableColumnIndexById(String colId) {
        if ( null == colId) {
            return -1;
        }
        List<TableColumn<S,?>> columnList = getColumns();
        for ( int i=0;i<columnList.size();i++) {
            TableColumn<S,?> col = columnList.get(i);
            if ( colId.equals(col.getId())) {
                return i;
            }
        }
        return -1;
    }
    public int getTableColumnIndexByText(String colText) {
        return getTableColumnIndexByText(colText,getColumns());
    }
    private int getTableColumnIndexByText(String colText,List<TableColumn<S,?>> columnList) {
        if ( null == colText) {
            return -1;
        }
        for(int i=0;i<columnList.size();i++) {
            TableColumn<S,?> col = columnList.get(i);
            if ( colText.equals(col.getText())) {
                return i;
            } else {
                List<TableColumn<S,?>> subTableColumn = col.getColumns();
                if ( null != subTableColumn) {
                    int index = getTableColumnIndexByText(colText,subTableColumn);
                    if ( 0 <= index) {
                        return index;
                    }
                }
            }
        }
        return -1;
    }
    public void setColumnVisible(String colText,boolean visible) {
        TableColumn<S,?> tc = getTableColumnByColumnText(colText);
        if ( null != tc) {
            tc.setVisible(visible);
        }
    }
    private TableColumn<S,?> getTableColumnByColumnText(final String colText) {
        return getColumns().stream().filter( (tableColumn) -> {
            return tableColumn.getText().equals(colText);
        }).findFirst().get();
    }
    public void clear() {
        if ( null != getItems()) {
            getItems().clear();
        }
    }
    public List<TableColumn<S,?>> getLeafTableColumns() {
       List<TableColumn<S,?>> rtn = new ArrayList<>();
       for(int i=0;i<getColumns().size();i++) {
           addLeafColumns(rtn,getColumns().get(i));
       }
       return rtn;
    }
    private void addLeafColumns(List<TableColumn<S,?>> rtnList, TableColumn<S,?> tc) {
        List<TableColumn<S,?>> subColumns = tc.getColumns();
        if ( subColumns.isEmpty()){
            rtnList.add(tc);
        } else {
            for(int i=0;i<subColumns.size();i++) {
                addLeafColumns(rtnList,subColumns.get(i));
            }
        }
    }
    public List<String> getColumnHeaderStyleClasses() {

        if ( null == columnHeaderStyleClasses) {
            Set<Node> columnHeaders = lookupAll(".column-header > .label");
            Iterator<Node> ite = columnHeaders.iterator();
            while ( ite.hasNext()) {
                Node node = ite.next();
                if ( node instanceof TableColumnHeader) {
                    columnHeaderStyleClasses =  node.getStyleClass();
                    break;
                }
            }
            if ( null == columnHeaderStyleClasses) {
                columnHeaderStyleClasses = new ArrayList<>();
            }
        }
        return columnHeaderStyleClasses;
    }
    public TableColumnHeader getColumnHeader(TableColumnBase<S,?> tc) {

        Set<Node> columnHeaders = lookupAll (".column-header ");
        Iterator<Node> ite = columnHeaders.iterator();
        while ( ite.hasNext()) {
            Node node = ite.next();
            if ( node instanceof TableColumnHeader && ((TableColumnHeader) node).getTableColumn() == tc) {
                return (TableColumnHeader) node;
            }
        }
        return null;
    }
    public Label getColumnHeaderLabel(TableColumnHeader tch) {
        for ( Node node: tch.getChildrenUnmodifiable()) {
            if ( node instanceof Label ) {
                return (Label)node;
            }
        }
        return null;
    }
    public TableColumnHeader getColumnHeader(int columnIndex) {
        return getColumnHeader(getColumns().get(columnIndex));
    }
    public boolean isExpandable() {
       return null != getDetailRegionCreater();
    }
    @Override
    public void edit(int row, TableColumn<S,?> column) {
        if ( null == column) {
            super.edit(row,column);
            return;
        }

        boolean editable = true;
        if ( RowNoColTitle.equals(column.getText())) {
            editable = false;
        }
//        else if ( null != column.getCellFactory() && column.getCellFactory() instanceof EditableCellFactory ) {
////            editable = ((EditableCellFactory)column.getCellFactory()).isEditable(row);
////        }
        else {
            editable = 0 <= row && row < this.getItems().size();
        }

        if ( editable) {
            super.edit(row,column);
        }
    }
    public TableRowMouseEventListener<S> getTableRowMouseEventListener() {
        return tableRowMouseEventListener;
    }
    @Override
    public boolean isCacheable() {
        return isCacheable;
    }
    @Override
    public void setNoDataPrompt(String noDataPrompt) {
        this.noDataPrompt = noDataPrompt;
    }
    @Override
    public void setItems(List<S> tableData) {
        if ( null == tableData || 0 == tableData.size()) {
            getItems().clear();
            restoreNoDataPrompt();
        } else {
            getItems().setAll(tableData);
        }
    }
    @Override
    public UiInputFieldsBinder getQueryConditionPropBinder() {
        if ( null == queryConditionPropBinder){
            queryConditionPropBinder = new UiInputFieldsBinder("",null,ComponentConstants.StatusFieldId);
        }
        return queryConditionPropBinder;
    }
    @Override
    public void prepareLoading(String loadingPrompt) {
        if ( null == loadingPrompt) {
            loadingPrompt = dataLoadingPrompt;
        }
        showPlaceHolderText(loadingPrompt);
        getPromptPlaceHolder().setStyle(" -fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: green; -fx-opacity: 0.75; ");
        getItems().clear();
    }
 ///////////////////////////////////////////////////////////////////////////
    private static class ExpandableTableRowFactory<S extends SbtTableRowData> implements Callback<TableView<S>, TableRow<S>> {

         @Override
         public TableRow<S> call(TableView<S> param) {
             final SbtTableView<S> table =  (SbtTableView<S>)param;
             ExpandableTableRow<S> row = new ExpandableTableRow<S>();

             row.setOnMouseClicked( event -> {

                 if ( null != table.rowSelectCallback && event.getButton().equals(MouseButton.SECONDARY)) {
                     table.rowSelectCallback.call(event);
                 } else if ( table.isExpandable() && 2 == event.getClickCount() && ! row.isEmpty()) {
                     S rowData = row.getItem();
                     boolean isExpanded = rowData.toggleRowExpandStatus();
                     if ( isExpanded ) {
                         row.showExpandPanel(row.getSbtRowExpandPanel());
                     } else {
                         row.restore();
                     }
                 }
             });

             row.addEventFilter(MouseEvent.ANY, (event) -> {

                 TableRowMouseEventListener eventHandler = table.getTableRowMouseEventListener();
                 if ( null !=  eventHandler ) {
                     eventHandler.handle(event, row.getIndex());
                 }

                 if ( MouseEvent.MOUSE_RELEASED.equals(event.getEventType())) {
                     ContextMenu contextMenu = table.getContextMenu();
                     if ( contextMenu instanceof SbtTableRowContextMenu ) {

                         if ( MouseButton.SECONDARY.equals(event.getButton())) {
                             ((SbtTableRowContextMenu)contextMenu).setRowIndex(row.getIndex());
                         } else {
                             ((SbtTableRowContextMenu)contextMenu).setRowIndex(-1);
                         }
                     }
                 }
             });
             return row;
         }
     }
 ///////////////////////////////////////////////////////////////////////////
    private static class HeaderMouseEventDelegator implements EventHandler<MouseEvent> {

        private final EventHandler<? super MouseEvent> defaultMousePressHandler;
        public HeaderMouseEventDelegator(EventHandler<? super MouseEvent> defaultMousePressHandler) {
            this.defaultMousePressHandler = defaultMousePressHandler;
        }

         @Override
         public void handle(MouseEvent me) {

            TableColumnHeader header = ( TableColumnHeader)me.getSource();
            TableColumn parentTc = (TableColumn<?,?>) header.getTableColumn().getParentColumn();
            SbtTableView tableView = (SbtTableView<?>)parentTc.getTableView();
            TableColumnHeader parentTch = tableView.getColumnHeader(parentTc);

            double diffy = ( parentTch.getHeight() + header.getHeight())/2.0;

            double y = me.getSceneY() - me.getY() - header.getHeight() + 2.0;
            double x = me.getSceneX();

            double screenY = me.getScreenY() - me.getSceneY() + y;
            double screenX = me.getScreenX();

            me.consume();

            MouseEvent newMouseEvent = new MouseEvent(null,parentTch, me.getEventType(),x,y,screenX,screenY,me.getButton(),
                    me.getClickCount(), me.isShiftDown(), me.isControlDown(), me.isAltDown(), me.isMetaDown(), me.isPrimaryButtonDown(),
                    me.isMiddleButtonDown(), me.isSecondaryButtonDown(), me.isSynthesized(), me.isPopupTrigger(),
                    me.isStillSincePress(), me.getPickResult());

            Event.fireEvent(parentTch,newMouseEvent);

            if ( null != defaultMousePressHandler) {
                defaultMousePressHandler.handle(me);
            }
         }
 }
}
