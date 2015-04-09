package fr.isen.shazamphoto.utils.UpdateMonument;

import android.app.Activity;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;

import fr.isen.shazamphoto.database.Monument;
import fr.isen.shazamphoto.events.EventInternetTask;
import fr.isen.shazamphoto.events.EventMonumentUpdated;
import fr.isen.shazamphoto.ui.ItemUtils.UpdateMonumentItem;
import fr.isen.shazamphoto.utils.ConfigurationShazam;
import fr.isen.shazamphoto.utils.InternetTask;

public abstract class   UpdateMonumentTask extends InternetTask<Monument, Void, EventInternetTask> {

    private UpdateMonumentItem updateMonumentItem;

    protected UpdateMonumentTask(Activity activity, UpdateMonumentItem updateMonumentItem) {
        super(activity);
        this.updateMonumentItem = updateMonumentItem;
    }

    public abstract List<NameValuePair> createArguments(Monument monument);

    @Override
    protected EventInternetTask doInBackground(Monument... params) {
        Monument monument = params[0];
        Boolean isNetworkFound = false;
        JSONObject jsonResponse = new JSONObject();
        try {
            isNetworkFound = checkNetwork();
            if(isNetworkFound){
                List<NameValuePair> args = createArguments(monument);
                for (NameValuePair value : args){
                    Log.v("Shazam", "UMT ListNamePairValue key : "+value.getName()+" value :"+value.getValue());
                }
                UrlEncodedFormEntity entity = new UrlEncodedFormEntity(createArguments(monument));
                entity.setContentType("application/x-www-form-urlencoded");

                HttpClient httpclient = new DefaultHttpClient();
                HttpPut httpPut = new HttpPut("http://"+ ConfigurationShazam.IP_SERVER+"/shazam/api.php");
                httpPut.setEntity(entity);

                // Executing HTTP Post Request
                HttpResponse response = httpclient.execute(httpPut);


                if(response.getStatusLine().getStatusCode() == 200) {
                    // Retrieve the monument
                    BufferedReader rd = new BufferedReader(new InputStreamReader(
                            response.getEntity().getContent()));
                    StringBuffer result = new StringBuffer();
                    String line = "";
                    while ((line = rd.readLine()) != null) {
                        result.append(line);
                    }

                    Log.v("Shazam", "UMT result : "+result.toString());

                    jsonResponse = new JSONObject(result.toString());

                }
            }

        }
        catch(Exception e) {
            e.printStackTrace();
            Log.e("Shazam", "Exception in UMT : "+e.getMessage());
        }

        return new EventInternetTask(isNetworkFound, jsonResponse);
    }

    @Override
    public void onPostExecute(EventInternetTask event){
        Monument m = new Monument(event.getJsonResponse());
        boolean isNetworkFound = event.isInternetfound();

        if(isNetworkFound){
            updateMonumentItem.monumentUpdated(new EventMonumentUpdated(m));
        }
    }

}
