package com.example.ofir.social_geha.Activities_and_Fragments.FileHandlers;

import android.content.Context;
import android.util.Log;

import com.example.ofir.social_geha.AnonymousIdentity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ContactListFileHandler {
    private static final String filename = "contactList";
    private File mFile;
    private Context context;


    public ContactListFileHandler(Context context) {
        Log.d("CONTACTS", "FileHandler c'tor");
        mFile = new File(context.getFilesDir(), filename); //just to actually create the file
        this.context = context;
    }

    public void changeName(String uid, String realname) {
        HashMap<String, Contact> contacts = getContacts();
        Contact contact = contacts.get(uid);
        contact.setRealName(realname);
        addContact(contact);
    }


    public void changeLastChatViewDate(String uid, Date date) {

        HashMap<String, Contact> contacts = getContacts();
        if(!contacts.containsKey(uid))
            return;
        Contact contact = contacts.get(uid);
        contact.setLastChatViewDate(date);
        addContact(contact);
    }

    //overwrites is uid is already found
    public HashMap<String, Contact> addContact(Contact c) {
        HashMap<String, Contact> contacts = getContacts();

        contacts.put(c.getUid(),c);

        try {
            FileOutputStream outputStream = context.openFileOutput(filename, Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(outputStream);
            oos.writeObject(contacts);
            oos.flush();
            oos.close();
            outputStream.close();
        } catch (Exception e) {
            Log.d("CONTACTS", e.getClass().getSimpleName());
        }
        return contacts;
    }

    public Contact getContact(String uid){
        HashMap<String, Contact> contacts = getContacts();
        return contacts.get(uid);
    }


    public HashMap<String, Contact> getContacts() {
        HashMap<String, Contact> contacts;
        try {
            FileInputStream inputStream = context.openFileInput(filename);
            ObjectInputStream ois = new ObjectInputStream(inputStream);
            contacts = (HashMap<String, Contact>) ois.readObject();
            ois.close();
            inputStream.close();
            return contacts;
        } catch (Exception e) {
            return new HashMap<>();
        }
    }


    public static class Contact implements Serializable {
        public static final String UNKNOWN_NAME = "UNKNOWN";
        private String uid;
        private String realName;
        private String desc;
        private AnonymousIdentity anonID;
        private Date lastChatViewDate;

        public Contact(String uid, String realName, String desc, AnonymousIdentity anonID, Date lastChatViewDate) {
            this.uid = uid;
            this.realName = realName;
            this.anonID = anonID;
            this.desc = desc;
            this.lastChatViewDate = lastChatViewDate;
        }

        public String getUid() {
            return uid;
        }

        public void setUid(String uid) {
            this.uid = uid;
        }

        public String getRealName() {
            return realName;
        }

        public void setRealName(String realName) {
            this.realName = realName;
        }

        public AnonymousIdentity getAnonID() {
            return anonID;
        }

        public void setAnonID(AnonymousIdentity anonID) {
            this.anonID = anonID;
        }

        public Date getLastChatReadDate() {
            return lastChatViewDate;
        }

        public void setLastChatViewDate(Date lastChatViewDate) {
            this.lastChatViewDate = lastChatViewDate;
        }

        @Override
        public String toString() {
            return "{" + uid + "|" + realName + "|" + anonID.toString() + "}";
        }

        public String getDescription() {
            return desc;
        }

        public void setDescription(String desc) {
            this.desc = desc;
        }
    }
}
