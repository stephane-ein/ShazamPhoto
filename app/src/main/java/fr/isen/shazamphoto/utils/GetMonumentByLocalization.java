package fr.isen.shazamphoto.utils;

import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;

import fr.isen.shazamphoto.database.Localization;
import fr.isen.shazamphoto.database.Monument;
import fr.isen.shazamphoto.events.RequestIdentifyByLocalization;
import fr.isen.shazamphoto.events.RequestLocalization;
import fr.isen.shazamphoto.events.RequestNearestFromMonument;
import fr.isen.shazamphoto.events.RequestNearestMonuments;
import fr.isen.shazamphoto.ui.DetailMonument;
import fr.isen.shazamphoto.ui.Home;
import fr.isen.shazamphoto.ui.NearestMonumentsFragment;
import fr.isen.shazamphoto.ui.UnidentifiedMonument;

public class GetMonumentByLocalization extends AsyncTask<String, Void, JSONObject> {

    private static final String URL = "http://37.187.216.159/shazam/api.php?";
    private HttpClient client;
    private JSONObject jsonResponse;
    private String urlWithArguments;
    private RequestLocalization requestLocalization;
    private Localization localization;

    public GetMonumentByLocalization(RequestLocalization requestLocalization) {
        client = new DefaultHttpClient();
        client.getParams().setParameter(CoreProtocolPNames.USER_AGENT,
                "android");

        this.requestLocalization = requestLocalization;
    }

    public void setArgument(String latitude, String longitude, String delta) {
        urlWithArguments = URL + "la=" + latitude + "&lo=" + longitude + "&o=" + delta;
    }

    // Indicate the latitude, longitude and the delta in the arguments
    public JSONObject doInBackground(String... args) {
        jsonResponse = null;

        if(args.length == 3){
            setArgument(args[0], args[1], args[2]);
            localization = new Localization(-1, Float.valueOf(args[0]), Float.valueOf(args[1]));
            try {
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
            } catch (Exception e) {}
        }

        return jsonResponse;
    }

    public void onPostExecute(JSONObject result) {
        if (jsonResponse != null) {
            ArrayList<Monument> monuments = new ArrayList<>();
            try {
                JSONArray monumentsJSON = result.getJSONArray("Search");
                int nbMonuments = monumentsJSON.length();
                for (int i = 0; i < nbMonuments; i++) {
                    JSONObject monumentJSON = monumentsJSON.getJSONObject(i);
                    monuments.add(new Monument(monumentJSON));
                }
            } catch (Exception e) {
            }

            if(requestLocalization instanceof RequestNearestFromMonument){
                RequestNearestFromMonument requestNearestFromMonument = (RequestNearestFromMonument) requestLocalization;
                Toast.makeText(requestNearestFromMonument.getDetailMonument(),"Thread executed", Toast.LENGTH_LONG).show();
            }
            requestLocalization.doPostAction(monuments, localization);
        }

    }
}