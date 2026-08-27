import java.io.*;
import java.net.*;
import java.util.Scanner;

public class RSABob {
    public static void main(String[] args) throws Exception {
        Scanner sc = new Scanner(System.in);

        // Bob chooses primes
        System.out.print("Enter prime p for Bob: ");
        int p = sc.nextInt();
        System.out.print("Enter prime q for Bob: ");
        int q = sc.nextInt();

        int n = p * q;
        int phi = (p - 1) * (q - 1);

        System.out.print("Enter Bob public key Kub : ");
        int Kub = sc.nextInt();
        int Krb = modInverse(Kub, phi);

        System.out.println("Bob Public Key {Kub, n} = {" + Kub + ", " + n + "}");
        System.out.println("Bob Private Key {Krb, n} = {" + Krb + ", " + n + "}");

        try (ServerSocket server = new ServerSocket(8000)) {
            System.out.println("Bob waiting for Alice...");
            Socket socket = server.accept();
            DataInputStream in = new DataInputStream(socket.getInputStream());
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());

            // Send Bob’s public key to Alice
            out.writeInt(Kub);
            out.writeInt(n);

            // Receive Alice’s public key
            int Kua = in.readInt();
            int na = in.readInt();

            // Receive Alice’s message + signature
            int aliceMsg = in.readInt();
            int aliceSig = in.readInt();

            System.out.println("\nReceived Alice Message: " + aliceMsg);
            System.out.println("Received Alice Signature: " + aliceSig);

            // Verify Alice’s signature
            int verified = modPow(aliceSig, Kua, na);
            if (verified == aliceMsg) {
                System.out.println("Authentication SUCCESS: Alice message verified.");
            } else {
                System.out.println("Authentication FAILED.");
            }

            // Bob sends his own message to Alice (confidentiality)
            System.out.print("\nEnter Bob message : ");
            int bobMsg = sc.nextInt();
            int bobCipher = modPow(bobMsg, Kua, na);

            out.writeInt(bobCipher);
            System.out.println("Bob sent ciphertext: " + bobCipher);
        }
    }

    static int modPow(int base, int exp, int mod) {
        int result = 1;
        base = base % mod;
        while (exp > 0) {
            if ((exp & 1) == 1) result = (result * base) % mod;
            exp >>= 1;
            base = (base * base) % mod;
        }
        return result;
    }

    static int modInverse(int a, int m) {
        int m0 = m, t, q;
        int x0 = 0, x1 = 1;
        while (a > 1) {
            q = a / m;
            t = m;
            m = a % m; a = t;
            t = x0;
            x0 = x1 - q * x0;
            x1 = t;
        }
        if (x1 < 0) x1 += m0;
        return x1;
    }
}
