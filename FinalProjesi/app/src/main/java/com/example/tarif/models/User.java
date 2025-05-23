package com.example.tarif.models;

public class User {
    private String id;
    private String name;

    private String surname;
    private String email;

    public User(String id, String name, String surname, String email) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.email = email;
    }


    // Getter ve Setter metodları
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getSurname() { return surname; }
    public void setSurname(String surname) { this.surname = surname; }
}