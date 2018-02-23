package com.sbt.component.table;

import javafx.beans.NamedArg;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.TableColumnBase;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SecondHeaderLineProp {

    public static final SecondHeaderLineProp Total;
    public static final String BorderStyle = " -fx-border-color:#bbbbbb; -fx-border-width: 0 0 1 0; -fx-border-style: solid; ";
    private static final Pattern negativeNumberPattern = Pattern.compile("^-[\\.0-9]+[\\.0-9\\,]*");

    static {

        Total = new SecondHeaderLineProp("Sum"," -fx-text-fill: black; -fx-background-color: -fx-control-outer-background; -fx-border-color: blue; ") {

            @Override
            public String getStyle(TableColumnBase<?,?> tc) {
                String headerText = tc.getText().trim();
                Matcher m = negativeNumberPattern.matcher(headerText);
                if ( m.matches()) {
                    return "-fx-text-fill: red; -fx-background-color: white; -fx-alignment: CENTER-RIGHT; ";
                }else {
                    return "-fx-text-fill: black; -fx-background-color: white; -fx-alignment: CENTER-RIGHT; ";
                }
            }
        };
    }

    private final StringProperty leadingTextProperty = new SimpleStringProperty();
    private final StringProperty styleProperty = new SimpleStringProperty();

    public SecondHeaderLineProp(@NamedArg("leadingTextProperty") String leadingText, @NamedArg("styleProperty") String style) {
        leadingTextProperty.set(leadingText);
        styleProperty.set(style);
    }
    public String getLeadingText() {
        return leadingTextProperty.get();
    }
    public String getStyle(TableColumnBase<?,?> tc) {
        return styleProperty.get();
    }
    public void setLeadingText(String leadingText) {
        leadingTextProperty.set(leadingText);
    }
    public void setStyle(String style) {
        styleProperty.set(style);
    }
    @Override
    public String toString() {
        return "leading text="+leadingTextProperty.get()+ " style = "+styleProperty.get();
    }
}
