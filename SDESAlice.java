
import java.io.*;
import java.net.*;
import java.util.*;

public class SDESAlice {

    static int[] P10  = {3,5,2,7,4,10,1,9,8,6};
    static int[] P8   = {6,3,7,4,8,5,10,9};
    static int[] IP   = {2,6,3,1,4,8,5,7};
    static int[] IP1  = {4,1,3,5,7,2,8,6};
    static int[] EP   = {4,1,2,3,2,3,4,1};
    static int[] P4   = {2,4,3,1};

    static int[][] S0 = {
        {1,0,3,2},
        {3,2,1,0},
        {0,2,1,3},
        {3,1,3,2}
    };

    static int[][] S1 = {
        {0,1,2,3},
        {2,0,1,3},
        {3,0,1,0},
        {2,1,0,3}
    };

    static String permute(String s, int[] p) {
        StringBuilder r = new StringBuilder();

        for (int i : p)
            r.append(s.charAt(i - 1));

        return r.toString();
    }

    static String leftShift(String s, int n) {
        return s.substring(n) + s.substring(0, n);
    }

    static String xor(String a, String b) {
        StringBuilder r = new StringBuilder();

        for (int i = 0; i < a.length(); i++)
            r.append(a.charAt(i) == b.charAt(i) ? '0' : '1');

        return r.toString();
    }

    static String sbox(String bits, int[][] box) {

        int row = Integer.parseInt(
                "" + bits.charAt(0) + bits.charAt(3), 2);

        int col = Integer.parseInt(
                "" + bits.charAt(1) + bits.charAt(2), 2);

        String result = Integer.toBinaryString(box[row][col]);

        return result.length() == 1 ? "0" + result : result;
    }

    static String function(String bits, String key) {

        String left = bits.substring(0, 4);
        String right = bits.substring(4);

        String ep = permute(right, EP);

        String x = xor(ep, key);

        String s0 = sbox(x.substring(0, 4), S0);
        String s1 = sbox(x.substring(4), S1);

        String p4 = permute(s0 + s1, P4);

        left = xor(left, p4);

        return left + right;
    }

    static String swap(String s) {
        return s.substring(4) + s.substring(0, 4);
    }

    static String[] generateKeys(String key) {

        String p10 = permute(key, P10);

        String left = p10.substring(0, 5);
        String right = p10.substring(5);

        // LS-1
        left = leftShift(left, 1);
        right = leftShift(right, 1);

        String k1 = permute(left + right, P8);

        // LS-2
        left = leftShift(left, 2);
        right = leftShift(right, 2);

        String k2 = permute(left + right, P8);

        return new String[]{k1, k2};
    }

    static String encrypt(String plaintext, String key) {

        String[] keys = generateKeys(key);

        String k1 = keys[0];
        String k2 = keys[1];

        String result = permute(plaintext, IP);

        result = function(result, k1);

        result = swap(result);

        result = function(result, k2);

        result = permute(result, IP1);

        return result;
    }

    public static void main(String[] args) throws Exception {

        Scanner sc = new Scanner(System.in);

        String key;
            while (true) {
            System.out.print("Enter 10-bit key: ");
            key = sc.next();

            if (key.length() == 10) {
                break;
            } else if (key.length() > 10) {
                System.out.println("Enter a maximum of 10 characters for key.");
            } else {
                System.out.println("Enter a minimum of 10 characters for key.");
            }
        }

        String plaintext;
        while (true) {
            System.out.print("Enter 8-bit plaintext: ");
            plaintext = sc.next();

            if (plaintext.length() == 8) {
                break;
            } else if (plaintext.length() > 8) {
                System.out.println("Enter a maximum of 8 characters for Plaintext.");
            } else {
                System.out.println("Enter a minimum of 8 characters for Plaintext.");
            }
        }

        String cipher = encrypt(plaintext, key);

        System.out.println("Ciphertext: " + cipher);

        Socket socket = new Socket("localhost", 6000);

        DataOutputStream out =
                new DataOutputStream(socket.getOutputStream());

        out.writeUTF(key);
        out.writeUTF(cipher);

        System.out.println("Ciphertext sent to Bob.");

        socket.close();
    }
}


 