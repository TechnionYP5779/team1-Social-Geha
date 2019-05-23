package com.example.ofir.social_geha.Activities_and_Fragments.FileHandlers;

import android.content.Context;
import android.util.Log;

import com.example.ofir.social_geha.AnonymousIdentity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

import javax.crypto.SecretKey;

public class ContactListFileHandler {
    private static final String filename = "contactList";
    private File mFile;
    private Context context;


    public ContactListFileHandler(Context context) {
        Log.d("CONTACTS", "FileHandler c'tor");
        mFile = new File(context.getFilesDir(), filename); //just to actually create the file
        this.context = context;
    }

    //overwrites is uid is already found
    public ArrayList<Contact> addContact(Contact c) {
        ArrayList<Contact> contacts = getContacts();

        for(int i = 0; i < contacts.size(); i++) {
            if(contacts.get(i).getUid().equals(c.getUid())) {
                contacts.set(i, c);
            }
        }

        //we haven't found it in the scan
        if(!contacts.contains(c)) {
            contacts.add(c);
        }

        StringBuilder lstStr = new StringBuilder();
        for (Contact con : contacts) {
            lstStr.append(con.toString()).append(" ");
        }

        Log.d("CONTACTS", "addContact: ADDING CONTACT " + c.toString());
        Log.d("CONTACTS", "addContact: new contactList is:" + lstStr);

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


    public ArrayList<Contact> getContacts() {
        ArrayList<Contact> contacts;
        try {
            FileInputStream inputStream = context.openFileInput(filename);
            ObjectInputStream ois = new ObjectInputStream(inputStream);
            contacts = (ArrayList<Contact>) ois.readObject();
            ois.close();
            inputStream.close();

            StringBuilder lstStr = new StringBuilder();
            for (Contact con : contacts) {
                lstStr.append(con.toString()).append(" ");
            }
            Log.d("CONTACTS", "getContacts: contactList is:" + lstStr);

            return contacts;
        } catch (Exception e) {
            Log.d("CONTACTS", e.getClass().getSimpleName());
            return new ArrayList<>();
        }
    }


    public static class Contact implements Serializable {
        public static final String UNKNOWN_NAME = "UNKNOWN";
        private String uid;
        private String realName;
        private String desc;
        private AnonymousIdentity anonID;

        public Contact(String uid, String realName, String desc, AnonymousIdentity anonID) {
            this.uid = uid;
            this.realName = realName;
            this.anonID = anonID;
            this.desc = desc;
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
