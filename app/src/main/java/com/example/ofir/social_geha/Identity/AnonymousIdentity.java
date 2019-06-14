package com.example.ofir.social_geha.Identity;

import java.io.Serializable;

/**
 * Encapsulates the concept of an anonymous identity for a user.
 * A user's anonymous identity is composed of an anonymous name, an anonymous picture,
 * an a color for personalization
 */
public class AnonymousIdentity implements Serializable {
    public String name;
    public String imageName;
    public String imageColor;

    AnonymousIdentity(String n, String im, String col) {
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

    @SuppressWarnings("NullableProblems")
    @Override
    public String toString() {
        return "{" + name + "|" + imageName + "|" + imageColor + "}";
    }
}
