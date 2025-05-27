package com.example.veriyapilariprojee;

import org.json.JSONObject;

public class Node {
    JSONObject veri;
    Node next;

    public Node(JSONObject veri) {
        this.veri = veri;
        this.next = null;
    }
}
