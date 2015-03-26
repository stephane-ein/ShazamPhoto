package fr.isen.shazamphoto.utils;

import android.app.Activity;
import android.app.ProgressDialog;
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
import fr.isen.shazamphoto.events.EventDisplayDetailMonument;
import fr.isen.shazamphoto.events.EventUnidentifiedMonument;
import fr.isen.shazamphoto.model.ModelNavigation;

public class ShazamProcessingTask extends AsyncTask<String, Void, JSONObject> {

    // Attributes to contact thr server
    private HttpClient httpclient = new DefaultHttpClient();
    private HttpPost httppost =
            new HttpPost("http://"+ConfigurationShazam.IP_SERVER+"/shazam/identify.php");
    private HttpResponse response;
    private ProgressDialog dialog;

    // Arguments to send to the server
    private Localization localization;
    private Mat descriptors;
    private KeyPoint[] keyPoints;

    // Handle the UI of the application
    private ModelNavigation modelNavigation;

    // To display the different result
    private Activity activity;

    // Attributes to handle the thread
    private boolean isSend;
    private long startTime = 0;
    private Handler timerHandler = new Handler();
    private Runnable timerRunnable;

    // Path of the picture of the monument
    private String photoPath;

    public ShazamProcessingTask(ModelNavigation modelNavigation, Activity activity) {
        this.localization = null;
        this.descriptors = null;
        this.keyPoints = null;
        this.modelNavigation = modelNavigation;
        this.activity = activity;
        this.isSend = false;
        this.dialog = new ProgressDialog(activity);
        this.localization = new Localization(-1, 0.0, 0.0);
    }

    public void setLocalization(Localization localization) {
        this.localization = localization;
        checkSendRequest();
    }

    public void setDescriptorsKeyKeyPoints(Mat descriptors, KeyPoint[] keyPoints) {
        this.descriptors = descriptors;
        this.keyPoints = keyPoints;

        // Set the timer
        timerRunnable = new Runnable() {

            @Override
            public void run() {
                long millis = System.currentTimeMillis() - startTime;
                int seconds = (int) (millis / 1000);
                seconds = seconds % 60;

                // Check if the request has not been already sent and if we have all the argument in
                // the remaining time
                if( isSend==false && (seconds >= 5 || checkSendRequest())){
                    isSend = true;
                    // Execute the request (despite not having a localization)
                    execute();
                    // Remove the timer
                    timerHandler.removeCallbacks(timerRunnable);

                    if(checkSendRequest()){
                        Toast.makeText(activity, "identify a monument with localization",Toast.LENGTH_SHORT).show();
                    }else if(seconds >= 5){
                        Toast.makeText(activity, "identify a monument without localization",Toast.LENGTH_SHORT).show();
                    }

                }
                timerHandler.postDelayed(this, 500);
            }
        };


        // Start the timer
        startTime = System.currentTimeMillis();
        timerHandler.postDelayed(timerRunnable, 0);

    }

    public boolean checkSendRequest(){
        // If we have all the argument required, we send the request
        return localization != null && descriptors != null && keyPoints != null;
    }

    @Override
    protected void onPreExecute() {
        this.dialog.setMessage("Sending the descriptors to the server");
        this.dialog.show();
    }

    @Override
    protected JSONObject doInBackground(String... params) {

        JSONObject jsonResponse = null;
        Descriptors.activity = activity;

        try {
            // Set the arguments in the POST request
            List<NameValuePair> nameValuePairs = new ArrayList<>(3);
            nameValuePairs.add(new BasicNameValuePair("listskeypoints", KeyPoints.toJson(keyPoints).toString()));
            nameValuePairs.add(new BasicNameValuePair("descriptors", Descriptors.toJson(descriptors).toString()));

            if(localization != null) {
                nameValuePairs.add(new BasicNameValuePair("localization", localization.toJson().toString()));
            }

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

            System.out.println("Result Identify : " + result.toString());

        } catch (Exception e) {}

        return jsonResponse;
    }

    @Override
    public void onPostExecute(JSONObject result) {

        try {

            if(result != null){
                if (result.toString().equals("{}")) {
                    Monument monument = new Monument(keyPoints, descriptors, localization, photoPath);
                    Toast.makeText(activity, " Monument not identify", Toast.LENGTH_LONG).show();
                    modelNavigation.changeAppView(
                            new EventUnidentifiedMonument(activity, monument));
                } else{
                    Monument m = new Monument(result);
                    // Add the monument to the monument tagged
                    FunctionsDB.addMonumentToDB(m, activity);
                    FunctionsDB.addMonumentToTaggedMonument(m, activity);
                    // Display the monument identified
                    modelNavigation.changeAppView(new EventDisplayDetailMonument(activity, m));
                }
            }else{
               Toast.makeText(activity, "The server is not avaible", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(activity, "Error in ShazamProcessing : "+e.getMessage(), Toast.LENGTH_LONG).show();
        }

        if (dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    public void setPhotoPath(String path) {
        this.photoPath = path;
    }

}
