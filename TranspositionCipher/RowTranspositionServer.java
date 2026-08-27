import java.io.*;
import java.net.*;

public class RowTranspositionServer {

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

                        System.out.println("Cipher Text   : "
                                + cipherText);

                        System.out.println("Key           : "
                                + key);

                        System.out.println("Plain Message : "
                                + decryptedText);

                    } else {

                        System.out.println(
                                "Invalid message: " + message);
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

        int rows = cipher.length() / key;

        char[][] matrix = new char[rows][key];

        int index = 0;

        // Fill matrix column-wise
        for (int j = 0; j < key; j++) {

            for (int i = 0; i < rows; i++) {

                matrix[i][j] = cipher.charAt(index++);
            }
        }

        StringBuilder plain = new StringBuilder();

        // Read matrix row-wise
        for (int i = 0; i < rows; i++) {

            for (int j = 0; j < key; j++) {

                plain.append(matrix[i][j]);
            }
        }

        // Remove padding X
        while (plain.length() > 0 &&
                plain.charAt(plain.length() - 1) == 'X') {

            plain.deleteCharAt(plain.length() - 1);
        }

        return plain.toString();
    }
}