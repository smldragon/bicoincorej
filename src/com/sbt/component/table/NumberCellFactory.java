package com.sbt.component.table;

import javafx.geometry.Pos;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.Callback;
import javafx.util.StringConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.NumberFormat;
import java.text.ParseException;

public class NumberCellFactory<S extends SbtTableRowData,T extends Number> extends FormattedTableCellFactory<S,T> {

    private static final Logger logger = LoggerFactory.getLogger(NumberCellFactory.class);

    public NumberCellFactory(NumberFormat numberFormat) {
        super(new NumberCallback<>(numberFormat));
    }
 //////////////////////////////////////////////////////////////////////////////////////////////////
    private static class NumberCallback<S extends SbtTableRowData, T extends Number> implements Callback<TableColumn<S,T>, TableCell<S,T>> {

        private final NumberFormat numberFormat;
        public NumberCallback(NumberFormat numberFormat) {
            this.numberFormat = numberFormat;
        }
        @Override
        public TableCell<S,T> call(TableColumn<S,T> param) {

            final TextFieldTableCell<S,T> tableCell = new TextFieldTableCell<>(new StringConverter<T>() {
                @Override
                public String toString(T value) {
                    return numberFormat.format(value);
                }

                @Override
                public T fromString(String sValue) {
                    try {
                        return (T)numberFormat.parse(sValue);
                    } catch (ParseException e) {
                        logger.error("",e);
                        return null;
                    }
                }
            });

            tableCell.setOnKeyPressed( (event)-> {

                TextFieldTableCell<S,T> source = (TextFieldTableCell<S,T>)event.getSource();
                String text = source.getText();
                try {
                    source.converterProperty().get().fromString(text);
                }catch ( Exception e) {
                    System.out.println("Invalid text="+text);
                }
            });

            tableCell.setAlignment(Pos.CENTER_RIGHT);
            return tableCell;
        }
    }
}
