package com.sbt.component.table;

import javafx.util.converter.NumberStringConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.NumberFormat;

public class NumberCellFactory<S extends SbtTableRowData> extends FormattedTableCellFactory<S,Number> {

    private static final Logger logger = LoggerFactory.getLogger(NumberCellFactory.class);

    public NumberCellFactory(NumberFormat numberFormat) {
        super(new FormattedCallback<>( new NumberStringConverter(numberFormat)));
    }
}
