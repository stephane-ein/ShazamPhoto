package fr.isen.shazamphoto.utils.GetMonumentTask;

import android.app.Activity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import fr.isen.shazamphoto.database.Monument;
import fr.isen.shazamphoto.events.EventInternetTask;
import fr.isen.shazamphoto.ui.ItemUtils.SearchableItem;
import fr.isen.shazamphoto.ui.NetworkInfoArea;

public class GetMonumentsByName extends GetMonumentTask {
    private SearchableItem searchableItem;

    public GetMonumentsByName(NetworkInfoArea networkInfoArea, Activity activity,
                              SearchableItem searchableItem, String searchName) {
        super(networkInfoArea, activity);
        this.searchableItem = searchableItem;
        searchName = searchName.replace(" ", "+");
        setUrlWithArguments("n="+searchName);
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

            this.searchableItem.onPostSearch(monuments);
        }
    }
}