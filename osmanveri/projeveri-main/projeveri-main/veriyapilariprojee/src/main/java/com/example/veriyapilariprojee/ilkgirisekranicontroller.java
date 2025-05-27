package com.example.veriyapilariprojee;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

public class ilkgirisekranicontroller {
    @FXML
    private AnchorPane mainscreenanchorpane;
    @FXML
    private ImageView mainscreenimageview;

    @FXML
    private Button müsteributton;

    @FXML
    private Button personelbutton;

    @FXML
    public void getpersonelgirisekrani(ActionEvent event) throws IOException{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("personelanaekran.fxml"));
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(loader.load()));
        stage.show();
    }
    public void getmüsterigirissecimekrani(ActionEvent event) throws IOException{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("müsterigirissecimekrani.fxml"));
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(loader.load()));
        stage.show();
    }


}