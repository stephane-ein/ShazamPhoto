package fr.isen.shazamphoto.utils;

import android.app.Activity;
import android.os.AsyncTask;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
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

import fr.isen.shazamphoto.R;
import fr.isen.shazamphoto.database.Monument;
import fr.isen.shazamphoto.events.SearchMonument;
import fr.isen.shazamphoto.events.SearchMonumentByName;
import fr.isen.shazamphoto.events.SearchMonumentUnidentified;
import fr.isen.shazamphoto.ui.AddMonument;
import fr.isen.shazamphoto.ui.CustomListAdapter;
import fr.isen.shazamphoto.ui.Home;
import fr.isen.shazamphoto.ui.Shazam;
import fr.isen.shazamphoto.ui.UnidentifiedMonument;

public class GetMonumentSearch extends AsyncTask<String, Void, JSONObject> {
    private static final String URL = "http://37.187.216.159/shazam/api.php?n=";
    private HttpClient client;
    private ArrayList<Monument> monuments;
    private JSONObject jsonResponse;
    private SearchMonument searchMonument;

    public GetMonumentSearch(SearchMonument event) {
        client = new DefaultHttpClient();
        client.getParams().setParameter(CoreProtocolPNames.USER_AGENT,
                "android");
        monuments = new ArrayList<>();
        this.searchMonument = event;
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

            if (searchMonument instanceof SearchMonumentByName) {
                SearchMonumentByName event = (SearchMonumentByName) searchMonument;
                Shazam shazam = event.getShazam();
                shazam.setListResult(monuments);
            } else if (searchMonument instanceof SearchMonumentUnidentified) {
                SearchMonumentUnidentified event = (SearchMonumentUnidentified) searchMonument;
                UnidentifiedMonument unidentifiedMonument = event.getUnidentifiedMonument();
                if (monuments.isEmpty()) {
                    Toast.makeText(unidentifiedMonument, "Thanks, you added more informations !", Toast.LENGTH_LONG).show();
                    unidentifiedMonument.finish();
                } else {
                    //change the fragment to add the new monument discovered
                    unidentifiedMonument.getSupportFragmentManager().beginTransaction()
                            .replace(R.id.container, new AddMonument())
                            .commit();
                }
            }

        }
    }
}