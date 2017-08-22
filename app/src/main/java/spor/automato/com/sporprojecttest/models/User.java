package spor.automato.com.sporprojecttest.models;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class User {
    public String id;
    public String name;
    public String email;
    public double money;
    public boolean hasImage;
    public String birthday;
    public String gender;

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
    }

    public User getUser(String id){
        return null;
    }
}