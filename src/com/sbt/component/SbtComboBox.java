package com.sbt.component;

import javafx.scene.control.ComboBox;
import javafx.util.StringConverter;

public class SbtComboBox<T> extends ComboBox<T> implements QueryConditionNode {

    private final StringConverter<T> stringConverter;

    public SbtComboBox() {
        this(null);
    }
    public SbtComboBox(StringConverter<T> stringConverter) {
        this.stringConverter = stringConverter;
    }

    @Override
    public String getName() {
        return getId();
    }

    @Override
    public String getStringValue() {
        T selectedValue = getSelectionModel().getSelectedItem();
        if ( null != stringConverter) {
            return stringConverter.toString(selectedValue);
        }
        return null != selectedValue? String.valueOf(selectedValue):null;
    }

    @Override
    public void setStringValue(String valueStr) {

        T value;
        if ( null != stringConverter){
            value = stringConverter.fromString(valueStr);
        } else {
            value = null;
            for(int i=0;i<getItems().size();i++) {
                T item = getItems().get(i);
                if ( valueStr.equals(String.valueOf(item))) {
                    value = item;
                    break;
                }
            }
        }

        if ( null != value) {
            getSelectionModel().select(value);
        }
    }
}
