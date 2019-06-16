package com.example.ofir.social_geha.Encryption;


import android.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/***
 * Class used to encrypt messages using a symmetric encryption method
 */
public class AES {
    public SecretKey key;

    /***
     * Creates a new instance of AES encrypt/decrypt
     * @param keySize can be 128, 192 or 256 bits
     */
    public AES(int keySize) {
        key = genKeys(keySize);
    }

    /***
     * Use this when reading a key from a file to create a new instance of AES
     * @param key the key read from the file
     */
    public AES(SecretKey key) {
        this.key = key;
    }

    /// Service methods to transform byte to String
    private static byte[] toByte(String hexString) {
        int len = hexString.length() / 2;
        byte[] result = new byte[len];
        for (int i = 0; i < len; i++)
            result[i] = Integer.valueOf(hexString.substring(2 * i, 2 * i + 2),
                    16).byteValue();
        return result;
    }

    private static String toHex(byte[] buf) {
        if (buf == null)
            return "";
        StringBuffer result = new StringBuffer(2 * buf.length);
        for (byte b : buf) {
            appendHex(result, b);
        }
        return result.toString();
    }

    private final static String HEX = "0123456789ABCDEF";

    private static void appendHex(StringBuffer sb, byte b) {
        sb.append(HEX.charAt((b >> 4) & 0x0f)).append(HEX.charAt(b & 0x0f));
    }

    /***
     * Encrypts the message using the instance's key
     * @param message the message to encrypt
     * @return the encrypted message in hex encoding
     *          This is a String so it can be sent and read without problem
     */
    public String encrypt(String message) {
        try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return toHex(cipher.doFinal(message.getBytes()));
        } catch (Exception e) {
            throw new AssertionError(e.getClass().getSimpleName());
        }
    }

    /***
     * Decrypts the cipher text using the instance's key
     * @param cipherText an encrypted message in the hex format used above
     * @return the decrypted message
     */
    public String decrypt(String cipherText) {
        try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, key);
            return new String(cipher.doFinal(toByte(cipherText)));
        } catch (Exception e) {
            throw new AssertionError(e.getClass().getSimpleName());
        }
    }

    /// Service method to create new key
    private static SecretKey genKeys(int keySize) {
        try {
            KeyGenerator aesGen = KeyGenerator.getInstance("AES");
            aesGen.init(keySize);
            return aesGen.generateKey();
        } catch (Exception e) {
            throw new AssertionError(e.getClass().getSimpleName());
        }
    }

    /// translate SecretKey to String
    public static String keyToString(SecretKey key) {
        return Base64.encodeToString(key.getEncoded(), Base64.DEFAULT);
    }

    /// translate String (read from a file) to SecretKey
    public static SecretKey stringToKey(String key) {
        byte[] encodedKey = Base64.decode(key, Base64.DEFAULT);
        return new SecretKeySpec(encodedKey, 0, encodedKey.length, "AES");
    }
}
