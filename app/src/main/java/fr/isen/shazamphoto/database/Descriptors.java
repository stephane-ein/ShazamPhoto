package fr.isen.shazamphoto.database;

import android.util.Base64;

import org.json.JSONArray;
import org.json.JSONObject;
import org.opencv.core.Mat;

import java.util.Arrays;

public class Descriptors {

    public static final String KEY = "descriptors";

    public static JSONArray toJson(Mat descriptors){

        //Parse the JSON descriptor
        JSONArray jsonArrayDescriptor = new JSONArray();
        JSONObject objDesciprtor = new JSONObject();

        try{
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
                // String dataString = new String(Base64.encode(data, Base64.DEFAULT));
                // objDesciprtor.put("data", dataString);

                byte buff[] = new byte[(int)descriptors.total() * descriptors.channels()];
                descriptors.get(0, 0, buff);

                System.out.println("Description \n"+ Arrays.toString(buff));
                objDesciprtor.put("data",  Arrays.toString(buff));
            }

            jsonArrayDescriptor.put(objDesciprtor);

        }catch(Exception e){}

        return jsonArrayDescriptor;

    }
}
