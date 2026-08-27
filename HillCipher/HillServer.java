import java.io.*;
import java.net.*;

public class HillServer {

    // Modular inverse of a number mod 26
    static int modInverse(int a, int m) {
        a = ((a % m) + m) % m;
        for (int x = 1; x < m; x++) {
            if ((a * x) % m == 1)
                return x;
        }
        return -1;
    }

    // Determinant of matrix
    static int determinant(int[][] matrix, int n) {

        if (n == 1)
            return matrix[0][0];

        if (n == 2)
            return matrix[0][0] * matrix[1][1]
                    - matrix[0][1] * matrix[1][0];

        int det = 0;

        for (int col = 0; col < n; col++) {

            int[][] sub = new int[n - 1][n - 1];

            for (int i = 1; i < n; i++) {

                int subCol = 0;

                for (int j = 0; j < n; j++) {

                    if (j == col)
                        continue;

                    sub[i - 1][subCol++] = matrix[i][j];
                }
            }

            det += (col % 2 == 0 ? 1 : -1)
                    * matrix[0][col]
                    * determinant(sub, n - 1);
        }

        return det;
    }

    // Adjugate Matrix
    static int[][] adjugate(int[][] matrix, int n) {

        int[][] adj = new int[n][n];

        if (n == 1) {
            adj[0][0] = 1;
            return adj;
        }

        for (int i = 0; i < n; i++) {

            for (int j = 0; j < n; j++) {

                int[][] sub = new int[n - 1][n - 1];

                int rowSub = 0;

                for (int r = 0; r < n; r++) {

                    if (r == i)
                        continue;

                    int colSub = 0;

                    for (int c = 0; c < n; c++) {

                        if (c == j)
                            continue;

                        sub[rowSub][colSub++] = matrix[r][c];
                    }

                    rowSub++;
                }

                int sign = ((i + j) % 2 == 0) ? 1 : -1;

                adj[j][i] = (sign * determinant(sub, n - 1)) % 26;
            }
        }

        return adj;
    }

    // Inverse Matrix
    static int[][] inverseMatrix(int[][] key, int n) {

        int det = determinant(key, n);
        det = ((det % 26) + 26) % 26;

        int detInv = modInverse(det, 26);

        if (detInv == -1) {
            throw new IllegalArgumentException(
                    "Key matrix is NOT invertible modulo 26.");
        }

        int[][] adj = adjugate(key, n);
        int[][] inv = new int[n][n];

        for (int i = 0; i < n; i++) {

            for (int j = 0; j < n; j++) {

                inv[i][j] = ((adj[i][j] * detInv) % 26 + 26) % 26;
            }
        }

        return inv;
    }

    // Decrypt
    static String decrypt(String ciphertext, int[][] invKey, int n) {

        StringBuilder plain = new StringBuilder();

        for (int i = 0; i < ciphertext.length(); i += n) {

            int[] block = new int[n];

            for (int j = 0; j < n; j++) {

                if (i + j < ciphertext.length())
                    block[j] = ciphertext.charAt(i + j) - 'A';
                else
                    block[j] = 'X' - 'A';
            }

            for (int r = 0; r < n; r++) {

                int sum = 0;

                for (int c = 0; c < n; c++) {

                    sum += invKey[r][c] * block[c];
                }

                plain.append((char) (((sum % 26) + 26) % 26 + 'A'));
            }
        }

        return plain.toString();
    }

    public static void main(String[] args) {

        try (
                ServerSocket server = new ServerSocket(5000)) {

            System.out.println("Server Started...");
            System.out.println("Waiting for Client...");

            Socket socket = server.accept();

            System.out.println("Client Connected.");

            DataInputStream in =
                    new DataInputStream(socket.getInputStream());

            DataOutputStream out =
                    new DataOutputStream(socket.getOutputStream());

            // Read matrix size
            int n = in.readInt();

            int[][] key = new int[n][n];

            System.out.println("\nReceived Key Matrix:");

            for (int i = 0; i < n; i++) {

                for (int j = 0; j < n; j++) {

                    key[i][j] = in.readInt();
                    System.out.print(key[i][j] + " ");
                }

                System.out.println();
            }

            // Read Cipher Text
            String ciphertext = in.readUTF();

            System.out.println("\nCipher Text : " + ciphertext);

            // Compute inverse
            int[][] invKey = inverseMatrix(key, n);

            System.out.println("\nInverse Key Matrix:");

            for (int i = 0; i < n; i++) {

                for (int j = 0; j < n; j++) {

                    System.out.print(invKey[i][j] + " ");
                }

                System.out.println();
            }

            // Decrypt
            String plaintext = decrypt(ciphertext, invKey, n);

            System.out.println("\nDecrypted Text : " + plaintext);

            // Send decrypted text to client
            out.writeUTF(plaintext);
            out.flush();

            in.close();
            out.close();
            socket.close();

        } catch (Exception e) {

            e.printStackTrace();
        }
    }
}