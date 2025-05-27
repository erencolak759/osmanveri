package com.example.veriyapilariprojee;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class personelgirisekranicontroller {
    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button geriButton;

    @FXML
    private Label gerilabel;

    @FXML
    private Hyperlink hyperlinkpsw;

    @FXML
    private Hyperlink hyperlinksignup;

    @FXML
    private Button looginbutton;

    @FXML
    private PasswordField pswField;

    @FXML
    private TextField nameField;

    private final String pathPersonel = "C:/veri_yapilari/banka müsterikuyrugu/projeveri-main/projeveri-main/veriyapilariprojee/personel.json";

    @FXML
    public void getpersonelanaekran(ActionEvent event) throws IOException {
        String girilenIsim = nameField.getText().trim();
        String girilenSifre = pswField.getText().trim();

        if (girilenIsim.isEmpty() || girilenSifre.isEmpty()) {
            showAlert("Lütfen tüm alanları doldurun.");
            return;
        }

        // JSON dosyasını oku
        File file = new File(pathPersonel);
        if (!file.exists()) {
            showAlert("Personel verileri bulunamadı.");
            return;
        }

        String content = new String(Files.readAllBytes(Paths.get(pathPersonel)), StandardCharsets.UTF_8);
        JSONArray personeller = new JSONArray(content);

        for (int i = 0; i < personeller.length(); i++) {
            JSONObject personel = personeller.getJSONObject(i);
            String isim = personel.getString("isim");
            String sifre = personel.getString("şifre");

            if (isim.equals(girilenIsim) && sifre.equals(girilenSifre)) {
                String mail = personel.getString("mail");

                // Sayfa geçişi ve veri aktarımı
                FXMLLoader loader = new FXMLLoader(getClass().getResource("personelanaekran.fxml"));
                Stage stage = (Stage) nameField.getScene().getWindow();
                Scene scene = new Scene(loader.load());

                // Controller'a eriş ve kullanıcı bilgilerini gönder
                personelanaekrancontroller controller = loader.getController();
                controller.setPersonelBilgi(isim, mail);

                stage.setScene(scene);
                stage.show();
                return;
            }
        }

        // Hiç eşleşme olmadıysa:
        showAlert("Böyle bir personel yoktur.");
    }

    private void showAlert(String mesaj) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Giriş Hatası");
        alert.setHeaderText(null);
        alert.setContentText(mesaj);
        alert.showAndWait();


    }
    public void backtoilkgirisekrani(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("ilkgirisekrani.fxml"));
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(loader.load()));
        stage.show();
    }
}
