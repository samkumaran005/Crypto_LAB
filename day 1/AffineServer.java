import java.io.*;
import java.net.*;

public class AffineServer {

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

                    String[] parts = message.split("\\|", 3);
                    int a=0,b=0;
                    if (parts.length == 3) {
                        String cipherText = parts[0];
                        a = Integer.parseInt(parts[1]);
                        b = Integer.parseInt(parts[2]);

                        String decryptedText = decrypt(cipherText, a , b);

                        System.out.println("Ciphertext : " + cipherText);
                        System.out.println("Shift Key           : " + a+" "+b);
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

    static int modInverse(int a) {
        a %= 26;
        for (int i = 1; i < 26; i++)
            if ((a * i) % 26 == 1)
                return i;
        return -1;
    }

     static String decrypt(String cipher, int a, int b) {

        int inv = modInverse(a);

        StringBuilder plain = new StringBuilder();

        for (int i = 0; i < cipher.length(); i++) {

            char ch = cipher.charAt(i);

            if (Character.isLetter(ch)) {
                int y = ch - 'A';
                plain.append((char) (((inv * (y - b + 26)) % 26) + 'A'));
            } else
                plain.append(ch);
        }

        return plain.toString();
    }
    
}