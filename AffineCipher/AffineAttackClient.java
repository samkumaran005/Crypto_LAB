import java.io.*;
import java.net.*;

public class AffineAttackClient {

    private Socket s = null;
    private BufferedReader in = null;
    private DataOutputStream out = null;

    public AffineAttackClient(String addr, int port) {

        try {
            s = new Socket(addr, port);

            System.out.println(
                    "Connected to server: " + addr + ":" + port);

            in = new BufferedReader(
                    new InputStreamReader(System.in));

            out = new DataOutputStream(
                    s.getOutputStream());

            while (true) {

                System.out.print("Enter Cipher Text : ");
                String cipherText = in.readLine();

                if (cipherText == null ||
                        cipherText.equalsIgnoreCase("Over")) {

                    out.writeUTF("Over");
                    break;
                }

                System.out.print(
                        "Enter two known plaintext letters : ");

                String plainPair =
                        in.readLine().toUpperCase();

                System.out.print(
                        "Enter corresponding cipher letters : ");

                String cipherPair =
                        in.readLine().toUpperCase();

                if (plainPair.length() != 2 ||
                        cipherPair.length() != 2) {

                    System.out.println(
                            "Enter exactly two letters.");

                    continue;
                }

                String payload =
                        cipherText + "|" +
                        plainPair + "|" +
                        cipherPair;

                out.writeUTF(payload);

                System.out.println(
                        "Data sent to server.");
            }

        } catch (UnknownHostException u) {

            System.out.println(
                    "Host unknown: " + u.getMessage());

        } catch (IOException i) {

            System.out.println(
                    "I/O Error: " + i.getMessage());

        } finally {

            try {

                if (in != null)
                    in.close();

                if (out != null)
                    out.close();

                if (s != null)
                    s.close();

                System.out.println(
                        "Connection closed.");

            } catch (IOException i) {

                System.out.println(
                        "Error closing resources: "
                                + i.getMessage());
            }
        }
    }

    public static void main(String[] args) {

        AffineAttackClient c =
                new AffineAttackClient(
                        "127.0.0.1", 5000);
    }
}