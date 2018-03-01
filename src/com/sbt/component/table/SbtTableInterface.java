package com.sbt.component.table;

import com.sbt.component.UiInputFieldsBinder;
import javafx.scene.Cursor;
import javafx.scene.control.Skin;
import javafx.scene.control.TableColumn;

import java.util.List;

public interface SbtTableInterface<T extends SbtTableRowData> {

    void prepareLoading(String loadingStr);
    void setNoDataPrompt(String prompt);
    void showPrompt(String prompt);
    void setItems(List<T> items);
    List<T> getItems();
    UiInputFieldsBinder getQueryConditionPropBinder();
    void setDisable(boolean disabled);
    void setCursor(Cursor cursor);
    String getId();
    boolean isCacheable();
    List<TableColumn<T,?>> getTableColumns();
    void scrollToRow(int rowIndex, TableColumn<T,?> tc);
    Skin<?> getSkin();
}
