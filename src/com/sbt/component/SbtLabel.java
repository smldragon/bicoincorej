package com.sbt.component;

import com.sbt.connect.PreparedStatementParamStruct;
import javafx.scene.control.Label;

public class SbtLabel extends Label implements QueryConditionNode {
    @Override
    public String getName() {
        return getId();
    }

    @Override
    public String getStringValue() {
        return textProperty().get();
    }

    @Override
    public void setStringValue(String value) {
        textProperty().set(value);
    }

    @Override
    public PreparedStatementParamStruct buildCondition() {
        return null;
    }
}
