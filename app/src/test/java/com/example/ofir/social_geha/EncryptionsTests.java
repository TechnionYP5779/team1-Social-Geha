package com.example.ofir.social_geha;

import com.example.ofir.social_geha.Encryption.AES;
import com.example.ofir.social_geha.Encryption.RSA;

import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class EncryptionsTests {
    private RSA rsa = new RSA(1024);

    @Test
    public void RSAEncryptionIsNotTrivial() {
        String message = "Hello!";
        assertEquals(message, rsa.decrypt(rsa.encrypt(message)));
        assertNotEquals(message, Arrays.toString(rsa.encrypt(message)));
//        Log.i("RSA_ENCRYPT", "|" + Arrays.toString(rsa.encrypt(message)) + "|");
    }

    @Test
    public void checkSignature() {
        String message = "Wow!!!! Much Secure!!!";
        assertTrue(rsa.verify(message, rsa.sign(message)));
    }

    @Test
    public void AESEncryptionIsNotTrivial() {
        AES aes = new AES(128);
        String message = "Hello!";
        assertEquals(message, aes.decrypt(aes.encrypt(message)));
        assertNotEquals(message, Arrays.toString(aes.encrypt(message)));
//        Log.i("RSA_ENCRYPT", "|" + Arrays.toString(rsa.encrypt(message)) + "|");
    }
}