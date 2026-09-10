import java.io.*;
import java.math.BigInteger;
import java.net.*;
import java.util.Scanner;

public class ElGamalServer {

    public static void main(String[] args) {

        try {
                // ==========================================
                // ELGAMAL KEY GENERATION (User Input)
                // ==========================================
                Scanner sc = new Scanner(System.in);

                // Large prime q
                System.out.print("Enter large prime q: ");
                BigInteger q = sc.nextBigInteger();

                // Primitive root of q
                System.out.print("Enter primitive root a: ");
                BigInteger a = sc.nextBigInteger();

                // Alice's private key
                System.out.print("Enter Alice's private key XA: ");
                BigInteger xA = sc.nextBigInteger();

                // YA = a^XA mod q
                BigInteger yA = a.modPow(xA, q);

                System.out.println("\n========== ELGAMAL SERVER ==========");
                System.out.println("q = " + q);
                System.out.println("a = " + a);
                System.out.println("Alice Private Key (XA) = " + xA);
                System.out.println("Alice Public Key (YA) = " + yA);

            // ==========================================
            // CREATE SERVER SOCKET
            // ==========================================

            ServerSocket serverSocket = new ServerSocket(5000);

            System.out.println("\nWaiting for Bob...");

            Socket socket = serverSocket.accept();

            System.out.println("Bob connected.");

            // ==========================================
            // CREATE INPUT/OUTPUT STREAMS
            // ==========================================

            DataInputStream in =
                    new DataInputStream(socket.getInputStream());

            DataOutputStream out =
                    new DataOutputStream(socket.getOutputStream());

            // ==========================================
            // SEND PUBLIC KEY TO BOB
            // ==========================================

            out.writeUTF(q.toString());
            out.writeUTF(a.toString());
            out.writeUTF(yA.toString());

            System.out.println("\nPublic key sent to Bob.");

            // ==========================================
            // RECEIVE CIPHERTEXT
            // ==========================================

            BigInteger c1 =
                    new BigInteger(in.readUTF());

            BigInteger c2 =
                    new BigInteger(in.readUTF());

            System.out.println("\nCiphertext received:");
            System.out.println("C1 = " + c1);
            System.out.println("C2 = " + c2);

            // ==========================================
            // DECRYPTION
            // K = C1^XA mod q
            // ==========================================

            BigInteger k =
                    c1.modPow(xA, q);

            System.out.println("Calculated K = " + k);

            // ==========================================
            // Calculate K inverse
            // K^-1 mod q
            // ==========================================

            BigInteger kInverse =
                    k.modInverse(q);

            System.out.println("K inverse = " + kInverse);

            // ==========================================
            // M = C2 * K^-1 mod q
            // ==========================================

            BigInteger message =
                    c2.multiply(kInverse).mod(q);

            System.out.println("Decrypted Message = " + message);

            // ==========================================
            // CLOSE CONNECTION
            // ==========================================

            socket.close();
            serverSocket.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}