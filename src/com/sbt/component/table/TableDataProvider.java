package com.sbt.component.table;

import com.sbt.component.AsynEventHandler;
import com.sbt.component.ComponentConstants;
import com.sbt.component.UiInputFieldsBinder;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.util.Callback;

public class TableDataProvider<T extends SbtTableRowData> implements AsynEventHandler<ActionEvent,ObservableList<T> > {

    public TableDataProvider(SbtTableInterface<T> table,TableDataFetcher<T> tableDataFetcher,Callback<Boolean, String> callBack) {
        this.table = table;
        this.tableDataFetcher = tableDataFetcher;
        this.callBack = callBack;
    }
    @Override
    public boolean prepareInFxThread(ActionEvent event) {
        if ( null != event) {
            invokingObject = event.getSource();
        } else {
            invokingObject = null;
        }

        errMsg = null;

        table.prepareLoading(ComponentConstants.LoadingPrompt);

        boolean status = AsynEventHandler.super.prepareInFxThread(event);
        if ( status ) {
            setControlDisabled(true);
        }

        updateLoadingStatics("");
        return status;
    }
    @Override
    public ObservableList<T> doInBackground(ActionEvent event) {

        try {
            return tableDataFetcher.getTableData();
        } catch(Exception e) {
            errMsg = e.getMessage();
            return null;
        }
    }
    @Override
    public long getTimeElapsed() {
        return timeElapsed;
    }
    @Override
    public void updateInFxThread(ObservableList<T> tableData) {
        boolean status = true;
        if ( null == tableData || 0 == tableData.size()) {
            if ( null != errMsg) {
                showPlaceHolderText(errMsg);
                status = false;
            } else {
                table.getItems().clear();
                table.showPrompt(ComponentConstants.NoDataPrompt);
            }
        } else {
            table.setItems(tableData);
        }

        if ( null != callBack) {
            String statusText = callBack.call(status);
            updateLoadingStatics(statusText);
        }

        SbtTableCacheMng.fireTableDataChanged(table);
        setControlDisabled(false);
    }
    @Override
    public void setTimeElapsed(long timeElapsed) {
        this.timeElapsed = timeElapsed;
    }
    private void updateLoadingStatics(String text) {
        UiInputFieldsBinder binder = table.getQueryConditionPropBinder();
        if ( null != binder) {
            Label label = binder.getStatusLabel();
            if ( null != label) {
                label.setText(text);
            }
        }
    }
    private void showPlaceHolderText(String text) {
        table.setNoDataPrompt(text);
    }
    private void setControlDisabled(boolean disabled) {
        Cursor cursor;
        if ( disabled) {
            cursor = Cursor.WAIT;
        } else {
            cursor = Cursor.DEFAULT;
        }

        if ( invokingObject instanceof Node) {
            ((Node) invokingObject).setDisable(disabled);
            ((Node) invokingObject).setCursor(cursor);
        }

        table.setDisable(disabled);
        table.setCursor(cursor);

        UiInputFieldsBinder binder = table.getQueryConditionPropBinder();
        if ( null != binder) {
            binder.setDisabled(disabled);
        }
    }
    private final SbtTableInterface<T> table;
    private final TableDataFetcher<T> tableDataFetcher;
    private final Callback<Boolean, String> callBack;
    private String errMsg;
    private Object invokingObject;
    private long timeElapsed;
}
