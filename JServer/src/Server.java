import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Guy Hawkins
 * uteid - gh6783
 * cs    - ghawk88
 * email - texasgh@gmail.com
 */
public class Server {

    private static int port=61846, maxConnections=0;
    // Listen for incoming connections and handle them
    public static void main(String[] args) {
        int i=0;

        try{
            ServerSocket listener = new ServerSocket(port);
            Socket server;

            System.out.println("Listening on: " + listener.toString());

            while((i++ < maxConnections) || (maxConnections == 0)){
                //Connection connection;

                server = listener.accept();
                Connection connection= new Connection(server);
                Thread t = new Thread(connection);
                t.start();
            }
        } catch (IOException ioe) {
            System.out.println("IOException on socket listen: " + ioe);
            ioe.printStackTrace();
        }
    }

}

