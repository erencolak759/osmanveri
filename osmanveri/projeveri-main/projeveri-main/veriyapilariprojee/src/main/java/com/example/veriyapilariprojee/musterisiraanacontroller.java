package com.example.veriyapilariprojee;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class musterisiraanacontroller {

    @FXML
    private Button siraalBtn;

    @FXML
    private Button girisBtn;

    @FXML
    private Label siraLabel;

    private final String AKTIF_KULLANICI_YOLU = "C:\\veri_yapilari\\osmanveri\\osmanveri\\aktifKullanici.json";
    private final String KUYRUK_YOLU = "C:\\veri_yapilari\\osmanveri\\osmanveri\\paracekmekuyruğu.json";

    @FXML
    public void handleSiraAl(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("musterisiraal.fxml"));
        Stage stage = (Stage) siraalBtn.getScene().getWindow();
        stage.setScene(new Scene(loader.load()));
        stage.show();
    }

    @FXML
    public void handleGiris(ActionEvent event) {
        try {
            // Aktif kullanıcı dosyasını oku
            if (!Files.exists(Paths.get(AKTIF_KULLANICI_YOLU))) {
                showAlert("Hata", "Aktif kullanıcı bulunamadı!", Alert.AlertType.ERROR);
                return;
            }

            String aktifIcerik = Files.readString(Paths.get(AKTIF_KULLANICI_YOLU), StandardCharsets.UTF_8);
            if (aktifIcerik.isBlank()) {
                showAlert("Hata", "Aktif kullanıcı dosyası boş!", Alert.AlertType.ERROR);
                return;
            }

            JSONObject aktifKullanici = new JSONObject(aktifIcerik);
            String aktifTc = aktifKullanici.optString("Tc", "").trim();
            String aktifIsim = aktifKullanici.optString("isim", "").trim();

            if (aktifTc.isEmpty() || aktifIsim.isEmpty()) {
                showAlert("Hata", "Aktif kullanıcı bilgileri eksik!", Alert.AlertType.ERROR);
                return;
            }

            // Kuyruğu oku
            if (!Files.exists(Paths.get(KUYRUK_YOLU))) {
                showAlert("Hata", "Kuyruk dosyası bulunamadı!", Alert.AlertType.ERROR);
                return;
            }

            String kuyrukIcerik = Files.readString(Paths.get(KUYRUK_YOLU), StandardCharsets.UTF_8);
            if (kuyrukIcerik.isBlank()) {
                showAlert("Hata", "Kuyruk boş!", Alert.AlertType.ERROR);
                return;
            }

            JSONArray kuyruk = new JSONArray(kuyrukIcerik);

            if (kuyruk.length() == 0) {
                showAlert("Bilgi", "Kuyrukta kimse yok!", Alert.AlertType.INFORMATION);
                return;
            }

            // İlk sıradaki kişi kontrol edilir
            JSONObject birinciKisi = kuyruk.getJSONObject(0);
            String birinciTc = birinciKisi.optString("Tc", "").trim();
            String birinciIsim = birinciKisi.optString("isim", "").trim();

            // Aktif kullanıcı ilk sıradaysa sahne geçişi yapılır
            if (aktifTc.equals(birinciTc) && aktifIsim.equalsIgnoreCase(birinciIsim)) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("musterianaekran.fxml"));
                Stage stage = (Stage) girisBtn.getScene().getWindow();
                stage.setScene(new Scene(loader.load()));
                stage.show();
            } else {
                showAlert("Sıra Sizde Değil", "Şu anda sıra sizde değil. Lütfen bekleyiniz.", Alert.AlertType.WARNING);
            }

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Hata", "Bir hata oluştu: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    public void handleAyril(ActionEvent event) {
        try {
            if (!Files.exists(Paths.get(AKTIF_KULLANICI_YOLU))) {
                siraLabel.setText("Aktif kullanıcı bulunamadı!");
                return;
            }

            String aktifIcerik = Files.readString(Paths.get(AKTIF_KULLANICI_YOLU), StandardCharsets.UTF_8);
            if (aktifIcerik.isBlank()) {
                siraLabel.setText("Aktif kullanıcı dosyası boş!");
                return;
            }

            JSONObject aktifKullanici = new JSONObject(aktifIcerik);
            String aktifTc = aktifKullanici.optString("Tc", "").trim();
            String aktifIsim = aktifKullanici.optString("isim", "").trim();

            if (aktifTc.isEmpty() || aktifIsim.isEmpty()) {
                siraLabel.setText("Aktif kullanıcı bilgileri eksik!");
                return;
            }

            JSONArray kuyruk = new JSONArray();
            if (Files.exists(Paths.get(KUYRUK_YOLU))) {
                String kuyrukIcerik = Files.readString(Paths.get(KUYRUK_YOLU), StandardCharsets.UTF_8);
                if (!kuyrukIcerik.isBlank()) {
                    kuyruk = new JSONArray(kuyrukIcerik);
                }
            }

            JSONArray yeniKuyruk = new JSONArray();
            boolean silindi = false;

            for (int i = 0; i < kuyruk.length(); i++) {
                JSONObject kisi = kuyruk.getJSONObject(i);
                String kisiTc = kisi.optString("Tc", "").trim();
                String kisiIsim = kisi.optString("isim", "").trim();

                if (kisiTc.equals(aktifTc) && kisiIsim.equalsIgnoreCase(aktifIsim)) {
                    silindi = true;
                    continue;
                }
                yeniKuyruk.put(kisi);
            }

            Files.write(
                    Paths.get(KUYRUK_YOLU),
                    yeniKuyruk.toString(4).getBytes(StandardCharsets.UTF_8),
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING
            );

            if (silindi) {
                siraLabel.setText("Kuyruktan çıkarıldınız.");
                showAlert("Bilgi", "Başarıyla kuyruktan ayrıldınız.", Alert.AlertType.INFORMATION);
            } else {
                siraLabel.setText("Kuyrukta zaten yoktunuz.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            siraLabel.setText("Hata: " + e.getMessage());
            showAlert("Hata", e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    public void backtomüsterigirissekmesi(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("müstergirissekmesi.fxml"));
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(loader.load()));
        stage.show();
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}
