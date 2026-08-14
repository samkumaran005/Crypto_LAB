import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Server {

    static final int PORT = 5000;

    public static void main(String args[]) {

        try {

            ServerSocket server = new ServerSocket(PORT);

            System.out.println("Server Started...");
            System.out.println("Waiting for Client...");

            Socket socket = server.accept();

            System.out.println("Client Connected...\n");

            DataInputStream in = new DataInputStream(socket.getInputStream());

            // Receive Matrix Size
            int n = in.readInt();

            // Receive Key Matrix
            int key[][] = new int[n][n];

            for(int i = 0; i < n; i++) {
                for(int j = 0; j < n; j++) {
                    key[i][j] = in.readInt();
                }
            }

            // Receive Cipher Text
            String cipher = in.readUTF();

            System.out.println("===== DATA RECEIVED =====");
            System.out.println("Matrix Size : " + n);

            System.out.println("\nKey Matrix:");
            for(int i = 0; i < n; i++) {
                for(int j = 0; j < n; j++) {
                    System.out.print(key[i][j] + " ");
                }
                System.out.println();
            }

            System.out.println("\nCipher Text : " + cipher);

            // =========================================================
            // PAUSE HERE: Wait for user to press ENTER before decrypting
            // =========================================================
            System.out.print("\n>>> Press [ENTER] to perform decryption... ");
            Scanner console = new Scanner(System.in);
            console.nextLine(); 

            // Decrypt
            String plain = decrypt(cipher, key, n);

            System.out.println("Original Text : " + plain);

            // Clean up resources
            console.close();
            in.close();
            socket.close();
            server.close();

        } catch(Exception e) {
            System.out.println("Error: " + e.getMessage());
        }

    }

    //----------------------------------------------------
    // Hill Cipher Decryption
    //----------------------------------------------------
    static String decrypt(String cipher, int key[][], int n) {

        int inverse[][] = inverseMatrix(key, n);
        String plain = "";

        for(int i = 0; i < cipher.length(); i += n) {

            int cipherVector[] = new int[n];

            for(int j = 0; j < n; j++) {
                cipherVector[j] = cipher.charAt(i + j) - 65;
            }

            int plainVector[] = new int[n];

            for(int r = 0; r < n; r++) {
                plainVector[r] = 0;

                for(int c = 0; c < n; c++) {
                    plainVector[r] += inverse[r][c] * cipherVector[c];
                }

                plainVector[r] %= 26;

                if(plainVector[r] < 0)
                    plainVector[r] += 26;
            }

            for(int j = 0; j < n; j++) {
                plain += (char)(plainVector[j] + 65);
            }
        }

        return plain;
    }

    //----------------------------------------------------
    // Find Inverse Matrix
    //----------------------------------------------------
    static int[][] inverseMatrix(int key[][], int n) {

        int det = determinant(key, n);
        det = ((det % 26) + 26) % 26;

        int detInverse = modInverse(det);

        if(detInverse == -1) {
            System.out.println("Inverse Matrix Not Possible");
            System.exit(0);
        }

        int adj[][] = adjoint(key, n);
        int inverse[][] = new int[n][n];

        for(int i = 0; i < n; i++) {
            for(int j = 0; j < n; j++) {
                inverse[i][j] = (adj[i][j] * detInverse) % 26;

                if(inverse[i][j] < 0)
                    inverse[i][j] += 26;
            }
        }

        return inverse;
    }

    //----------------------------------------------------
    // Modular Multiplicative Inverse
    //----------------------------------------------------
    static int modInverse(int num) {

        num = ((num % 26) + 26) % 26;

        for(int i = 1; i < 26; i++) {
            if((num * i) % 26 == 1)
                return i;
        }

        return -1;
    }

    //----------------------------------------------------
    // Determinant
    //----------------------------------------------------
    static int determinant(int matrix[][], int n) {

        if(n == 1)
            return matrix[0][0];

        if(n == 2) {
            return matrix[0][0] * matrix[1][1] - matrix[0][1] * matrix[1][0];
        }

        int det = 0;
        int sign = 1;

        for(int col = 0; col < n; col++) {
            int minor[][] = getMinor(matrix, 0, col, n);
            det += sign * matrix[0][col] * determinant(minor, n - 1);
            sign = -sign;
        }

        return det;
    }

    //----------------------------------------------------
    // Get Minor Matrix
    //----------------------------------------------------
    static int[][] getMinor(int matrix[][], int row, int col, int n) {

        int minor[][] = new int[n - 1][n - 1];
        int r = 0;

        for(int i = 0; i < n; i++) {
            if(i == row) continue;

            int c = 0;

            for(int j = 0; j < n; j++) {
                if(j == col) continue;

                minor[r][c] = matrix[i][j];
                c++;
            }
            r++;
        }

        return minor;
    }

    //----------------------------------------------------
    // Cofactor Matrix
    //----------------------------------------------------
    static int[][] cofactor(int matrix[][], int n) {

        int cof[][] = new int[n][n];

        if(n == 1) {
            cof[0][0] = 1;
            return cof;
        }

        for(int i = 0; i < n; i++) {
            for(int j = 0; j < n; j++) {

                int minor[][] = getMinor(matrix, i, j, n);
                int sign = ((i + j) % 2 == 0) ? 1 : -1;

                cof[i][j] = sign * determinant(minor, n - 1);
            }
        }

        return cof;
    }

    //----------------------------------------------------
    // Adjoint Matrix
    //----------------------------------------------------
    static int[][] adjoint(int matrix[][], int n) {

        int cof[][] = cofactor(matrix, n);
        int adj[][] = new int[n][n];

        for(int i = 0; i < n; i++) {
            for(int j = 0; j < n; j++) {
                adj[i][j] = cof[j][i];
                adj[i][j] = ((adj[i][j] % 26) + 26) % 26;
            }
        }

        return adj;
    }
}                                   