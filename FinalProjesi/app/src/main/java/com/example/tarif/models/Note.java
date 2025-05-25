package com.example.tarif.models;

public class Note {
    private String text;

    public Note() {
        // Firebase için boş constructor
    }

    public Note(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
