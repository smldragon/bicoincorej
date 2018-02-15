package com.sbt.component.table;

import javafx.scene.control.TableRow;
import javafx.scene.input.MouseEvent;

public class TableRowMouseEventListener<S extends SbtTableRowData> {

    private boolean isDragging = false;
    private SbtTableView<S> table;
    private int lastClickedRow = -1;
    private boolean clickedRowChanged;

    public void handle(MouseEvent event, final int rowIndex) {

        TableRow<S> tc = (TableRow<S>)event.getSource();
        clickedRowChanged = rowIndex != lastClickedRow;
        table = (SbtTableView<S>)tc.getTableView();
        if ( MouseEvent.MOUSE_PRESSED.getName().equals(event.getEventType().getName())) {
            isDragging = false;
            handleMousePressed(event,rowIndex);
        } else if (MouseEvent.MOUSE_DRAGGED.getName().equals(event.getEventType().getName()) ) {
            isDragging = true;
            lastClickedRow = -1;
        } else if (MouseEvent.MOUSE_RELEASED.getName().equals(event.getEventType().getName()) ) {
            if ( isDragging) {
                handleMouseDraggedFinished(event,rowIndex);
                isDragging = false;
            } else {
                handleMouseReleased(event,rowIndex);
            }
        } else if (MouseEvent.MOUSE_CLICKED.getName().equals(event.getEventType().getName()) ) {
            handleMouseClick(event,rowIndex);
            lastClickedRow = rowIndex;
        }
    }
    public boolean isClickedRowChanged() {
        return clickedRowChanged;
    }
    public SbtTableView<S> getTable() {
        return table;
    }
    protected void handleMousePressed(MouseEvent event,int rowIndex) {

    }
    protected void handleMouseClick(MouseEvent event,int rowIndex) {

    }
    protected void handleMouseReleased(MouseEvent event,int rowIndex) {

    }
    protected void handleMouseDraggedFinished(MouseEvent event,int rowIndex) {

    }
}
