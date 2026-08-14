import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client {

    static final String SERVER = "127.0.0.1";
    static final int PORT = 5000;

    public static void main(String args[]) {

        try {

            Socket socket = new Socket(SERVER, PORT);

            DataOutputStream out =
                    new DataOutputStream(socket.getOutputStream());

            Scanner sc = new Scanner(System.in);

            System.out.println("===== HILL CIPHER CLIENT =====");

            System.out.print("Enter Matrix Size (2/3/4/5) : ");
            int n = sc.nextInt();

            int key[][] = new int[n][n];

            System.out.println("Enter Key Matrix");

            for(int i=0;i<n;i++)
            {
                for(int j=0;j<n;j++)
                {
                    key[i][j]=sc.nextInt();
                }
            }

            sc.nextLine();

            System.out.print("Enter Plain Text : ");
            String plain = sc.nextLine();

            // Encryption
            String cipher = encrypt(plain,key,n);

            System.out.println("\nEncrypted Text : "+cipher);

            // Send Matrix Size
            out.writeInt(n);

            // Send Key Matrix
            for(int i=0;i<n;i++)
            {
                for(int j=0;j<n;j++)
                {
                    out.writeInt(key[i][j]);
                }
            }

            // Send Cipher Text
            out.writeUTF(cipher);

            out.flush();

            System.out.println("\nCipher Text Sent Successfully.");

            out.close();
            socket.close();
            sc.close();

        }

        catch(Exception e)
        {
            System.out.println(e);
        }

    }

    //---------------------------------------------------
    // Hill Cipher Encryption
    //---------------------------------------------------

    static String encrypt(String text,int key[][],int n)
    {

        text=text.toUpperCase();

        while(text.length()%n!=0)
        {
            text=text+"X";
        }

        String cipher="";

        for(int i=0;i<text.length();i+=n)
        {

            int plainVector[]=new int[n];

            for(int j=0;j<n;j++)
            {
                plainVector[j]=text.charAt(i+j)-65;
            }

            int cipherVector[]=new int[n];

            for(int r=0;r<n;r++)
            {

                cipherVector[r]=0;

                for(int c=0;c<n;c++)
                {
                    cipherVector[r]+=key[r][c]*plainVector[c];
                }

                cipherVector[r]=cipherVector[r]%26;

                cipher+=(char)(cipherVector[r]+65);

            }

        }

        return cipher;

    }

}