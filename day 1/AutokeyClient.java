import java.io.*;
import java.net.*;

public class AutokeyClient {

    private Socket s = null;
    private BufferedReader in = null;
    private DataOutputStream out = null;

    public AutokeyClient(String addr, int port) {

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

        text = text.toUpperCase().replaceAll("[^A-Z]", "");
        key = key.toUpperCase().replaceAll("[^A-Z]", "");

        // Autokey = Initial Key + Plaintext
        String keyStream = key + text;

        StringBuilder cipher = new StringBuilder();

        for (int i = 0; i < text.length(); i++) {

            int p = text.charAt(i) - 'A';
            int k = keyStream.charAt(i) - 'A';

            int c = (p + k) % 26;

            cipher.append((char) (c + 'A'));
        }

        return cipher.toString();
    }


    public static void main(String[] args) {

        AutokeyClient c =
                new AutokeyClient("127.0.0.1", 5000);
    }
}