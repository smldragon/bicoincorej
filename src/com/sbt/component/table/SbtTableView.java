package com.sbt.component.table;

import javafx.scene.control.TableView;

public class SbtTableView<S extends SbtTableRowData> extends TableView<S> {

    @Override
    public void layoutChildren() {

        if ( !initStatus) {
            initComponents();
        }

        installRowNumberColumn();

        super.layoutChildren();

        if ( !inRestoredFromCache) {
            inRestoredFromCache = true;
            restoreFromCache();
        }

        if ( ! initStatus ) {
            TableViewWorkAround.patchContextMenuLeak(this);
        }
        initStatus = true;

    }
    private void installRowNumberColumn() {
        if ( ! rowNumberColStatus) {
            rowNumberColStatus = true;
            doInstallRowNumberColumn();
        }
    }
    private boolean initStatus = false;
    private boolean inRestoredFromCache = false;
    private boolean rowNumberColStatus = false;
}
