package com.sbt.component.table;

import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

import java.util.ArrayList;
import java.util.List;

public class TableCellsSelector<S extends SbtTableRowData> {

    public enum Mode {
        Column(true,false),Row(false,true),Cell(true,false),All(false,true);
        private Mode(boolean cellSelectionEnabled, boolean rowSelectionAllowed) {
            this.cellSelectionEnabled=cellSelectionEnabled;
            this.rowSelectionAllowed = rowSelectionAllowed;
        }
        public final boolean cellSelectionEnabled;
        public final boolean rowSelectionAllowed;
    };
    public TableCellsSelector(SbtTableView<S> fxTable) {
        this.fxTable = fxTable;
    }
    public void setMouseLocation(MouseEvent event) {
        TableCell<S,?> tc = (TableCell<S,?>)event.getSource();
        mouseLocation = new MouseLocation<S>(tc);
    }
    public void onMouseReleased(MouseEvent event) {
        if ( Mode.Column.equals(mode) && event.getButton().equals(MouseButton.PRIMARY)) {
            TableCell<S,?> tc = (TableCell<S,?>)event.getSource();
            if ( ! isDragging && ! event.isControlDown()) {
                selectedColumns.clear();
            }
        } else {
            isDragging = false;
        }
    }
    public SbtTableView<S> getFxTable() {
        return fxTable;
    }

    public Mode getSelectionMode() {
        return mode;
    }

    public void setSelectionMode(Mode mode) {
        this.mode = mode;
    }
    private final SbtTableView<S> fxTable;
    private Mode mode = Mode.Row;
    private MouseLocation<S> mouseLocation;
    private boolean isDragging = false;
    private final List<TableColumn<S,?>> selectedColumns = new ArrayList<>();
    /////////////////////////////////////////////////////////////////////////////////
    public static class MouseLocation <S extends SbtTableRowData> {

        public MouseLocation(TableCell<S,?> tc) {
            this(tc.getIndex(),tc.getTableColumn());
        }
        public MouseLocation(int rowIndex,TableColumn<S,?> tableColumn) {
            this.tableColumn = tableColumn;
            this.rowIndex = rowIndex;
        }
        public final int rowIndex;
        public final TableColumn<S,?> tableColumn;
    }
}
