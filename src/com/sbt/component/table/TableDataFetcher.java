package com.sbt.component.table;

import javafx.collections.ObservableList;

public interface TableDataFetcher<T> {
    ObservableList<T> getTableData() throws Exception;
}
