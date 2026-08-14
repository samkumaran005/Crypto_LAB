import java.io.*;
import java.net.*;

public class VigenereClient {

    private Socket s = null;
    private BufferedReader in = null;
    private DataOutputStream out = null;

    public VigenereClient(String addr, int port) {

        try {
            s = new Socket(addr, port);
            System.out.println("Connected to server: " + addr + ":" + port);

            in = new BufferedReader(new InputStreamReader(System.in));
            out = new DataOutputStream(s.getOutputStream());

            String message = "";

            while (true) {

                System.out.print("Enter message : ");
                message = in.readLine();

                if (message == null || message.equalsIgnoreCase("Over")) {
                    out.writeUTF("Over");
                    break;
                }

                System.out.print("Enter key : ");
                String key = in.readLine();

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

    static String encrypt(String text, String key) {

        text = text.toUpperCase();
        key = key.toUpperCase();

        StringBuilder cipher = new StringBuilder();

        int j = 0;

        for (int i = 0; i < text.length(); i++) {

            char ch = text.charAt(i);

            if (Character.isLetter(ch)) {

                int p = ch - 'A';
                int k = key.charAt(j % key.length()) - 'A';

                int c = (p + k) % 26;

                cipher.append((char) (c + 'A'));

                j++;

            } else {
                cipher.append(ch);
            }
        }

        return cipher.toString();
    }

    public static void main(String[] args) {

        VigenereClient c =
                new VigenereClient("127.0.0.1", 5000);
    }
}