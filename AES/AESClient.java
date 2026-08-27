import java.io.DataOutputStream;
import java.net.Socket;
import java.util.Arrays;
import java.util.Scanner;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class AESClient {
    static final String SERVER_ADDRESS = "127.0.0.1";
    static final int PORT = 7000;

    public static void main(String[] args) {
        try (Socket socket = new Socket(SERVER_ADDRESS, PORT);
             DataOutputStream out = new DataOutputStream(socket.getOutputStream());
             Scanner scanner = new Scanner(System.in)) {

            System.out.println("Connected to server...\n");

            // Input plaintext
            System.out.print("Enter plaintext: ");
            String plainText = scanner.nextLine();
            byte[] plainBytes = plainText.getBytes("UTF-8");

            // Input key from user
            System.out.print("Enter AES key : ");
            String keyInput = scanner.nextLine();

            // Normalize key length (pad or truncate)
            byte[] keyBytes = normalizeKey(keyInput, 16); // AES-128
            SecretKey secretKey = new SecretKeySpec(keyBytes, "AES");

            // IV (fixed zeros for demo; use SecureRandom in real apps)
            byte[] iv = new byte[16];
            Arrays.fill(iv, (byte) 0x00);
            IvParameterSpec ivSpec = new IvParameterSpec(iv);

            // Encrypt
            Cipher cipher = Cipher.getInstance("AES/CTR/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec);
            byte[] cipherBytes = cipher.doFinal(plainBytes);

            System.out.println("\nGenerated Ciphertext (Hex): " + bytesToHex(cipherBytes));

            // Send key, IV, and ciphertext
            out.writeUTF(bytesToHex(secretKey.getEncoded())); // send key in hex
            out.writeUTF(bytesToHex(iv));                     // send IV in hex
            out.writeInt(cipherBytes.length);
            out.write(cipherBytes);
            out.flush();

            System.out.println("Data sent to server successfully.");

        } catch (Exception e) {
            System.out.println("Client Error: " + e.getMessage());
        }
    }

    // Helper: convert bytes to hex string
    static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X", b));
        }
        return sb.toString();
    }
        // Normalize key to required length
    static byte[] normalizeKey(String key, int length) throws Exception {
        byte[] keyBytes = key.getBytes("UTF-8");
        if (keyBytes.length < length) {
            return Arrays.copyOf(keyBytes, length); // pad with zeros
        } else if (keyBytes.length > length) {
            return Arrays.copyOf(keyBytes, length); // truncate
        }
        return keyBytes;
    }
}
