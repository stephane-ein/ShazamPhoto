package fr.isen.shazamphoto.database;

import org.json.JSONArray;
import org.json.JSONObject;
import org.opencv.features2d.KeyPoint;


public class KeyPoints {

    public static final String KEY ="listskeypoints";

    public static JSONObject toJson(KeyPoint[] keyPoints){

        JSONObject list = new JSONObject();

        try{
            JSONArray jsonArr = new JSONArray();
            for (int i = 0; i < keyPoints.length; i++) {
                KeyPoint kp = keyPoints[i];

                JSONObject obj = new JSONObject();

                obj.put("class_id", kp.class_id);
                obj.put("x", kp.pt.x);
                obj.put("y", kp.pt.y);
                obj.put("size", kp.size);
                obj.put("angle", kp.angle);
                obj.put("octave", kp.octave);
                obj.put("response", kp.response);

                jsonArr.put(obj);
            }

            list.put("keypoints", jsonArr);

        }catch (Exception e){}

        return list;
    }
}
