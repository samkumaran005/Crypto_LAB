import java.io.*;
import java.net.*;

public class ColumnTranspositionClient {

    private Socket s = null;
    private BufferedReader in = null;
    private DataOutputStream out = null;

    public ColumnTranspositionClient(String addr, int port) {

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
                String key = in.readLine().toUpperCase();

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


    static String encrypt(String text, String key) {

        text = text.toUpperCase().replaceAll("\\s+", "");
        key = key.toUpperCase();

        int cols = key.length();

        int rows = (int) Math.ceil(
                (double) text.length() / cols);

        // Add X padding
        while (text.length() < rows * cols) {
            text += "X";
        }

        char[][] matrix = new char[rows][cols];

        int index = 0;

        // Fill row-wise
        for (int i = 0; i < rows; i++) {

            for (int j = 0; j < cols; j++) {

                matrix[i][j] = text.charAt(index++);
            }
        }

        // Get column order based on key
        int[] order = getOrder(key);

        StringBuilder cipher = new StringBuilder();

        // Read columns according to key order
        for (int num = 0; num < cols; num++) {

            int column = order[num];

            for (int row = 0; row < rows; row++) {

                cipher.append(matrix[row][column]);
            }
        }

        return cipher.toString();
    }


    static int[] getOrder(String key) {

        int n = key.length();

        int[] order = new int[n];

        boolean[] used = new boolean[n];

        for (int k = 0; k < n; k++) {

            int minIndex = -1;

            for (int i = 0; i < n; i++) {

                if (!used[i] &&
                        (minIndex == -1 ||
                         key.charAt(i) < key.charAt(minIndex))) {

                    minIndex = i;
                }
            }

            order[k] = minIndex;

            used[minIndex] = true;
        }

        return order;
    }


    public static void main(String[] args) {

        ColumnTranspositionClient c =
                new ColumnTranspositionClient(
                        "127.0.0.1", 5000);
    }
}