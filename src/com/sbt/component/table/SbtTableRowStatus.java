package com.sbt.component.table;

import java.io.Serializable;
import java.util.function.BiFunction;

public class SbtTableRowStatus implements Serializable{

    public static final SbtTableRowStatus Normal = new SbtTableRowStatus("",false);
    public static final SbtTableRowStatus Edited = new SbtTableRowStatus("*",true);
    public static final SbtTableRowStatus Transmitting = new SbtTableRowStatus("...",false);
    private static final BiFunction<Integer, SbtTableRowStatus , String> errorDisplayConstructor;

    static {
        errorDisplayConstructor = (rowIndex,tableRowStatus)-> tableRowStatus.prefix;
    }
    public static SbtTableRowStatus getErrorStatus(String errMsg) {

        SbtTableRowStatus rtn = new SbtTableRowStatus("Error",errMsg,"red",true);
        rtn.displayConstructor = errorDisplayConstructor;
        return rtn;
    }
    public SbtTableRowStatus(String prefix, boolean isEdited){
        this(prefix,"","black",isEdited);
    }
    public SbtTableRowStatus(String prefix, String toolTip, String fontColor, boolean isEdited) {
        this.prefix = prefix;
        this.toolTip = toolTip;
        this.fontColor = fontColor;
        this.isEdited = isEdited;

    }
    public String getText(int rowIndex) {
        if ( null == displayConstructor) {
            return prefix + String.valueOf(rowIndex);
        } else {
            return displayConstructor.apply(rowIndex,this);
        }
    }
    public String getPrefix() {
        return prefix;
    }

    public String getToolTip() {
        return toolTip;
    }

    public String getFontColor() {
        return fontColor;
    }

    public boolean isEdited() {
        return isEdited;
    }
    private final String prefix;
    private final String toolTip;
    private final String fontColor;
    private final boolean isEdited;
    private transient BiFunction<Integer, SbtTableRowStatus , String> displayConstructor;
}
