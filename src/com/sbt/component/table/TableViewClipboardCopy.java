package com.sbt.component.table;

import javafx.event.EventHandler;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.InputEvent;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

import java.util.List;

public class TableViewClipboardCopy<S extends SbtTableRowData> implements EventHandler<InputEvent> {

    private static final char CellDelimiter = '\t';
    private static final char lineDelimiter = '\n';
    private final SbtTableView<S> table;
    public TableViewClipboardCopy(SbtTableView<S> table) {
        this.table = table;
    }

    @Override
    public void handle(InputEvent event) {

        if ( event instanceof KeyEvent) {
            handleKeyEvent((KeyEvent)event);
        } else if ( event instanceof MouseEvent) {
            handleMouseEvent((MouseEvent)event);
        }

    }
    private void handleMouseEvent(MouseEvent event) {

        Object source = event.getSource();
        if ( source instanceof TableCell) {
            TableCell<S,?> cell = (TableCell<S,?>)source;
            final ClipboardContent content = new ClipboardContent();
            content.putString( cell.getText());
            Clipboard.getSystemClipboard().setContent(content);
        }
    }
    private void handleKeyEvent(KeyEvent event) {

        if ( ! event.isControlDown()) {
            return;
        }
        StringBuilder clipboardString = new StringBuilder();
        if ( TableCellsSelector.Mode.Row != table.getTableCellsSelector().getSelectionMode()) {
            copySelectedCells(clipboardString);
        } else {
            copyHighlightedRows(clipboardString);
        }
        final ClipboardContent content = new ClipboardContent();
        content.putString( clipboardString.toString());
        Clipboard.getSystemClipboard().setContent(content);
    }
    private void copySelectedCells( StringBuilder clipboardString) {
        List<TablePosition> selectedCells = table.getSelectionModel().getSelectedCells();
        int currentRow = -1;
        for ( int i=0;i<selectedCells.size();i++) {
            TablePosition cell = selectedCells.get(i);
            TableColumn<S, ?> tc = cell.getTableColumn();
            int rowIndex = cell.getRow();

            if (currentRow == rowIndex) {
                clipboardString.append(CellDelimiter);
            } else {
                if (currentRow >= 0) {
                    clipboardString.append(lineDelimiter);
                }
                currentRow = rowIndex;
            }

            Object cellValue = tc.getCellData(rowIndex);
            if (null == cellValue) {
                cellValue = "";
            }
            clipboardString.append(cellValue);
        }
        if ( selectedCells.size() > 1) {
            clipboardString.append(lineDelimiter);
        }
    }
    private void copyHighlightedRows(StringBuilder clipboardString) {
        List<Integer> selectedRowIndice = table.getSelectionModel().getSelectedIndices();
        List<TableColumn<S,?>> leafColumns = table.getLeafTableColumns();

        appendRowData(clipboardString,-1,leafColumns);
        clipboardString.append(lineDelimiter);

        for(int rowIndex: selectedRowIndice) {
            appendRowData(clipboardString,rowIndex,leafColumns);
            if ( rowIndex != selectedRowIndice.get(selectedRowIndice.size()-1)) {
                clipboardString.append(lineDelimiter);
            }
        }
    }
    private void appendRowData(StringBuilder clipboardString,int rowIndex,List<TableColumn<S,?>> leafColumns) {

        boolean toAddDelimier = false;
        for (int columnIndex = 0; columnIndex<leafColumns.size();columnIndex++) {

            TableColumn<S,?> tc = leafColumns.get(columnIndex);
            if ( tc instanceof RowNumberColumn.TableColumnRnc) {
                continue;
            }
            Object cellValue;
            if ( 0 <= rowIndex ) {
                cellValue = tc.getCellData(rowIndex);
            } else {
                cellValue = tc.getText();
            }

            if ( null == cellValue) {
                cellValue = "";
            }

            if ( toAddDelimier) {
                clipboardString.append(CellDelimiter);
            }

            clipboardString.append(cellValue);

            toAddDelimier = true;

        }
    }
}
