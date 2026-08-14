import java.io.*;
import java.net.*;

public class RowTranspositionClient {

    private Socket s = null;
    private BufferedReader in = null;
    private DataOutputStream out = null;

    public RowTranspositionClient(String addr, int port) {

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

                System.out.print("Enter number of columns : ");
                int key = Integer.parseInt(in.readLine());

                if (key <= 0) {
                    System.out.println("Key must be greater than 0.");
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

                System.out.println("Error closing resources: "
                        + i.getMessage());
            }
        }
    }


    static String encrypt(String text, int key) {

        // Remove spaces and convert to uppercase
        text = text.toUpperCase().replaceAll("\\s+", "");

        // Calculate number of rows
        int rows = (int) Math.ceil((double) text.length() / key);

        // Padding
        while (text.length() < rows * key) {
            text += "X";
        }

        char[][] matrix = new char[rows][key];

        int index = 0;

        // Fill matrix row-wise
        for (int i = 0; i < rows; i++) {

            for (int j = 0; j < key; j++) {

                matrix[i][j] = text.charAt(index++);
            }
        }

        StringBuilder cipher = new StringBuilder();

        // Read column-wise
        for (int j = 0; j < key; j++) {

            for (int i = 0; i < rows; i++) {

                cipher.append(matrix[i][j]);
            }
        }

        return cipher.toString();
    }


    public static void main(String[] args) {

        RowTranspositionClient c =
                new RowTranspositionClient("127.0.0.1", 5000);
    }
}