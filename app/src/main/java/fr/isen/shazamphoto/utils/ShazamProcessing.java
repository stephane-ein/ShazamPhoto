package fr.isen.shazamphoto.utils;


import android.app.Activity;
import android.os.AsyncTask;
import android.os.Handler;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;
import org.opencv.core.Mat;
import org.opencv.features2d.KeyPoint;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import fr.isen.shazamphoto.database.Descriptors;
import fr.isen.shazamphoto.database.KeyPoints;
import fr.isen.shazamphoto.database.Localization;
import fr.isen.shazamphoto.database.Monument;
import fr.isen.shazamphoto.events.EventUnidentifiedMonument;
import fr.isen.shazamphoto.model.ModelNavigation;

public class ShazamProcessing  extends AsyncTask<String, Void, JSONObject> {

    // Attributes to contact thr server
    private HttpClient httpclient = new DefaultHttpClient();
    private HttpPost httppost =
            new HttpPost("http://+"+ConfigurationShazam.IP_SERVER+"/shazam/identify.php");
    private HttpResponse response;

    // Arguments to send to the server
    private Localization localization;
    private Mat descriptors;
    private KeyPoint[] keyPoints;

    // Handle the UI of the application
    private ModelNavigation modelNavigation;

    //To display the different result
    private Activity activity;

    private boolean isSend;

    public ShazamProcessing(ModelNavigation modelNavigation, Activity activity) {
        this.localization = null;
        this.descriptors = null;
        this.keyPoints = null;
        this.modelNavigation = modelNavigation;
        this.activity = activity;
        isSend = false;
    }

    public void setLocalization(Localization localization) {
        this.localization = localization;
        checkSendRequest();
    }

    public void setDescriptors(Mat descriptors) {
        this.descriptors = descriptors;
        checkSendRequest();

        // Start the timer
        startTime = System.currentTimeMillis();
        timerHandler.postDelayed(timerRunnable, 0);
    }

    public void setKeyPoints(KeyPoint[] keyPoints) {
        this.keyPoints = keyPoints;
        checkSendRequest();
    }

    public void checkSendRequest(){
        // If we have all the argument required, we send the request
        //if( localization != null && descriptors != null && keyPoints != null) execute();
    }

    long startTime = 0;
    //runs without a timer by reposting this handler at the end of the runnable
    Handler timerHandler = new Handler();
    Runnable timerRunnable = new Runnable() {

        @Override
        public void run() {
            long millis = System.currentTimeMillis() - startTime;
            int seconds = (int) (millis / 1000);
            seconds = seconds % 60;
            // If we did not have a localization, we send the request
            if(seconds >= 5 && !isSend){
                isSend = true;
                // Execute the request despite not having a localization
                execute();
                // Remove the timer
                timerHandler.removeCallbacks(timerRunnable);
                Toast.makeText(activity, "identify a monument without localization",Toast.LENGTH_SHORT).show();
            }
            timerHandler.postDelayed(this, 500);
        }
    };

    @Override
    protected JSONObject doInBackground(String... params) {

        JSONObject jsonResponse = null;

        try {
            // Set the arguments in the POST request
            List<NameValuePair> nameValuePairs = new ArrayList<>(3);
            nameValuePairs.add(new BasicNameValuePair("listskeypoints", KeyPoints.toJson(keyPoints).toString()));
            nameValuePairs.add(new BasicNameValuePair("descriptors", Descriptors.toJson(descriptors).toString()));
            nameValuePairs.add(new BasicNameValuePair("localization", localization.toJson().toString()));
            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(nameValuePairs);

            entity.setContentType("application/x-www-form-urlencoded");
            httppost.setEntity(entity);

            // Executing HTTP Post Request
            response = httpclient.execute(httppost);

            // Retrieving the answer
            BufferedReader rd = new BufferedReader(new InputStreamReader(
                    response.getEntity().getContent()));
            StringBuffer result = new StringBuffer();
            String line = "";
            while ((line = rd.readLine()) != null) {
                result.append(line);
            }
            jsonResponse = new JSONObject(result.toString());

            isSend = true;
            System.out.println("Result Identify : " + result.toString());

        } catch (Exception e) {}

        return jsonResponse;
    }

    @Override
    public void onPostExecute(JSONObject result) {
        //monument doesn't exist
      /*  Monument monument1 = new Monument();
        monument1.setDescriptors(this.descriptors);
        monument1.setKeyPoints(this.keyPointArray);
        Intent intent = new Intent(this.getActivityContext(), UnidentifiedMonument.class);
        Bundle args = new Bundle();
        args.putSerializable(Monument.NAME_SERIALIZABLE, monument1);
        intent.putExtras(args);
        getActivityContext().startActivity(intent);*/

        try {

            if(result != null){
                if (result.toString().equals("{}")) {
                    Toast.makeText(activity, " Monument not identify", Toast.LENGTH_LONG).show();
                    Monument monument = new Monument(keyPoints, descriptors);
                    modelNavigation.changeAppView(
                            new EventUnidentifiedMonument(activity, monument));
                   /* Intent intent = new Intent(this.getActivityContext(), UnidentifiedMonument.class);
                    Bundle args = new Bundle();
                    args.putSerializable(Monument.NAME_SERIALIZABLE, monument);
                    intent.putExtras(args);
                    getActivityContext().startActivity(intent);*/
                } else{
                    Toast.makeText(activity, "Monument identified", Toast.LENGTH_LONG).show();
                    Toast.makeText(activity, "Result : "+result.toString(), Toast.LENGTH_LONG).show();
                }
            }else{
                Toast.makeText(activity, "Answser server NULL", Toast.LENGTH_LONG).show();
                Monument monument = new Monument(keyPoints, descriptors);
                modelNavigation.changeAppView(
                        new EventUnidentifiedMonument(activity, monument));
            }
        } catch (Exception e) {
            Toast.makeText(activity, "Error : "+e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
