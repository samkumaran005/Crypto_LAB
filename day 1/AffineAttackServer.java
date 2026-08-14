import java.io.*;
import java.net.*;

public class AffineAttackServer {

    public static void main(String[] args) {

        try (ServerSocket serverSocket =
                     new ServerSocket(5000)) {

            System.out.println(
                    "Server started. Waiting for client...");

            Socket socket =
                    serverSocket.accept();

            System.out.println(
                    "Client connected: "
                            + socket.getInetAddress());

            DataInputStream in =
                    new DataInputStream(
                            socket.getInputStream());

            while (true) {

                try {

                    String message =
                            in.readUTF();

                    if (message.equalsIgnoreCase("Over")) {

                        System.out.println(
                                "Client initiated shutdown.");

                        break;
                    }

                    String[] parts =
                            message.split("\\|", 3);

                    if (parts.length == 3) {

                        String cipherText =
                                parts[0].toUpperCase();

                        String plainPair =
                                parts[1].toUpperCase();

                        String cipherPair =
                                parts[2].toUpperCase();

                        attack(
                                cipherText,
                                plainPair,
                                cipherPair);

                    } else {

                        System.out.println(
                                "Invalid data received.");
                    }

                } catch (EOFException e) {

                    System.out.println(
                            "Client disconnected.");

                    break;
                }
            }

            socket.close();

        } catch (IOException e) {

            e.printStackTrace();
        }
    }


    // GCD
    static int gcd(int a, int b) {

        while (b != 0) {

            int temp = b;

            b = a % b;

            a = temp;
        }

        return Math.abs(a);
    }


    // Modular inverse
    static int modInverse(int a) {

        a = ((a % 26) + 26) % 26;

        for (int i = 1; i < 26; i++) {

            if ((a * i) % 26 == 1) {

                return i;
            }
        }

        return -1;
    }


    // Cryptanalytic attack
    static void attack(
            String cipherText,
            String plainPair,
            String cipherPair) {

        int x1 = plainPair.charAt(0) - 'A';
        int x2 = plainPair.charAt(1) - 'A';

        int y1 = cipherPair.charAt(0) - 'A';
        int y2 = cipherPair.charAt(1) - 'A';


        int plainDifference =
                (x1 - x2 + 26) % 26;

        int cipherDifference =
                (y1 - y2 + 26) % 26;


        int inverse =
                modInverse(plainDifference);

        if (inverse == -1) {

            System.out.println(
                    "Cannot determine unique key.");

            System.out.println(
                    "Choose another pair of known letters.");

            return;
        }


        // Find a
        int a =
                (cipherDifference * inverse) % 26;


        if (gcd(a, 26) != 1) {

            System.out.println(
                    "Recovered 'a' is not valid.");

            return;
        }


        // Find b
        int b =
                (y1 - a * x1) % 26;

        b = (b + 26) % 26;


        System.out.println(
                "\n===== AFFINE CRYPTANALYTIC ATTACK =====");

        System.out.println(
                "Known Plain Pair  : " + plainPair);

        System.out.println(
                "Known Cipher Pair : " + cipherPair);

        System.out.println(
                "\nRecovered a = " + a);

        System.out.println(
                "Recovered b = " + b);


        String plainText =
                decrypt(cipherText, a, b);


        System.out.println(
                "\nCipher Text : " + cipherText);

        System.out.println(
                "Plain Text  : " + plainText);
    }


    // Decryption
    static String decrypt(
            String cipher,
            int a,
            int b) {

        int inverse =
                modInverse(a);

        StringBuilder plain =
                new StringBuilder();

        for (int i = 0;
             i < cipher.length();
             i++) {

            char ch =
                    cipher.charAt(i);

            if (Character.isLetter(ch)) {

                int y =
                        ch - 'A';

                int x =
                        (inverse *
                        (y - b + 26)) % 26;

                plain.append(
                        (char) (x + 'A'));

            } else {

                plain.append(ch);
            }
        }

        return plain.toString();
    }
}