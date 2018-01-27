package com.sbt.bitcoin.view;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class BitCoinExplorerMain extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("/BitCoinExplorer.fxml"));
        Scene scene = new Scene(root, 1260, 900);
        primaryStage.setTitle("BitCoinCore Explorer");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}