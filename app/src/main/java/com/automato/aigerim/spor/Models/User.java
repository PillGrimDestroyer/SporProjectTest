package com.automato.aigerim.spor.Models;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;

@IgnoreExtraProperties
public class User {
    public String id;
    public String name;
    public String email;
    public double money;
    public boolean hasImage;
    public String birthday;
    public String gender;
    public boolean receiveNotifications;
    public HashMap<String, Notification> history;

    public User() {

    }

    public User(String id, String name, String email, String birthday, String gender) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.money = 1000.0;
        this.hasImage = false;
        this.birthday = birthday;
        this.gender = gender;
        this.receiveNotifications = true;
    }
}