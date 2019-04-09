package com.example.ofir.social_geha.Firebase;

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

    public String getFromUserID() {
        return fromUserID;
    }

    public void setFromUserID(String fromUserID) {
        this.fromUserID = fromUserID;
    }

    public String getToUserID() {
        return toUserID;
    }

    public void setToUserID(String toUserID) {
        this.toUserID = toUserID;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
