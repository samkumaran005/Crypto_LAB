import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.net.*;
import java.util.Base64;
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
            String cipherText = in.readUTF();

            System.out.println("===== DATA RECEIVED =====");
            System.out.println("Key         : " + key);
            System.out.println("Cipher Text : " + cipherText);

            // =========================================================
            // PAUSE HERE: Wait for user to press ENTER before decrypting
            // =========================================================
            System.out.print("\n>>> Press [ENTER] to perform DES decryption... ");
            Scanner console = new Scanner(System.in);
            console.nextLine();

            // Decrypt using DES Algorithm
            String plainText = decryptDES(cipherText, key);

            System.out.println("Original Text : " + plainText);

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
    // DES Decryption
    //----------------------------------------------------
    static String decryptDES(String cipherText, String key) throws Exception {
        SecretKeySpec keySpec = new SecretKeySpec(key.getBytes("UTF-8"), "DES");
        Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, keySpec);

        byte[] decodedBytes = Base64.getDecoder().decode(cipherText);
        byte[] decryptedBytes = cipher.doFinal(decodedBytes);

        return new String(decryptedBytes, "UTF-8");
    }
}