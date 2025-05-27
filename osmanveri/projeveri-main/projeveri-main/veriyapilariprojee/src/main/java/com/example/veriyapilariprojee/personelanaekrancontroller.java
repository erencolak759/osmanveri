package com.example.veriyapilariprojee;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.json.JSONArray;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ResourceBundle;

public class personelanaekrancontroller implements Initializable {

    @FXML
    private Label kisiSayisiLabel;

    @FXML
    private Label isimLabel;

    @FXML
    private Label mailLabel;

    private final String jsonDosyaYolu = "C:\\veri_yapilari\\banka müsterikuyrugu\\paracekmekuyruğu.json";

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            File file = new File(jsonDosyaYolu);
            if (file.exists()) {
                String content = new String(Files.readAllBytes(Paths.get(jsonDosyaYolu)), StandardCharsets.UTF_8);
                JSONArray jsonArray = new JSONArray(content);
                int kisiSayisi = jsonArray.length();
                kisiSayisiLabel.setText(String.valueOf(kisiSayisi));
            } else {
                kisiSayisiLabel.setText("0");
            }
        } catch (IOException e) {
            kisiSayisiLabel.setText("HATA");
            e.printStackTrace();
        }
    }

    // Giriş yapan personelin bilgilerini ayarlamak için:
    @FXML
    public void setPersonelBilgi(String isim, String mail) {
        if (isimLabel != null && mailLabel != null) {
            isimLabel.setText(isim);
            mailLabel.setText(mail);
        }
    }

    public void getpersonelparayatırma(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("personelparacekme.fxml"));
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(loader.load()));
        stage.show();
    }
}
