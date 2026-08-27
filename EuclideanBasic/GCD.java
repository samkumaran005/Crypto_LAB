import java.util.*;
public class GCD {
    static int gcdIterative(int a, int b) {
        while (b != 0) {
            int temp = b;
            b = a % b;
            a = temp;
        }
        return a;
    }
    static int gcdRecursive(int a, int b) {
        if (b == 0) {
            return a;
        }
        return gcdRecursive(b, a % b);
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter first number: ");
        int num1 = scanner.nextInt();

        System.out.print("Enter second number: ");
        int num2 = scanner.nextInt();

        int resultIterative = gcdIterative(num1, num2);
        int resultRecursive = gcdRecursive(num1, num2);

        System.out.println("\nGCD of " + num1 + " and " + num2 + " (Iterative): " + resultIterative);
        System.out.println("GCD of " + num1 + " and " + num2 + " (Recursive): " + resultRecursive);
        scanner.close();
    }
}
