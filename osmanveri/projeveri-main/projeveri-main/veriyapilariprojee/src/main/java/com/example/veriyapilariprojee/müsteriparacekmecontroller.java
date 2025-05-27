package com.example.veriyapilariprojee;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.json.JSONArray;
import org.json.JSONObject;

import java.awt.*;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ResourceBundle;

public class müsteriparacekmecontroller implements Initializable {


    @FXML
    private Button ParaCekBtn;

    @FXML
    private TextField paraMiktarTxt;

    @FXML
    private Label siraLabel;

    @FXML
    void handlegir(ActionEvent event) {

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}
