import java.math.BigInteger;
import java.util.Scanner;

public class MillerRabin {

    static boolean isPrime(BigInteger n, BigInteger a) {

        // Step 1: n - 1 = 2^s * m
        BigInteger m = n.subtract(BigInteger.ONE);
        int s = 0;

        while (m.mod(BigInteger.TWO).equals(BigInteger.ZERO)) {
            m = m.divide(BigInteger.TWO);
            s++;
        }

        System.out.println("n - 1 = 2^" + s + " * " + m);

        // Step 2: b0 = a^m mod n
        BigInteger b = a.modPow(m, n);

        System.out.println("b0 = " + b);

        // If b0 = 1 or n-1, probably prime
        if (b.equals(BigInteger.ONE) ||
            b.equals(n.subtract(BigInteger.ONE))) {
            return true;
        }

        // Step 3: bi = bi-1^2 mod n
        for (int i = 1; i < s; i++) {

            b = b.multiply(b).mod(n);

            System.out.println("b" + i + " = " + b);

            // If bi = n - 1, probably prime
            if (b.equals(n.subtract(BigInteger.ONE))) {
                return true;
            }

            // If bi becomes 1 before n-1, composite
            if (b.equals(BigInteger.ONE)) {
                return false;
            }
        }

        return false;
    }

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        System.out.print("Enter number n: ");
        BigInteger n = sc.nextBigInteger();

        System.out.print("Enter base a: ");
        BigInteger a = sc.nextBigInteger();

        // Basic checks
        if (n.compareTo(BigInteger.TWO) < 0) {
            System.out.println("Composite");
            return;
        }

        if (n.equals(BigInteger.TWO) ||
            n.equals(BigInteger.valueOf(3))) {
            System.out.println("Probably Prime");
            return;
        }

        if (n.mod(BigInteger.TWO).equals(BigInteger.ZERO)) {
            System.out.println("Composite");
            return;
        }

        // Check validity of a
        if (a.compareTo(BigInteger.TWO) < 0 ||
            a.compareTo(n.subtract(BigInteger.TWO)) > 0) {

            System.out.println("Invalid value of a");
            return;
        }

        // Miller-Rabin test
        boolean result = isPrime(n, a);

        if (result) {
            System.out.println("n is Probably Prime");
        } else {
            System.out.println("n is Composite");
        }

        sc.close();
    }
}