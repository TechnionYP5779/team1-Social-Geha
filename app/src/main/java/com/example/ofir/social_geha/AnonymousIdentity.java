package com.example.ofir.social_geha;

public class AnonymousIdentity {
    public String name;
    public String imageName;
    public String imageColor;

    public AnonymousIdentity() {
    }

    public AnonymousIdentity(String n, String im, String col) {
        name = n;
        imageName = im;
        imageColor = col;
    }

    public String getName() {
        return name;
    }

    public String getImageName() {
        return imageName;
    }

    public String getImageColor() {
        return imageColor;
    }
}
