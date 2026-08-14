import java.io.*;
import java.net.*;
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
            System.out.print("Enter Plain Text: ");
            String plainText = scanner.nextLine();

            // Input Key
            System.out.print("Enter Keyword   : ");
            String key = scanner.nextLine();

            // Encrypt using Autokey Cipher
            String cipherText = encrypt(plainText, key);

            System.out.println("\nGenerated Cipher Text: " + cipherText);

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
    // Autokey Cipher Encryption
    //----------------------------------------------------
    static String encrypt(String text, String key) {
        StringBuilder cipher = new StringBuilder();
        StringBuilder extendedKey = new StringBuilder(key.toUpperCase());

        // Append plaintext letters to keyword to form the autokey stream
        for (char c : text.toCharArray()) {
            if (Character.isLetter(c)) {
                extendedKey.append(Character.toUpperCase(c));
            }
        }

        int keyIndex = 0;
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);

            if (Character.isUpperCase(c)) {
                int shift = extendedKey.charAt(keyIndex++) - 'A';
                char encrypted = (char) (((c - 'A' + shift) % 26) + 'A');
                cipher.append(encrypted);
            } else if (Character.isLowerCase(c)) {
                int shift = extendedKey.charAt(keyIndex++) - 'A';
                char encrypted = (char) (((c - 'a' + shift) % 26) + 'a');
                cipher.append(encrypted);
            } else {
                cipher.append(c); // Preserve spaces and special characters
            }
        }
        return cipher.toString();
    }
}