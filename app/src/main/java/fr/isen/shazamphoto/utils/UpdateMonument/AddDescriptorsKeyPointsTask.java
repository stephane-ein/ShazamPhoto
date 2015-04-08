package fr.isen.shazamphoto.utils.UpdateMonument;

import android.app.Activity;
import android.util.Log;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import fr.isen.shazamphoto.database.Descriptors;
import fr.isen.shazamphoto.database.KeyPoints;
import fr.isen.shazamphoto.database.Monument;
import fr.isen.shazamphoto.ui.ItemUtils.UpdateMonumentItem;

public class AddDescriptorsKeyPointsTask extends UpdateMonumentTask{

    public AddDescriptorsKeyPointsTask(Activity activity, UpdateMonumentItem updateMonumentItem) {
        super(activity, updateMonumentItem);
    }

    @Override
    public List<NameValuePair> createArguments(Monument monument) {

        List<NameValuePair> nameValuePairs = new ArrayList<>(3);
        try{
            nameValuePairs.add(new BasicNameValuePair("id", String.valueOf(monument.getDatabaseId())));
            nameValuePairs.add(new BasicNameValuePair("descriptors", Descriptors.toJson(monument.getDescriptors()).toString()));
            JSONObject keyPoints = KeyPoints.toJson(monument.getKeyPoints());
            JSONArray keyPointsArray = new JSONArray();
            JSONObject keyPointsObject = new JSONObject();
            keyPointsObject.put("listskeypoints", keyPoints);
            keyPointsArray.put(keyPoints);
            nameValuePairs.add(new BasicNameValuePair("listskeypoints", keyPointsArray.toString()));
        }catch (Exception e){
            e.printStackTrace();
            Log.e("Shazam", "Exception in ADKPT : "+e.getMessage());
        }


        return nameValuePairs;
    }
}
