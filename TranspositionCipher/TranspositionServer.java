import java.io.*;
import java.net.*;

public class TranspositionServer {

    public static void main(String[] args) {

        try (ServerSocket serverSocket = new ServerSocket(5000)) {

            System.out.println("Server started. Waiting for client...");

            Socket socket = serverSocket.accept();

            System.out.println(
                    "Client connected: " + socket.getInetAddress());

            DataInputStream in =
                    new DataInputStream(socket.getInputStream());

            while (true) {

                try {

                    String message = in.readUTF();

                    if (message.equalsIgnoreCase("Over")) {

                        System.out.println("Client initiated shutdown.");
                        break;
                    }

                    String[] parts = message.split("\\|", 2);

                    if (parts.length == 2) {

                        String cipherText = parts[0];
                        int key = Integer.parseInt(parts[1]);

                        String decryptedText =
                                decrypt(cipherText, key);

                        System.out.println("Cipher Text   : " + cipherText);
                        System.out.println("Key           : " + key);
                        System.out.println("Plain Message : " + decryptedText);

                    } else {

                        System.out.println("Invalid message: " + message);
                    }

                } catch (EOFException e) {

                    System.out.println("Client disconnected.");
                    break;
                }
            }

            System.out.println("Closing connection.");

            socket.close();

        } catch (IOException e) {

            e.printStackTrace();
        }
    }

    static String decrypt(String cipher, int key) {

        int n = cipher.length();

        if (key <= 1 || key >= n)
            return cipher;

        char[][] rail = new char[key][n];

        boolean down = true;
        int row = 0;

        // Mark positions
        for (int col = 0; col < n; col++) {

            rail[row][col] = '*';

            if (row == 0)
                down = true;
            else if (row == key - 1)
                down = false;

            if (down)
                row++;
            else
                row--;
        }

        // Fill ciphertext
        int index = 0;

        for (int i = 0; i < key; i++) {

            for (int j = 0; j < n; j++) {

                if (rail[i][j] == '*' && index < n) {

                    rail[i][j] = cipher.charAt(index++);
                }
            }
        }

        // Read in zig-zag order
        StringBuilder plain = new StringBuilder();

        row = 0;
        down = true;

        for (int col = 0; col < n; col++) {

            plain.append(rail[row][col]);

            if (row == 0)
                down = true;
            else if (row == key - 1)
                down = false;

            if (down)
                row++;
            else
                row--;
        }

        return plain.toString();
    }
}