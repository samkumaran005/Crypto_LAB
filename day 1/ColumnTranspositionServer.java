import java.io.*;
import java.net.*;

public class ColumnTranspositionServer {

    public static void main(String[] args) {

        try (ServerSocket serverSocket =
                     new ServerSocket(5000)) {

            System.out.println(
                    "Server started. Waiting for client...");

            Socket socket = serverSocket.accept();

            System.out.println(
                    "Client connected: "
                    + socket.getInetAddress());

            DataInputStream in =
                    new DataInputStream(
                            socket.getInputStream());

            while (true) {

                try {

                    String message = in.readUTF();

                    if (message.equalsIgnoreCase("Over")) {

                        System.out.println(
                                "Client initiated shutdown.");

                        break;
                    }

                    String[] parts =
                            message.split("\\|", 2);

                    if (parts.length == 2) {

                        String cipherText = parts[0];

                        String key = parts[1];

                        String decryptedText =
                                decrypt(cipherText, key);

                        System.out.println(
                                "Cipher Text   : "
                                + cipherText);

                        System.out.println(
                                "Key           : "
                                + key);

                        System.out.println(
                                "Plain Message : "
                                + decryptedText);

                    } else {

                        System.out.println(
                                "Invalid message: "
                                + message);
                    }

                } catch (EOFException e) {

                    System.out.println(
                            "Client disconnected.");

                    break;
                }
            }

            System.out.println(
                    "Closing connection.");

            socket.close();

        } catch (IOException e) {

            e.printStackTrace();
        }
    }


    static String decrypt(String cipher,
                          String key) {

        key = key.toUpperCase();

        int cols = key.length();

        int rows = cipher.length() / cols;

        char[][] matrix =
                new char[rows][cols];

        int[] order = getOrder(key);

        int index = 0;

        // Fill columns in key order
        for (int num = 0; num < cols; num++) {

            int column = order[num];

            for (int row = 0; row < rows; row++) {

                matrix[row][column] =
                        cipher.charAt(index++);
            }
        }

        StringBuilder plain =
                new StringBuilder();

        // Read row-wise
        for (int row = 0; row < rows; row++) {

            for (int col = 0; col < cols; col++) {

                plain.append(matrix[row][col]);
            }
        }

        // Remove padding X
        while (plain.length() > 0 &&
                plain.charAt(
                        plain.length() - 1) == 'X') {

            plain.deleteCharAt(
                    plain.length() - 1);
        }

        return plain.toString();
    }


    static int[] getOrder(String key) {

        int n = key.length();

        int[] order = new int[n];

        boolean[] used =
                new boolean[n];

        for (int k = 0; k < n; k++) {

            int minIndex = -1;

            for (int i = 0; i < n; i++) {

                if (!used[i] &&
                        (minIndex == -1 ||
                         key.charAt(i) <
                         key.charAt(minIndex))) {

                    minIndex = i;
                }
            }

            order[k] = minIndex;

            used[minIndex] = true;
        }

        return order;
    }
}