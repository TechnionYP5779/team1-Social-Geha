package com.example.ofir.social_geha;

import android.content.Context;

import com.example.ofir.social_geha.Identity.AnonymousIdentity;
import com.google.firebase.firestore.Exclude;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

import static com.example.ofir.social_geha.Person.Language.ENGLISH;

/***
 * The core class to describe any user of the app
 * This class is a fancy POJO (because it can be reconstructed by Firebase)
 */
public class Person {
    /**
     * Field enums
     */

    public enum Gender {
        MALE, FEMALE, UNDISCLOSED
    }

    public enum Language {
        HEBREW, ENGLISH, RUSSIAN, FRENCH, ARABIC, AMHARIC
    }

    public enum Religion {
        RELIGIOUS, TRADITIONAL, SECULAR, ARABIC, UNDISCLOSED
    }

    /**
     * The kind of user
     */
    public enum Kind {
        PATIENT, FAMILY_MEMBER, PAST_PATIENT, PAST_FAMILY_MEMBER, STAFF
    }

    private String personID;
    // the real name of the user (e.g., John Smith)
    private String realName;
    // the anonymous identity of the user, including the picture (e.g., Fancy Cat)
    private AnonymousIdentity anonymousIdentity;
    private long birthDate;
    private Gender gender;
    private Religion religion;
    private List<Language> spokenLanguages;
    private Kind kind;
    // the unique identifier
    private String userID;
    // the user's bio (visible when looking at a user before starting a conversation)
    private String description;
    // whether the user wishes to accept new conversation requests
    private Boolean availability;
    private List<Integer> whiteList;

    //FROM ADMIN-SIDE
    private AdminGivenData adminGivenData;

    public Person() {}

    public Person(String realName, AnonymousIdentity anonymousIdentity,
                  long birthDate, Gender gender, Religion religion,
                  List<Language> spokenLanguages, Kind kind, String userID,
                  String description, Boolean availability, List<Integer> whiteList) {
        this.realName = realName;
        this.anonymousIdentity = anonymousIdentity;
        this.birthDate = birthDate;
        this.gender = gender;
        this.religion = religion;
        this.spokenLanguages = spokenLanguages;
        this.kind = kind;
        this.userID = userID;
        this.description = description;
        this.availability = availability;
        this.whiteList = whiteList;
    }

    /// the admin data getter and setter
    public void setAdminGivenData(AdminGivenData adminGivenData) {
        this.adminGivenData = adminGivenData;
    }

    public AdminGivenData getAdminGivenData() {
        return adminGivenData;
    }

    /// person's real name getter and setter
    public String getRealName() {
        return realName;
    }

    public Person setRealName(String realName) {
        this.realName = realName;
        return this;
    }

    /// anonymous id getter and setter
    public AnonymousIdentity getAnonymousIdentity() {
        return anonymousIdentity;
    }

    public Person setAnonymousIdentity(AnonymousIdentity anonymousIdentity) {
        this.anonymousIdentity = anonymousIdentity;
        return this;
    }

    @Exclude
    public Calendar getCalendarBirthDate() {
        Calendar date = Calendar.getInstance();
        date.setTimeInMillis(birthDate);
        return date;
    }

    /// gender getter and setter
    public Gender getGender() {
        return gender;
    }

    public Person setGender(Gender gender) {
        this.gender = gender;
        return this;
    }

    // religion getter and setter
    public Religion getReligion() {
        return religion;
    }

    public Person setReligion(Religion religion) {
        this.religion = religion;
        return this;
    }

    /// spoken languages getter and setter
    public List<Language> getSpokenLanguages() {
        return spokenLanguages;
    }

    public Person setSpokenLanguages(List<Language> spokenLanguages) {
        this.spokenLanguages = spokenLanguages;
        return this;
    }

    /// kind getter and setter
    public Kind getKind() {
        return kind;
    }

    public Person setKind(Kind kind) {
        this.kind = kind;
        return this;
    }

    /// user id getter and setter
    public String getUserID() {
        return userID;
    }

    // should be used only if absolutely necessary since this is a unique identifier
    public void setUserID(String id) {
        this.userID = id;
    }

    /// user's bio getter and setter
    public String getDescription() {
        return description;
    }

    public Person setDescription(String description) {
        this.description = description;
        return this;
    }

    /// birth date getter and setter (long is the result of Calendar.getTimeInMillis)
    public long getBirthDate() {
        return birthDate;
    }

    public Person setBirthDate(long birthDate) {
        this.birthDate = birthDate;
        return this;
    }

    /// availability getter and setter
    public Boolean getAvailability() {
        return availability;
    }

    public Person setAvailability(Boolean newAvail) {
        availability = newAvail;
        return this;
    }

    public Person setPersonID(String personID) {
        this.personID = personID;
        return this;
    }

    public Person setWhiteList(List<Integer> whiteList) {
        this.whiteList = whiteList;
        return this;
    }

    /// equality functions
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

    /// translate String to Kind
    public static Person.Kind kindStringToKindEnum(String kind) {
        kind = kind.toLowerCase();
        switch (kind) {
            case "former_patient":
                return Kind.PAST_PATIENT;
            case "current_patient":
                return Kind.PATIENT;
            case "former_family_member":
                return Kind.PAST_FAMILY_MEMBER;
            case "current_family_member":
                return Kind.FAMILY_MEMBER;
            case "staff_member":
                return Kind.STAFF;
        }
        return Kind.PAST_PATIENT;
    }

    /// translate String to Language
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

    /// translate Language to String
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

    /***
     * Translate String to Religion
     * @param religion the string with the religion description
     * @param canBeNull whether the result can be null in the current context
     *                  otherwise Religion.UNDISCLOSED is returned
     * @return the corresponding Religion value
     */
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

    /// translate String to Gender
    public static Gender fromStringToGenderEnum(String gender, Context mContext, boolean canBeNull) {
        String[] allGenders = mContext.getResources().getStringArray(R.array.gender_preferences);
        if (gender.equals(allGenders[0])) {
            if (canBeNull)
                return null;
            return Gender.UNDISCLOSED;
        }
        else if (gender.equals(allGenders[1]))
            return Gender.MALE;
        return Gender.FEMALE;
    }

    /// translate Gender to Integer
    public static int fromGenderEnumToGenderIndex(Gender gender, Context mContext) {
        String[] allGenders = mContext.getResources().getStringArray(R.array.gender_preferences);
        switch (gender) {
            case UNDISCLOSED:
                return 0;
            case MALE:
                return 1;
            case FEMALE:
                return 2;
        }
        return -1;
    }

    /// translate Religion to Integer
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
}
