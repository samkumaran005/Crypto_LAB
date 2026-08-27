import java.util.*;
public class Coprime {
    static int gcdIterative(int a, int b) {
        while (b != 0) {
            int temp = b;
            b = a % b;
            a = temp;
        }
        return a;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter first number: ");
        int num1 = scanner.nextInt();

        System.out.print("Enter second number: ");
        int num2 = scanner.nextInt();

        int resultIterative = gcdIterative(num1, num2);

        if(resultIterative==1) System.out.println(num1 + " and " + num2 + " are Coprimes");
        else System.out.println(num1 + " and " + num2 + " are not Coprimes");
        scanner.close();
    }
}