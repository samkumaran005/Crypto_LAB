import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.net.*;
import java.util.Base64;
import java.util.Scanner;

public class Client {
    static final String SERVER_ADDRESS = "127.0.0.1";
    static final int PORT = 5000;

    public static void main(String[] args) {
        try (
            Socket socket = new Socket(SERVER_ADDRESS, PORT);
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            Scanner scanner = new Scanner(System.in)
        ) {
            System.out.println("Connected to Server...\n");

            // Input Plaintext
            System.out.print("Enter Plain Text        : ");
            String plainText = scanner.nextLine();

            // Input Key (DES requires exactly an 8-character / 64-bit key)
            System.out.print("Enter Key (8 characters): ");
            String key = scanner.nextLine();

            // Ensure key is exactly 8 bytes long
            if (key.length() < 8) {
                key = String.format("%-8s", key).replace(' ', '0'); // Pad with zeros
            } else if (key.length() > 8) {
                key = key.substring(0, 8); // Truncate to 8 chars
            }

            // Encrypt using DES Algorithm
            String cipherText = encryptDES(plainText, key);

            System.out.println("\nGenerated Cipher Text (Base64): " + cipherText);

            // Send Key and Cipher Text to Server
            out.writeUTF(key);
            out.writeUTF(cipherText);
            out.flush();

            System.out.println("Data sent to server successfully.");

        } catch (Exception e) {
            System.out.println("Client Error: " + e.getMessage());
        }
    }

    //----------------------------------------------------
    // DES Encryption
    //----------------------------------------------------
    static String encryptDES(String plainText, String key) throws Exception {
        SecretKeySpec keySpec = new SecretKeySpec(key.getBytes("UTF-8"), "DES");
        Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, keySpec);

        byte[] encryptedBytes = cipher.doFinal(plainText.getBytes("UTF-8"));
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }
}