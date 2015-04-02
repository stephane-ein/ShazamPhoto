package fr.isen.shazamphoto.utils;


import android.os.AsyncTask;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;

import fr.isen.shazamphoto.database.Localization;

public class GetDistance extends AsyncTask<Localization, Void, JSONObject> {

    private HttpClient client = new DefaultHttpClient();
    private HttpResponse response;


    @Override
    public JSONObject doInBackground(Localization... localizations) {
        Localization origin = localizations[0];
        Localization dest = localizations[1];
        JSONObject jsonResponse = null;
        try {

            String originURL = "origin="+Double.valueOf(origin.getLatitude()).toString()+","+Double.valueOf(origin.getLongitude()).toString();
            String destURL = "destination="+Double.valueOf(dest.getLatitude()).toString()+","+Double.valueOf(dest.getLongitude()).toString();
            String URL = "https://maps.googleapis.com/maps/api/directions/json?";
            String urlWithArguments = URL +originURL+"&"+destURL+"&avoid=highways&mode=walking&key=AIzaSyDWQbON7gqa8LGgbVGJxInUo0YEMaS2CWY";
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

        } catch (Exception e) {
            System.out.println("Exception in AddMonument: "+e.getMessage());
        }

        return jsonResponse;
    }

    @Override
    public void onPostExecute(JSONObject result2) {
        try {
        } catch (Exception e) {
        }

    }
}