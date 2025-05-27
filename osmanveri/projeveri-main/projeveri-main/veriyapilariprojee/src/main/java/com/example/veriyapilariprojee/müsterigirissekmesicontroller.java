package com.example.veriyapilariprojee;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class müsterigirissekmesicontroller {

    @FXML
    private TextField name;

    @FXML
    private PasswordField tcField;

    @FXML
    private Button loginbutton;

    private final String DATABASE_YOLU = "C:\\veri_yapilari\\osmanveri\\osmanveri\\database.json";
    private final String AKTIF_KULLANICI_YOLU = "C:\\veri_yapilari\\osmanveri\\osmanveri\\aktifKullanici.json";

    @FXML
    public void getmüsterisiraana(ActionEvent event) {
        String kullaniciAdi = name.getText().trim();
        String tc = tcField.getText().trim();

        if (kullaniciAdi.isEmpty() || tc.isEmpty()) {
            gosterUyari("Hata", "Eksik Bilgi", "Lütfen tüm bilgileri giriniz.");
            return;
        }

        try {
            JSONArray veritabani;

            if (Files.exists(Paths.get(DATABASE_YOLU))) {
                String icerik = Files.readString(Paths.get(DATABASE_YOLU));
                veritabani = icerik.isEmpty() ? new JSONArray() : new JSONArray(icerik);
            } else {
                veritabani = new JSONArray();
            }

            boolean kullaniciVarMi = false;

            for (int i = 0; i < veritabani.length(); i++) {
                JSONObject musteri = veritabani.getJSONObject(i);
                if (musteri.has("isim") && musteri.has("Tc")) {
                    if (musteri.getString("isim").equals(kullaniciAdi) && musteri.getString("Tc").equals(tc)) {
                        kullaniciVarMi = true;
                        break;
                    }
                }
            }

            if (!kullaniciVarMi) {
                JSONObject yeniMusteri = new JSONObject();
                yeniMusteri.put("isim", kullaniciAdi);
                yeniMusteri.put("Tc", tc);
                yeniMusteri.put("bakiye", "0");
                yeniMusteri.put("öncelik", false);
                veritabani.put(yeniMusteri);

                Files.createDirectories(Paths.get(DATABASE_YOLU).getParent());
                try (FileWriter writer = new FileWriter(DATABASE_YOLU)) {
                    writer.write(veritabani.toString(4));
                }

                gosterBilgi("Kayıt Tamamlandı", "Yeni kullanıcı başarıyla kaydedildi.");
            } else {
                gosterBilgi("Giriş Başarılı", "Hoş geldiniz, " + kullaniciAdi + "!");
            }

            // .json dosyasını sil ve yeniden oluştur
            Files.deleteIfExists(Paths.get(AKTIF_KULLANICI_YOLU));
            JSONObject aktifKullanici = new JSONObject();
            aktifKullanici.put("isim", kullaniciAdi);
            aktifKullanici.put("Tc", tc);

            try (FileWriter writer = new FileWriter(AKTIF_KULLANICI_YOLU)) {
                writer.write(aktifKullanici.toString(4));
            }

            // Geçiş yap
            Parent root = FXMLLoader.load(getClass().getResource("musterisiraana.fxml"));
            Stage stage = (Stage) loginbutton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();

        } catch (Exception e) {
            gosterUyari("Hata", "Beklenmeyen Hata", e.getMessage());
            e.printStackTrace();
        }
    }

    private void gosterUyari(String baslik, String baslik2, String icerik) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(baslik);
        alert.setHeaderText(baslik2);
        alert.setContentText(icerik);
        alert.showAndWait();
    }

    private void gosterBilgi(String baslik, String icerik) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Başarılı");
        alert.setHeaderText(baslik);
        alert.setContentText(icerik);
        alert.showAndWait();
    }
}
