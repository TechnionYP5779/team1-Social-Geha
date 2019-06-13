package com.example.ofir.social_geha.Activities_and_Fragments.FileHandlers;

import android.content.Context;
import com.example.ofir.social_geha.Firebase.Message;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

/**
 * A class that maintains the file in which all messages sent to/from the logged in user are stored
 */
public class MessageFileHandler {
    private String filename = "messages";
    private File mFile;
    private Context context;

    /**
     * A constructor - creates the file with an empty message list if it does not exists
     *
     * @param context - the context of the activity (or fragment) which accessed the file (necessary for file access in android)
     */
    public MessageFileHandler(Context context){
        mFile = new File(context.getFilesDir(), filename);

        if (!mFile.exists()) {
            try {
                mFile.createNewFile();
                FileOutputStream outputStream = context.openFileOutput(filename, Context.MODE_PRIVATE);
                ObjectOutputStream oos = new ObjectOutputStream(outputStream);
                oos.writeObject(new ArrayList<Message>());
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
     * Adds a new message to the file
     * @param message - the new message
     * @return - the message list after the addition
     */
    public ArrayList<Message> writeMessage(Message message){
        ArrayList<Message> messages = getMessages();
        messages.add(message);

        try {
            FileOutputStream outputStream = context.openFileOutput(filename, Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(outputStream);
            oos.writeObject(messages);
            oos.close();
            outputStream.close();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
        return messages;
    }

    /**
     *
     * @return - a list of all the recorded messages
     */
    public ArrayList<Message> getMessages(){
        ArrayList<Message> messages;
        try {
            FileInputStream inputStream = context.openFileInput(filename);
            ObjectInputStream ois = new ObjectInputStream(inputStream);
            messages = (ArrayList<Message>)ois.readObject();
            ois.close();
            inputStream.close();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
        return messages;
    }

}
