package com.example.ofir.social_geha.Firebase;

import com.example.ofir.social_geha.Person;
import com.google.firebase.firestore.ServerTimestamp;

import java.io.Serializable;
import java.util.Date;


/* This class represents a message to be saved on the FireStore database
 */
public class Message implements Serializable {
    private static final long serialVersionUID = 1L;
    private boolean shown;
    private String message;
    private String fromPersonID;
    private String toPersonID;
    private @ServerTimestamp Date messageDate;


    //DO NOT REMOVE: this is required for saving on FireStore
    public Message(){

    }

    public Message(String message, String fromPersonID, String toPersonID, boolean shown){
        this.message = message;
        this.fromPersonID = fromPersonID;
        this.toPersonID = toPersonID;
        this.messageDate = new Date();
        this.shown = shown;
    }

    public Message(String message, Person fromPerson, Person toPerson, boolean shown){
        this.message = message;
        this.fromPersonID = fromPerson.getUserID();
        this.toPersonID = toPerson.getUserID();
        this.messageDate = new Date();
        this.shown = shown;
    }

    public void setShown(boolean shown) {
        this.shown = shown;
    }

    public boolean getShown() {
        return shown;
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

    public Date getMessageDate() {
        return messageDate;
    }

    @Override
    public String toString() {
        return fromPersonID +"##"+ toPersonID +"##"+message+"##";
    }
}
