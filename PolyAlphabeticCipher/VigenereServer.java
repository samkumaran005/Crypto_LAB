import java.io.*;
import java.net.*;

public class VigenereServer {

    public static void main(String[] args) {

        try (ServerSocket serverSocket = new ServerSocket(6000)) {

            System.out.println("Server started. Waiting for client...");

            Socket socket = serverSocket.accept();

            System.out.println(
                    "Client connected: " + socket.getInetAddress());

            DataInputStream in =
                    new DataInputStream(socket.getInputStream());

            String message = "";

            while (true) {

                try {

                    message = in.readUTF();

                    if (message.equalsIgnoreCase("Over")) {

                        System.out.println("Client initiated shutdown.");
                        break;
                    }

                    String[] parts = message.split("\\|", 2);

                    if (parts.length == 2) {

                        String cipherText = parts[0];
                        String key = parts[1];

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

    static String decrypt(String cipher, String key) {

        cipher = cipher.toUpperCase();
        key = key.toUpperCase();

        StringBuilder plain = new StringBuilder();

        int j = 0;

        for (int i = 0; i < cipher.length(); i++) {

            char ch = cipher.charAt(i);

            if (Character.isLetter(ch)) {

                int c = ch - 'A';
                int k = key.charAt(j % key.length()) - 'A';

                int p = (c - k + 26) % 26;

                plain.append((char) (p + 'A'));

                j++;

            } else {

                plain.append(ch);
            }
        }

        return plain.toString();
    }
}