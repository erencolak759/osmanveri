package com.example.veriyapilariprojee;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
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

public class musterisiraalcontroller {

    @FXML
    private Button girButton;

    @FXML
    private Label siraLabel;

    private final String AKTIF_KULLANICI_YOLU = "C:\\veri_yapilari\\osmanveri\\osmanveri\\aktifKullanici.json";
    private final String KUYRUK_YOLU = "C:\\veri_yapilari\\osmanveri\\osmanveri\\paracekmekuyruğu.json";

    @FXML
    void handlegir(ActionEvent event) {
        try {
            if (!Files.exists(Paths.get(AKTIF_KULLANICI_YOLU))) {
                siraLabel.setText("Aktif kullanıcı dosyası yok!");
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
                siraLabel.setText("TC veya isim bilgisi eksik!");
                return;
            }

            JSONArray kuyruk = new JSONArray();
            if (Files.exists(Paths.get(KUYRUK_YOLU))) {
                String kuyrukIcerik = Files.readString(Paths.get(KUYRUK_YOLU), StandardCharsets.UTF_8);
                if (!kuyrukIcerik.isBlank()) {
                    kuyruk = new JSONArray(kuyrukIcerik);
                }
            }

            boolean zatenVar = false;
            int sira = -1;

            for (int i = 0; i < kuyruk.length(); i++) {
                JSONObject kisi = kuyruk.getJSONObject(i);
                String kisiTc = kisi.optString("Tc", "").trim();
                String kisiIsim = kisi.optString("isim", "").trim();

                if (kisiTc.equals(aktifTc) && kisiIsim.equalsIgnoreCase(aktifIsim)) {
                    zatenVar = true;
                    sira = i + 1;
                    break;
                }
            }

            if (!zatenVar) {
                JSONObject yeniKisi = new JSONObject();
                yeniKisi.put("isim", aktifIsim);
                yeniKisi.put("Tc", aktifTc);
                yeniKisi.put("öncelik", aktifKullanici.optBoolean("öncelik", false));
                yeniKisi.put("bakiye", aktifKullanici.optString("bakiye", "0"));

                kuyruk.put(yeniKisi);
                sira = kuyruk.length();

                Files.write(
                        Paths.get(KUYRUK_YOLU),
                        kuyruk.toString(4).getBytes(StandardCharsets.UTF_8),
                        StandardOpenOption.CREATE,
                        StandardOpenOption.TRUNCATE_EXISTING
                );
            }

            siraLabel.setText("Sıranız: " + sira);

        } catch (Exception e) {
            e.printStackTrace();
            siraLabel.setText("Hata: " + e.getMessage());
        }
    }

    public void backmusterisiraana(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("musterisiraana.fxml"));
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(loader.load()));
        stage.show();
    }
}
