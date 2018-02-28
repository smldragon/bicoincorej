package com.sbt.component.table;

import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

public abstract class EditableCellFactory<S,T> implements Callback<TableColumn<S,T>, TableCell<S,T>> {

    public EditableCellFactory(Callback<TableColumn<S,T>, TableCell<S,T>> delegate) {
        this.delegate = delegate;
    }
    @Override
    public TableCell<S,T> call(TableColumn<S,T> param) {
        return delegate.call(param);
    }
    private final Callback<TableColumn<S,T>, TableCell<S,T>> delegate;
}
