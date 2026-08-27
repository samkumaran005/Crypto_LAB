import java.io.*;
import java.net.*;

public class AutokeyServer {

    public static void main(String[] args) {

        try (ServerSocket serverSocket = new ServerSocket(6000)) {

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

        cipher = cipher.toUpperCase().replaceAll("[^A-Z]", "");
        key = key.toUpperCase().replaceAll("[^A-Z]", "");

        StringBuilder plain = new StringBuilder();

        for (int i = 0; i < cipher.length(); i++) {

            int c = cipher.charAt(i) - 'A';

            int k;

            // First use original key
            if (i < key.length()) {

                k = key.charAt(i) - 'A';

            } else {

                // Then use previously decrypted plaintext
                k = plain.charAt(i - key.length()) - 'A';
            }

            int p = (c - k + 26) % 26;

            plain.append((char) (p + 'A'));
        }

        return plain.toString();
    }
}