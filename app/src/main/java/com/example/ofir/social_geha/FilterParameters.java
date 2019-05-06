package com.example.ofir.social_geha;

import android.util.Range;

import java.io.Serializable;
import java.util.EnumSet;
import java.util.Set;

public class FilterParameters implements Serializable {
    private EnumSet<Person.Language> languages;
    private Person.Kind kind;
    private Person.Gender gender;
    private Person.Religion religion;
    private Integer lower_bound;
    private Integer upper_bound;

    public FilterParameters(EnumSet<Person.Language> languages, Person.Kind kind, Person.Gender gender,
                            Person.Religion religion, Integer lower, Integer upper) {
        this.languages = languages;
        this.kind = kind;
        this.gender = gender;
        this.religion = religion;
        this.lower_bound = lower;
        this.upper_bound = upper;
    }

    public EnumSet<Person.Language> getLanguages() {
        return languages;
    }

    public void setLanguages(EnumSet<Person.Language> languages) {
        this.languages = languages;
    }

    public Person.Kind getKind() {
        return kind;
    }

    public void setKind(Person.Kind kind) {
        this.kind = kind;
    }

    public Person.Gender getGender() {
        return gender;
    }

    public void setGender(Person.Gender gender) {
        this.gender = gender;
    }

    public Person.Religion getReligion() {
        return religion;
    }

    public void setReligion(Person.Religion religion) {
        this.religion = religion;
    }

    public Integer getLower_bound() {
        return lower_bound;
    }

    public void setLower_bound(Integer lower_bound) {
        this.lower_bound = lower_bound;
    }

    public Integer getUpper_bound() {
        return upper_bound;
    }

    public void setUpper_bound(Integer upper_bound) {
        this.upper_bound = upper_bound;
    }
}
