package com.example.ofir.social_geha.Firebase;

import com.example.ofir.social_geha.Person;
import com.google.firebase.firestore.ServerTimestamp;

import java.io.Serializable;
import java.util.Date;

public class Message implements Serializable {
    private static final long serialVersionUID = 1L;
    private String message;
    private String fromPersonID;
    private String toPersonID;
    private @ServerTimestamp
    Date messageDate;

    public Message(){

    }

    public Message(String message, String fromPersonID, String toPersonID){
        this.message = message;
        this.fromPersonID = fromPersonID;
        this.toPersonID = toPersonID;
    }

    public Message(String message, Person fromPerson, Person toPerson){
        this.message = message;
        this.fromPersonID = fromPerson.getUserID();
        this.toPersonID = toPerson.getUserID();
    }

    public String getFromPersonID() {
        return fromPersonID;
    }



    public void setFromPersonID(String fromUserID) {
        this.fromPersonID = fromUserID;
    }

    public String getToPersonID() {
        return toPersonID;
    }

    public void setToPersonID(String toUserID) {
        this.toPersonID = toUserID;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return fromPersonID +"##"+ toPersonID +"##"+message+"##";
    }
}
