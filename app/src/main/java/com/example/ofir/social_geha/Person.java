package com.example.ofir.social_geha;

import android.util.Pair;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static java.util.Optional.empty;

public class Person<Date> {
    // ==================================
    //          CLASS VARIABLES
    // ==================================
    public enum Gender {
        MALE, FEMALE, UNDISCLOSED
    }

    public enum Language {
        HEBREW, ENGLISH, RUSSIAN, FRENCH, ARABIC, AMHARIC
    }

    public enum Religion {
        RELIGIOUS, TRADITIONAL, SECULAR, ARABIC, UNDISCLOSED
    }

    private String realName;
    private Optional<String> realImageURL; //may not exist
    private String description;
    private String personID;
    private Gender gender;
    private Date birthDate;
    private Religion religion;
    private EnumSet<Language> spokenLanguages;
    //List of personIDs to whom this person is willing to expose details to
    private List<String> whiteList;
    // Immutable - Given at initialization
    // Pair(imageName in drawable, fictitious name)
    private AnonymousIdentity anonymousIdentity;
    // ==================================
    //          CONSTRUCTORS
    // ==================================
    /*public Person(String name, String personID) {
        this.name = name;
        this.personID = personID;
    }*/

    /*public Person(String name, String description, String imageUrl) {

        this.name = name;
        this.description = description;
        imageURL = imageUrl;
    }*/

    public Person(String realname, String description, Date birthDate, Gender g, EnumSet<Language> l, Religion r) {

        this.realName = realname;
        this.realImageURL = Optional.empty();
        this.description = description;
        this.anonymousIdentity = FictitiousIdentityGenerator.getAnonymousIdentity(g);
        this.birthDate = birthDate;
        this.gender = g;
        this.spokenLanguages = l;
        this.religion = r;
    }

    public Person(DocumentSnapshot documentSnapshot) {
        //TODO: based on a retrieved entry from the database construct a Person
        throw new UnsupportedOperationException();
    }

    // ==================================
    //          GETTERS & SETTERS
    // ==================================
    public String getRealName() {
        return realName;
    }

    public Optional<String> getRealImage(){
        return realImageURL;
    }

    public String getAnonymousName(){
        return anonymousIdentity.name;
    }

    public String getAnonymousImageURL() {
        return "drawable://" + anonymousIdentity.imageName;
    }

//    public void setName(String name) {
//        this.name = name;

//    }

    public String getDescription() {
        return description;
    }
//    public void setDescription(String description) {
//        this.description = description;

//    }

//    public void setImageURL(String imageURL) {
//        this.imageURL = imageURL;
//    }

    public String getPersonID() {
        return personID;
    }

//    public void setPersonID(String personID) {
//        this.personID = personID;
//    }

    // ==================================
    //          MODIFIERS & UTILITY METHODS
    // ==================================


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Person<?> person = (Person<?>) o;
        return Objects.equals(personID, person.personID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(personID);
    }

    public void approve(String personID) {
        //ADD CHECK THAT PERSONID IS VALID
        if (!whiteList.contains(personID)) {
            whiteList.add(personID);
        }
    }

    public void disapprove(String personID) {
        //ADD CHECK THAT PERSONID IS VALID
        whiteList.remove(personID);
    }

    public boolean isApproved(String personID) {
        return whiteList.contains(personID);
    }

}
