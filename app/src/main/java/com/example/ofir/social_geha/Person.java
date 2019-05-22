package com.example.ofir.social_geha;

import android.content.Context;

import com.google.firebase.firestore.Exclude;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

import static com.example.ofir.social_geha.Person.Language.ENGLISH;

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
        RELIGIOUS, TRADITIONAL, SECULAR, ARABIC,UNDISCLOSED
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

    public List<Language> getSpokenLanguages() {
        return spokenLanguages;
    }

    public Kind getKind() {
        return kind;
    }

    public String getUserID() {
        return userID;
    }

    public String getDescription() {
        return description;
    }

    public long getBirthDate() {
        return birthDate;
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


    public void setUserID(String id) {
        this.userID = id;
    }

    public static List<Person.Language> languagesStringToLanguageEnum(String[] languages) {
        List<Person.Language> mLanguages = new ArrayList<>();
        for (String l : languages) {
            switch (l) {
                case "עברית":
                    mLanguages.add(Person.Language.HEBREW);
                    break;
                case "אנגלית":
                    mLanguages.add(ENGLISH);
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

    public static List<String> languagesEnumToLanguageString(List<Person.Language> languages) {
        List<String> mLanguages = new ArrayList<>();
        for (Person.Language l : languages) {
            switch (l) {
                case HEBREW:
                    mLanguages.add("עברית");
                    break;
                case ENGLISH:
                    mLanguages.add("אנגלית");
                    break;
                case FRENCH:
                    mLanguages.add("צרפתית");
                    break;
                case RUSSIAN:
                    mLanguages.add("רוסית");
                    break;
                case ARABIC:
                    mLanguages.add("ערבית");
                    break;
                case AMHARIC:
                    mLanguages.add("אמהרית");
                    break;
            }
        }
        return mLanguages;
    }


    public static Religion fromStringToReligion(String religion, boolean canBeNull) {
        if (religion != null) {
            switch (religion) {
                case "דתי":
                    return Person.Religion.RELIGIOUS;
                case "מסורתי":
                    return Person.Religion.TRADITIONAL;
                case "חילוני":
                    return Person.Religion.SECULAR;
                case "ערבי":
                    return Person.Religion.ARABIC;
            }
        }
        if (canBeNull)
            return null;
        return Person.Religion.UNDISCLOSED;
    }

    public static Gender fromStringToGenderEnum(String gender, Context mContext, boolean canBeNull) {
        String[] allGenders = mContext.getResources().getStringArray(R.array.gender_preferences);
        if (gender.equals(allGenders[0])) {
            if (canBeNull)
                return null;
            return Gender.UNDISCLOSED;
        }
        if (gender.equals(allGenders[1]))
            return Gender.MALE;
        return Gender.FEMALE;
    }

    public static int fromGenderEnumToGenderIndex(Gender gender, Context mContext) {
        String[] allGenders = mContext.getResources().getStringArray(R.array.gender_preferences);
        switch (gender){
            case UNDISCLOSED: return 0;
            case MALE: return 1;
            case FEMALE: return 2;
        }
        return -1;
    }

    public static int fromReligionToReligionIndex(Religion religion) {
        switch (religion) {
            case UNDISCLOSED:
                return 0;
            case RELIGIOUS:
                return 1;
            case TRADITIONAL:
                return 2;
            case SECULAR:
                return 3;
            case ARABIC:
                return 4;

        }
        return -1;
    }

    public Person setPersonID(String personID) {
        this.personID = personID;
        return this;
    }

    public Person setRealName(String realName) {
        this.realName = realName;
        return this;
    }

    public Person setAnonymousIdentity(AnonymousIdentity anonymousIdentity) {
        this.anonymousIdentity = anonymousIdentity;
        return this;
    }

    public Person setBirthDate(long birthDate) {
        this.birthDate = birthDate;
        return this;
    }

    public Person setGender(Gender gender) {
        this.gender = gender;
        return this;
    }

    public Person setReligion(Religion religion) {
        this.religion = religion;
        return this;
    }

    public Person setSpokenLanguages(List<Language> spokenLanguages) {
        this.spokenLanguages = spokenLanguages;
        return this;
    }

    public Person setKind(Kind kind) {
        this.kind = kind;
        return this;
    }

    public Person setDescription(String description) {
        this.description = description;
        return this;
    }

    public Person setWhiteList(List<Integer> whiteList) {
        this.whiteList = whiteList;
        return this;
    }
}
