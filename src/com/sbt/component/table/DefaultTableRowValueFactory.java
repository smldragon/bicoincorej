package com.sbt.component.table;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;

public class DefaultTableRowValueFactory<S,T> implements Callback<TableColumn.CellDataFeatures<DefaultTableRowData<S>,T>,ObservableValue<T>> {

    private static final Logger logger = LoggerFactory.getLogger(DefaultTableRowValueFactory.class);
    private final String fieldName;
    private Field field;

    public DefaultTableRowValueFactory(String fieldName) {
        this.fieldName = fieldName;
    }
    @Override
    public ObservableValue<T> call(TableColumn.CellDataFeatures<DefaultTableRowData<S>,T> param) {

        S rowData = param.getValue().getRowDataBean();
        try {
            if ( null == field ) {
                field = getField(rowData.getClass(),fieldName);
            }
            T value = (T) field.get(rowData);
            return new SimpleObjectProperty<T>(value);
        } catch (IllegalAccessException e) {
            logger.error("Code: ",e);
            return null;
        }
    }
    private static Field getField(Class<?> cls, String fieldName) {
        try {
            Field f = cls.getDeclaredField(fieldName);
            f.setAccessible(true);
            return f;
        } catch (NoSuchFieldException e) {
            logger.error("Code: ",e);
            return null;
        }
    }
}
