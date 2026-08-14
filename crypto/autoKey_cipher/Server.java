import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Server {

    static final int PORT = 5000;

    public static void main(String[] args) {

        try {
            ServerSocket server = new ServerSocket(PORT);

            System.out.println("Server Started...");
            System.out.println("Waiting for Client...");

            Socket socket = server.accept();

            System.out.println("Client Connected...\n");

            DataInputStream in = new DataInputStream(socket.getInputStream());

            // Receive Key and Cipher Text
            String key = in.readUTF();
            String cipher = in.readUTF();

            System.out.println("===== DATA RECEIVED =====");
            System.out.println("Keyword     : " + key);
            System.out.println("Cipher Text : " + cipher);

            // =========================================================
            // PAUSE HERE: Wait for user to press ENTER before decrypting
            // =========================================================
            System.out.print("\n>>> Press [ENTER] to perform decryption... ");
            Scanner console = new Scanner(System.in);
            console.nextLine();

            // Decrypt using Autokey Cipher
            String plain = decrypt(cipher, key);

            System.out.println("Original Text : " + plain);

            // Clean up resources
            console.close();
            in.close();
            socket.close();
            server.close();

        } catch (Exception e) {
            System.out.println("Server Error: " + e.getMessage());
        }

    }

    //----------------------------------------------------
    // Autokey Cipher Decryption
    //----------------------------------------------------
    static String decrypt(String cipher, String key) {
        StringBuilder plain = new StringBuilder();
        StringBuilder extendedKey = new StringBuilder(key.toUpperCase());
        int keyIndex = 0;

        for (int i = 0; i < cipher.length(); i++) {
            char c = cipher.charAt(i);

            if (Character.isUpperCase(c)) {
                int shift = extendedKey.charAt(keyIndex++) - 'A';
                char decrypted = (char) (((c - 'A' - shift + 26) % 26) + 'A');
                plain.append(decrypted);

                // Dynamically extend key with decrypted plaintext character
                extendedKey.append(decrypted);

            } else if (Character.isLowerCase(c)) {
                int shift = extendedKey.charAt(keyIndex++) - 'A';
                char decrypted = (char) (((c - 'a' - shift + 26) % 26) + 'a');
                plain.append(decrypted);

                // Dynamically extend key with decrypted plaintext character
                extendedKey.append(Character.toUpperCase(decrypted));

            } else {
                plain.append(c); // Preserve spaces and special characters
            }
        }
        return plain.toString();
    }
}