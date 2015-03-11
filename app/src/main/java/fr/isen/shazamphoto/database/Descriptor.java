package fr.isen.shazamphoto.database;

import android.util.Base64;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.opencv.core.Mat;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by sylvain on 11/03/15.
 */
public class Descriptor extends Mat implements Serializable {
    public JSONObject toJson() {
        JSONObject jsonObj = new JSONObject();
        try {
            // Here we convert Java Object to JSO
            jsonObj.put("dims", Integer.valueOf(dims()).toString());
            jsonObj.put("rows", Integer.valueOf(rows()).toString());
            jsonObj.put("cols", Integer.valueOf(cols()).toString());
            byte[] data = new byte[(int)(cols() * rows() * elemSize())];

            get(0, 0, data);

            // We cannot set binary data to a json object, so:
            // Encoding data byte array to Base64.
            String dataString = new String(Base64.encode(data, Base64.DEFAULT));
            jsonObj.put("data", dataString);
        } catch (JSONException ex) {}

        return jsonObj;
    }
}
