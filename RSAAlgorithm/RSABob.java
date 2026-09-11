import java.io.*;
import java.math.BigInteger;
import java.net.*;
import java.util.Scanner;

public class RSABob {
    public static void main(String[] args) throws Exception {
        Scanner sc = new Scanner(System.in);

        // Bob chooses primes
        System.out.print("Enter prime p for Bob: ");
        BigInteger p = sc.nextBigInteger();
        System.out.print("Enter prime q for Bob: ");
        BigInteger q = sc.nextBigInteger();

        BigInteger n = p.multiply(q);
        BigInteger phi = (p.subtract(BigInteger.ONE)).multiply(q.subtract(BigInteger.ONE));

        System.out.print("Enter Bob public key Kub : ");
        BigInteger Kub = sc.nextBigInteger();
        BigInteger Krb = Kub.modInverse(phi);

        System.out.println("Bob Public Key {Kub, n} = {" + Kub + ", " + n + "}");
        System.out.println("Bob Private Key {Krb, n} = {" + Krb + ", " + n + "}");

        try (ServerSocket server = new ServerSocket(8000)) {
            System.out.println("Bob waiting for Alice...");
            Socket socket = server.accept();
            DataInputStream in = new DataInputStream(socket.getInputStream());
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());

            // Send Bob’s public key to Alice
            out.writeUTF(Kub.toString());
            out.writeUTF(n.toString());

            // Receive Alice’s public key
            BigInteger Kua = new BigInteger(in.readUTF());
            BigInteger na = new BigInteger(in.readUTF());

            // Receive Alice’s message + signature
            BigInteger aliceMsg = new BigInteger(in.readUTF());
            BigInteger aliceSig = new BigInteger(in.readUTF());

            System.out.println("\nReceived Alice Message: " + aliceMsg);
            System.out.println("Received Alice Signature: " + aliceSig);

            // Verify Alice’s signature
            BigInteger verified = aliceSig.modPow(Kua, na);
            System.out.println("Verification result = " + verified);

            if (verified.equals(aliceMsg)) {
                System.out.println("Authentication SUCCESS: Alice message verified.");
            } else {
                System.out.println("Authentication FAILED.");
            }

            // Bob sends his own message to Alice (confidentiality)
            System.out.print("\nEnter Bob message : ");
            BigInteger bobMsg = sc.nextBigInteger();
            BigInteger bobCipher = bobMsg.modPow(Kua, na);

            out.writeUTF(bobCipher.toString());
            System.out.println("Bob sent ciphertext: " + bobCipher);
        }
        sc.close();
    }
}
