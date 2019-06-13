package com.example.ofir.social_geha.Activities_and_Fragments.FileHandlers;

import android.content.Context;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

/**
 * A sharings is a situation where the user has shared their information with another party.
 * The purpose of this class is to manage a file of sharings committed by the logged it user
 */
public class SharingsFileHandler {

    private String filename = "sharings2"; //the name of the file in which the sharings are stored
    private File mFile;
    private Context context;

    /**
     * Constructor: If the file exists, opens it. Otherwise, creates it and writes an empty list to it.
     *
     * @param context - the context of the activity (or fragment) which accessed the file (necessary for file access in android)
     */
    public SharingsFileHandler(Context context){
        mFile = new File(context.getFilesDir(), filename);
        if (!mFile.exists()) {
            try {
                mFile.createNewFile();
                FileOutputStream outputStream = context.openFileOutput(filename, Context.MODE_PRIVATE);
                ObjectOutputStream oos = new ObjectOutputStream(outputStream);
                oos.writeObject(new ArrayList<String>());
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
     * Adds a new entry to the list of persons with whom the user has shared his/her information.
     * The addition is saved persistently in the file.
     *
     * @param uid - the of id of someone the user's information has been shared with
     * @return the list of id's maintained by the file (after the addition)
     */
    public ArrayList<String> addUID(String uid){
        ArrayList<String> uids = getUIDs();
        uids.add(uid);

        try {
            FileOutputStream outputStream = context.openFileOutput(filename, Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(outputStream);
            oos.writeObject(uids);
            oos.flush();
            oos.close();
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return uids;
    }

    /**
     * @return the list of sharings recorded in the file
     */
    public ArrayList<String> getUIDs(){
        ArrayList<String> uids;
        try {
            FileInputStream inputStream = context.openFileInput(filename);
            ObjectInputStream ois = new ObjectInputStream(inputStream);
            uids = (ArrayList<String>)ois.readObject();
            ois.close();
            inputStream.close();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
        return uids;
    }
}
