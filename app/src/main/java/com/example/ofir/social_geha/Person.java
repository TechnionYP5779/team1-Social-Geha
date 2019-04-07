package com.example.ofir.social_geha;

public class Person {
    // ==================================
    //          CLASS VARIABLES
    // ==================================
    private String name;
    private String description;
    private String ImageUrl;

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

    public String getImageUrl() {
        return ImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        ImageUrl = imageUrl;
    }

    // ==================================
    //          CONSTRUCTOR
    // ==================================
    public Person(String name, String description, String imageUrl) {

        this.name = name;
        this.description = description;
        ImageUrl = imageUrl;
    }
}
