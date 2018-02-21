package com.sbt.component.table;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.scene.control.ContextMenu;

import java.util.ArrayList;
import java.util.List;

public class SbtTableRowContextMenu<S extends SbtTableRowData> extends ContextMenu {

    private final SimpleIntegerProperty rightClickedRowIndex = new SimpleIntegerProperty(-1);
    private final SbtTableView<S> tableView;
    private final List<RowIndexChangeListener> listeners = new ArrayList<>();

    public SbtTableRowContextMenu(SbtTableView<S> tableView) {
        this.tableView = tableView;
        rightClickedRowIndex.addListener((observable,oldValue,newValue) -> {
            if ( 0 <= newValue.intValue()) {
                for(RowIndexChangeListener l: listeners) {
                    l.changed(observable,oldValue,newValue);
                }
            }
        });
    }
    public void addRowIndexChangedListener(RowIndexChangeListener l) {
        listeners.add(l);
    }
    public int getRowIndex() {
        return rightClickedRowIndex.get();
    }
    public void setRowIndex(int rowIndex) {
        rightClickedRowIndex.set(rowIndex);
    }
//////////////////////////////////////////////////////////////////////////
    public interface RowIndexChangeListener extends ChangeListener<Number> {

    }
}
