package com.sbt.component.table;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.io.Serializable;

public abstract class SbtTableRowData implements Serializable{

    public SbtTableRowStatus getRowStatus() {
        initRowStatus();
        return rowStatus.get();
    }
    public void setRowStatusValue(SbtTableRowStatus rowStatusValue) {
        this.rowStatusValue = rowStatusValue;
        if ( null == rowStatus) {
            rowStatus = new SimpleObjectProperty<>(this.rowStatusValue);
        } else {
            rowStatus.set(this.rowStatusValue);
        }
    }
    public boolean isEdited() {
        return rowStatusValue == null?false:rowStatusValue.isEdited();
    }
    private synchronized void initRowStatus() {
        if ( null == rowStatus) {
            rowStatus = new SimpleObjectProperty<>(rowStatusValue);
        }
    }
    private SbtTableRowStatus rowStatusValue = SbtTableRowStatus.Normal;
    private transient ObjectProperty<SbtTableRowStatus> rowStatus;
}
