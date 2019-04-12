package com.example.ofir.social_geha;

import java.util.EnumSet;

public class Person<Date> {
    // ==================================
    //          CLASS VARIABLES
    // ==================================
    public enum Gender {
        MALE, FEMALE, IRRELEVANT
    }

    public enum Language {
        HEBREW, ENGLISH, RUSSIAN, FRENCH, ARABIC, AMHARIC
    }

    public enum Religion {
        RELIGIOUS, TRADITIONAL, SECULAR, ARABIC, IRRELEVANT
    }

    private String name;
    private String description;
    private String imageURL;
    private String personID;
    private Gender gender;
    private Date birthDate;
    private Religion religion;
    private EnumSet<Language> spokenLanguages;

    // ==================================
    //          CONSTRUCTORS
    // ==================================
    public Person(String name, String personID) {
        this.name = name;
        this.personID = personID;
    }

    public Person(String name, String description, String imageUrl) {

        this.name = name;
        this.description = description;
        imageURL = imageUrl;
    }

    public Person(String name, String description, String imageUrl, Date birthDate, Gender g, EnumSet<Language> l,Religion r ) {

        this.name = name;
        this.description = description;
        imageURL = imageUrl;
        this.birthDate = birthDate;
        this.gender = g;
        this.spokenLanguages = l;
        this.religion = r;
    }

    // ==================================
    //          GETTERS & SETTERS
    // ==================================
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getPersonID() {
        return personID;
    }

    public void setPersonID(String personID) {
        this.personID = personID;
    }

}
