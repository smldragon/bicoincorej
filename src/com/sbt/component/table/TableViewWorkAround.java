package com.sbt.component.table;

import com.sun.javafx.scene.control.skin.ContextMenuContent;
import com.sun.javafx.scene.control.skin.ContextMenuSkin;
import com.sun.javafx.scene.control.skin.TableColumnHeader;
import com.sun.javafx.scene.control.skin.TableHeaderRow;
import com.sun.javafx.scene.control.skin.TableViewSkin;
import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.PopupControl;
import javafx.scene.control.TableColumn;
import javafx.scene.input.MouseEvent;

import java.util.List;

@Deprecated
public abstract class TableViewWorkAround {

    public static <T extends SbtTableRowData> void patchContextMenuLeak(SbtTableView<T> table) {

        TableHeaderRow tableHeaderRow = TableUtil.getTableHeaderRow((TableViewSkin)table.getSkin());
        if ( null != tableHeaderRow) {
            ContextMenu tableRowHeaderContextMenu = TableUtil.getContextMenu(tableHeaderRow);
            for(int i=0;i<table.getColumns().size();i++) {
                TableColumn<T,?> tc = table.getColumns().get(i);
                setDragEventHandler(table,tableRowHeaderContextMenu,tc);
            }
        }
    }
    private static <T extends SbtTableRowData> void setDragEventHandler(SbtTableView<T> table, PopupControl popupControl,TableColumn<T,?> tableColumn) {

        TableColumnHeader tch = table.getColumnHeader(tableColumn);
        if ( null !=  tch ) {
            EventHandler<? super MouseEvent> defaultMousePressHandler = tch.getOnMouseDragged();
            tch.setOnMouseDragged(new HeaderMouseDragEventDelegator(popupControl,defaultMousePressHandler));
            List<?> subColumns = tableColumn.getColumns();
            if ( null != subColumns) {
                for(int i=0;i<subColumns.size();i++) {
                    setDragEventHandler(table,popupControl,(TableColumn<T,?>)subColumns.get(i));
                }
            }
        }
    }
////////////////////////////////////////////////////////////////////////////////
    private static class HeaderMouseDragEventDelegator implements EventHandler<MouseEvent> {

        private final EventHandler<? super MouseEvent> defaultMouseEventHandler;
        private final PopupControl popupControl;

        public HeaderMouseDragEventDelegator(PopupControl popupControl,EventHandler<? super MouseEvent> defaultMouseEventHandler) {
            this.defaultMouseEventHandler = defaultMouseEventHandler;
            this.popupControl = popupControl;
        }

        @Override
        public void handle(MouseEvent me) {
            ContextMenuSkin conttextMenuSkin = (ContextMenuSkin)popupControl.getSkin();
            if ( null != conttextMenuSkin) {
                ContextMenuContent contextMenuContent = (ContextMenuContent)conttextMenuSkin.getNode();
                contextMenuContent.dispose();
            }
            popupControl.skinProperty().set(null);

            if ( null != defaultMouseEventHandler) {
                defaultMouseEventHandler.handle(me);
            }
        }
}
}
