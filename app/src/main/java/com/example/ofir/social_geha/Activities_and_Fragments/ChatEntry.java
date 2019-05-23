package com.example.ofir.social_geha.Activities_and_Fragments;

import com.example.ofir.social_geha.Activities_and_Fragments.FileHandlers.ContactListFileHandler;
import com.example.ofir.social_geha.Firebase.Message;
import static com.example.ofir.social_geha.Activities_and_Fragments.FileHandlers.ContactListFileHandler.Contact.UNKNOWN_NAME;

public class ChatEntry {

    private ContactListFileHandler.Contact contact;
    private Message message;


    ChatEntry(ContactListFileHandler.Contact contact, Message message) {
        this.contact = contact;
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

        return this.contact.equals(c.contact);
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
}
