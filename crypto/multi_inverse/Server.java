import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Server {

    static final int PORT = 5000;

    public static void main(String[] args) {

        try {
            ServerSocket server = new ServerSocket(PORT);

            System.out.println("Server Started...");
            System.out.println("Waiting for Client...");

            Socket socket = server.accept();

            System.out.println("Client Connected...\n");

            DataInputStream in = new DataInputStream(socket.getInputStream());

            // Receive Number (a) and Modulus (m)
            int a = in.readInt();
            int m = in.readInt();

            System.out.println("===== DATA RECEIVED =====");
            System.out.println("Number (a)  : " + a);
            System.out.println("Modulus (m) : " + m);

            // =========================================================
            // PAUSE HERE: Wait for user to press ENTER before computing
            // =========================================================
            System.out.print("\n>>> Press [ENTER] to compute Multiplicative Inverse... ");
            Scanner console = new Scanner(System.in);
            console.nextLine();

            // Compute Multiplicative Inverse using Extended Euclidean Algorithm
            int result = extendedEuclideanInverse(a, m);

            if (result == -1) {
                System.out.println("Multiplicative Inverse does NOT exist (GCD != 1).");
            } else {
                System.out.println("Multiplicative Inverse (" + a + "^-1 mod " + m + ") = " + result);
                System.out.println("Verification: (" + a + " * " + result + ") % " + m + " = " + ((long)a * result % m));
            }

            // Clean up resources
            console.close();
            in.close();
            socket.close();
            server.close();

        } catch (Exception e) {
            System.out.println("Server Error: " + e.getMessage());
        }

    }

    //------------------------------------------------------------------
    // Extended Euclidean Algorithm for Modular Multiplicative Inverse
    // Solves for x in: (a * x) % m = 1
    //------------------------------------------------------------------
    static int extendedEuclideanInverse(int a, int m) {
        int m0 = m;
        int y = 0, x = 1;

        if (m == 1) return 0;

        // Check if GCD is 1
        if (gcd(a, m) != 1) return -1;

        while (a > 1) {
            // q is quotient
            int q = a / m;
            int t = m;

            // m is remainder
            m = a % m;
            a = t;
            t = y;

            // Update x and y
            y = x - q * y;
            x = t;
        }

        // Make x positive if it is negative
        if (x < 0) {
            x = x + m0;
        }

        return x;
    }

    // Helper: Calculate Greatest Common Divisor (GCD)
    static int gcd(int a, int b) {
        while (b != 0) {
            int temp = b;
            b = a % b;
            a = temp;
        }
        return a;
    }
}