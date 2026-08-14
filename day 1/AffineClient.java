import java.io.*;
import java.net.*;

public class AffineClient {

    private Socket s = null;
    private BufferedReader in = null;
    private DataOutputStream out = null;

    public AffineClient(String addr, int port) {
       
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
                int a = 0,b=0;
                try {
                    a = Integer.parseInt(in.readLine());
                    b = Integer.parseInt(in.readLine());
                } catch (NumberFormatException e) {
                    System.out.println("Invalid shift key.");
                }

                String encryptedText = encrypt(message, a , b);

                String payload = encryptedText + "|" + a+"|"+b;
                out.writeUTF(payload);
                System.out.println("Sent encrypted message: " + encryptedText + " (Shift: " + a+" "+b + ")");
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

    static String encrypt(String text, int a, int b) {

        text = text.toUpperCase();

        StringBuilder cipher = new StringBuilder();

        for (int i = 0; i < text.length(); i++) {

            char ch = text.charAt(i);

            if (Character.isLetter(ch)) {
                int x = ch - 'A';
                cipher.append((char) (((a * x + b) % 26) + 'A'));
            } else
                cipher.append(ch);
        }

        return cipher.toString();
    }

    public static void main(String[] args) {
        AffineClient c = new AffineClient("127.0.0.1", 5000);
    }
}