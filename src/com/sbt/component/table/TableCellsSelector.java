package com.sbt.component.table;

import javafx.event.EventHandler;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
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
        if ( event.getSource() instanceof TableCell) {
            TableCell<S, ?> tc = (TableCell<S, ?>) event.getSource();
            mouseLocation = new MouseLocation<S>(tc);
        }
    }
    public void onMouseReleased(MouseEvent event) {
        if ( Mode.Column.equals(mode) && event.getButton().equals(MouseButton.PRIMARY)) {
            TableCell<S,?> tc = (TableCell<S,?>)event.getSource();
            if ( ! isDragging ) {
                if ( ! event.isControlDown()) {
                    selectedColumns.clear();
                }
                toggleColumnSelection(new MouseLocation<S>(tc));
            }
        } else {
            isDragging = false;
        }
    }
    public EventHandler<MouseEvent> getMouseEventHandler() {
        return (event) -> {
            TableCell<S,?> tc = (TableCell<S,?> )event.getSource();
            tc.startFullDrag();
        };
    }
    public EventHandler<MouseEvent> getMousePressedHandler() {
        return (event) -> {
            setMouseLocation(event);
        };
    }
    public EventHandler<MouseEvent> getMouseReleasedHandler() {
        return (event) -> {
            onMouseReleased(event);
        };
    }
    public EventHandler<MouseEvent> getMouseDragEventHandler() {
        return (mouseDragEvent) -> {
            isDragging = true;
            TableCell<S,?> tc = (TableCell<S,?>)mouseDragEvent.getSource();
            int rowIndex = tc.getIndex();
            TableView.TableViewSelectionModel<S> selectionMode = tc.getTableView().getSelectionModel();
            if ( Mode.Cell.equals(mode)) {
                clearAndSelectRegion(rowIndex,tc.getTableColumn());
            }else if ( Mode.Row.equals(mode)) {
                clearAndSelectRows(rowIndex);
            } else if ( Mode.Column.equals(mode)) {
                clearAndSelectColumns(tc.getTableColumn());
            }
        };
    }
    private void clearAndSelectRows(int rowIndex) {
        fxTable.getSelectionModel().clearSelection();
        int [] minMaxRows = getRegionMinMaxRowIndex(rowIndex);
        int lastColumnIndex = fxTable.getColumns().size();
        fxTable.getSelectionModel().selectRange(minMaxRows[0],fxTable.getColumns().get(0),minMaxRows[1],fxTable.getColumns().get(lastColumnIndex-1));
    }
    private void toggleColumnSelection(MouseLocation mouseLocation_) {
        TableColumn<S,?> tc = mouseLocation_.tableColumn;
        boolean isColumnSelected = 0 <= selectedColumns.indexOf(tc);
        if ( ! isColumnSelected ) {
            selectedColumns.add(tc);
        }else {
            fxTable.getSelectionModel().clearSelection();
            selectedColumns.remove(tc);
        }
        selectColumns();
    }
    private void selectColumns() {
        int lastRowIndex = fxTable.getItems().size()-1;
        for(TableColumn<S,?> tc: selectedColumns) {
            fxTable.getSelectionModel().selectRange(0,tc,lastRowIndex,tc);
        }
    }
    private void clearAndSelectColumns(TableColumn<S,?> tc) {

        TableColumn<S,?> [] minMaxCol = getRegionMinMaxColIndex(tc);
        fxTable.getSelectionModel().clearSelection();
        fxTable.getSelectionModel().selectRange(0,minMaxCol[0],fxTable.getItems().size()-1, minMaxCol[1]);
        selectedColumns.clear();
        boolean isColBetweenMinAndMax = false;
        for(TableColumn<S,?> leafCol: fxTable.getLeafTableColumns()) {
            if ( leafCol.equals(minMaxCol[0])) {
                isColBetweenMinAndMax = true;
            }
            if ( isColBetweenMinAndMax) {
                selectedColumns.add(leafCol);
            }

            if ( leafCol.equals(minMaxCol[1])) {
                break;
            }
        }
    }
    private void clearAndSelectRegion(int rowIndex,TableColumn<S,?> tc) {
        int [] minMaxRowIndex = getRegionMinMaxRowIndex(rowIndex);
        int minRowIndex = minMaxRowIndex[0];
        int maxRowIndex = minMaxRowIndex[1];

        TableColumn<S,?> [] minMaxCol = getRegionMinMaxColIndex(tc);
        fxTable.getSelectionModel().clearSelection();
        fxTable.getSelectionModel().selectRange(minRowIndex,minMaxCol[0],maxRowIndex,minMaxCol[1]);
    }
    private int [] getRegionMinMaxRowIndex(int mouseRowIndex) {
        int [] rtn = new int [2];
        int cellSelectionStartRowIndex = mouseLocation.rowIndex;
        if ( mouseRowIndex >=  cellSelectionStartRowIndex) {
            rtn[0] = cellSelectionStartRowIndex;
            rtn[1] = mouseRowIndex;
        } else {
            rtn[0] = mouseRowIndex;
            rtn[1] = cellSelectionStartRowIndex;
        }

        return rtn;
    }
    private TableColumn<S,?> [] getRegionMinMaxColIndex(TableColumn<S,?> tc) {
        int cellSelectionStartColumnIndex = fxTable.getColumnIndex(mouseLocation.tableColumn);
        int mouseColIndex = fxTable.getColumnIndex(tc);

        TableColumn<S,?> [] rtn = new TableColumn [2];
        if ( mouseColIndex>= cellSelectionStartColumnIndex) {
            rtn[0] = mouseLocation.tableColumn;
            rtn[1] = tc;
        } else {
            rtn[0] = tc;
            rtn[1] = mouseLocation.tableColumn;
        }
        return rtn;
    }
    public SbtTableView<S> getFxTable() {
        return fxTable;
    }

    public Mode getSelectionMode() {
        return mode;
    }

    public void setSelectionMode(Mode mode) {
        this.mode = mode;
        setCellSelectionEnabled(mode.cellSelectionEnabled);
        setRowSelectionAllowed(mode.rowSelectionAllowed);
        selectedColumns.clear();

        if (Mode.Cell.equals(mode)) {
            clearAndSelectRegion(mouseLocation.rowIndex,mouseLocation.tableColumn);
        } else if ( Mode.Column.equals(mode)) {
            fxTable.getSelectionModel().clearSelection();
            selectedColumns.add(mouseLocation.tableColumn);
            selectColumns();
        } else if ( Mode.Row.equals(mode)) {
            fxTable.getSelectionModel().clearSelection();
            fxTable.getSelectionModel().select(mouseLocation.rowIndex);
        } else if ( Mode.All.equals(mode)) {
            fxTable.getSelectionModel().selectRange(0,fxTable.getFirstColumnDeep(),fxTable.getItems().size()-1,fxTable.getLastColumnDeep());
        }
    }
    private void setCellSelectionEnabled(boolean enabled) {
        fxTable.getSelectionModel().setCellSelectionEnabled(enabled);
    }
    private void setRowSelectionAllowed(boolean allowed) {
        if ( allowed) {
            fxTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        } else {
            fxTable.getSelectionModel().setSelectionMode(null);
        }
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
