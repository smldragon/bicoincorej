package com.sbt.component;

import com.sbt.utils.ComponentUtil;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.Pane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class UiInputFieldsBinder {

    public static Parent rootPane;
    private final Map<String, QueryConditionNode> elementPropertyMap = new HashMap<>();
    private final SbtLabel statusField;
    private final String messageType;
    private final Pane filterPane;
    private static final Logger logger = LoggerFactory.getLogger(UiInputFieldsBinder.class);

    public UiInputFieldsBinder(String messageType, Pane filterPane,Node ...nodes ) {
        this.messageType = messageType;
        this.filterPane = null;
        SbtLabel temp = null;
        for(Node n: nodes) {
            String id = n.getId();
            if ( ComponentConstants.StatusFieldId.equals(id) && n instanceof SbtLabel) {
                temp = (SbtLabel)n;
            } else if ( n instanceof QueryConditionNode) {
                elementPropertyMap.put(id, (QueryConditionNode)n);
            }
        }
        statusField = temp;

    }
    public UiInputFieldsBinder(String messageType, Pane filterPane,String ...elementIds ) {
        this.messageType = messageType;
        this.filterPane = filterPane;

        if ( null != filterPane) {
            for( String eleId: elementIds) {
                Node node = filterPane.lookup("#"+eleId);
                if ( node instanceof QueryConditionNode) {
                    elementPropertyMap.put(eleId, (QueryConditionNode)node);
                }
            }
        }

        SbtLabel tmp;
        if ( null == filterPane) {
            tmp = null;
        } else {
            tmp = ComponentUtil.searchNode(filterPane,"#"+ComponentConstants.StatusFieldId);
        }

        if ( null == tmp && null != rootPane) {
            tmp = ComponentUtil.searchNode(rootPane,"#"+ComponentConstants.StatusFieldId);
        }
        statusField = tmp;

    }
    public SbtLabel getStatusLabel() {
        return statusField;
    }
    @Override
    public boolean isSameAs(String mesg) {
        return false;
    }
    public void setDisabled(boolean disabled) {

        if ( null != filterPane) {
            ComponentUtil.setDisabled(filterPane,disabled);
        }

        elementPropertyMap.values().stream().forEach( (node) -> {
            node.setDisable(disabled);
        });
    }
}
