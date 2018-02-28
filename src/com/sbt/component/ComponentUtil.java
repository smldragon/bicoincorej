package com.sbt.component;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class ComponentUtil {

    public static String convertToCamelString(String original) {
        return convertToCamelString(original,"\\s\\s*");
    }
    public static String convertToCamelString(String original,String separator) {
        if ( null  == original) {
            return null;
        }
        original = original.replaceAll(separator," ");
        String [] words = original.split(" ");
        StringBuffer sb = new StringBuffer();
        boolean firstWord = true;
        for (int i=0;i<words.length;i++) {
            String str = words[i].trim().toLowerCase();
            if ( 0 == str.length()) {
                continue;
            }
            String firstChar = str.substring(0,1);
            if ( firstWord ) {
                firstWord = false;
            } else {
                firstChar = firstChar.toUpperCase();
            }
            String convertedStr = firstChar + str.substring(1);
            sb.append(convertedStr);
        }
        return sb.toString();
    }

    /**
     * convert camel string to "_" separated string
     * @param input
     * @return
     */
    private static final String camelStringReg = "[A-Z]";
    public static String convertToUnderScoreString(String input) {
        Pattern pattern = Pattern.compile(camelStringReg);
        Matcher matcher = pattern.matcher(input);
        while ( matcher.find()) {
            int startIndex = matcher.start();
            String upperCaseLetter = input.substring(startIndex,(startIndex+1));
            StringBuffer sb = new StringBuffer();
            sb.append(input.substring(0,startIndex)).append("_").append(upperCaseLetter.toLowerCase()).append(input.substring((startIndex+1),input.length()));
            input = sb.toString();
            matcher = pattern.matcher(input);
        }
        return input;
    }
    public static String toRGBCode(Color color) {

        return String.format("#%02X%02X%02X",(int)(color.getRed()*255),(int)(color.getGreen()*255), (int)(color.getBlue()*255));
    }
    public static void setDisable(Pane pane, boolean disabled) {
        pane.setDisable(disabled);
    }
    public static Node searchNode(Parent parent, String nodeId) {
        Node rtn = searchNodeRecursivelyDown(parent,nodeId);
        if ( null == rtn) {
            rtn = searchNodeRecursivelyUp(parent,nodeId);
        }
        return rtn;
    }
    private static Node searchNodeRecursivelyUp(Parent parent, String nodeId) {
        Node rtn = searchNodeRecursivelyDown(parent,nodeId);
        if ( null == rtn) {
            Parent myParent = parent.getParent();
            if ( null != myParent && parent != myParent) {
                rtn = searchNodeRecursivelyUp(myParent,nodeId);
                if ( null != rtn) {
                    return rtn;
                }
            }
        }
        return rtn;
    }
    private static Node searchNodeRecursivelyDown(Parent parent, String nodeId) {
        Node rtn = parent.lookup(nodeId);
        if ( null == rtn) {
            for ( Node node: parent.getChildrenUnmodifiable()) {
                if ( node instanceof Parent) {
                    rtn = searchNodeRecursivelyDown( (Parent)node,nodeId);
                    if ( null != rtn) {
                        return rtn;
                    }
                }
            }
        }
        return rtn;
    }
}
