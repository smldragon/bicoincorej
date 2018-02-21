package com.sbt.component.table;

import javafx.scene.control.TableCell;

public interface TableCellFormatter<S,T> {

    void formatValue(String style, TableCell<S,T> cell, T item, boolean empty);
}
