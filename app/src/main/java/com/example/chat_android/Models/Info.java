package com.example.chat_android.Models;

public class Info {

    private String name;
    private String email;
    private String phone;
    private String dateOfBird;

    public Info(String name, String email, String phone, String date) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.dateOfBird = date;
    }

    public Info() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getDate() {
        return dateOfBird;
    }

    public void setDate(String date) {
        this.dateOfBird = date;
    }
}
