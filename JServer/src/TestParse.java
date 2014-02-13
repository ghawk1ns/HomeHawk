import almonds.*;

import java.io.*;
import java.util.Properties;


class testParse {

    //parse client data members
    private static final String LAST_CHECK_IN = "lastCheckIn";
    private static final String NUM_CHECK_INS= "numCheckIns";


    public static void main(String[] args) throws Exception {
        init();
        String cId = "RvMQuSPEDk";
        ParsePushNotification pn = new ParsePushNotification();
        pn.pushInBackground(cId, "Warning from sensor A", new SaveCallback()
        {
            @Override
            public void done(ParseException e)
            {
                if(e != null){
                    e.printStackTrace();
                }
                else{
                    System.out.println("Push Notification sent");
                }

            }
        });

    }

    private static void updateClientCheckIn(String id)
    {
        try {
            ParseQuery pq = new ParseQuery("Client");
            ParseObject po = pq.get(id);
            po.save();

        } catch (ParseException e) {
            e.printStackTrace();
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
            Parse.initialize(_aID,_rID);

            //server properties
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