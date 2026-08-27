import java.io.DataInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class AESServer {
    static final int PORT = 7000;

    public static void main(String[] args) {
        try (ServerSocket server = new ServerSocket(PORT)) {
            System.out.println("Server started. Waiting for client...");
            Socket socket = server.accept();
            System.out.println("Client connected.\n");

            DataInputStream in = new DataInputStream(socket.getInputStream());

            // Receive key and IV (hex)
            String keyHex = in.readUTF();
            String ivHex = in.readUTF();

            // Convert back to bytes
            byte[] keyBytes = hexToBytes(keyHex);
            byte[] ivBytes = hexToBytes(ivHex);

            SecretKey secretKey = new SecretKeySpec(keyBytes, "AES");
            IvParameterSpec ivSpec = new IvParameterSpec(ivBytes);

            // Receive ciphertext
            int length = in.readInt();
            byte[] cipherBytes = new byte[length];
            in.readFully(cipherBytes);

            System.out.println("===== DATA RECEIVED =====");
            System.out.println("Key (Hex)        : " + keyHex);
            // System.out.println("IV (Hex)         : " + ivHex);
            System.out.println("Ciphertext (Hex) : " + bytesToHex(cipherBytes));

            // Decrypt
            Cipher cipher = Cipher.getInstance("AES/CTR/NoPadding");
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec);
            byte[] decryptedBytes = cipher.doFinal(cipherBytes);

            System.out.println("Decrypted Plaintext: " + new String(decryptedBytes, "UTF-8"));

            socket.close();
        } catch (Exception e) {
            System.out.println("Server Error: " + e.getMessage());
        }
    }

    // Helper: convert hex string to bytes
    static byte[] hexToBytes(String hex) {
        int len = hex.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4)
                                + Character.digit(hex.charAt(i+1), 16));
        }
        return data;
    }

    // Helper: convert bytes to hex string
    static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X", b));
        }
        return sb.toString();
    }
}
