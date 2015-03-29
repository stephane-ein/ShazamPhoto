package fr.isen.shazamphoto.utils.UpdateMonument;

import android.app.Activity;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

import fr.isen.shazamphoto.database.Monument;
import fr.isen.shazamphoto.ui.ItemUtils.UpdateMonumentItem;
import fr.isen.shazamphoto.ui.NetworkInfoArea;

public class AddLikeTask extends UpdateMonumentTask {

    public AddLikeTask(NetworkInfoArea networkInfo, Activity activity, UpdateMonumentItem updateMonumentItem) {
        super(networkInfo, activity, updateMonumentItem);
    }

    @Override
    public List<NameValuePair> createArguments(Monument monument) {
        UrlEncodedFormEntity entity = null;

        List<NameValuePair> nameValuePairs = new ArrayList<>(2);
        nameValuePairs.add(new BasicNameValuePair("id", String.valueOf(monument.getDatabaseId())));
        nameValuePairs.add(new BasicNameValuePair("nblikes", "true"));

        return nameValuePairs;
    }
}
