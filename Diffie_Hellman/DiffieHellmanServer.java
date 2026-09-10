import java.io.*;
import java.math.BigInteger;
import java.net.*;
import java.util.Scanner;

public class DiffieHellmanServer {
    public static void main(String[] args) {
        try {
            Scanner sc = new Scanner(System.in);

            // ==========================================
            // PUBLIC PARAMETERS (User Input)
            // ==========================================
            System.out.print("Enter prime number q: ");
            BigInteger q = sc.nextBigInteger();

            System.out.print("Enter primitive root a: ");
            BigInteger a = sc.nextBigInteger();

            // ==========================================
            // ALICE'S PRIVATE KEY (User Input)
            // ==========================================
            System.out.print("Enter Alice's private key XA: ");
            BigInteger xA = sc.nextBigInteger();

            // Calculate Alice's public key
            BigInteger yA = a.modPow(xA, q);

            System.out.println("\n===== DIFFIE-HELLMAN SERVER =====");
            System.out.println("q = " + q);
            System.out.println("a = " + a);
            System.out.println("Alice Private Key XA = " + xA);
            System.out.println("Alice Public Key YA = " + yA);

            // ==========================================
            // CREATE SERVER SOCKET
            // ==========================================
            ServerSocket serverSocket = new ServerSocket(5000);
            System.out.println("\nWaiting for Bob...");
            Socket socket = serverSocket.accept();
            System.out.println("Bob connected.");

            DataInputStream in = new DataInputStream(socket.getInputStream());
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());

            // Send public parameters and YA
            out.writeUTF(q.toString());
            out.writeUTF(a.toString());
            out.writeUTF(yA.toString());
            System.out.println("Sent q, a and YA to Bob.");

            // Receive Bob's public key
            BigInteger yB = new BigInteger(in.readUTF());
            System.out.println("Received Bob's Public Key YB = " + yB);

            // Calculate shared secret
            BigInteger sharedKey = yB.modPow(xA, q);
            System.out.println("\nShared Secret Key K = " + sharedKey);
            System.out.println("\nDiffie-Hellman Key Exchange Successful!");

            socket.close();
            serverSocket.close();
            sc.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
