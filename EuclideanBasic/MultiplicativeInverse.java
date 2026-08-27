import java.util.Scanner;

public class MultiplicativeInverse {
    static int extendedGCD(int a, int b, int[] x, int[] y) {
        if (b == 0) {
            x[0] = 1;
            y[0] = 0;
            return a;
        }
        int[] x1 = new int[1];
        int[] y1 = new int[1];
        int gcd = extendedGCD(b, a % b, x1, y1);
        x[0] = y1[0];
        y[0] = x1[0] - (a / b) * y1[0];
        return gcd;
    }

    static int multiplicativeInverse(int a, int m) {
        int[] x = new int[1];
        int[] y = new int[1];
        int gcd = extendedGCD(a, m, x, y);
        if (gcd != 1) {
            return -1;
        }
        return (x[0] % m + m) % m;
    }

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);
        System.out.print("Enter number : ");
        int a = sc.nextInt();
        System.out.print("Enter modulo : ");
        int m = sc.nextInt();
        int inverse = multiplicativeInverse(a, m);
        if (inverse == -1) {
            System.out.println("Multiplicative inverse does not exist.");
        } else {
            System.out.println("Multiplicative inverse = " + inverse);
        }
        sc.close();
    }
}