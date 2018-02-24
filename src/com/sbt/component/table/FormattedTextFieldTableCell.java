package com.sbt.component.table;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.StringConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.Toolkit;
import java.lang.reflect.Field;

public class FormattedTextFieldTableCell<S extends SbtTableRowData, T> extends TextFieldTableCell<S,T> {

    private static final Logger logger = LoggerFactory.getLogger(FormattedTextFieldTableCell.class);
    private boolean isListenerInstalled = false;
    private T value;
    public FormattedTextFieldTableCell() {
        super();
    }
    public FormattedTextFieldTableCell(StringConverter<T> stringConverter) {
        super(stringConverter);
    }
    @Override
    public void startEdit() {
        super.startEdit();
        if ( ! isListenerInstalled) {
            TextField textFieldTmp;
            try {
                Field field = TextFieldTableCell.class.getDeclaredField("textField");
                field.setAccessible(true);
                textFieldTmp = (TextField)field.get(this);
            }catch( NoSuchFieldException | IllegalAccessException e) {
                logger.error("",e);
                textFieldTmp = null;
            }

            final TextField textField = textFieldTmp;
            if ( null != textField ) {

                textField.setOnKeyReleased( (event)-> {
                    try {

                        StringConverter<T> stringConverter = getConverter();
                        int rowIndex = getIndex();
                        S rowData = getTableView().getItems().get(rowIndex);
                        String text = textField.getText();
                        Field field = null;
                        try {

                            String fieldName = ((PropertyValueFactory)getTableColumn().getCellValueFactory()).getProperty();
                            field = rowData.getClass().getDeclaredField(fieldName);
                            field.setAccessible(true);
                            value = stringConverter.fromString(text);
                            field.set(rowData,value);
                        }catch ( Exception e) {
                            if ( null != field) {
                                value = (T)field.get(rowData);
                                textField.setText(stringConverter.toString(value));
                                Toolkit.getDefaultToolkit().beep();
                            }
                        }
                    }catch(Exception e){
                        logger.error("",e);
                    }
                });

                textField.focusedProperty().addListener( (observable,oldValue,newValue) -> {
                    if ( ! newValue) {
                        if ( null != value) {
                            FormattedTextFieldTableCell.this.commitEdit(value);
                            ContextMenu contextMenu = FormattedTextFieldTableCell.this.getTableView().getContextMenu();
                            if ( contextMenu instanceof SbtTableRowContextMenu) {
                                ((SbtTableRowContextMenu)contextMenu).setRowIndex(FormattedTextFieldTableCell.this.getIndex());
                            } else {
                                FormattedTextFieldTableCell.this.cancelEdit();
                            }
                        }

                    }
                });
            }
            isListenerInstalled = true;
        }
    }
}
