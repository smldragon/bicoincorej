package com.sbt.component.table;

import com.sbt.utils.Amount;
import com.sbt.utils.AmountFormat;

public class AmountCellFactory<S extends SbtTableRowData> extends NumberCellFactory<S,Amount>{

    public AmountCellFactory() {
        super( new AmountFormat("#,##0.00"));
    }
}
