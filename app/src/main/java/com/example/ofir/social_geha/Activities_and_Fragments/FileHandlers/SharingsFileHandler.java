package com.example.ofir.social_geha.Activities_and_Fragments.FileHandlers;

import android.content.Context;
import android.util.Log;

import com.example.ofir.social_geha.Firebase.Message;
import com.example.ofir.social_geha.Person;
import com.google.firebase.firestore.ServerTimestamp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class SharingsFileHandler {

    private String sharingsFile = "sharings";
    private File mFile;
    private Context context;
    FileOutputStream outputStream;
    FileInputStream inputStream;


    public SharingsFileHandler(Context context){
        mFile = new File(context.getFilesDir(), sharingsFile);
        this.context = context;
    }

    public ArrayList<UID> addUID(String uid){
        Log.d("POPO", "writeMessage: DOES THIS");
        ArrayList<UID> uids = getUIDs();
        uids.add(new UID(uid));
        for(UID u : uids){
            Log.d("POPO", "add UID: "+u.getUID());
        }
        try {
            outputStream = context.openFileOutput(sharingsFile, Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(outputStream);
            oos.writeObject(uids);
            oos.close();
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return uids;
    }

    public ArrayList<UID> getUIDs(){
        ArrayList<UID> uids;
        try {
            inputStream = context.openFileInput(sharingsFile);
            ObjectInputStream ois = new ObjectInputStream(inputStream);
            uids = (ArrayList<UID>)ois.readObject();
            ois.close();
            inputStream.close();
        } catch (Exception e) {
            uids = new ArrayList<>();
        }
        return uids;
    }

    public class UID implements Serializable {
        private static final long serialVersionUID = 1L;
        private String uid;

        public UID(String u){
            uid=u;

        }


        public String getUID() {
            return uid;
        }

        public void setUID(String uid) {
            this.uid = uid;
        }

        @Override
        public String toString() {
            return  "##"+ uid +"##";
        }
    }
}
