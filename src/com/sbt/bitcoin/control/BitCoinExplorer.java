package com.sbt.bitcoin.control;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.Pane;

import java.net.URL;
import java.util.ResourceBundle;

public class BitCoinExplorer implements Initializable {

    @FXML
    private Pane rootPane;
    @Override
    public void initialize(URL location, ResourceBundle resources) {

        rootPane.getStylesheets().add("/BitCoinExplorer.css");
    }
}
