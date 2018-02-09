package com.sbt.component;

import javafx.event.Event;
import javafx.event.EventHandler;

public interface AsynEventHandler<T extends Event, V> extends EventHandler<T> {

    default void updateInFxThread(V valueFromBackgroundThread) {
        //doing nothing
    }
    default V doInBackground(T event) {
        return null;
    }
    default boolean prepareInFxThread(T event) {
        return true;
    }
    @Override
    default void handle(T event) {
        AsynEventExecutor.execute(this,event);
    }
    default long getTimeElapsed() {
        return 0l;
    }
    default void setTimeElapsed(long timeElapsed) {

    }
}
