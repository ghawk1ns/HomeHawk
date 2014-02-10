import com.google.gson.Gson;

import java.io.*;
import java.net.Socket;
import java.util.Random;
import java.util.Scanner;

/**
 * Guy Hawkins
 * uteid - gh6783
 * cs    - ghawk88
 * email - texasgh@gmail.com
 */
class Connection implements Runnable {
    private Socket server;
    private String dataString,messageString;
    boolean end = false;
    byte[] messageByte = new byte[1000];


    String[] invalidMessage = {"Ah ah ah, you didn't say the magic word","Invalid","Better luck next time"};
    int Min = 0;
    int Max = invalidMessage.length-1;

    Connection(Socket server) {
        this.server=server;
    }

    public void run () {

        System.out.println("\nIncoming from: " + server.toString());

        dataString="";
        messageString = "";

        try {
            PrintStream out = new PrintStream(server.getOutputStream());
            DataInputStream in = new DataInputStream(server.getInputStream());

            while(!end)
            {
                int bytesRead = in.read(messageByte);
                messageString += new String(messageByte, 0, bytesRead);
                if (messageString.contains("\n"))
                {
                    end = true;
                }
            }

            ClientData data;
            try{
                data = new Gson().fromJson(messageString, ClientData.class);
                // Show it.
                System.out.println("Overall message is:" + data + "\n");
                out.println("Overall message is:" + data);

            }
            catch (Exception e){
                String m =  invalidMessage[Min + (int)(Math.random() * ((Max - Min) + 1))];
                System.out.print(m + "\n");
                out.println(m);
            }
            //close things
            in.close();
            server.close();

        } catch (IOException ioe) {
            System.out.println("IOException on socket listen: " + ioe);
            ioe.printStackTrace();
        }
    }
}
