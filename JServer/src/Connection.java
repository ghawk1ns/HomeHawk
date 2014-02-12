import DataObjects.ClientData;

import com.google.gson.Gson;

import java.io.*;
import java.net.Socket;
import java.util.Date;
import java.util.List;

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
    //parse client data members
    private static final String LAST_CHECK_IN = "lastCheckIn";
    private static final String NUM_CHECK_INS= "numCheckIns";

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
                //make sense of the data
                data = new Gson().fromJson(messageString, ClientData.class);
                //is this device checking in?
                if(data.checkingIn()){
                    //checkIn
                    updateClientCheckIn(data);
                    System.out.println(data.getName() + " is checking in");
                }
                else{
                    //
                    System.out.println("Overall message is:" + data);
                    out.println("Overall message is:" + data);
                }
            }
            catch (Exception e){
                String m =  invalidMessage[Min + (int)(Math.random() * ((Max - Min) + 1))];
                System.out.println(m);
                System.out.println(messageString);
                out.println(m);
            }
            //close things
            System.out.println("closing connection...\n");
            in.close();
            server.close();

        } catch (IOException ioe) {
            System.out.println("IOException on socket listen: " + ioe);
            ioe.printStackTrace();
        }
    }


    /*
    Queries parse for the client given its objectId and updates the number of times it has checked in
     */
    private void updateClientCheckIn(ClientData data)
    {

    }
}
