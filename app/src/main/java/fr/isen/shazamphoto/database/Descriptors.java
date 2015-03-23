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

                String byteString = new String(data);

                dataString = mEncode(byteString);

                // Display data encoded
                Log.v("Shazam", "Data encoded : " + dataString);

                if(activity != null){
                    try {
                        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString();
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
        } catch (Exception e) { }

        return jsonArrayDescriptor;
    }

    public static String mEncode(String toEncode) {

        int expectedSize = toEncode.length();
        String base64Str = "";

        if (toEncode.length() == 0) {
            return base64Str;
        } else {
            while ((expectedSize % 3) != 0) {
                expectedSize++;
            }

            int i = 0;
            for (i = 0; i < toEncode.length(); i += 3) {
                String block = toEncode.substring(i, i + 3);
                String encodedBlock=blockEncode(block);
                base64Str = base64Str + encodedBlock;

            }
            if(toEncode.length()<expectedSize){
                String lastBlock = "";
                for (; i < expectedSize; i++) {
                    if (i < toEncode.length()) {
                        lastBlock = lastBlock + toEncode.charAt(i);
                    }else{

                        lastBlock = lastBlock + "0";
                    }
                }

                String encodedBlock=blockEncode(lastBlock);
                base64Str = base64Str + encodedBlock;
            }
        }
        return base64Str;
    }

    public static String blockEncode(String block) {

        char mask1 = 0b00000011;
        char mask2 = 0b11110000;
        char mask3 = 0b00001111;
        char mask4 = 0b11000000;

        int[] b64Index = new int[4];
        b64Index[0] = block.charAt(0)>>2;
        b64Index[1] = ((block.charAt(0) & mask1 ) <<4 ) ^((block.charAt(1) & mask2) >>4 );
        b64Index[2] = ((block.charAt(1) & mask3 ) <<2 ) ^((block.charAt(2) & mask4) >>6 );
        b64Index[3] = block.charAt(2) & (~mask4);

        char[] myChar = {base64_chars.charAt(b64Index[0]), base64_chars.charAt(b64Index[1]), base64_chars.charAt(b64Index[2]), base64_chars.charAt(b64Index[3])};

        String encodedBlock =  new String(myChar);

        return encodedBlock;
    }
}