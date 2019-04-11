package com.example.ofir.social_geha.Firebase;

import com.example.ofir.social_geha.Person;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class Message {
    private String message;
    private String fromUserID;
    private String toUserID;
    private @ServerTimestamp
    Date messageDate;

    public Message(String message, String fromUserID, String toUserID){
        this.message = message;
        this.fromUserID = fromUserID;
        this.toUserID = toUserID;
    }

    public Message(String message, Person fromPerson, Person toPerson){
        this.message = message;
        this.fromUserID = fromPerson.getPersonID();
        this.toUserID = toPerson.getPersonID();
    }

    public String getFromPersonID() {
        return fromUserID;
    }

    public void setFromPersonID(String fromUserID) {
        this.fromUserID = fromUserID;
    }

    public String getToPersonID() {
        return toUserID;
    }

    public void setToPersonID(String toUserID) {
        this.toUserID = toUserID;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
