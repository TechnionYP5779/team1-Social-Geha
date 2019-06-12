package com.example.ofir.social_geha.Activities_and_Fragments.FileHandlers;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

public class SharingsFileHandler {

    private String filename = "sharings2";
    private File mFile;
    private Context context;


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
                Log.d("TEST-CTOR", e.getClass().getSimpleName());
            }
        }
        this.context = context;
    }

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

    public ArrayList<String> getUIDs(){
        ArrayList<String> uids;
        try {
            Log.d("TEST","1");
            FileInputStream inputStream = context.openFileInput(filename);
            Log.d("TEST","2");
            ObjectInputStream ois = new ObjectInputStream(inputStream);
            Log.d("TEST","3");
            uids = (ArrayList<String>)ois.readObject();
            Log.d("TEST","4");
            ois.close();
            Log.d("TEST","5");
            inputStream.close();
        } catch (Exception e) {
            //uids = new ArrayList<>();
            //uids.add(e.getMessage());
            Log.d("TEST",e.getClass().getSimpleName());
            Log.d("TEST",e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
        return uids;
    }

//    public static class UID implements Serializable {
//        private static final long serialVersionUID = 1L;
//        private String uid;
//
//        public UID(String u){
//            uid = u;
//        }
//
//
//        public String getUID() {
//            return uid;
//        }
//
//        public void setUID(String uid) {
//            this.uid = uid;
//        }
//
//        @Override
//        public String toString() {
//            return  "##"+ uid +"##";
//        }
//    }
}
