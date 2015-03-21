package fr.isen.shazamphoto.database;

import android.util.Base64;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONObject;
import org.opencv.core.Mat;

import java.util.Arrays;

public class Descriptors {

    public static final String KEY = "descriptors";

    public static JSONArray toJson(Mat descriptors) {

        //Parse the JSON descriptor
        JSONArray jsonArrayDescriptor = new JSONArray();
        JSONObject objDesciprtor = new JSONObject();
        String dataString = "";

        try {
            if (descriptors.isContinuous()) {


                int cols = descriptors.cols();
                int rows = descriptors.rows();
                int elemSize = (int) descriptors.elemSize();

                byte[] data = new byte[cols * rows * elemSize];

                descriptors.get(0, 0, data);

                objDesciprtor.put("rows", descriptors.rows());
                objDesciprtor.put("cols", descriptors.cols());
                objDesciprtor.put("type", descriptors.type());

                // We cannot set binary data to a json object, so:
                // Encoding data byte array to Base64.
                dataString = new String(Base64.encode(data, Base64.DEFAULT));

                objDesciprtor.put("data", dataString);

                // Display the data encoded
                //System.out.println(dataString);

                // Display the data decoded
                for(int i =0; i<60; i++) System.out.println(data[i]);

            }

            jsonArrayDescriptor.put(objDesciprtor);

        } catch (Exception e) {
        }

        return jsonArrayDescriptor;

    }
}