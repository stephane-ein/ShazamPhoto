package fr.isen.shazamphoto.utils;

import android.app.Activity;
import android.os.Handler;
import android.util.Log;
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
import fr.isen.shazamphoto.events.EventInternetTask;
import fr.isen.shazamphoto.events.EventMonumentUpdated;
import fr.isen.shazamphoto.events.EventUnidentifiedMonument;
import fr.isen.shazamphoto.model.ModelNavigation;
import fr.isen.shazamphoto.ui.ItemUtils.UpdateMonumentItem;
import fr.isen.shazamphoto.utils.UpdateMonument.AddVisitorTask;

public class ShazamProcessingTask extends InternetTask<String, Void, EventInternetTask> implements UpdateMonumentItem {

    // Attributes to contact thr server
    private HttpClient httpclient = new DefaultHttpClient();
    private HttpPost httppost =
            new HttpPost("http://"+ConfigurationShazam.IP_SERVER+"/shazam/identify.php");
    private HttpResponse response;

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
        super(activity);
        this.localization = null;
        this.descriptors = null;
        this.keyPoints = null;
        this.modelNavigation = modelNavigation;
        this.activity = activity;
        this.isSend = false;
    }

    public void setLocalization(Localization localization) {
        this.localization = localization;
        checkSendRequest();
    }

    public void setPhotoPath(String path) {
        this.photoPath = path;
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
                        Log.v("Shazam", "identify a monument with localization : "+localization);
                    }else if(seconds >= 5){
                        Log.v("Shazam", "identify a monument without localization : "+localization);
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
    protected EventInternetTask doInBackground(String... params) {

        JSONObject jsonResponse = null;
        boolean isInternetFound = false;
        try {
            isInternetFound = checkNetwork();
            if(isInternetFound){
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
            }
        } catch (Exception e) {
            Log.e("Shazam", "Exception in SPT DIB: "+e.getMessage());
        }

        return new EventInternetTask(isInternetFound, jsonResponse);
    }

    @Override
    public void onPostExecute(EventInternetTask result) {

        JSONObject jsonReponse = result.getJsonResponse();
        updateUI(result.isInternetfound());
        try {

            if(jsonReponse != null){
                if (jsonReponse.toString().equals("{}")) {
                    Monument monument = new Monument(keyPoints, descriptors, localization, photoPath);
                    monument.setPhotoPathLocal(photoPath);
                    modelNavigation.changeAppView(
                            new EventUnidentifiedMonument(activity, monument, modelNavigation));
                } else{
                    Monument m = new Monument(jsonReponse);
                    // Add the monument to the monument tagged
                    FunctionsDB.addMonumentToDB(m, activity);
                    FunctionsDB.addMonumentToTaggedMonument(m, activity);
                    // Increase the number of visitor of this monument
                    AddVisitorTask addVisitorTask = new AddVisitorTask(getActivity(), this);
                    addVisitorTask.execute(m);
                    // Display the monument identified
                    modelNavigation.changeAppView(new EventDisplayDetailMonument(activity, m, null));
                }
            }else if(jsonReponse == null && result.isInternetfound()) {
               Toast.makeText(activity, "The server is not available ", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Log.e("Shazam", "Exception in SPT OPR: "+e.getMessage());
        }
    }

    @Override
    public void monumentUpdated(EventMonumentUpdated eventLocalizationFound) {

    }
}
