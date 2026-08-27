import java.io.*;
import java.net.*;

public class TranspositionClient {

    private Socket s = null;
    private BufferedReader in = null;
    private DataOutputStream out = null;

    public TranspositionClient(String addr, int port) {

        try {
            s = new Socket(addr, port);
            System.out.println("Connected to server: " + addr + ":" + port);

            in = new BufferedReader(new InputStreamReader(System.in));
            out = new DataOutputStream(s.getOutputStream());

            while (true) {

                System.out.print("Enter message : ");
                String message = in.readLine();

                if (message == null || message.equalsIgnoreCase("Over")) {
                    out.writeUTF("Over");
                    break;
                }

                System.out.print("Enter key (number of rails) : ");
                int key = Integer.parseInt(in.readLine());

                if (key < 2) {
                    System.out.println("Key must be at least 2.");
                    continue;
                }

                String encryptedText = encrypt(message, key);

                String payload = encryptedText + "|" + key;

                out.writeUTF(payload);

                System.out.println("Encrypted Message : " + encryptedText);
                System.out.println("Key               : " + key);
            }

        } catch (UnknownHostException u) {

            System.out.println("Host unknown: " + u.getMessage());

        } catch (IOException i) {

            System.out.println("I/O Error: " + i.getMessage());

        } finally {

            try {

                if (in != null)
                    in.close();

                if (out != null)
                    out.close();

                if (s != null)
                    s.close();

                System.out.println("Connection closed.");

            } catch (IOException i) {

                System.out.println("Error closing resources: " + i.getMessage());
            }
        }
    }

    static String encrypt(String text, int key) {

        text = text.toUpperCase().replaceAll("\\s+", "");

        if (key <= 1 || key >= text.length())
            return text;

        StringBuilder[] rail = new StringBuilder[key];

        for (int i = 0; i < key; i++) {
            rail[i] = new StringBuilder();
        }

        int row = 0;
        boolean down = true;

        for (int i = 0; i < text.length(); i++) {

            rail[row].append(text.charAt(i));

            if (row == 0)
                down = true;
            else if (row == key - 1)
                down = false;

            if (down)
                row++;
            else
                row--;
        }

        StringBuilder cipher = new StringBuilder();

        for (int i = 0; i < key; i++) {
            cipher.append(rail[i]);
        }

        return cipher.toString();
    }

    public static void main(String[] args) {

        TranspositionClient c =
                new TranspositionClient("127.0.0.1", 5000);
    }
}