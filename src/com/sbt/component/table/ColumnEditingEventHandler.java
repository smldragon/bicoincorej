package com.sbt.component.table;

import javafx.event.EventHandler;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;

public class ColumnEditingEventHandler<S extends SbtTableRowData,T> implements EventHandler<TableColumn.CellEditEvent<S,T>> {

    private static final Logger logger = LoggerFactory.getLogger(ColumnEditingEventHandler.class);

    public ColumnEditingEventHandler() {

    }
    protected void onEvent(TableColumn.CellEditEvent<S,T> event) {

        TableColumn<S,T> tc = (TableColumn<S,T>)event.getSource();
        S rowData = getRowData(event);
        T cellValue = event.getNewValue();
        String propertyName = ((PropertyValueFactory)tc.getCellValueFactory()).getProperty();
        try {
            Field field = rowData.getClass().getDeclaredField(propertyName);
            field.setAccessible(true);
            field.set(rowData,cellValue);
        }catch(Exception e) {
            logger.error("",e);
        }
    }
    @Override
    public final void handle(TableColumn.CellEditEvent<S,T> event) {
        if ( "EDIT_COMMIT".equals(event.getEventType().getName())) {
            SbtTableView<S> table = getTableView(event);
            SbtTableCacheMng.fireTableDataChanged(table);
            onEvent(event);
            S dataRow = getRowData(event);
            dataRow.setRowStatus(SbtTableRowStatus.Edited);
            table.refresh();
        }
    }
    public SbtTableView<S> getTableView(TableColumn.CellEditEvent<S,T> event) {
        return (SbtTableView<S>)event.getTableView();
    }
    public S getRowData(TableColumn.CellEditEvent<S,T> event) {
        SbtTableView<S> table = getTableView(event);
        int rowClicked = event.getTablePosition().getRow();
        return table.getItems().get(rowClicked);
    }
}
