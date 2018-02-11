package com.sbt.component;

import com.sbt.connect.PreparedStatementParamStruct;

public interface QueryConditionNode {

    String getName();
    String getStringValue();
    void setStringValue(String value);
    void setDisable(boolean disabled);
    default PreparedStatementParamStruct buildCondition() {

        String value = getStringValue();
        if ( null == value) {
            return null;
        }
        value = value.trim();
        if ( "".equals(value)) {
            return null;
        }

        StringBuilder sb = new StringBuilder();
        sb.append(getName()).append("=?");

        return new PreparedStatementParamStruct(sb.toString(),value);
    }
}
