package fr.isen.shazamphoto.utils;

import android.os.AsyncTask;

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

import fr.isen.shazamphoto.database.Monument;
import fr.isen.shazamphoto.ui.SearchableItem;

public class GetMonumentSearch extends AsyncTask<String, Void, JSONObject> {
    private static final String URL = "http://37.187.216.159/shazam/api.php?n=";
    private HttpClient client;
    private ArrayList<Monument> monuments;
    private JSONObject jsonResponse;
   // private SearchMonument searchMonument;
    private SearchableItem searchableItem;

    public GetMonumentSearch(/*SearchMonument event*/SearchableItem searchableItem) {
        client = new DefaultHttpClient();
        client.getParams().setParameter(CoreProtocolPNames.USER_AGENT,
                "android");
        monuments = new ArrayList<>();
       // this.searchMonument = event;
        this.searchableItem = searchableItem;
    }

    public JSONObject doInBackground(String... imdbId) {
        jsonResponse = null;
        String urlWithArguments = URL + imdbId[0];
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
        return jsonResponse;
    }

    public void onPostExecute(JSONObject result) {
        monuments = new ArrayList<>();
        if (jsonResponse != null) {
            try {
                JSONArray monumentsJSON = result.getJSONArray("Search");
                int nbMonuments = monumentsJSON.length();
                for (int i = 0; i < nbMonuments; i++) {
                    JSONObject monumentJSON = monumentsJSON.getJSONObject(i);
                    monuments.add(new Monument(monumentJSON));
                }
            } catch (Exception e) {
            }

            this.searchableItem.onPostSearch(monuments);
/*
           if (searchMonument instanceof SearchMonumentUnidentified) {
                SearchMonumentUnidentified event = (SearchMonumentUnidentified) searchMonument;
                UnidentifiedMonument unidentifiedMonument = event.getUnidentifiedMonument();
                if (!monuments.isEmpty()) {
                    Toast.makeText(unidentifiedMonument, "Thanks, you added more informations !", Toast.LENGTH_LONG).show();
                    unidentifiedMonument.finish();
                } else {
                    //change the fragment to add the new monument discovered
                    unidentifiedMonument.getSupportFragmentManager().beginTransaction()
                            .replace(R.id.container, new AddMonumentFragment())
                            .commit();
                }
            }*/

        }
    }
}