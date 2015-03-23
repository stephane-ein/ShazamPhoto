package fr.isen.shazamphoto.utils;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

import fr.isen.shazamphoto.database.Monument;

/**
 * Created by sylvain on 23/03/15.
 */
public class AddLikeTask extends UpdateMonumentTask {
    @Override
    public List<NameValuePair> createArguments(Monument monument) {
        UrlEncodedFormEntity entity = null;

        List<NameValuePair> nameValuePairs = new ArrayList<>(2);
        nameValuePairs.add(new BasicNameValuePair("id", String.valueOf(monument.getDatabaseId())));
        nameValuePairs.add(new BasicNameValuePair("nblikes", "true"));

        return nameValuePairs;
    }
}
