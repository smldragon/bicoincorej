package com.sbt.utils;

import com.sbt.component.CursorContainer;
import javafx.util.Callback;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FxWorker<T> {

    public FxWorker(CursorContainer cursorContainer) {
        this.cursorContainer = cursorContainer;
    }
    public CursorContainer getCursorContainer() {
        return cursorContainer;
    }

    public Callable<T> getDoActionInBackground() {
        return doActionInBackground;
    }

    public void setDoActionInBackground(Callable<T> doActionInBackground) {
        this.doActionInBackground = doActionInBackground;
    }

    public Callback<T, Void> getUiAction() {
        return uiAction;
    }

    public void setUiAction(Callback<T, Void> uiAction) {
        this.uiAction = uiAction;
    }

    public Callable<Boolean> getPrepareAction() {
        return prepareAction;
    }

    public void setPrepareAction(Callable<Boolean> prepareAction) {
        this.prepareAction = prepareAction;
    }
    private final CursorContainer cursorContainer;
    private Callable<T> doActionInBackground;
    private Callback<T,Void> uiAction;
    private Callable<Boolean> prepareAction;
    private static final ExecutorService backgroundThreadExecutor = Executors.newFixedThreadPool(1);
}
