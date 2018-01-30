package com.sbt.test;

import com.sbt.bitcoin.wallet.control.GuiUtils;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class TableViewTest extends Application {

    @Override
    public void start(Stage mainWindow) throws Exception {
        try {
            realStart(mainWindow);
        } catch (Throwable e) {
            GuiUtils.crashAlert(e);
            throw e;
        }
    }
    private void realStart(Stage mainWindow) throws IOException {

        URL location = getClass().getResource("/test/tableViewTest.fxml");
        FXMLLoader loader = new FXMLLoader(location);
        Scene scene = new Scene( loader.load());
        scene.getStylesheets().add(getClass().getResource("/test/tableViewTest.css").toString());
        mainWindow.setScene(scene);
        mainWindow.setTitle("TableViewTest");
        mainWindow.show();
    }
    public static void main(String[] args) {
        launch(args);
    }
}
