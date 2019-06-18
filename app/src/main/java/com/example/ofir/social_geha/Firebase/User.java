package com.example.ofir.social_geha.Firebase;

/**
 * DEPRECATED
 * Encapsulates the concept of a user.
 * This class is a fancy POJO (because it can be reconstructed by Firebase)
 */
public class User {
    private String name;
    private String personalCode;
    private String userID;

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public User(String name, String personalCode, String userID){
        this.name = name;
        this.personalCode = personalCode;
        this.userID = userID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPersonalCode() {
        return personalCode;
    }

    public void setPersonalCode(String personalCode) {
        this.personalCode = personalCode;
    }
}
