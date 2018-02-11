package com.sbt.connect;

public class PreparedStatementParamStruct {

    public final String whereClause;
    public final Object [] params;

    public PreparedStatementParamStruct(String whereClause,Object ... params) {
        this.whereClause = whereClause;
        this.params = params;
    }
}
