package com.sbt.component.table;

import com.sbt.utils.AmountFormat;

public class AmountCellFactory<S extends SbtTableRowData> extends NumberCellFactory<S>{

    public AmountCellFactory() {
        super( new AmountFormat("#,##0.00"));
    }
}
