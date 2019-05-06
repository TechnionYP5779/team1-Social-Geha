package com.example.ofir.social_geha;

import android.content.Context;
import android.util.Pair;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Exclude;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static java.util.Optional.empty;

public class Person {
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
        RELIGIOUS, TRADITIONAL, SECULAR, ARABIC
    }

    public enum Kind {
        PATIENT, FAMILY_MEMBER, PAST_PATIENT, PAST_FAMILY_MEMBER
    }

    private String personID;
    private String realName;
    private AnonymousIdentity anonymousIdentity;
    private long birthDate;
    private Gender gender;
    private Religion religion;
    private List<Language> spokenLanguages;
    private Kind kind;
    private String userID;
    private String description;
    //List of personIDs to whom this person is willing to expose details to
    private List<Integer> whiteList;
    // Immutable - Given at initialization
    // Pair(imageName in drawable, fictitious name)
    // ==================================
    //          CONSTRUCTORS
    // ==================================


    public Person() {

    }

    public Person(String realName, AnonymousIdentity anonymousIdentity,
                  long birthDate, Gender gender, Religion religion,
                  List<Language> spokenLanguages, Kind kind, String userID,
                  String description, List<Integer> whiteList) {
        this.realName = realName;
        this.anonymousIdentity = anonymousIdentity;
        this.birthDate = birthDate;
        this.gender = gender;
        this.religion = religion;
        this.spokenLanguages = spokenLanguages;
        this.kind = kind;
        this.userID = userID;
        this.description = description;
        this.whiteList = whiteList;
    }

    public String getRealName() {
        return realName;
    }

    public AnonymousIdentity getAnonymousIdentity() {
        return anonymousIdentity;
    }

    @Exclude
    public Calendar getCalendarBirthDate() {
        Calendar date = Calendar.getInstance();
        date.setTimeInMillis(birthDate);
        return date;
    }

    public Gender getGender() {
        return gender;
    }

    public Religion getReligion() {
        return religion;
    }

    public List<Language> getSpokenLanguages() { return spokenLanguages; }

    public Kind getKind() {
        return kind;
    }

    public String getUserID() {
        return userID;
    }

    public String getDescription() {
        return description;
    }


    // ==================================
    //          MODIFIERS & UTILITY METHODS
    // ==================================


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Person person = (Person) o;
        return Objects.equals(userID, person.userID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userID);
    }

    public void approve(int userID) {
        //ADD CHECK THAT PERSONID IS VALID
        if (!whiteList.contains(userID)) {
            whiteList.add(userID);
        }
    }

    public void disapprove(int userID) {
        //ADD CHECK THAT PERSONID IS VALID
        whiteList.remove(userID);
    }

    public boolean isApproved(int userID) {
        return whiteList.contains(userID);
    }


    public void setUserID(String id){
        this.userID = id;
    }

    public static List<Person.Language> languagesStringToLanguageEnum(String[] languages){
        List<Person.Language> mLanguages = new ArrayList<>();
        for(String l : languages){
            switch(l){
                case "עברית":
                    mLanguages.add(Person.Language.HEBREW);
                    break;
                case "אנגלית":
                    mLanguages.add(Person.Language.ENGLISH);
                    break;
                case "צרפתית":
                    mLanguages.add(Person.Language.FRENCH);
                    break;
                case "רוסית":
                    mLanguages.add(Person.Language.RUSSIAN);
                    break;
                case "ערבית":
                    mLanguages.add(Person.Language.ARABIC);
                    break;
                case "אמהרית":
                    mLanguages.add(Person.Language.AMHARIC);
                    break;
            }
        }
        return mLanguages;
    }

    public static Religion fromStringToReligion(String religion, boolean canBeNull){
        switch (religion){
            case "דתי":
                return Person.Religion.RELIGIOUS;
            case "מסורתי":
                return Person.Religion.TRADITIONAL;
            case "חילוני":
                return Person.Religion.SECULAR;
            case "ערבי":
                return Person.Religion.ARABIC;
        }
        if(canBeNull)
            return null;
        return Person.Religion.SECULAR;
    }

    public static Gender fromStringToGenderEnum(String gender, Context mContext, boolean canBeNull){
        String[] allGenders = mContext.getResources().getStringArray(R.array.gender_preferences);
        if(gender.equals(allGenders[0])){
            if(canBeNull)
                return null;
            return Gender.UNDISCLOSED;
        }
        if(gender.equals(allGenders[1]))
            return Gender.MALE;
        return Gender.FEMALE;
    }
}
