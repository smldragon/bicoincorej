package com.sbt.component.table;

import com.sun.javafx.scene.control.skin.TableHeaderRow;
import com.sun.javafx.scene.control.skin.TableViewSkin;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.TableColumn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.List;

public abstract class TableUtil {

    private static final Logger logger = LoggerFactory.getLogger(TableUtil.class);
    public static TableHeaderRow getTableHeaderRow(TableViewSkin<?> tableSkin) {

        if ( null == tableSkin) {
            return null;
        }

        List<Node> children = tableSkin.getChildren();
        for(Node node: children) {
            if ( node instanceof TableHeaderRow) {
                return (TableHeaderRow)node;
            }
        }
        return null;
    }
    public static ContextMenu getContextMenu(TableHeaderRow headerRow){

        try {

            Field privateContextMenuField = TableHeaderRow.class.getDeclaredField("columnPopupMenu");
            privateContextMenuField.setAccessible(true);
            return (ContextMenu)privateContextMenuField.get(headerRow);
        } catch( Exception e) {
            logger.error("",e);
        }
        return null;
    }
    public static <S extends SbtTableRowData> int getTableColumnIndexDeep(List<TableColumn<S,?>> columnList, TableColumn<S,?> tc ) {

        int index = columnList.indexOf(tc);
        if ( 0 > index ) {
            int leftColumns = 0;
            for( TableColumn<S,?> tblColumn: columnList) {
                int subIndex = tblColumn.getColumns().indexOf(tc);
                if ( 0 <= subIndex) {
                    index = leftColumns + subIndex;
                    break;
                } else {
                    leftColumns += tblColumn.getColumns().size();
                }
            }
        }
        return index;
    }
}
