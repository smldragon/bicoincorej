package com.sbt.component.table;

import javafx.geometry.Pos;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.ComboBoxTableCell;

public class ComboBoxTableCellFactory<S> extends EditableCellFactory<S,String> {
    public ComboBoxTableCellFactory(String ... comboBoxValues) {
        super(ComboBoxTableCell.forTableColumn(comboBoxValues));
    }
    @Override
    public TableCell<S,String> call(TableColumn<S,String> param) {
        TableCell<S,String> rtn = super.call(param);
        rtn.setAlignment(Pos.CENTER);
        return rtn;
    }
}
