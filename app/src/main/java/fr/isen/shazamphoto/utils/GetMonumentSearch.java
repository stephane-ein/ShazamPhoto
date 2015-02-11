package fr.isen.shazamphoto.utils;

import android.app.Activity;
import android.os.AsyncTask;
import android.widget.TextView;
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

import fr.isen.shazamphoto.R;
import fr.isen.shazamphoto.database.Monument;
import fr.isen.shazamphoto.ui.Home;

public class GetMonumentSearch extends AsyncTask<String, Void, JSONObject> {
    private static final String URL = "http://37.187.216.159/shazam/api.php?n=";
    private HttpClient client;
    private Monument monument;
    private Home home;
    private  JSONObject jsonResponse;
    private String error;

    public GetMonumentSearch(Activity act) {
        client = new DefaultHttpClient();
        client.getParams().setParameter(CoreProtocolPNames.USER_AGENT,
                "android");
        this.home = (Home) act;
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
        } catch (Exception e) {
            error = e.getMessage();
        }
        return jsonResponse;
    }

    public void onPostExecute(JSONObject result) {
       // this.monument = new Monument(result);
        if(jsonResponse != null ) {
            Monument monument = null;
            try{
                JSONArray monumentsJSON = result.getJSONArray("Search");
                TextView textView = (TextView) home.findViewById(R.id.textview_json);
                int nbMonuments = monumentsJSON.length();
                for(int i =0; i<nbMonuments; i++){
                    JSONObject monumentJSON = monumentsJSON.getJSONObject(i);
                    monument = new Monument(monumentJSON, textView);
                }
                //textView.setText(result.getJSONArray("Search").getJSONObject(0).getJSONArray("characteristics").getJSONObject(0).getString("description").toString());
                if(monument != null){
                   // textView.setText("monument : "+ monument.getName());
                }else{
                   // textView.setText("Null");
                }
            }catch(Exception e){
                TextView textView = (TextView) home.findViewById(R.id.textview_json);
                textView.setText(e.getMessage());
            }
        }else{
            Toast.makeText(this.home, error, Toast.LENGTH_LONG).show();
        }
    }

    public String getMonument(String id) {
       // this.monument = new Monument(doInBackground(id));

        return doInBackground(id).toString();
    }
}