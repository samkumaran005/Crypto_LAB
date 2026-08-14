import java.io.*;
import java.net.*;

public class CServer {

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(5000)) {
            System.out.println("Server started. Waiting for client...");
            
            Socket socket = serverSocket.accept();
            System.out.println("Client connected: " + socket.getInetAddress());

            DataInputStream in = new DataInputStream(socket.getInputStream());
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
                        int shift = Integer.parseInt(parts[1]);

                        String decryptedText = decrypt(cipherText, shift);

                        System.out.println("Ciphertext : " + cipherText);
                        System.out.println("Shift Key           : " + shift);
                        System.out.println("Plain Message   : " + decryptedText);
                    } else {
                        System.out.println(" message: " + message);
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

    public static String decrypt(String text, int shift) {
        shift = -shift;
        StringBuilder result = new StringBuilder();

        shift = ((shift % 26) + 26) % 26;

        for (char ch : text.toCharArray()) {
            if (Character.isLetter(ch)) {
         
                char base = Character.isUpperCase(ch) ? 'A' : 'a';

                char shifted = (char) ((ch - base + shift) % 26 + base);
                result.append(shifted);
            } else {
            
                result.append(ch);
            }
        }

        return result.toString();
    }
}