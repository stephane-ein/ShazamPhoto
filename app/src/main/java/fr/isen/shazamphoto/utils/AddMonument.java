package fr.isen.shazamphoto.utils;

import android.os.AsyncTask;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import fr.isen.shazamphoto.database.Monument;
import fr.isen.shazamphoto.ui.UnidentifiedMonument;

public class AddMonument extends AsyncTask<Monument, Void, String> {

    private HttpClient httpclient = new DefaultHttpClient();
    private HttpPost httppost = new HttpPost("http://"+ConfigurationShazam.IP_SERVER+"/shazam/api.php");
    private UnidentifiedMonument home;
    private HttpResponse response;

    public AddMonument(UnidentifiedMonument home) {
        this.home = home;
    }

    public String doInBackground(Monument... monuments) {
        Monument monument = monuments[0];

        try {
            String url = "http://"+ConfigurationShazam.IP_SERVER+"/shazam/api.php";
            File file = new File(monument.getPhotoPath());
            HttpClient httpclient = new DefaultHttpClient();

            HttpPost httppost = new HttpPost(url);
            httppost.setHeader("enctype", "multipart/form-data");

            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);

            FileBody cbFile = new FileBody(file);
            builder.addPart("photo", cbFile);
            builder.addPart("monument", new StringBody(monument.toJSON().toString(), ContentType.APPLICATION_JSON));

            httppost.setEntity(builder.build());
            response = httpclient.execute(httppost);
        }
        catch (Exception e) {
            System.out.println("Exception in AddMonument: "+e.getMessage());
        }

        return monument.getName().toString();
    }
}
