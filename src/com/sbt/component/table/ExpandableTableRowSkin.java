package com.sbt.component.table;

import com.sun.javafx.scene.control.skin.TableRowSkin;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Labeled;
import javafx.scene.control.SkinBase;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumnBase;
import javafx.scene.control.TableRow;

import java.util.List;

public class ExpandableTableRowSkin<S extends SbtTableRowData> extends TableRowSkin<S> {

    public ExpandableTableRowSkin( ExpandableTableRow<S> expandableTableRow) {
        super(expandableTableRow);
    }
    @Override
    protected void layoutChildren(final double x, final double y, final double w, final double h) {

        super.layoutChildren(x,y,w,h);

        TableRow<S> row = getBehavior().getControl();
        SbtTableView<S> table = getTable();
        if ( ! table.isDefaultRowHeightSet()) {
            table.setDefaultRowHeight(row.getHeight());
        }

        row.selectedProperty().addListener( (observable, oldValue,newValue) ->{
            bugWalkAround(row);
        });
    }
    private void bugWalkAround(TableRow<S> row) {
        int rowIndex = row.getIndex();

        SkinBase<?> skin = (SkinBase<?>)row.getSkin();
        List<Node> cells = skin.getChildren();
        for ( Node c: cells) {
            if ( rowIndex >= row.getTableView().getItems().size() || rowIndex < 0) {
                c.setStyle(" -fx-background-color: transparent; ");
            } else if ( c instanceof FormattedTableCell) {
                ((FormattedTableCell)c).updateTableCellBackgroundColor(row.isSelected());
            }
        }
    }
    public void setAlignment(Pos pos) {
        List<Node> cells = getChildren();
        for(int i=0;i<cells.size();i++) {
            Node node = cells.get(i);
            if ( node instanceof RowNumberCell) {
                ((Labeled)node).setAlignment(Pos.CENTER);
            } else if ( node instanceof Labeled) {
                ((Labeled)node).setAlignment(pos);
            }
        }
    }
    public void setFontStyle(String fontStyle) {
        List<Node> cells = getChildren();
        for(Node node: cells) {
            if ( node instanceof Labeled) {
                ((Labeled)node).setStyle(fontStyle);
            }
        }
    }
    @Override
    protected TableCell<S,?> getCell(TableColumnBase tcb) {
        TableCell<S,?> cell = super.getCell(tcb);
        return cell;
    }
    private SbtTableView<S> getTable() {
        return (SbtTableView<S>)getBehavior().getControl().getTableView();
    }
}
