package com.sbt.component;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

public abstract class ComponentUtil {

    public static String toRGBCode(Color color) {

        return String.format("#%02X%02X%02X",(int)(color.getRed()*255),(int)(color.getGreen()*255), (int)(color.getBlue()*255));
    }
    public static void setDisable(Pane pane, boolean disabled) {
        pane.setDisable(disabled);
    }
    public static Node searchNode(Parent parent, String nodeId) {
        Node rtn = searchNodeRecursivelyDown(parent,nodeId);
        missing codes;
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
