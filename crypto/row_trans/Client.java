import java.io.*;
import java.net.*;
import java.util.Arrays;
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

            // Input Key (Keyword)
            System.out.print("Enter Keyword   : ");
            String key = scanner.nextLine();

            // Encrypt using Row Transposition Cipher
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
    // Row Transposition Cipher Encryption
    //----------------------------------------------------
    static String encrypt(String text, String key) {
        String cleanText = text.replaceAll("[^a-zA-Z]", "").toUpperCase();
        key = key.toUpperCase();

        int cols = key.length();
        int rows = (int) Math.ceil((double) cleanText.length() / cols);
        int totalLength = rows * cols;

        // Pad with 'X' to complete the matrix grid
        StringBuilder paddedText = new StringBuilder(cleanText);
        while (paddedText.length() < totalLength) {
            paddedText.append('X');
        }

        // Fill grid row by row
        char[][] grid = new char[rows][cols];
        int idx = 0;
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                grid[r][c] = paddedText.charAt(idx++);
            }
        }

        // Get column reading order from keyword
        int[] order = getKeyOrder(key);
        StringBuilder cipher = new StringBuilder();

        // Read matrix column by column in key order
        for (int colIndex : order) {
            for (int r = 0; r < rows; r++) {
                cipher.append(grid[r][colIndex]);
            }
        }

        return cipher.toString();
    }

    // Helper: Determine column reading order based on alphabetical sort of key letters
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