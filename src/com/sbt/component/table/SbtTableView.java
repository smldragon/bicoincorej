package com.sbt.component.table;

import com.sun.javafx.scene.control.skin.TableHeaderRow;
import javafx.collections.ListChangeListener;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.util.List;

public class SbtTableView<S extends SbtTableRowData> extends TableView<S> {

    @Override
    public void layoutChildren() {

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
    public TablePropertyInitializer getTablePropertyInitializer() {
        return tablePropertyInitializer;
    }

    public void setTablePropertyInitializer(TablePropertyInitializer tablePropertyInitializer) {
        this.tablePropertyInitializer = tablePropertyInitializer;
    }
    private void installRowNumberColumn() {
        if ( ! rowNumberColStatus) {
            rowNumberColStatus = true;
            doInstallRowNumberColumn();
        }
    }
    protected void initComponents() {

        tableCellsSelector = new TableCellsSelector(this);

        if ( null != tablePropertyInitializer) {
            tablePropertyInitializer.initTableProperty(this);
        }

        setTableMenuButtonVisible(true);

        setRowFactory( new ExpandableTableRowFactory<S>());

        if ( null == promptPlaceHolder ) {
            showPlaceHolderText(null);
        }

        getColumns().addListener((ListChangeListener.Change<? extends TableColumn<S, ?>> c)-> {
            if ( 0 == getColumns().size()) {
                rowNumbeInstalled = false;
            }
        });

        TableHeaderRow thr = Utils.getTableHeaderRow(getSkin());
        if ( null != thr) {
            ContextMenu contextMenu = Utils.getContextMenu(thr);
            List<MenuItem> menuItems = contextMenu.getItems();
            contextMenu.setOnShown( (event) -> {
                for(MenuItem item: menuItems)  {
                    standarizeCheckMenuItemText(item);
                    if ( RowNoColTitle.equals(item.getText())) {
                        item.setDisable(true);
                    }
                }
                SbtTableCacheMng.addColumnConfigChangedTable(SbtTableView.this);
            });
        }

        installKeyListeners();

        checkTableCellFactory();
    }
    private boolean rowNumbeInstalled = false;
    private TablePropertyInitializer tablePropertyInitializer;
    private TableCellsSelector tableCellsSelector;
    private boolean initStatus = false;
    private boolean inRestoredFromCache = false;
    private boolean rowNumberColStatus = false;
}
