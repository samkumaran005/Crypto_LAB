import java.io.*;
import java.net.*;

public class CaesarCipherClient {

    private Socket s = null;
    private BufferedReader in = null;
    private DataOutputStream out = null;

    public CaesarCipherClient(String addr, int port) {
       
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
                int shift = 0;
                try {
                    shift = Integer.parseInt(in.readLine());
                } catch (NumberFormatException e) {
                    System.out.println("Invalid shift key.");
                }

                String encryptedText = encrypt(message, shift);

                String payload = encryptedText + "|" + shift;
                out.writeUTF(payload);
                System.out.println("Sent encrypted message: " + encryptedText + " (Shift: " + shift + ")");
            }

        } catch (UnknownHostException u) {
            System.out.println("Host unknown: " + u.getMessage());
        } catch (IOException i) {
            System.out.println("I/O Error: " + i.getMessage());
        } finally {
            
            try {
                if (in != null) in.close();
                if (out != null) out.close();
                if (s != null) s.close();
                System.out.println("Connection closed.");
            } catch (IOException i) {
                System.out.println("Error closing resources: " + i.getMessage());
            }
        }
    }

    public static String encrypt(String text, int shift) {
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

    public static void main(String[] args) {
        CaesarCipherClient c = new CaesarCipherClient("127.0.0.1", 5000);
    }
}