package com.sbt.component;

import com.sbt.connect.PreparedStatementParamStruct;
import com.sbt.message.MessageFormat;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.Pane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

public class UiInputFieldsBinder implements MessageFormat {

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

        Node tmp;
        if ( null == filterPane) {
            tmp = null;
        } else {
            tmp = ComponentUtil.searchNode(filterPane,"#"+ComponentConstants.StatusFieldId);
        }

        if ( null == tmp && null != rootPane) {
            tmp = ComponentUtil.searchNode(rootPane,"#"+ComponentConstants.StatusFieldId);
        }

        if ( ! (tmp instanceof SbtLabel ) ){
            tmp = null;
        }
        statusField = (SbtLabel)tmp;

    }
    public Pane getFilterPane() {
        return filterPane;
    }
    public SbtLabel getStatusLabel() {
        return statusField;
    }
    @Override
    public String getMessageType() {
        return messageType;
    }
    @Override
    public String buildMessage(){
        Set<String> elementIds = elementPropertyMap.keySet();
        StringBuffer rtn = new StringBuffer();
        for(String eleName: elementIds) {
            QueryConditionNode node = elementPropertyMap.get(eleName);
            rtn.append(node.getStringValue()).append(MessageFormat.MSG_DELIMITER);
        }
        return rtn.toString();
    }
    @Override
    public boolean isSameAs(String mesg) {
        return false;
    }
    public void clear() {
        Set<String> elementIds = elementPropertyMap.keySet();
        for(String eleName: elementIds) {
            QueryConditionNode node = elementPropertyMap.get(eleName);
            if ( null != node) {
                node.setStringValue("");
            }
        }
    }
    public Iterator<String> getElementNames() {
        return elementPropertyMap.keySet().iterator();
    }
    public void setValue(String eleName,String value) {

        QueryConditionNode node = elementPropertyMap.get(eleName);
        if ( null != node) {
            node.setStringValue(value);
        }
    }
    public String getValue(String eleName) {
        if ( null == eleName) {
            return null;
        }
        QueryConditionNode node = elementPropertyMap.get(eleName);
        if ( null != node) {
            return node.getStringValue();
        } else {
            return null;
        }
    }
    public Properties toProps() {
        Properties rtn = new Properties();
        elementPropertyMap.keySet().stream().forEach((name) -> {
            String value = getValue(name);
            if ( null != name && null != value) {
                rtn.setProperty(name,value);
            }
        });
        return rtn;
    }
    public PreparedStatementParamStruct buildQueryCondition() {

        StringBuilder sb = new StringBuilder();
        List<Object> paramList = new ArrayList<>();

        elementPropertyMap.values().stream().forEach( (node) -> {
            PreparedStatementParamStruct preparedStatementParamStruct = node.buildCondition();
            if ( null != preparedStatementParamStruct) {
                if ( sb.length() > 0) {
                    sb.append(" and ");
                }
                sb.append(preparedStatementParamStruct.whereClause);
                paramList.add(preparedStatementParamStruct.params[0]);
            }
        });

        if ( 0 < sb.length()) {

            Object [] paramArr = paramList.toArray(new Object[paramList.size()]);
            return new PreparedStatementParamStruct(" where "+sb.toString(),paramArr);
        } else {
            return null;
        }
    }
    public void setDisabled(boolean disabled) {

        if ( null != filterPane) {
            ComponentUtil.setDisable(filterPane,disabled);
        }

        elementPropertyMap.values().stream().forEach( (node) -> {
            node.setDisable(disabled);
        });
    }
}
