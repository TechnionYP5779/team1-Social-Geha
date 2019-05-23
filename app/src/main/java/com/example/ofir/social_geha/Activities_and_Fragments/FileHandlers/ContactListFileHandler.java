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

        try {
            FileOutputStream outputStream = context.openFileOutput(filename, Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(outputStream);
            oos.writeObject(new ArrayList<Contact>());
            oos.flush();
            oos.close();
            outputStream.close();
        } catch (IOException e) {
            throw new RuntimeException("File was not created properly");
        }
    }

    public ArrayList<Contact> addContact(Contact c) {
        ArrayList<Contact> contacts = getContacts();
        contacts.add(c);

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
            return contacts;
        } catch (Exception e) {
            Log.d("CONTACTS", e.getClass().getSimpleName());
            throw new RuntimeException("Failed writing contacts file");
        }

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
            throw new RuntimeException("Failed reading contacts file");
        }
    }


    public static class Contact implements Serializable {
        private String uid;
        private String realName;
        private AnonymousIdentity anonID;

        public Contact(String uid, String realName, AnonymousIdentity anonID) {
            this.uid = uid;
            this.realName = realName;
            this.anonID = anonID;
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
    }
}
