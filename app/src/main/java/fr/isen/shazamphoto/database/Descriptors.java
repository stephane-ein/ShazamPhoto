package fr.isen.shazamphoto.database;

import android.app.Activity;
import android.content.Context;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONObject;
import org.opencv.core.Mat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Arrays;

public class Descriptors {

    public static final String KEY = "descriptors";
    public static Activity activity;

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

                if(activity != null){
                    try {
                        String path =  Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString();
                        File file = new File(path + "/dataString2.txt");
                        FileOutputStream stream = new FileOutputStream(file);
                        try {
                            stream.write(dataString.getBytes());
                        } finally {
                            stream.close();
                        }
                    }
                    catch (IOException e) {
                        Log.e("Exception", "File write failed: " + e.toString());
                    }
                }else {
                    System.out.println("Activity null");
                }


                objDesciprtor.put("data", dataString);

                // Display the data encoded
                //System.out.println(dataString);

                // Display the data decoded
                //for(int i =0; i<data.length; i++) System.out.println(data[i]);

            }

            jsonArrayDescriptor.put(objDesciprtor);

        } catch (Exception e) {
        }

        return jsonArrayDescriptor;

    }
}