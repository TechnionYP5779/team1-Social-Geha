package com.example.ofir.social_geha.Firebase;

public class User {
    private String name;
    private String personalCode;

    public User(String name, String personalCode){
        this.name = name;
        this.personalCode = personalCode;
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
