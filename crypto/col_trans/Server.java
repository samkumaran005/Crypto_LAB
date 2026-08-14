import java.io.*;
import java.net.*;
import java.util.Arrays;
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

            // Decrypt using Columnar Transposition Cipher
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
    // Columnar Transposition Cipher Decryption
    //----------------------------------------------------
    static String decrypt(String cipher, String key) {
        key = key.toUpperCase();
        int cols = key.length();
        int rows = cipher.length() / cols;

        char[][] grid = new char[rows][cols];
        int[] order = getKeyOrder(key);

        int cipherIdx = 0;

        // Populate grid columns in alphabetical order of key
        for (int colIndex : order) {
            for (int r = 0; r < rows; r++) {
                grid[r][colIndex] = cipher.charAt(cipherIdx++);
            }
        }

        // Read grid row by row to recreate original text
        StringBuilder plain = new StringBuilder();
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                plain.append(grid[r][c]);
            }
        }

        return plain.toString();
    }

    // Helper: Find column reading order by sorting key characters alphabetically
    static int[] getKeyOrder(String key) {
        int n = key.length();
        Integer[] indices = new Integer[n];
        for (int i = 0; i < n; i++) indices[i] = i;

        Arrays.sort(indices, (a, b) -> {
            if (key.charAt(a) != key.charAt(b)) {
                return Character.compare(key.charAt(a), key.charAt(b));
            }
            return Integer.compare(a, b);
        });

        int[] order = new int[n];
        for (int i = 0; i < n; i++) {
            order[i] = indices[i];
        }
        return order;
    }
}