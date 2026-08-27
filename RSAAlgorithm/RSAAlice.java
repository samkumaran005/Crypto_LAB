import java.io.*;
import java.net.*;
import java.util.Scanner;

public class RSAAlice {
    public static void main(String[] args) throws Exception {
        Scanner sc = new Scanner(System.in);

        // Alice chooses primes
        System.out.print("Enter prime p for Alice: ");
        int p = sc.nextInt();
        System.out.print("Enter prime q for Alice: ");
        int q = sc.nextInt();

        int n = p * q;
        int phi = (p - 1) * (q - 1);

        System.out.print("Enter Alice public key Kua : ");
        int Kua = sc.nextInt();
        int Kra = modInverse(Kua, phi);

        System.out.println("Alice Public Key {Kua, n} = {" + Kua + ", " + n + "}");
        System.out.println("Alice Private Key {Kra, n} = {" + Kra + ", " + n + "}");

        try (Socket socket = new Socket("127.0.0.1", 8000);
             DataInputStream in = new DataInputStream(socket.getInputStream());
             DataOutputStream out = new DataOutputStream(socket.getOutputStream())) {

            // Receive Bob’s public key
            int Kub = in.readInt();
            int nb = in.readInt();
            System.out.println("Received Bob Public Key {Kub, n} = {" + Kub + ", " + nb + "}");

            // Send Alice’s public key
            out.writeInt(Kua);
            out.writeInt(n);

            // Alice’s message + signature
            System.out.print("Enter Alice message: ");
            int aliceMsg = sc.nextInt();
            int aliceSig = modPow(aliceMsg, Kra, n);

            out.writeInt(aliceMsg);
            out.writeInt(aliceSig);

            System.out.println("Alice sent message: " + aliceMsg);
            System.out.println("Alice sent signature: " + aliceSig);

            // Receive Bob’s ciphertext
            int bobCipher = in.readInt();
            int bobPlain = modPow(bobCipher, Kra, n);

            System.out.println("\nReceived Bob Ciphertext: " + bobCipher);
            System.out.println("Decrypted Bob Message: " + bobPlain);
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
