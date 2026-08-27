import java.io.*;
import java.net.*;
import java.util.Scanner;

public class PlayfairClient {
    static final String SERVER_ADDRESS = "127.0.0.1";
    static final int PORT = 6000;

    public static void main(String[] args) {
        try (Socket socket = new Socket(SERVER_ADDRESS, PORT);
             DataOutputStream out = new DataOutputStream(socket.getOutputStream());
             Scanner scanner = new Scanner(System.in)) {

            System.out.println("Connected to server...\n");

            System.out.print("Enter key: ");
            String key = scanner.nextLine();

            System.out.print("Enter plaintext: ");
            String plainText = scanner.nextLine();

            PlayfairCipher cipher = new PlayfairCipher(key);
            String cipherText = cipher.encrypt(plainText);

            System.out.println("Generated ciphertext: " + cipherText);

            out.writeUTF(key);
            out.writeUTF(cipherText);
            out.flush();

            System.out.println("Data sent to server successfully.");

        } catch (Exception e) {
            System.out.println("Client Error: " + e.getMessage());
        }
    }
}

class PlayfairCipher {
    private char[][] matrix;

    public PlayfairCipher(String key) {
        matrix = generateMatrix(key);
    }

    private char[][] generateMatrix(String key) {
        key = key.toUpperCase().replaceAll("[^A-Z]", "").replace("J", "I");
        StringBuilder sb = new StringBuilder();
        boolean[] used = new boolean[26];

        for (char c : key.toCharArray()) {
            if (!used[c - 'A']) {
                sb.append(c);
                used[c - 'A'] = true;
            }
        }
        for (char c = 'A'; c <= 'Z'; c++) {
            if (c == 'J') continue;
            if (!used[c - 'A']) {
                sb.append(c);
                used[c - 'A'] = true;
            }
        }

        char[][] mat = new char[5][5];
        int idx = 0;
        for (int i = 0; i < 5; i++)
            for (int j = 0; j < 5; j++)
                mat[i][j] = sb.charAt(idx++);
        return mat;
    }

    public String encrypt(String text) {
        return process(text, true);
    }

    public String decrypt(String text) {
        return process(text, false);
    }

    private String process(String text, boolean encrypt) {
        text = text.toUpperCase().replaceAll("[^A-Z]", "").replace("J", "I");
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < text.length(); i++) {
            char a = text.charAt(i);
            char b = (i + 1 < text.length()) ? text.charAt(i + 1) : 'X';
            if (a == b) {
                sb.append(a).append('X');
            } else {
                sb.append(a).append(b);
                i++;
            }
        }
        if (sb.length() % 2 != 0) sb.append('X');

        StringBuilder result = new StringBuilder();
        for (int i = 0; i < sb.length(); i += 2) {
            char a = sb.charAt(i), b = sb.charAt(i + 1);
            int[] posA = findPos(a), posB = findPos(b);

            if (posA[0] == posB[0]) {
                result.append(matrix[posA[0]][(posA[1] + (encrypt ? 1 : 4)) % 5]);
                result.append(matrix[posB[0]][(posB[1] + (encrypt ? 1 : 4)) % 5]);
            } else if (posA[1] == posB[1]) {
                result.append(matrix[(posA[0] + (encrypt ? 1 : 4)) % 5][posA[1]]);
                result.append(matrix[(posB[0] + (encrypt ? 1 : 4)) % 5][posB[1]]);
            } else {
                result.append(matrix[posA[0]][posB[1]]);
                result.append(matrix[posB[0]][posA[1]]);
            }
        }
        return result.toString();
    }

    private int[] findPos(char c) {
        for (int i = 0; i < 5; i++)
            for (int j = 0; j < 5; j++)
                if (matrix[i][j] == c) return new int[]{i, j};
        return null;
    }
}
