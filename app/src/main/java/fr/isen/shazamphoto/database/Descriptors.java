package fr.isen.shazamphoto.database;
import android.app.Activity;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONObject;
import org.opencv.core.Mat;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class Descriptors {

    public static final String KEY = "descriptors";
    public static Activity activity;
    public static char[] b64 = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".toCharArray();
    public static String base64_chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";

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
                //dataString = new String(Base64.encode(data, Base64.DEFAULT));

                Log.v("Shazam", "Descriptors before ");

                //String byteString = new String(data);
                StringBuilder byteString = new StringBuilder();
                for(int i = 0; i < data.length; i++){
                    byteString.append(Integer.valueOf(data[i]).toString());

                }


                dataString = mEncode(byteString.toString());

                // Display data encoded


                objDesciprtor.put("data", dataString);

                // Display the data encoded
                //System.out.println(dataString);
                // Display the data decoded
                //for(int i =0; i<data.length; i++) System.out.println(data[i]);
            }

            jsonArrayDescriptor.put(objDesciprtor);
        } catch (Exception e) {
            Log.v("Shazam", "Descriptors exception : "+e.getMessage());
        }

        return jsonArrayDescriptor;
    }

    public static String mEncode(String toEncode) {

        Log.v("Shazam", "Descriptors : " + toEncode);
        int expectedSize = toEncode.length();
        StringBuilder base64Str = new StringBuilder();

        if (toEncode.length() == 0) {
            return base64Str.toString();
        } else {
            while ((expectedSize % 3) != 0) {
                expectedSize++;
            }

            int i = 0;
            for (i = 0; i < toEncode.length()-3; i += 3) {
                String block = toEncode.substring(i, i + 3);
                String encodedBlock=blockEncode(block);
                base64Str.append(encodedBlock);

            }
            if(toEncode.length()<expectedSize){
                StringBuilder lastBlock = new StringBuilder();
                for (; i < expectedSize; i++) {
                    if (i < toEncode.length()) {
                        lastBlock.append(toEncode.charAt(i));
                    }else{

                        lastBlock.append("0");
                    }
                }

                String encodedBlock=blockEncode(lastBlock.toString());
                base64Str.append(encodedBlock);
            }
        }
        return base64Str.toString();
    }

    public static String blockEncode(String block) {

        char mask1 = 0b0000000000000011;
        char mask2 = 0b0000000011110000;
        char mask3 = 0b0000000000001111;
        char mask4 = 0b0000000011000000;
        char mask5 = 0b0000000000111111;
        int[] b64Index = new int[4];
        b64Index[0] = block.charAt(0)>>2;
        b64Index[1] = ((block.charAt(0) & mask1 ) <<4 ) ^((block.charAt(1) & mask2) >>4 );
        b64Index[2] = ((block.charAt(1) & mask3 ) <<2 ) ^((block.charAt(2) & mask4) >>6 );
        b64Index[3] = block.charAt(2) & (mask5);

        char[] myChar = {base64_chars.charAt(b64Index[0]), base64_chars.charAt(b64Index[1]), base64_chars.charAt(b64Index[2]), base64_chars.charAt(b64Index[3])};

        String encodedBlock =  new String(myChar);

        return encodedBlock;
    }
}