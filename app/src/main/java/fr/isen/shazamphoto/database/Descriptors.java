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
        String dataStringToSend = "";

        try {
            if (descriptors.isContinuous()) {


              /*  int cols = descriptors.cols();
                int rows = descriptors.rows();
                int elemSize = (int) descriptors.elemSize();

                byte[] data = new byte[cols * rows * elemSize];

                descriptors.get(0, 0, data);

                objDesciprtor.put("rows", descriptors.rows());
                objDesciprtor.put("cols", descriptors.cols());
                objDesciprtor.put("type", descriptors.type());
*/
                // We cannot set binary data to a json object, so:
                // Encoding data byte array to Base64.
                //dataString = Base64.encodeToString(data, Base64.DEFAULT);
                dataString = matToJson(descriptors);

               // objDesciprtor.put("data", dataString);


                System.out.println(dataString);
                // Display the data
               /* byte[] data2 = new byte[cols * rows * elemSize];
                descriptors.get(0, 0, data2);
                System.out.println("Descriptors data : \n\n");
                for(int i =0; i<data2.length; i++){

                    System.out.println(data2[i]);
                }*/

                /*
                byte[] data2 = Base64.decode(dataString.getBytes(), Base64.DEFAULT);
                String text = new String(dataString.getBytes("UTF-8"), "UTF-8");
                System.out.println("Descriptors decoded\n" + text);*/

                // byte buff[] = new byte[(int)descriptors.total() * descriptors.channels()];
                // descriptors.get(0, 0, buff);
                // System.out.println("Description \n"+ Arrays.toString(buff));
                // objDesciprtor.put("data",  Arrays.toString(buff));
            }

            jsonArrayDescriptor.put(dataString);

        } catch (Exception e) {
        }

        return jsonArrayDescriptor;

    }

    public static String matToJson(Mat mat) {
        JsonObject obj = new JsonObject();

        if (mat.isContinuous()) {
            int cols = mat.cols();
            int rows = mat.rows();
            int elemSize = (int) mat.elemSize();

            byte[] data = new byte[cols * rows * elemSize];

            mat.get(0, 0, data);

            obj.addProperty("rows", mat.rows());
            obj.addProperty("cols", mat.cols());
            obj.addProperty("type", mat.type());

            // We cannot set binary data to a json object, so:
            // Encoding data byte array to Base64.
            String dataString = new String(Base64.encode(data, Base64.DEFAULT));

            obj.addProperty("data", dataString);

            Gson gson = new Gson();
            String json = gson.toJson(obj);

            return json;
        } else {

        }
        return "{}";
    }


    public static Mat matFromJson(String json) {
        JsonParser parser = new JsonParser();
        JsonObject JsonObject = parser.parse(json).getAsJsonObject();

        int rows = JsonObject.get("rows").getAsInt();
        int cols = JsonObject.get("cols").getAsInt();
        int type = JsonObject.get("type").getAsInt();

        String dataString = JsonObject.get("data").getAsString();
        byte[] data = Base64.decode(dataString.getBytes(), Base64.DEFAULT);

        Mat mat = new Mat(rows, cols, type);
        mat.put(0, 0, data);

        return mat;
    }
}