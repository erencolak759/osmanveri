package com.example.veriyapilariprojee;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ResourceBundle;

public class müsteriparayatırmacontroller implements Initializable {

    @FXML
    private Button girButton;

    @FXML
    private Button geriButton;
    @FXML
    private Button paraField;

    @FXML
    private Label siraLabel;

    @FXML
    private TextField bakiyelbl;

    private final String AKTIF_KULLANICI_YOLU = "C:\\veri_yapilari\\osmanveri\\osmanveri\\aktifKullanici.json";
    private final String DATABASE_YOLU = "C:\\veri_yapilari\\osmanveri\\osmanveri\\database.json";
    private final String KUYRUK_YOLU = "C:\\veri_yapilari\\osmanveri\\osmanveri\\paracekmekuyruğu.json";

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        bakiyeyiGuncelle();
    }

    private void bakiyeyiGuncelle() {
        try {
            if (Files.exists(Paths.get(AKTIF_KULLANICI_YOLU))) {
                String aktifStr = Files.readString(Paths.get(AKTIF_KULLANICI_YOLU), StandardCharsets.UTF_8);

                if (aktifStr.trim().isEmpty()) {
                    bakiyelbl.setText("Bakiye bilgisi bulunamadı.");
                    return;
                }

                JSONObject aktifKullanici = new JSONObject(aktifStr);
                String bakiyeStr = getBakiyeFromJSON(aktifKullanici);
                bakiyelbl.setText("Bakiye: " + bakiyeStr + " ₺");

            } else {
                bakiyelbl.setText("Bakiye dosyası bulunamadı.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            bakiyelbl.setText("Bakiye okunamadı.");
        }
    }

    private String getBakiyeFromJSON(JSONObject jsonObj) {
        if (jsonObj.has("bakiye")) {
            Object bakiyeObj = jsonObj.get("bakiye");
            if (bakiyeObj instanceof String) {
                return (String) bakiyeObj;
            } else if (bakiyeObj instanceof Integer) {
                return String.valueOf((Integer) bakiyeObj);
            } else if (bakiyeObj instanceof Double) {
                return String.valueOf(((Double) bakiyeObj).intValue());
            }
        }
        return "0";
    }

    private int getBakiyeAsInt(JSONObject jsonObj) {
        String bakiyeStr = getBakiyeFromJSON(jsonObj);
        try {
            return Integer.parseInt(bakiyeStr);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    @FXML
    public void parayatir(ActionEvent event) {
        try {
            if (!Files.exists(Paths.get(AKTIF_KULLANICI_YOLU))) {
                showAlert("Hata", "Aktif kullanıcı bulunamadı!", Alert.AlertType.ERROR);
                return;
            }

            String aktifStr = Files.readString(Paths.get(AKTIF_KULLANICI_YOLU), StandardCharsets.UTF_8);
            if (aktifStr.trim().isEmpty()) {
                showAlert("Hata", "Aktif kullanıcı dosyası boş!", Alert.AlertType.ERROR);
                return;
            }

            JSONObject aktifKullanici = new JSONObject(aktifStr);
            String aktifTc = aktifKullanici.optString("Tc", "").trim();
            String aktifIsim = aktifKullanici.optString("isim", "").trim();

            if (aktifTc.isEmpty() || aktifIsim.isEmpty()) {
                showAlert("Hata", "Aktif kullanıcı bilgileri eksik!", Alert.AlertType.ERROR);
                return;
            }

            String girilenMiktarStr = paraField.getText().trim();
            if (girilenMiktarStr.isEmpty() || !girilenMiktarStr.matches("\\d+")) {
                showAlert("Hata", "Lütfen geçerli bir miktar girin!", Alert.AlertType.ERROR);
                return;
            }

            int miktar = Integer.parseInt(girilenMiktarStr);
            if (miktar <= 0) {
                showAlert("Hata", "Lütfen pozitif bir miktar girin!", Alert.AlertType.ERROR);
                return;
            }

            int eskiBakiye = getBakiyeAsInt(aktifKullanici);
            int yeniBakiye = eskiBakiye + miktar;

            aktifKullanici.put("bakiye", String.valueOf(yeniBakiye));
            String guncelAktifJson = aktifKullanici.toString(4);

            Files.write(Paths.get(AKTIF_KULLANICI_YOLU),
                    guncelAktifJson.getBytes(StandardCharsets.UTF_8),
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING);

            guncelleDatabase(aktifTc, miktar);
            guncelleKuyruk(aktifTc, aktifIsim, miktar);

            bakiyeyiGuncelle();

            showAlert("Başarılı",
                    miktar + " ₺ başarıyla yatırıldı!\nEski bakiye: " + eskiBakiye + " ₺\nYeni bakiye: " + yeniBakiye + " ₺",
                    Alert.AlertType.INFORMATION);
            paraField.clear();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Hata", "Bir hata oluştu: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void guncelleDatabase(String aktifTc, int miktar) {
        try {
            if (!Files.exists(Paths.get(DATABASE_YOLU))) return;

            String dbStr = Files.readString(Paths.get(DATABASE_YOLU), StandardCharsets.UTF_8);
            JSONArray dbArray = new JSONArray(dbStr);
            boolean bulundu = false;

            for (int i = 0; i < dbArray.length(); i++) {
                JSONObject obj = dbArray.getJSONObject(i);
                if (obj.optString("Tc").equals(aktifTc)) {
                    int eski = getBakiyeAsInt(obj);
                    obj.put("bakiye", String.valueOf(eski + miktar));
                    bulundu = true;
                    break;
                }
            }

            if (bulundu) {
                Files.write(Paths.get(DATABASE_YOLU),
                        dbArray.toString(4).getBytes(StandardCharsets.UTF_8),
                        StandardOpenOption.CREATE,
                        StandardOpenOption.TRUNCATE_EXISTING);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void guncelleKuyruk(String aktifTc, String aktifIsim, int miktar) {
        try {
            if (!Files.exists(Paths.get(KUYRUK_YOLU))) return;

            String kuyrukStr = Files.readString(Paths.get(KUYRUK_YOLU), StandardCharsets.UTF_8);
            JSONArray kuyrukArray = new JSONArray(kuyrukStr);
            boolean bulundu = false;

            for (int i = 0; i < kuyrukArray.length(); i++) {
                JSONObject kisi = kuyrukArray.getJSONObject(i);
                if (kisi.optString("Tc").equals(aktifTc) &&
                        kisi.optString("isim").equalsIgnoreCase(aktifIsim)) {
                    int eski = getBakiyeAsInt(kisi);
                    kisi.put("bakiye", String.valueOf(eski + miktar));
                    bulundu = true;
                    break;
                }
            }

            if (bulundu) {
                Files.write(Paths.get(KUYRUK_YOLU),
                        kuyrukArray.toString(4).getBytes(StandardCharsets.UTF_8),
                        StandardOpenOption.CREATE,
                        StandardOpenOption.TRUNCATE_EXISTING);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void geriDon(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("musterisiraana.fxml"));
        Stage stage = (Stage) geriButton.getScene().getWindow();
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
