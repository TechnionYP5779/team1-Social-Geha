package com.example.ofir.social_geha.Encryption;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;

import javax.crypto.Cipher;

/***
 * Class used to encrypt messages using an asymmetric encryption method
 * This class supports the use of signatures and verifications of them
 * The class itself is not yet integrated into the main app, so use with caution
 */
public class RSA {
    private PublicKey publicKey;
    private PrivateKey privateKey;

    /***
     * Creates a new instance of AES encrypt/decrypt
     * A version which only encrypts is not yet implemented
     * @param keySize Starting from 1024 and above (2048 is a standard)
     */
    public RSA(int keySize) {
        KeyPair keyPair = genKeys(keySize);
        this.privateKey = keyPair.getPrivate();
        this.publicKey = keyPair.getPublic();
    }

    public byte[] encrypt(String message) {
        try {
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            return cipher.doFinal(message.getBytes());
        } catch (Exception e) {
            throw new AssertionError(e.getClass().getSimpleName());
        }
    }

    public String decrypt(byte[] cipherText) {
        try {
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, this.privateKey);
            return new String(cipher.doFinal(cipherText));
        } catch (Exception e) {
            throw new AssertionError(e.getClass().getSimpleName());
        }
    }

    public byte[] sign(String message) {
        try {
            Signature sha256withRSA = Signature.getInstance("SHA256withRSA");
            sha256withRSA.initSign(privateKey);
            sha256withRSA.update(message.getBytes());
            return sha256withRSA.sign();
        } catch (Exception e) {
            throw new AssertionError(e.getClass().getSimpleName());
        }
    }

    public boolean verify(String message, byte[] signature) {
        try {
            Signature sha256withRSA = Signature.getInstance("SHA256withRSA");
            sha256withRSA.initVerify(publicKey);
            sha256withRSA.update(message.getBytes());
            return sha256withRSA.verify(signature);
        } catch (Exception e) {
            throw new AssertionError(e.getClass().getSimpleName());
        }
    }

    /// Service method to create a new pair of keys
    private static KeyPair genKeys(int keySize) {
        try {
            KeyPairGenerator rsaGen = KeyPairGenerator.getInstance("RSA");
            rsaGen.initialize(keySize);
            return rsaGen.generateKeyPair();
        } catch (Exception e) {
            throw new AssertionError(e.getClass().getSimpleName());
        }
    }
}
