package com.sbt.component.table;

public interface TablePropertyInitializer<T extends SbtTableRowData> {

    void initTableProperties(SbtTableView<T> table);
}
