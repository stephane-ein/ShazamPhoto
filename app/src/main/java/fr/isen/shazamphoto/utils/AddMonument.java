package fr.isen.shazamphoto.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import fr.isen.shazamphoto.database.Monument;
import fr.isen.shazamphoto.utils.Images.LoadPicture;

public class AddMonument extends AsyncTask<Monument, Void, Boolean> {

    public AddMonument() {
    }

    public Boolean doInBackground(Monument... monuments) {
        Monument monument = monuments[0];
        Boolean returnValue = false;
        try {
            File file = new File(monument.getPhotoPath());

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("http://"+ConfigurationShazam.IP_SERVER+"/shazam/api.php");
            httppost.setHeader("enctype", "multipart/form-data");

            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);

            FileBody cbFile = new FileBody(file);
            builder.addPart("photo", cbFile);
            builder.addPart("monument", new StringBody(monument.toJSON().toString(), ContentType.APPLICATION_JSON));

            httppost.setEntity(builder.build());
            HttpResponse response = httpclient.execute(httppost);

            if(response.getStatusLine().getStatusCode() == 200) {
                returnValue = true;
            }
        }
        catch (Exception e) {
            Log.e("Shazam", "Exception in AddMonument: " + e.getMessage());
        }

        return returnValue;
    }
}
