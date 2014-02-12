


import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Properties;

/**
 * Guy Hawkins
 * uteid - gh6783
 * cs    - ghawk88
 * email - texasgh@gmail.com
 */
public class Server {

    private static int port, maxConnections;
    // Listen for incoming connections and handle them
    public static void main(String[] args) {
        int i=0;
        //configure server
        init();
        try{

            ServerSocket listener = new ServerSocket(port);
            Socket server;

            System.out.println("Listening on: " + listener.toString());

            while((i++ < maxConnections) || (maxConnections == 0)){

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


    //initialize server and backend
    private static void init(){
        InputStream input = null;
        try {
            input = new FileInputStream("config.properties");

            Properties properties = new Properties();
            properties.load(input);
            //load parse information
            String _aID = properties.getProperty("applicationID");
            String _rID = properties.getProperty("restAPIKey");
            //initialize parse
            //Parse.initialize(_aID, _rID);
            //server properties
            port = Integer.parseInt(properties.getProperty("port"));
            maxConnections = Integer.parseInt(properties.getProperty("maxConnections"));
        } catch (IOException ex) {
            System.out.print("Failed to configure");
            ex.printStackTrace();
            System.exit(-1);
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    System.out.print("Failed to close");
                    e.printStackTrace();
                    System.exit(-1);
                }
            }
        }
    }

}

