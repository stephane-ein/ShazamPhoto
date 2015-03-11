package fr.isen.shazamphoto.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Base64;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.Point;
import org.opencv.core.Size;
import org.opencv.features2d.DescriptorExtractor;
import org.opencv.features2d.FeatureDetector;
import org.opencv.features2d.KeyPoint;
import org.opencv.imgproc.Imgproc;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import fr.isen.shazamphoto.database.Monument;
import fr.isen.shazamphoto.ui.DetailMonument;

/**
 * Created by carlo_000 on 09/03/2015.
 */
public class ImgProcessing extends AsyncTask<String, Void, JSONObject> {

    public static final Size size = new Size(640, 480);

    private HttpClient httpclient = new DefaultHttpClient();
    private HttpPost httppost = new HttpPost("http://37.187.216.159/shazam/identify.php");
    private HttpResponse response;
    private String keyPointToSend = null;
    private String descriptorToSend = null;


    private Context activityContext = null;


    public ImgProcessing(Context activityContext) {
        setActivityContext(activityContext);
    }

    public Context getActivityContext() {
        return activityContext;
    }

    public void setActivityContext(Context activityContext) {
        this.activityContext = activityContext;
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

    public static MatOfKeyPoint keypointsFromJson(String json) {
        MatOfKeyPoint result = new MatOfKeyPoint();

        JsonParser parser = new JsonParser();
        JsonArray jsonArr = parser.parse(json).getAsJsonArray();

        int size = jsonArr.size();

        KeyPoint[] kpArray = new KeyPoint[size];

        for (int i = 0; i < size; i++) {
            KeyPoint kp = new KeyPoint();

            JsonObject obj = (JsonObject) jsonArr.get(i);

            Point point = new Point(
                    obj.get("x").getAsDouble(),
                    obj.get("y").getAsDouble()
            );

            kp.pt = point;
            kp.class_id = obj.get("class_id").getAsInt();
            kp.size = obj.get("size").getAsFloat();
            kp.angle = obj.get("angle").getAsFloat();
            kp.octave = obj.get("octave").getAsInt();
            kp.response = obj.get("response").getAsFloat();

            kpArray[i] = kp;
        }

        result.fromArray(kpArray);

        return result;
    }

    public static String keypointsToJson(MatOfKeyPoint mat) {
        if (mat != null && !mat.empty()) {
            Gson gson = new Gson();

            JsonArray jsonArr = new JsonArray();

            KeyPoint[] array = mat.toArray();
            for (int i = 0; i < array.length; i++) {
                KeyPoint kp = array[i];

                JsonObject obj = new JsonObject();

                obj.addProperty("class_id", kp.class_id);
                obj.addProperty("x", kp.pt.x);
                obj.addProperty("y", kp.pt.y);
                obj.addProperty("size", kp.size);
                obj.addProperty("angle", kp.angle);
                obj.addProperty("octave", kp.octave);
                obj.addProperty("response", kp.response);

                jsonArr.add(obj);
            }

            String json = gson.toJson(jsonArr);

            return json;
        }
        return "{}";
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

    public void recognise() {
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_9, getActivityContext(), mOpenCVCallBack);
    }
    public final ImgProcessing process = this;
    public BaseLoaderCallback mOpenCVCallBack = new BaseLoaderCallback(getActivityContext()) {

        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {
                    String picturesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString();
                    File imgFile = new File(picturesDir + "/test1.jpg");
                    if (imgFile != null && imgFile.exists()) {

                        Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());

                        FeatureDetector detector = FeatureDetector.create(FeatureDetector.ORB);
                        DescriptorExtractor descriptor = DescriptorExtractor.create(DescriptorExtractor.ORB);


                        Mat img1 = new Mat();
                        Mat resized = new Mat();
                        Mat descriptors1 = new Mat();
                        MatOfKeyPoint keypoints1 = new MatOfKeyPoint();

                        Utils.bitmapToMat(myBitmap, img1);

                        Imgproc.resize(img1, resized, size);
                        Imgproc.cvtColor(img1, img1, Imgproc.COLOR_BGR2GRAY, 7);

                        detector.detect(img1, keypoints1);

                        descriptor.compute(img1, keypoints1, descriptors1);
                        setKeyPointToSend(keypointsToJson(keypoints1));
                        setDescriptorToSend(matToJson(descriptors1));
                        System.out.println(keypointsToJson(keypoints1));
                        process.execute();

                    }
                    else {
                        Toast.makeText(activityContext, "Error : image null or doesn't exist", Toast.LENGTH_LONG).show();
                    }


                }
                break;
                default: {
                    super.onManagerConnected(status);
                }
                break;
            }
        }
    };

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

    }

    @Override
    protected JSONObject doInBackground(String... params) {
        JSONObject jsonResponse=null;
        try {
            List<NameValuePair> nameValuePairs = new ArrayList<>(2);
            nameValuePairs.add(new BasicNameValuePair("keypoints", getKeyPointToSend()));
            nameValuePairs.add(new BasicNameValuePair("descriptor", getDescriptorToSend()));
            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(nameValuePairs);

            entity.setContentType("application/x-www-form-urlencoded");
            httppost.setEntity(entity);
            // Execute HTTP Post Request
            response = httpclient.execute(httppost);
            BufferedReader rd = new BufferedReader(new InputStreamReader(
                    response.getEntity().getContent()));
            StringBuffer result = new StringBuffer();
            String line = "";
            while ((line = rd.readLine()) != null) {
                result.append(line);
            }
            System.out.println(result.toString());
            jsonResponse = new JSONObject(result.toString());
        } catch (Exception e) {

        }
        return jsonResponse;
    }

    public Monument monument;
    @Override
    public void onPostExecute(JSONObject result) {
        try {
            Monument monument = new Monument(result);
            //this.monument = monument;
            System.out.println(monument.getDescription());
            /*Intent intent = new Intent(getActivityContext(), DetailMonument.class);
            FunctionsDB.addMonumentToDB(monument, getActivityContext());
            FunctionsDB.addMonumentToTaggedMonument(monument, getActivityContext());
            intent.putExtra(Monument.NAME_SERIALIZABLE, monument);
            getActivityContext().startActivity(intent);*/

        } catch (Exception e) {

        }
    }



    public HttpPost getHttppost() {
        return httppost;
    }
    public void setHttppost(HttpPost httppost) {
        this.httppost = httppost;
    }
    public HttpResponse getResponse() {
        return response;
    }
    public void setResponse(HttpResponse response) {
        this.response = response;
    }
    public HttpClient getHttpclient() {
        return httpclient;
    }
    public void setHttpclient(HttpClient httpclient) {
        this.httpclient = httpclient;
    }
    public String getKeyPointToSend() {
        return keyPointToSend;
    }
    public void setKeyPointToSend(String keyPointToSend) {
        this.keyPointToSend = keyPointToSend;
    }
    public String getDescriptorToSend() {
        return descriptorToSend;
    }
    public void setDescriptorToSend(String descriptorToSend) {
        this.descriptorToSend = descriptorToSend;
    }
}
