import com.google.gson.Gson;

import java.util.List;

/**
 * Created by guyhawkins on 2/10/14.
 */
public class Test
{


    public static void main(String[] args) {
        //'{ "clientId":"Arduino yo","sensorId":"PIR Sensor","sensorData": { "isActive": 1 ,"time":"2:31"} }'
        String json =
                "{"
                    + "'headerId': 'coolio123',"
                    + "'clientId' : 'Arduino',"
                    + "'sensorId' : 'pir',"
                    + "'checkIn' : True,"
                    + "'sensorData' : {"
                        + "'isActive' : 'true',"
                        + "'time' : '12:00'"
                        + "}\n"
                + "}";

        ClientData data;
        try{
            data = new Gson().fromJson(json, ClientData.class);
            // Show it.
            System.out.println(data);
        }
        catch (Exception e){
            System.out.print("Invalid lol");
        }




    }
}



