import javax.crypto.Cipher;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * Created by Yoav Zuriel on 4/5/2019.
 */
public class cipherRSA {

    public PublicKey publicKey;
    private PrivateKey privateKey;

    public cipherRSA(int keySize) {
        KeyPair keyPair = genKeys(keySize);
        this.privateKey = keyPair.getPrivate();
        this.publicKey = keyPair.getPublic();
    }

    public cipherRSA(String directory) {
        KeyPair keyPair = loadKeys(directory);
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


    private static KeyPair genKeys(int keySize) {
        try {
            KeyPairGenerator rsaGen = KeyPairGenerator.getInstance("RSA");
            rsaGen.initialize(keySize);
            KeyPair newKey = rsaGen.generateKeyPair();
            FileOutputStream out = new FileOutputStream("PrivateKey.key");
            out.write(newKey.getPrivate().getEncoded());
            out.close();
            System.err.println("Private key format: " + newKey.getPrivate().getFormat());
            out = new FileOutputStream("PublicKey.key");
            out.write(newKey.getPublic().getEncoded());
            out.close();
            System.err.println("Private key format: " + newKey.getPublic().getFormat());
            return newKey;
        } catch (Exception e) {
            throw new AssertionError(e.getClass().getSimpleName());
        }
    }

    private static KeyPair loadKeys(String dir) {
        try {
            Path path = Paths.get(dir + "/PublicKey.key");
            byte[] bytes = Files.readAllBytes(path);
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(bytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PublicKey publicKey = keyFactory.generatePublic(keySpec);
            path = Paths.get(dir + "/PrivateKey.key");
            bytes = Files.readAllBytes(path);
            PKCS8EncodedKeySpec keySpec1 = new PKCS8EncodedKeySpec(bytes);
            PrivateKey privateKey = keyFactory.generatePrivate(keySpec1);
            return new KeyPair(publicKey, privateKey);
        } catch (Exception e) {
            throw new AssertionError(e.getClass().getSimpleName());
        }
    }
}
