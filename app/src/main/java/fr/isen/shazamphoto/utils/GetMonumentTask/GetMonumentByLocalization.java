package fr.isen.shazamphoto.utils.GetMonumentTask;

import android.app.Activity;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import fr.isen.shazamphoto.database.Monument;
import fr.isen.shazamphoto.events.EventInternetTask;
import fr.isen.shazamphoto.ui.ItemUtils.SearchMonumentsByLocalization;
import fr.isen.shazamphoto.utils.ConfigurationShazam;

public class GetMonumentByLocalization extends GetMonumentTask {

    private SearchMonumentsByLocalization searchMonumentsByLocalization;

    public GetMonumentByLocalization(SearchMonumentsByLocalization item,
                                     Activity activity, Double latitude, Double longitude) {
        super(activity);
        this.searchMonumentsByLocalization = item;
        setUrlWithArguments("la=" + latitude + "&lo=" + longitude + "&o=" + ConfigurationShazam.DELTA_LOCALIZATION);
    }

    public void onPostExecute(EventInternetTask result) {
        // Retrieve the JSON Object
        JSONObject jsonResponse = result.getJsonResponse();
        // update the UI if there is not internet
        updateUI(result.isInternetfound());
        ArrayList<Monument> monuments = new ArrayList<>();
        try {
            JSONArray monumentsJSON = jsonResponse.getJSONArray("Search");
            int nbMonuments = monumentsJSON.length();
            for (int i = 0; i < nbMonuments; i++) {
                JSONObject monumentJSON = monumentsJSON.getJSONObject(i);
                monuments.add(new Monument(monumentJSON));
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("Shazam", "Exception in GetMonumentByLocalization : " + e.getMessage());
        }
        searchMonumentsByLocalization.monumentsFoundByLocalization(monuments);


    }
}