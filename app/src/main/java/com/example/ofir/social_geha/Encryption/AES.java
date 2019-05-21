package com.example.ofir.social_geha.Encryption;


import android.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class AES {
    public SecretKey key;

    public AES(int keySize) {
        key = genKeys(keySize);
    }

    public AES(SecretKey key) {
        this.key = key;
    }

    public byte[] encrypt(String message) {
        try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return cipher.doFinal(message.getBytes());
        } catch (Exception e) {
            throw new AssertionError(e.getClass().getSimpleName());
        }
    }

    public String decrypt(byte[] cipherText) {
        try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, key);
            return new String(cipher.doFinal(cipherText));
        } catch (Exception e) {
            throw new AssertionError(e.getClass().getSimpleName());
        }
    }

    private static SecretKey genKeys(int keySize) {
        try {
            KeyGenerator aesGen = KeyGenerator.getInstance("AES");
            aesGen.init(keySize);
            return aesGen.generateKey();
        } catch (Exception e) {
            throw new AssertionError(e.getClass().getSimpleName());
        }
    }

    public static String keyToString(SecretKey key) {
        return Base64.encodeToString(key.getEncoded(), Base64.DEFAULT);
    }

    public static SecretKey stringToKey(String key) {
        byte[] encodedKey = Base64.decode(key, Base64.DEFAULT);
        return new SecretKeySpec(encodedKey, 0, encodedKey.length, "AES");
    }
}
