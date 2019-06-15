package com.example.ofir.social_geha;

import java.io.Serializable;
import java.util.EnumSet;

/***
 * A POJO to send to the filterUsers function
 * Given these parameters all the matching mentors receive a conversation request
 */
public class FilterParameters implements Serializable {
    private EnumSet<Person.Language> languages;
    private Person.Kind kind;
    private Person.Gender gender;
    private Person.Religion religion;
    private Integer lower_bound;
    private Integer upper_bound;

    /**
     * The class constructor
     */
    public FilterParameters(EnumSet<Person.Language> languages, Person.Kind kind, Person.Gender gender,
                            Person.Religion religion, Integer lower, Integer upper) {
        this.languages = languages;
        this.kind = kind;
        this.gender = gender;
        this.religion = religion;
        this.lower_bound = lower;
        this.upper_bound = upper;
    }

    /// Languages getter and setter
    public EnumSet<Person.Language> getLanguages() {
        return languages;
    }

    public void setLanguages(EnumSet<Person.Language> languages) {
        this.languages = languages;
    }

    /// Kind (what is the type of the user) getter and setter
    public Person.Kind getKind() {
        return kind;
    }

    public void setKind(Person.Kind kind) {
        this.kind = kind;
    }

    /// Gender getter and setter
    public Person.Gender getGender() {
        return gender;
    }

    public void setGender(Person.Gender gender) {
        this.gender = gender;
    }

    /// Religion getter and setter
    public Person.Religion getReligion() {
        return religion;
    }

    public void setReligion(Person.Religion religion) {
        this.religion = religion;
    }

    /// Lower Bound (The minimum age) getter and setter
    public Integer getLower_bound() {
        return lower_bound;
    }

    public void setLower_bound(Integer lower_bound) {
        this.lower_bound = lower_bound;
    }

    /// Upper Bound (The maximum age) getter and setter
    public Integer getUpper_bound() {
        return upper_bound;
    }

    public void setUpper_bound(Integer upper_bound) {
        this.upper_bound = upper_bound;
    }
}
