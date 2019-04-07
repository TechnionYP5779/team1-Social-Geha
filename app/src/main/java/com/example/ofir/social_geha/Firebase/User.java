package com.example.ofir.social_geha.Firebase;

public class User {
    private String name;
    private String userID;

    public User(String name, String userID){
        this.name = name;
        this.userID = userID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }
}
