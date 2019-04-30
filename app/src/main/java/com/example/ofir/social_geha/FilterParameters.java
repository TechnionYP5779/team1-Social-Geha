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
    private Range<Integer> age = null;

    public FilterParameters(EnumSet<Person.Language> languages, Person.Kind kind, Person.Gender gender,
                            Person.Religion religion, Range<Integer> age) {
        this.languages = languages;
        this.kind = kind;
        this.gender = gender;
        this.religion = religion;
        this.age = age;
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

    public Range<Integer> getAge() {
        return age;
    }

    public void setAge(Range<Integer> age) {
        this.age = age;
    }
}
