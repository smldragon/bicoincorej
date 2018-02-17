package com.sbt.component.table;

import com.sbt.utils.Amount;
import com.sbt.utils.SbtConstants;
import javafx.geometry.Pos;
import javafx.scene.control.TableCell;

import java.text.DecimalFormat;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class FormattedTableCell<S,T> extends TableCell<S,T> {

    private TableCellFormatter<S,T> tableCellFormatter;
    private String alignment;
    private String style;
    private String columnColorName;
    private static final Map<String,String> bckStyleLookupMap = new ConcurrentHashMap<>();

    public FormattedTableCell(TableCellFormatter<S,T> tableCellFormatter, String alignment, String style,String columnColorName) {
        this.tableCellFormatter = tableCellFormatter;
        this.alignment = alignment;
        this.style = style;
        this.columnColorName = columnColorName;
    }
    @Override
    protected void updateItem(T item, boolean empty) {
        super.updateItem(item,empty);
        formatCell(item,empty);
    }
    private void formatCell(T item, boolean empty) {

        if ( null == tableCellFormatter) {
            tableCellFormatter = new DefaultTableCellFormatter<>();
        }
        Pos alignPos;
        String alignmentTxt;
        if ( null == alignment ) {
            alignmentTxt = null;
        }else {
            alignmentTxt = alignment.toUpperCase();
        }

        if ( null ==  alignmentTxt ) {
            if ( item instanceof Amount) {
                alignPos = Pos.CENTER_RIGHT;
            } else if ( item instanceof Number ) {
                alignPos = Pos.CENTER_RIGHT;
            } else {
                alignPos = Pos.CENTER;
            }
        } else {

            alignPos = Pos.valueOf(Pos.class, alignmentTxt);
        }

        setAlignment(alignPos);

        tableCellFormatter.formatValue(style,this,item,empty);

        setCellBackgroundColor();
    }
    private void setCellBackgroundColor() {
        int rowIndex = getIndex();
        if ( getTableView().getSelectionModel().isSelected(rowIndex)) {
            setStyle(" -fx-background-color:"+FormattedTableCellFactory.TableViewRowHighlightColorHexValue+";");
        } else if ( null != columnColorName ) {
            updateTableCellBackgrdoundColor(false);
        }
    }
    public void updateTableCellBackgrdoundColor(boolean isSelected) {
        if ( 0 > getIndex() || getIndex() >= getTableView().getItems().size()) {
            setStyle(" -fx-background-color: transparent; ");
            return;
        }
        if ( isSelected) {
            setStyle(" -fx-background-color:"+FormattedTableCellFactory.TableViewRowHighlightColorHexValue+";");
            return;
        }
        if ( null == columnColorName && ! isSelected) {
            setStyle(" -fx-background-color: transparent; ");
            return;
        }

        if ( null != columnColorName && ! isSelected) {
            String rtn = bckStyleLookupMap.get(columnColorName);
            if ( null == rtn) {
                rtn = " -fx-background-color:"+columnColorName+", -fx-border-color: transparent derive(rgba(242,242,242),-32%) derive(rgba(242,242,242),-32%) transparent;";
                bckStyleLookupMap.put(columnColorName,rtn);
            }
            setStyle(rtn);
            return;
        }
    }
 /////////////////////////////////////////////////////////////////////////////////////////////
    public static class DefaultTableCellFormatter<S,T> implements TableCellFormatter<S,T> {

        private static final DecimalFormat AmountFormatter = new DecimalFormat(SbtConstants.AmountFormatString);
        private static final DecimalFormat IntegerFormatter = new DecimalFormat(SbtConstants.IntegerFormatString);
        public DefaultTableCellFormatter() {

        }
        @Override
        public void formatValue(String style, TableCell<S,T> cell, T item, boolean empty) {

            if ( null != item && ! empty) {

                if ( item instanceof Amount) {
                    cell.setText(AmountFormatter.format(  ((Amount)item).doubleValue()));
                } else if ( item instanceof Integer) {
                    cell.setText(IntegerFormatter.format(  ((Integer)item).intValue()));
                }else {
                    cell.setText(String.valueOf(item));
                }
            } else {
                cell.setText(" ");
            }

            if ( null != style) {
                cell.setStyle(style);
            }
        }
     }
}
