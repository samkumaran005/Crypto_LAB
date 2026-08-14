import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client {
    static final String SERVER_ADDRESS = "127.0.0.1";
    static final int PORT = 5000;

    public static void main(String[] args) {
        try (
            Socket socket = new Socket(SERVER_ADDRESS, PORT);
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            Scanner scanner = new Scanner(System.in)
        ) {
            System.out.println("Connected to Server...\n");

            // Input number 'a' and modulus 'm'
            System.out.print("Enter number (a) : ");
            int a = scanner.nextInt();

            System.out.print("Enter modulus (m): ");
            int m = scanner.nextInt();

            // Send 'a' and 'm' to Server
            out.writeInt(a);
            out.writeInt(m);
            out.flush();

            System.out.println("\nData sent to server successfully.");

        } catch (Exception e) {
            System.out.println("Client Error: " + e.getMessage());
        }
    }
}