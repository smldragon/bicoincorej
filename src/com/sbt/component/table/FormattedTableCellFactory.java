package com.sbt.component.table;

import com.sbt.component.ComponentUtil;
import javafx.beans.DefaultProperty;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.paint.Color;
import javafx.util.Callback;

@DefaultProperty("tableCellFormatter")
public class FormattedTableCellFactory<S extends SbtTableRowData,T> implements Callback<TableColumn<S,T>,TableCell<S,T>> {

    public static final Color TableViewRowHighlightColor = Color.rgb(0,150,201);
    public static final String TableViewRowHighlightColorHexValue = ComponentUtil.toRGBCode(TableViewRowHighlightColor);
    private String style;
    private String columnColorName; //i.e. green,red,pink etc...
    private String alignment;
    private TableCellFormatter<S,T> tableCellFormatter;
    private Callback<TableColumn<S,T>, TableCell<S,T>> tableCellMaker;
    private boolean isEditable = false;
    private String name = null;

    public FormattedTableCellFactory() {

    }

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public String getColumnColorName() {
        return columnColorName;
    }

    public void setColumnColorName(String columnColorName) {
        this.columnColorName = columnColorName;
    }

    public String getAlignment() {
        return alignment;
    }

    public void setAlignment(String alignment) {
        this.alignment = alignment;
    }

    public TableCellFormatter<S, T> getTableCellFormatter() {
        return tableCellFormatter;
    }

    public void setTableCellFormatter(TableCellFormatter<S, T> tableCellFormatter) {
        this.tableCellFormatter = tableCellFormatter;
    }

    public Callback<TableColumn<S, T>, TableCell<S, T>> getTableCellMaker() {
        return tableCellMaker;
    }

    public boolean isEditable() {
        return isEditable;
    }

    public void setEditable(boolean editable) {
        isEditable = editable;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public FormattedTableCellFactory(Callback<TableColumn<S,T>, TableCell<S,T>> tableCellMaker) {
        this.tableCellMaker = tableCellMaker;
    }
    public void setTableCellMaker(Callback<TableColumn<S,T>, TableCell<S,T>> tableCellMaker) {
        this.tableCellMaker = tableCellMaker;
    }
    @Override
    public TableCell<S,T> call(TableColumn<S,T> p) {

        TableCell<S,T> tableCell;
        if ( null != tableCellMaker) {
            tableCell = tableCellMaker.call(p);
        }else {
            tableCell =  new FormattedTableCell<>(tableCellFormatter, alignment,style,columnColorName);
            tableCell.setEditable(isEditable);
            SbtTableView<S> table = (SbtTableView<S>)p.getTableView();
            tableCell.updateTableView(table);
            table.setOnMousePressed(table.getTableCellsSelector().getMousePressedHandler());
            table.setOnDragDetected(table.getTableCellsSelector().getMouseEventHandler());
            table.setOnMouseDragEntered(table.getTableCellsSelector().getMouseDragEventHandler());
            table.setOnMouseReleased((mouseEvent) -> {
                table.getTableCellsSelector().setMouseLocation(mouseEvent);
                table.getTableCellsSelector().onMouseReleased(mouseEvent);
                TableViewClipboardCopy<S> tableViewClipboardCopy = new TableViewClipboardCopy<>(table);
                tableViewClipboardCopy.handle(mouseEvent);
            });

            tableCell.selectedProperty().addListener((observable,oldValue,newValue) -> {
                int rowIndex = tableCell.getIndex();
                if ( rowIndex >= table.getItems().size() || 0 > rowIndex) {
                    tableCell.setStyle(" -fx-background-color: transparent; ");
                } else {
                    ((FormattedTableCell)tableCell).updateTableCellBackgrdoundColor(tableCell.isSelected());
                }
            });
        }
        return tableCell;
    }
}
