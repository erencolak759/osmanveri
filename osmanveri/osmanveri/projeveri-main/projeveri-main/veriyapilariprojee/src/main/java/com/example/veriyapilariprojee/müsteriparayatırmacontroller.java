package com.example.veriyapilariprojee;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ResourceBundle;

public class müsteriparayatırmacontroller implements Initializable {

    @FXML
    private Label siraLabel;
    @FXML
    private Button yatirButton;

    private final String pathKuyruk = "C:\\veri_yapilari\\banka müsterikuyrugu\\paracekmekuyruğu.json";

    private String kullaniciTC;

    public void setKullaniciTC(String tc) {
        this.kullaniciTC = tc;
        updateSiraLabel();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        siraLabel.setText("...");
    }

    private void updateSiraLabel() {
        try {
            File file = new File(pathKuyruk);
            if (!file.exists() || kullaniciTC == null) {
                siraLabel.setText("0");
                return;
            }

            String content = new String(Files.readAllBytes(Paths.get(pathKuyruk)), StandardCharsets.UTF_8);
            JSONArray kuyrukArray = new JSONArray(content);

            for (int i = 0; i < kuyrukArray.length(); i++) {
                JSONObject musteri = kuyrukArray.getJSONObject(i);
                if (musteri.getString("Tc").equals(kullaniciTC)) {
                    siraLabel.setText(String.valueOf(i));
                    return;
                }
            }

            siraLabel.setText("0");

        } catch (IOException e) {
            e.printStackTrace();
            siraLabel.setText("HATA");
        }
    }

    @FXML
    private void handlegir(ActionEvent event) {
     try {
        int sira = Integer.parseInt(siraLabel.getText().trim());

        if (sira == 0) {
            // Sıra 0 ise yeni sayfaya geç
            FXMLLoader loader = new FXMLLoader(getClass().getResource("paracekmeform.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Para Çekme Formu");
            stage.setScene(new Scene(root));
            stage.show();

            // İstersen mevcut pencereyi kapat
            Stage currentStage = (Stage) siraLabel.getScene().getWindow();
            currentStage.close();

        } else {
            // Sıra 0 değilse uyarı
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Bilgilendirme");
            alert.setHeaderText(null);
            alert.setContentText("Lütfen sıranızı bekleyiniz.");
            alert.showAndWait();
        }

    } catch (NumberFormatException e) {
        // Eğer etiket boş ya da sayı değilse
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Hata");
        alert.setHeaderText(null);
        alert.setContentText("Sıra numarası geçersiz!");
        alert.showAndWait();
    } catch (Exception e) {
        e.printStackTrace();
    }
}
}
