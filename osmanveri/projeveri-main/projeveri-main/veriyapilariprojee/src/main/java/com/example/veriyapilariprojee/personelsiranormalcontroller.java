package com.example.veriyapilariprojee;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import org.json.JSONArray;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class personelsiranormalcontroller {
    @FXML
    public void sirayiIlerle(ActionEvent event) {
        try {
            String path = "C:\\veri_yapilari\\banka müsterikuyrugu\\paracekmekuyruğu.json";
            String content = new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
            JSONArray kuyrukArray = new JSONArray(content);
            Kuyruk kuyruk = new Kuyruk();
            for (int i = 0; i < kuyrukArray.length(); i++) {
                JSONObject musteri = kuyrukArray.getJSONObject(i);
                kuyruk.enqueue(new Node(musteri));
            }


            if (kuyruk.isEmpty()) {
                System.out.println("Kuyruk boş.");
                return;
            }
            kuyruk.dequeue();

            // 3. Yeni listeye kalanları al
            ArrayList<JSONObject> liste = new ArrayList<>();
            while (!kuyruk.isEmpty()) {
                liste.add(kuyruk.dequeue().veri);
            }

            // 4. Her müşteriye yeni sıra ver
            int sira = 1;
            for (JSONObject musteri : liste) {
                musteri.put("sıra", sira++);
            }

            // 5. JSON dizisine çevir ve dosyaya yaz
            JSONArray yeniArray = new JSONArray();
            for (JSONObject musteri : liste) {
                yeniArray.put(musteri);
                System.out.println(musteri);
            }

            Files.write(Paths.get(path), yeniArray.toString(2).getBytes(StandardCharsets.UTF_8));
            System.out.println("Sıra başarıyla ilerletildi.");

        } catch (Exception e) {
            e.printStackTrace();

            System.out.println("hata algıladı");
            System.out.println(e.getMessage());
        }
    }

}
