package com.example.ofir.social_geha.Activities_and_Fragments;

import android.content.Context;

import com.example.ofir.social_geha.Firebase.Message;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class FileHandler {
    private String messagesFile = "messages";
    private File mFile;
    private Context context;
    FileOutputStream outputStream;
    FileInputStream inputStream;


    public FileHandler(Context context){
        mFile = new File(context.getFilesDir(), messagesFile);
        this.context = context;
    }

    public ArrayList<Message> writeMessage(Message message){
        ArrayList<Message> messages = getMessages();
        messages.add(message);
        try {
            outputStream = context.openFileOutput(messagesFile, Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(outputStream);
            oos.writeObject(messages);
            oos.close();
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return messages;
    }

    public ArrayList<Message> getMessages(){
        ArrayList<Message> messages;
        try {
            inputStream = context.openFileInput(messagesFile);
            ObjectInputStream ois = new ObjectInputStream(inputStream);
            messages = (ArrayList<Message>)ois.readObject();
            ois.close();
            inputStream.close();
        } catch (Exception e) {
            messages = new ArrayList<>();
        }
        return messages;
    }

}
