package com.example.ofir.social_geha.Activities_and_Fragments.FileHandlers;

import android.content.Context;
import com.example.ofir.social_geha.Identity.AnonymousIdentity;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;

/**
 * A contact is someone the user has conversed with. They are identified by their anonymous identity,
 * and/or their real identity if it is known to the user.
 *
 * This class maintains in a file the list of contacts of the user and their details
 */
public class ContactListFileHandler {
    private static final String filename = "contactList"; //the name of the file in which the contacts are stored
    private File mFile;
    private Context context;


    /**
     * A constructor, creates the file if it does not exist
     *
     * @param context - the context of the activity (or fragment) which accessed the file (necessary for file access in android)
     */
    public ContactListFileHandler(Context context) {
        mFile = new File(context.getFilesDir(), filename); //just to actually create the file

        if (!mFile.exists()) {
            try {
                mFile.createNewFile();
                FileOutputStream outputStream = context.openFileOutput(filename, Context.MODE_PRIVATE);
                ObjectOutputStream oos = new ObjectOutputStream(outputStream);
                oos.writeObject(new HashMap<String, Contact>());
                oos.flush();
                oos.close();
                outputStream.close();
            } catch (IOException e) {
                throw new RuntimeException(e.getMessage());
            }
        }

        this.context = context;
    }

    /**
     * Changes the (real) name of a contact in the contact-list
     * @param uid - the id of the contact whose name we wish to change
     * @param realname - the new name we'd like to install in the contact-list
     */
    public void changeName(String uid, String realname) {
        HashMap<String, Contact> contacts = getContacts();
        Contact contact = contacts.get(uid);
        contact.setRealName(realname);
        addContact(contact);
    }


    /**
     * Each contact is supplied with a timestamp representing the last time of change, this function modifies that value.
     * @param uid - the id of the person whose timestamp we wish to modify
     * @param date - the new timestamp we'd like to record.
     *
     * @apiNote the function does nothing if the given uid is not a contact.
     */
    public void changeLastChatViewDate(String uid, Date date) {

        HashMap<String, Contact> contacts = getContacts();
        if(!contacts.containsKey(uid))
            return;
        Contact contact = contacts.get(uid);
        contact.setLastChatViewDate(date);
        addContact(contact);
    }

    //overwrites is uid is already found

    /**
     * Adds a new contact to the list maintained by the value, it is modified persistently in the file by the time the function returns
     * @param c - the new contact we'd like to add
     * @return - the contact list (as a map from uid to contact object) after the addition
     *
     * @implNote - if a contact with the same uid exists it is overwritten, but we do not recommend using for function for overwriting a name (as it is less efficient)
     */
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
            throw new RuntimeException(e.getMessage());
        }
        return contacts;
    }

    /**
     *
     * @param uid - the id of a contact we'd like to read
     * @return - the contact matching this uid, or null if it does not match any contact
     */
    public Contact getContact(String uid){
        HashMap<String, Contact> contacts = getContacts();
        return contacts.get(uid);
    }


    /**
     *
     * @return - a map representing all the contacts
     */
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
            throw new RuntimeException(e.getMessage());
        }
    }


    /**
     * A supporting class to encapsulate the notion of a "contact"
     */
    public static class Contact implements Serializable {
        public static final String UNKNOWN_NAME = "UNKNOWN"; //Not a legal name, we use it to denote an unknown name
        private String uid;
        private String realName;
        private String desc;
        private AnonymousIdentity anonID;
        private Date lastChatViewDate;

        /**
         * A constructor for Contact
         * @param uid - id of the contact to construct
         * @param realName - realName of the contact to construct (or @var UNKNOWN_NAME)
         * @param desc - description of the contact to construct
         * @param anonID - anonymous identity of the contact to construct
         * @param lastChatViewDate - last-change timestamp of the contact to construct
         */
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
