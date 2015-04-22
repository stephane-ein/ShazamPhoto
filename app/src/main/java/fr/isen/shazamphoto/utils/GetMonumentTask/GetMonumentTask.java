package fr.isen.shazamphoto.utils.GetMonumentTask;

import android.app.Activity;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;

import fr.isen.shazamphoto.events.EventInternetTask;
import fr.isen.shazamphoto.utils.ConfigurationShazam;
import fr.isen.shazamphoto.utils.InternetTask;

public abstract class GetMonumentTask extends InternetTask<String, Void, EventInternetTask> {

    private static final String URL = "http://"+ ConfigurationShazam.IP_SERVER+"/shazam/api.php?";
    private HttpClient client;
    private String urlWithArguments;

    public GetMonumentTask(Activity activity) {
        super(activity);
        client = new DefaultHttpClient();
        client.getParams().setParameter(CoreProtocolPNames.USER_AGENT,
                "android");
    }

    public void setUrlWithArguments(String args){
        urlWithArguments = URL + args;
    }

    @Override
    protected EventInternetTask doInBackground(String... params) {

        JSONObject jsonResponse = null;
        boolean network = false;

        Log.v("Shazam", "Get Monument task doInBackground : "+urlWithArguments);
        try {
            network = checkNetwork();
            if(network){
                HttpGet request = new HttpGet(urlWithArguments);
                request.setHeader("Content-type", "application/json");
                request.setURI(new URI(urlWithArguments));
                HttpResponse response = client.execute(request);
                BufferedReader rd = new BufferedReader(new InputStreamReader(
                        response.getEntity().getContent()));
                StringBuffer result = new StringBuffer();
                String line = "";
                while ((line = rd.readLine()) != null) {
                    result.append(line);
                }
                jsonResponse = new JSONObject(result.toString());

                Log.v("Shazam", "Get Monument task : "+urlWithArguments);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("Shazam", "Exception in GetMonumentTask : "+e.getMessage());
        }
        return new EventInternetTask(network, jsonResponse);
    }

}