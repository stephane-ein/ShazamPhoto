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
                dataString = mEncode(data);
                objDesciprtor.put("data", dataString);
            }

            jsonArrayDescriptor.put(objDesciprtor);
        } catch (Exception e) {
            Log.v("Shazam", "Descriptors exception : "+e.getMessage());
        }

        return jsonArrayDescriptor;
    }

    public static String mEncode(byte[] toEncode) {

        int expectedSize = toEncode.length;
        StringBuilder base64Str = new StringBuilder();

        if (toEncode.length == 0) {
            return base64Str.toString();
        } else {
            while ((expectedSize % 3) != 0) {
                expectedSize++;
            }

            int i = 0;
            for (i = 0; i+3 < toEncode.length; i += 3) {
               // String block = toEncode.substring(i, i + 3);
                String encodedBlock = blockEncode(toEncode[i], toEncode[i+1], toEncode[i+2], toEncode[i+3]);
                base64Str.append(encodedBlock);
            }

            if(toEncode.length<expectedSize){
                byte[] lastBlock = new byte[4];
                int k = 0;
                for (int j = i; j < expectedSize; j++) {
                    if (j < toEncode.length) {
                        lastBlock[k] = toEncode[j];
                    }else{
                        lastBlock[k] = 0;
                    }
                    k++;
                }

                String encodedBlock=blockEncode(lastBlock[0], lastBlock[1], lastBlock[2], lastBlock[3]);
                base64Str.append(encodedBlock);
            }
        }

        return base64Str.toString();
    }

    public static String blockEncode(byte arg1B, byte arg2B, byte arg3B, byte arg4B) {

        int arg1 = checkByte(arg1B);
        int arg2 = checkByte(arg2B);
        int arg3 = checkByte(arg3B);
        int arg4 = checkByte(arg4B);

        char mask1 = 0b0000000000000011;
        char mask2 = 0b0000000011110000;
        char mask3 = 0b0000000000001111;
        char mask4 = 0b0000000011000000;
        char mask5 = 0b0000000000111111;
        int[] b64Index = new int[4];
        b64Index[0] = arg1>>2;
        b64Index[1] = ((arg1 & mask1 ) <<4 ) ^((arg2 & mask2) >>4 );
        b64Index[2] = ((arg2 & mask3 ) <<2 ) ^((arg3 & mask4) >>6 );
        b64Index[3] = arg3 & (mask5);

        char[] myChar = {base64_chars.charAt(b64Index[0]), base64_chars.charAt(b64Index[1]), base64_chars.charAt(b64Index[2]), base64_chars.charAt(b64Index[3])};

        String encodedBlock =  new String(myChar);

        return encodedBlock;
    }

    public static int checkByte(byte arg ){
        int a = Byte.valueOf(arg).intValue();
        if(a<0){
            a+=256;
        }

        return a;
    }
}