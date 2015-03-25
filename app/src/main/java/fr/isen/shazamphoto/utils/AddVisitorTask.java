package fr.isen.shazamphoto.utils;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

import fr.isen.shazamphoto.database.Monument;


public class AddVisitorTask extends UpdateMonumentTask {

    @Override
    public List<NameValuePair> createArguments(Monument monument) {

        List<NameValuePair> nameValuePairs = new ArrayList<>(2);
        nameValuePairs.add(new BasicNameValuePair("id", String.valueOf(monument.getDatabaseId())));
        nameValuePairs.add(new BasicNameValuePair("nbvisitors", "true"));

        return nameValuePairs;
    }
}