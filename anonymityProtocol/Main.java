import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

public class Main {


    private static void storeCipher(byte[] cipherText) {
        try (FileOutputStream out = new FileOutputStream("cipher.txt")) {
            out.write(cipherText);
        } catch (Exception e) {
            throw new AssertionError(e.getClass().getSimpleName());
        }
    }

    private static byte[] loadCipher(String dir) {
        try {
            Path path = Paths.get(dir + "/cipher.txt");
            return Files.readAllBytes(path);
        } catch (Exception e) {
            throw new AssertionError(e.getClass().getSimpleName());
        }
    }

    public static void main(String[] args) {
        cipherRSA cipherRSA = null;
        byte[] cipherText = null;
        while (true) {
            System.out.println("What would you like to do?");
            String command;
            Scanner reader = new Scanner(System.in);
            command = reader.nextLine();
            switch (command) {
                case "New Keys":
                    System.out.println("What size of key?");
                    cipherRSA = new cipherRSA(reader.nextInt());
                    break;
                case "Load Keys":
                    System.out.println("Assuming the format of the private key is PKCS#8");
                    System.out.println("Assuming the format of the public key is X.509");
                    System.out.println("From which directory?");
                    cipherRSA = new cipherRSA(reader.nextLine());
                    break;
                case "Encrypt":
                    if (cipherRSA == null)
                        System.out.println("The keys were not initialized");
                    else {
                        System.out.println("What is the message_layout?");
                        cipherText = cipherRSA.encrypt(reader.nextLine());
                        storeCipher(cipherText);
                    }
                    break;
                case "Decrypt":
                    if (cipherRSA == null)
                        System.out.println("The keys were not initialized");
                    else {
                        System.out.println("Would you like to provide a cipher text?");
                        if (reader.nextLine().equals("Y")) {
                            System.out.println("From which directory?");
                            cipherText = loadCipher(reader.nextLine());
                        } else {
                            if (cipherText == null) {
                                System.out.println("Cipher text was not initialized");
                                break;
                            } else
                                System.out.println("Using previous cipher text");
                        }
                        System.out.println(cipherRSA.decrypt(cipherText));
                    }
                    break;
                case "Exit":
                    return;
                default:
                    System.out.println("Sorry, I do not understand...");
                    break;
            }
        }
    }
}
