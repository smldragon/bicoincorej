package com.sbt.component;

import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.Event;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.ButtonBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

public class AsynEventExecutor {

    private static final Logger logger = LoggerFactory.getLogger(AsynEventExecutor.class);
    public static <T extends Event, V> void execute(AsynEventHandler<T,V> asynEventHandler, T event) {

        try {
            final long beginTime = System.currentTimeMillis();
            final AtomicReference<Boolean> toDoBackgroundJob = new AtomicReference<>();
            if (Platform.isFxApplicationThread()) {
                executeInFxThread(asynEventHandler, event, toDoBackgroundJob);
            } else {
                final CountDownLatch countDownLatch = new CountDownLatch(1);
                Platform.runLater(() -> {
                    executeInFxThread(asynEventHandler, event, toDoBackgroundJob);
                });
                try {
                    countDownLatch.await();
                } catch (InterruptedException e) {
                    logger.error("", e);
                }
            }

            if (toDoBackgroundJob.get()) {
                doBackgroundJob(asynEventHandler, event, beginTime);
            }

        }finally {
            setSourceWaitStatus(event, false);
        }
    }
    private static <T extends Event, V> void doBackgroundJob(AsynEventHandler<T,V> asynEventHandler,
                                                             T event, final long beginTime) {

        Service<Void> service = new Service<Void>() {

            @Override
            protected Task<Void> createTask() {
                return new Task<Void>() {

                    @Override
                    protected Void call() throws Exception {
                        AtomicReference<V> valueFromBackground = new AtomicReference<>(null);
                        valueFromBackground.set(asynEventHandler.doInBackground(event));
                        final long endTime = System.currentTimeMillis();
                        asynEventHandler.setTimeElapsed(endTime - beginTime);
                        Platform.runLater(() -> {
                            try {
                                asynEventHandler.updateInFxThread(valueFromBackground.get());
                            } finally {
                                setSourceWaitStatus(event,false);
                            }
                        });
                        return null;
                    }
                };
            }
        };
        service.start();
    }
    private static <T extends Event,V> void executeInFxThread(AsynEventHandler<T,V> asynEventHandler,T event,AtomicReference<Boolean> toDoBackgroundJob) {
        try {
            setSourceWaitStatus(event, true);
            boolean toContinue = asynEventHandler.prepareInFxThread(event);
            toDoBackgroundJob.set(toContinue);
        }catch(Exception e) {
            setSourceWaitStatus(event, false);
            logger.error("",e);
        }

    }
    private static <T extends Event> void setSourceWaitStatus(T event, boolean status) {

        if (Platform.isFxApplicationThread()) {
            esetSourceWaitStatusInFxThread(event,status);
        } else {
            Platform.runLater(() -> {
                esetSourceWaitStatusInFxThread(event,status);
            });
        }
    }
    private static <T extends Event> void esetSourceWaitStatusInFxThread(T event, boolean toWait) {
        Object source = event.getSource();
        if ( source instanceof Node) {
            boolean toDisable;
            Cursor cursor;
            if ( toWait) {

                toDisable = true;
                cursor = Cursor.WAIT;
            } else {

                toDisable = false;
                cursor = Cursor.DEFAULT;
            }

            ((Node) source).setCursor(cursor);
            if ( source instanceof ButtonBase) {
                ((ButtonBase) source).setDisable(toDisable);
            }
        }
    }
}
