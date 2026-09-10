import java.io.*;
import java.math.BigInteger;
import java.net.*;
import java.util.Scanner;

public class ElGamalClient {

    public static void main(String[] args) {

        try {

            // ==========================================
            // CONNECT TO SERVER
            // ==========================================

            Socket socket =
                    new Socket("localhost", 5000);

            System.out.println("Connected to Alice (Server).");

            // ==========================================
            // CREATE INPUT/OUTPUT STREAMS
            // ==========================================

            DataInputStream in =
                    new DataInputStream(socket.getInputStream());

            DataOutputStream out =
                    new DataOutputStream(socket.getOutputStream());

            // ==========================================
            // RECEIVE ALICE'S PUBLIC KEY
            // ==========================================

            BigInteger q =
                    new BigInteger(in.readUTF());

            BigInteger a =
                    new BigInteger(in.readUTF());

            BigInteger yA =
                    new BigInteger(in.readUTF());

            System.out.println("\nAlice's Public Key:");
            System.out.println("q = " + q);
            System.out.println("a = " + a);
            System.out.println("YA = " + yA);

            // ==========================================
            // GET MESSAGE
            // ==========================================

            Scanner sc = new Scanner(System.in);

            System.out.print("\nEnter message (number): ");

            BigInteger message =
                    sc.nextBigInteger();

            // Message must be smaller than q
            if (message.compareTo(q) >= 0) {

                System.out.println(
                    "Message must be less than q (" + q + ")"
                );

                socket.close();
                return;
            }

            // ==========================================
            // CHOOSE RANDOM SESSION KEY k
            // ==========================================

            System.out.print(
                "Enter session key k (1 to " +
                q.subtract(BigInteger.TWO) +
                "): "
            );

            BigInteger k =
                    sc.nextBigInteger();

            if (k.compareTo(BigInteger.ONE) < 0 ||
                k.compareTo(q.subtract(BigInteger.ONE)) >= 0) {

                System.out.println("Invalid k.");

                socket.close();
                return;
            }

            System.out.println("\nSession key k = " + k);

            // ==========================================
            // CALCULATE K
            //
            // K = YA^k mod q
            // ==========================================

            BigInteger sharedKey =
                    yA.modPow(k, q);

            System.out.println(
                "K = YA^k mod q = " + sharedKey
            );

            // ==========================================
            // CALCULATE C1
            //
            // C1 = a^k mod q
            // ==========================================

            BigInteger c1 =
                    a.modPow(k, q);

            System.out.println(
                "C1 = a^k mod q = " + c1
            );

            // ==========================================
            // CALCULATE C2
            //
            // C2 = K * M mod q
            // ==========================================

            BigInteger c2 =
                    sharedKey.multiply(message).mod(q);

            System.out.println(
                "C2 = K * M mod q = " + c2
            );

            // ==========================================
            // SEND CIPHERTEXT
            // ==========================================

            out.writeUTF(c1.toString());
            out.writeUTF(c2.toString());

            System.out.println(
                "\nCiphertext (C1, C2) sent to Alice."
            );

            // ==========================================
            // CLOSE
            // ==========================================

            socket.close();
            sc.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}