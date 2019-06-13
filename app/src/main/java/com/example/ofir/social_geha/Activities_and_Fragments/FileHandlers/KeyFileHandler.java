package com.example.ofir.social_geha.Activities_and_Fragments.FileHandlers;

import android.content.Context;
import com.example.ofir.social_geha.Encryption.AES;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import javax.crypto.SecretKey;

/**
 * Maintains a file containing a single encryption key, pertaining to the conversation between the logged in user and another used
 */
public class KeyFileHandler {
    private String keyFileSuffix = "convoKey";
    private String keyFileName;
    private File mFile;
    private Context context;
    private FileOutputStream outputStream;
    private FileInputStream inputStream;

    /**
     * A constructor, creates the file it does not already exist.
     *
     * @param context - the context of the activity (or fragment) which accessed the file (necessary for file access in android)
     * @param mOtherPersonId - the id of the other side of the conversation
     */
    public KeyFileHandler(Context context, String mOtherPersonId) {
        keyFileName = mOtherPersonId + keyFileSuffix;
        mFile = new File(context.getFilesDir(), keyFileName);
        this.context = context;
    }

    /**
     * Writes a secret encryption key to the file
     *
     * @param key - the key we would wish to record
     */
    public void writeKey(SecretKey key) {
        try {
            outputStream = context.openFileOutput(keyFileName, Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(outputStream);
            oos.writeObject(AES.keyToString(key));
            oos.close();
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @return - the encryption key currently stored in the file
     */
    public SecretKey getKey() {
        SecretKey key;
        try {
            inputStream = context.openFileInput(keyFileName);
            ObjectInputStream ois = new ObjectInputStream(inputStream);
            key = AES.stringToKey((String) ois.readObject());
            ois.close();
            inputStream.close();
        } catch (Exception e) {
            key = null;
        }
        return key;
    }

}
