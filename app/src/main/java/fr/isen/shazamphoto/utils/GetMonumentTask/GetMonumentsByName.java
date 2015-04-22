package fr.isen.shazamphoto.utils.GetMonumentTask;

import android.app.Activity;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import fr.isen.shazamphoto.database.Monument;
import fr.isen.shazamphoto.events.EventInternetTask;
import fr.isen.shazamphoto.ui.ItemUtils.SearchableItem;

public class GetMonumentsByName extends GetMonumentTask {
    private SearchableItem searchableItem;
    private String searchName;

    public GetMonumentsByName(Activity activity,
                              SearchableItem searchableItem, String searchName) {
        super(activity);
        this.searchableItem = searchableItem;
        searchName = searchName.replace(" ", "+");
        setUrlWithArguments("n="+searchName);
        this.searchName = searchName;
        Log.v("Shazam", "GetMonumentTask searchName : "+searchName);
    }

    public void onPostExecute(EventInternetTask result) {
        // Retrieve the JSON Object
        JSONObject jsonResponse = result.getJsonResponse();
        // update the UI if there is not internet
        updateUI(result.isInternetfound());
        if (jsonResponse != null) {
            ArrayList<Monument> monuments = new ArrayList<>();
            try {
                JSONArray monumentsJSON = jsonResponse.getJSONArray("Search");
                int nbMonuments = monumentsJSON.length();
                for (int i = 0; i < nbMonuments; i++) {
                    JSONObject monumentJSON = monumentsJSON.getJSONObject(i);
                    monuments.add(new Monument(monumentJSON));
                }
            } catch (Exception e) {
            }

            this.searchableItem.onPostSearch(monuments, searchName);
            Log.v("Shazam", "GMBN name : "+searchName+" found : "+monuments.size());
        }
    }
}