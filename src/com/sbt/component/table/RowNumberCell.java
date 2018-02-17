package com.sbt.component.table;

import javafx.geometry.Pos;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableRow;
import javafx.scene.control.Tooltip;

public class RowNumberCell<V extends SbtTableRowData> extends TableCell<V,String> {

    private final SbtTableView<V> tableView;

    public RowNumberCell(SbtTableView<V> tableView) {
        this.tableView = tableView;
        this.getStyleClass().clear();
        getStyleClass().addAll(tableView.getColumnHeaderStyleClasses());

        setWidth(10);
        if ( null != tableView.getTableCellsSelector()) {
            setOnDragDetected(tableView.getTableCellsSelector().getMouseEventHandler());
            setOnMouseDragEntered(tableView.getTableCellsSelector().getMouseDragEventHandler());
        }
    }
    public void setConfigWidth(double width) {
        setMinWidth(width);
        setWidth(width);
        setMaxWidth(width*1.5);
    }
    @Override
    protected void updateItem(String item, boolean empty) {

        super.updateItem(item,empty);

        TableRow<?> tr = getTableRow();
        if ( null != tr && ! empty) {

            Object rowData = tr.getItem();
            if ( rowData instanceof SbtTableRowData) {
                updateDisplay( ((SbtTableRowData)rowData).getRowStatus());
            }
            setAlignment(Pos.CENTER);
        } else if ( ! tableView.toFillTableWithBlankRows()){
            //to make space below last data row have same color as table view.
            setStyle(" -fx-background-color: transparent; ");
            setText("");
        }
    }
    private void updateDisplay(SbtTableRowStatus value) {

        if (null == value) {
            value = SbtTableRowStatus.Normal;
        }

        TableRow<?> tr = getTableRow();
        int rowIndex = tr.getIndex() + 1;
        setText(value.getText(rowIndex));
        String toolTipText = value.getToolTip(rowIndex);
        if ( null != toolTipText && 0 <toolTipText.trim().length()) {
            setTooltip(new Tooltip(toolTipText));
        } else {
            setTooltip(null);
        }

        String fontColor = value.getFontColor();
        if ( null != fontColor && 0 <fontColor.length()) {
            setStyle(" -fx-text-fill: "+fontColor+"; -fx-opacity: 0.7;");
        } else {
            setStyle(" " );
        }
    }
}
