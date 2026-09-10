import java.io.*;
import java.math.BigInteger;
import java.net.*;
import java.util.Scanner;

public class DiffieHellmanClient {
    public static void main(String[] args) {
        try {
            Socket socket = new Socket("localhost", 5000);
            System.out.println("Connected to Alice (Server).");

            DataInputStream in = new DataInputStream(socket.getInputStream());
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());

            // Receive public parameters
            BigInteger q = new BigInteger(in.readUTF());
            BigInteger a = new BigInteger(in.readUTF());
            BigInteger yA = new BigInteger(in.readUTF());

            System.out.println("\nReceived from Alice:");
            System.out.println("q = " + q);
            System.out.println("a = " + a);
            System.out.println("YA = " + yA);

            // ==========================================
            // BOB'S PRIVATE KEY (User Input)
            // ==========================================
            Scanner sc = new Scanner(System.in);
            System.out.print("\nEnter Bob's private key XB: ");
            BigInteger xB = sc.nextBigInteger();

            System.out.println("Bob Private Key XB = " + xB);

            // Calculate Bob's public key
            BigInteger yB = a.modPow(xB, q);
            System.out.println("Bob Public Key YB = " + yB);

            // Send YB to Alice
            out.writeUTF(yB.toString());
            System.out.println("Sent YB to Alice.");

            // Calculate shared secret
            BigInteger sharedKey = yA.modPow(xB, q);
            System.out.println("\nShared Secret Key K = " + sharedKey);
            System.out.println("\nDiffie-Hellman Key Exchange Successful!");

            socket.close();
            sc.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
