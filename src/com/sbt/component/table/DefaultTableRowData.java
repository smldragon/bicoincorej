package com.sbt.component.table;

import java.io.Serializable;

public class DefaultTableRowData<T extends Serializable> extends SbtTableRowData {

    public DefaultTableRowData(T rowDataBean) {
        this.rowDataBean = rowDataBean;
    }
    public T getRowDataBean() {
        return rowDataBean;
    }
    private final T rowDataBean;
    private static final long serialVersionUID = 2018022201l;
}
