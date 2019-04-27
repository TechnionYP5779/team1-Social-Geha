package com.example.ofir.social_geha;

public class AnonymousIdentity {
    public String name;
    public String imageName;

    public AnonymousIdentity() {

    }

    public AnonymousIdentity(String n, String im){
        name = n;
        imageName = im;
    }

    public String getName() {
        return name;
    }

    public String getImageName() {
        return imageName;
    }
}
