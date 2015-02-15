package fr.isen.shazamphoto.utils;

import android.app.Activity;
import android.content.Intent;
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
import fr.isen.shazamphoto.ui.DetailMonument;
import fr.isen.shazamphoto.ui.Home;
import fr.isen.shazamphoto.ui.UnidentifiedMonument;

public class IdentifyMonumentByLocalization extends AsyncTask<String, Void, JSONObject> {
    private static final String URL = "http://37.187.216.159/shazam/api.php?";
    private HttpClient client;
    private Home home;
    private  JSONObject jsonResponse;
    private String imagePath;

    public IdentifyMonumentByLocalization(Activity act, String _imagePath) {
        client = new DefaultHttpClient();
        client.getParams().setParameter(CoreProtocolPNames.USER_AGENT,
                "android");
        this.home = (Home) act;
        this.imagePath = _imagePath;
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
        if(jsonResponse != null ) {
            ArrayList<Monument> monuments = new ArrayList<>();
            try{
                JSONArray monumentsJSON = result.getJSONArray("Search");
                int nbMonuments = monumentsJSON.length();
                for(int i =0; i<nbMonuments; i++){
                    JSONObject monumentJSON = monumentsJSON.getJSONObject(i);
                    monuments.add(new Monument(monumentJSON));
                }
            }catch(Exception e){
            }

            if(!monuments.isEmpty()){
                Intent intent = new Intent(home, DetailMonument.class);
                monuments.get(0).setPhotoPath(imagePath);
                FunctionsDB.addMonumentToDB(monuments.get(0), home);
                FunctionsDB.addMonumentToTaggedMonument(monuments.get(0), home);
                intent.putExtra(Monument.NAME_SERIALIZABLE, monuments.get(0));
                home.startActivity(intent);
            }else{
                Intent intent = new Intent(home, UnidentifiedMonument.class);
                home.startActivity(intent);
            }

        }else{

            Intent intent = new Intent(home, UnidentifiedMonument.class);
            home.startActivity(intent);
        }
    }
}