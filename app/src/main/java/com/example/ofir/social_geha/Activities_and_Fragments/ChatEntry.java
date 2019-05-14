package com.example.ofir.social_geha.Activities_and_Fragments;

import com.example.ofir.social_geha.Firebase.Message;
import com.example.ofir.social_geha.Person;

public class ChatEntry {

    private Person person;
    private Message message;


    public ChatEntry(Person person, Message message) {
        this.person = person;
        this.message = message;
    }

    @Override
    public boolean equals(Object o) {

        if (o == this) {
            return true;
        }

        if (!(o instanceof ChatEntry)) {
            return false;
        }

        ChatEntry c = (ChatEntry) o;

        return this.person.equals(c.person);
    }

    public Person getPerson() {
        return person;
    }

    public Message getMessage() {
        return message;
    }
}
