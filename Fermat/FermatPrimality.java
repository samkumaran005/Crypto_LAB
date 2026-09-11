import java.math.BigInteger;
import java.util.Scanner;

public class FermatPrimality {

static boolean fermatTest(BigInteger p) {
    // Test all a from 1 to p-1
    for (BigInteger a = BigInteger.ONE;
         a.compareTo(p) < 0;
         a = a.add(BigInteger.ONE)) {

        // Calculate a^p
        BigInteger power = a.pow(p.intValue());

        // Calculate a^p - a
        BigInteger lhs = power.subtract(a);

        // Display the calculation
        if (p.compareTo(BigInteger.valueOf(100)) < 0) {
            System.out.println(
                "a = " + a + " -> " + a + "^" + p + " - " + a + " = " + lhs
            );
        }

        // Fermat condition: (a^p - a) mod p == 0
        if (!lhs.mod(p).equals(BigInteger.ZERO)) {
            return false;
        }
    }
    return true;
}




    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        System.out.print("Enter p: ");
        BigInteger p = sc.nextBigInteger();

        // Basic checks
        if (p.compareTo(BigInteger.TWO) < 0) {
            System.out.println("Composite");
            sc.close();
            return;
        }

        if (p.equals(BigInteger.TWO)) {
            System.out.println("Prime");
            sc.close();
            return;
        }

        // Even number greater than 2
        if (p.mod(BigInteger.TWO).equals(BigInteger.ZERO)) {
            System.out.println("Composite");
            sc.close();
            return;
        }

        boolean result = fermatTest(p);

        if (result) {
            System.out.println(
                    "\n" + p +
                    " is Probably Prime"
            );
        } else {
            System.out.println(
                    "\n" + p +
                    " is Composite"
            );
        }

        sc.close();
    }
}
