package almonds;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by guyhawkins on 2/11/14.
 */
public class ParsePushNotification
{


    public static void push(String channel, String message) throws ParseException
    {
        try
        {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(Parse.getParseAPIUrlPush());
            httppost.addHeader("X-Parse-Application-Id", Parse.getApplicationId());
            httppost.addHeader("X-Parse-REST-API-Key", Parse.getRestAPIKey());
            httppost.addHeader("Content-Type", "application/json");

            String entity =
                    "{" +
                        "\"channels\":[ \"" +
                             channel
                        +"\" ], " +
                        "\"data\": {" +
                            "\"alert\":\""+message+"\"" +
                        "}" +
                    "}";

//            String entity =
//                    "{" +
//                            "\"channels\":[], " +
//                            "\"data\": {" +
//                            "\"alert\":\""+message+"\"" +
//                            "}" +
//                            "}";


            StringEntity res =  new StringEntity(entity);

            httppost.setEntity(res);

            HttpResponse httpresponse = httpclient.execute(httppost);

            ParseResponse response = new ParseResponse(httpresponse);

            if (!response.isFailed())
            {
                JSONObject jsonResponse = response.getJsonObject();

                if (jsonResponse == null)
                {
                    throw response.getException();
                }
                //print out result!
                //System.out.println(jsonResponse.toString());
            }
            else
            {
                throw response.getException();
            }
        }
        catch (ClientProtocolException e)
        {
            throw ParseResponse.getConnectionFailedException(e);
        }
        catch (IOException e)
        {
            throw ParseResponse.getConnectionFailedException(e);
        }
    }

    /**
     * A private helper class to facilitate Push Notifications
     * in the background.
     *
     * @author ghawk1ns
     *
     */
    class PushInBackgroundThread extends Thread
    {
        SaveCallback mSaveCallback;
        String channel;
        String message;

        /**
         *
         * @param callback
         *            A function object of type Savecallback, whose method done
         *            will be called upon completion
         */
        PushInBackgroundThread(String ch, String m, SaveCallback callback)
        {
            mSaveCallback = callback;
            channel = ch;
            message = m;
        }

        public void run()
        {
            ParseException exception = null;

            try
            {
                push(channel, message);
            }
            catch (ParseException e)
            {
                exception = e;
            }

            if (mSaveCallback != null)
            {
                mSaveCallback.done(exception);
            }
        }
    }

    /**
     * initiates a push notification to the server in a background thread. This is
     * preferable to using save(), unless your code is already running from a
     * background thread.
     *
     * @param callback
     *            callback.done(e) is called when the save completes.
     */
    public void pushInBackground(String ch, String m, SaveCallback callback)
    {
        PushInBackgroundThread t = new PushInBackgroundThread(ch, m, callback);
        t.start();
    }

    /**
     * Initiates a push notification in a background thread. Use this when you
     * do not have code to run on completion of the push.
     */
    public void pushInBackground(String ch, String m)
    {
        pushInBackground(ch, m, null);
    }


}
