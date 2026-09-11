import java.io.*;
import java.net.*;
import java.util.Scanner;
import java.math.BigInteger;

public class RSAAlice {
    public static void main(String[] args) throws Exception {
        Scanner sc = new Scanner(System.in);

        // Alice chooses primes
        System.out.print("Enter prime p for Alice: ");
        BigInteger p = sc.nextBigInteger();
        System.out.print("Enter prime q for Alice: ");
        BigInteger q = sc.nextBigInteger();

        BigInteger n = p.multiply(q);
        BigInteger phi = (p.subtract(BigInteger.ONE)).multiply(q.subtract(BigInteger.ONE));

        System.out.print("Enter Alice public key Kua : ");
        BigInteger Kua = sc.nextBigInteger();
        BigInteger Kra = Kua.modInverse(phi);

        System.out.println("Alice Public Key {Kua, n} = {" + Kua + ", " + n + "}");
        System.out.println("Alice Private Key {Kra, n} = {" + Kra + ", " + n + "}");

        try (Socket socket = new Socket("127.0.0.1", 8000);
             DataInputStream in = new DataInputStream(socket.getInputStream());
             DataOutputStream out = new DataOutputStream(socket.getOutputStream())) {

            // Receive Bob’s public key
            BigInteger Kub = new BigInteger(in.readUTF());
            BigInteger nb = new BigInteger(in.readUTF());
            System.out.println("Received Bob Public Key {Kub, n} = {" + Kub + ", " + nb + "}");

            // Send Alice’s public key
            out.writeUTF(Kua.toString());
            out.writeUTF(n.toString());

            // Alice’s message + signature
            System.out.print("Enter Alice message: ");
            BigInteger aliceMsg = sc.nextBigInteger();
            BigInteger aliceSig = aliceMsg.modPow(Kra, n);

            out.writeUTF(aliceMsg.toString());
            out.writeUTF(aliceSig.toString());

            System.out.println("Alice sent message: " + aliceMsg);
            System.out.println("Alice sent signature: " + aliceSig);

            // Receive Bob’s ciphertext
            BigInteger bobCipher = new BigInteger(in.readUTF());
            BigInteger bobPlain = bobCipher.modPow(Kra, n);

            System.out.println("\nReceived Bob Ciphertext: " + bobCipher);
            System.out.println("Decrypted Bob Message: " + bobPlain);
        }
        sc.close();
    }
}
