package com.example.ofir.social_geha.Activities_and_Fragments;

import com.example.ofir.social_geha.Activities_and_Fragments.FileHandlers.ContactListFileHandler;
import com.example.ofir.social_geha.Firebase.Message;

import java.util.Date;

import static com.example.ofir.social_geha.Activities_and_Fragments.FileHandlers.ContactListFileHandler.Contact.UNKNOWN_NAME;

public class ChatEntry {

    private int unreadCount;
    private ContactListFileHandler.Contact contact;
    private Message message;

    ChatEntry(){
        this.unreadCount = 0;
        this.contact = null;
        this.message = null;
    }

    ChatEntry(ContactListFileHandler.Contact contact, Message message, int unreadCount) {
        this.contact = contact;
        this.message = message;
        this.unreadCount = unreadCount;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public void setUnreadCount(int unreadCount) {
        this.unreadCount = unreadCount;
    }

    public Date getLastChatReadDate(){
        return contact.getLastChatReadDate();
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

        return this.contact.equals(c.contact);
    }

    public int getUnreadCount() {
        return unreadCount;
    }

    String getName() {
        if(contact.getRealName().equals(UNKNOWN_NAME)) {
           return contact.getAnonID().getName();
        } else {
            return contact.getRealName();
        }
    }

    public Message getMessage() {
        return message;
    }

    String getUserID() {
        return contact.getUid();
    }

    String getImageName() {
        return contact.getAnonID().getImageName();
    }

    String getImageColor() {
        return contact.getAnonID().getImageColor();
    }

    String getDescription() {
        return contact.getDescription();
    }

    public void setRealName(String realName) {
        contact.setRealName(realName);
    }
}
