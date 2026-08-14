
import java.io.*;
import java.net.*;
import java.util.*;

public class HillClient {

    // Encrypt plaintext using Hill Cipher
    static String encrypt(String plaintext, int[][] key, int n) {

        plaintext = plaintext.replaceAll("[^A-Za-z]", "").toUpperCase();

        while (plaintext.length() % n != 0) {
            plaintext += "X";
        }

        StringBuilder cipher = new StringBuilder();

        for (int i = 0; i < plaintext.length(); i += n) {

            int[] vector = new int[n];

            for (int j = 0; j < n; j++) {
                vector[j] = plaintext.charAt(i + j) - 'A';
            }

            int[] result = new int[n];

            for (int row = 0; row < n; row++) {

                int sum = 0;

                for (int col = 0; col < n; col++) {
                    sum += key[row][col] * vector[col];
                }

                result[row] = ((sum % 26) + 26) % 26;
            }

            for (int value : result) {
                cipher.append((char) (value + 'A'));
            }
        }

        return cipher.toString();
    }

    public static void main(String[] args) {

        final String SERVER = "127.0.0.1";
        final int PORT = 5000;

        try (
                Socket socket = new Socket(SERVER, PORT);
                DataInputStream in = new DataInputStream(socket.getInputStream());
                DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                Scanner sc = new Scanner(System.in);
        ) {

            System.out.println("Connected to Server.");

            // Matrix dimension
            System.out.print("Enter matrix dimension (n): ");
            int n = sc.nextInt();

            out.writeInt(n);

            // Key Matrix
            int[][] key = new int[n][n];

            System.out.println("Enter " + n + " x " + n + " Key Matrix:");

            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    key[i][j] = sc.nextInt() % 26;
                    if (key[i][j] < 0)
                        key[i][j] += 26;

                    out.writeInt(key[i][j]);
                }
            }

            sc.nextLine();

            // Plaintext
            System.out.print("Enter Plaintext: ");
            String plaintext = sc.nextLine();

            // Encrypt
            String ciphertext = encrypt(plaintext, key, n);

            System.out.println("Encrypted Text : " + ciphertext);

            // Send ciphertext
            out.writeUTF(ciphertext);
            out.flush();

            // Receive decrypted text
            String decrypted = in.readUTF();

            System.out.println("Server Decrypted Text : " + decrypted);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}