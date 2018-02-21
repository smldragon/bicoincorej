package com.sbt.component.table;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

public class RowNumberColumn <V extends SbtTableRowData>  {

    private static final double defaultPrefWidth = 45d;
    private final DoubleProperty prefWidthProperty = new SimpleDoubleProperty(defaultPrefWidth);
    private final DoubleProperty minWidthProperty = new SimpleDoubleProperty(defaultPrefWidth);
    private final DoubleProperty maxWidthProperty = new SimpleDoubleProperty(defaultPrefWidth*2);
    private final SbtTableView<V> tableView;
    private final SecondHeaderLineProp secondHeaderLineProp;

    public RowNumberColumn(SbtTableView<V> tableView,SecondHeaderLineProp secondHeaderLineProp) {
        this.tableView = tableView;
        this.secondHeaderLineProp = secondHeaderLineProp;
    }
    public void install() {

        TableColumn<V,String> effectiveTableColumn;
        TableColumn<V,String> numberCol = new TableColumnRnc<>(SbtTableView.RowNoColTitle);
        TableColumn<V,String> subNumberCol;
        Callback<TableColumn.CellDataFeatures<V,String>, ObservableValue<String>> cellValueFactory = (param) -> {
            return null;
        };

        if ( null != secondHeaderLineProp){
            subNumberCol = new TableColumnRnc<>(SbtTableView.RowNoColTitle);
            numberCol.getColumns().add(subNumberCol);
            numberCol.setText(secondHeaderLineProp.getLeadingText());
            effectiveTableColumn = subNumberCol;
        } else {
            effectiveTableColumn = numberCol;
            subNumberCol = null;
        }

        effectiveTableColumn.setCellValueFactory(cellValueFactory);

        Callback<TableColumn<V,String>, TableCell<V,String>> cellFactory = (param) -> {
            return new RowNumberCell<V>(tableView);
        };

        effectiveTableColumn.maxWidthProperty().bindBidirectional(maxWidthProperty);
        effectiveTableColumn.minWidthProperty().bindBidirectional(minWidthProperty);
        effectiveTableColumn.prefWidthProperty().bindBidirectional(prefWidthProperty);

        effectiveTableColumn.setCellFactory(cellFactory);
        effectiveTableColumn.setSortable(false);

        numberCol.impl_setReorderable(false);

        tableView.getColumns().add(0,numberCol);
    }
    public void setPrefWidth(double prefWidth) {
        prefWidthProperty.set(prefWidth);
        minWidthProperty.set(prefWidth);
        maxWidthProperty.set(prefWidth*2);
    }
 ///////////////////////////////////////////////////////////////////////////
    public static class TableColumnRnc<S,T> extends TableColumn<S,T> {

        public TableColumnRnc(String text) {
            super(text);
        }
    }
}
