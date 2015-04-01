package fr.isen.shazamphoto.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.Size;
import org.opencv.features2d.DescriptorExtractor;
import org.opencv.features2d.FeatureDetector;
import org.opencv.features2d.KeyPoint;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class ImageProcessing /*extends AsyncTask<String, Void, JSONObject>*/ {

    public static final Size size = new Size(640, 480);

    private HttpClient httpclient = new DefaultHttpClient();
    private HttpPost httppost = new HttpPost("http://37.187.216.159/shazam/identify.php");
    private HttpResponse response;
    private String keyPointToSend = null;
    private String descriptorToSend = null;
    private File imgFile;

    // Information about the picture
    private Mat descriptors;
    private KeyPoint[] keyPointArray;
    private String photoPath;

    // Add the several information about the picture to the request to identify the monument
    private ShazamProcessingTask shazamProcessingTask;

    private Context activityContext = null;


    public ImageProcessing(Context activityContext, ShazamProcessingTask shazamProcessingTask,
                           String photoPath) {
        setActivityContext(activityContext);
        this.shazamProcessingTask = shazamProcessingTask;
        this.photoPath = photoPath;
    }

    public Context getActivityContext() {
        return activityContext;
    }

    public void setActivityContext(Context activityContext) {
        this.activityContext = activityContext;
    }

    public void recognise() {
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_9, getActivityContext(), mOpenCVCallBack);
    }

    public final ImageProcessing process = this;
    public BaseLoaderCallback mOpenCVCallBack = new BaseLoaderCallback(getActivityContext()) {

        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {
                    String picturesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString();
                    String path =  photoPath; //picturesDir +"/test1.jpg";
                    imgFile = new File(path);
                    if (imgFile != null && imgFile.exists()) {

                        //Bitmap myBitmap = BitmapFactory.decodeFile(path);
                        Bitmap myBitmap = LoadPicture.getPictureFromFile(photoPath, LoadPicture.IMAGE_PROCESS_WIDTH, LoadPicture.IMAGE_PROCESS_HEIGHT);
                        FeatureDetector detector = FeatureDetector.create(FeatureDetector.ORB);
                        DescriptorExtractor extractor = DescriptorExtractor.create(DescriptorExtractor.ORB);

                        File outputDir = getActivityContext().getCacheDir(); // If in an Activity (otherwise getActivity.getCacheDir();
                        File outputFile = null;
                        try {
                            outputFile = File.createTempFile("orbDetectorParams", ".YAML", outputDir);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        writeToFile(outputFile, "%YAML:1.0\nscaleFactor: 1.0\nnLevels: 8\nfirstLevel: 0 "+"\nedgeThreshold: 31\npatchSize: 31\nWTA_K: 2\nscoreType: 0\nnFeatures: 8000\n");
                        detector.read(outputFile.getPath());

                        Mat img1 = new Mat();
                        Mat resized = new Mat();
                        descriptors = new Mat();
                        MatOfKeyPoint keypoints = new MatOfKeyPoint();

                        Utils.bitmapToMat(myBitmap, img1);

                        Imgproc.resize(img1, resized, size);


                        detector.detect(resized, keypoints);

                        extractor.compute(resized, keypoints, descriptors);

                        descriptors = descriptors.clone();

                        byte buff[] = new byte[(int)descriptors.total() * descriptors.channels()];
                        descriptors.get(0, 0, buff);


                        keyPointArray = keypoints.toArray();

                        shazamProcessingTask.setDescriptorsKeyKeyPoints(descriptors, keyPointArray);
                        shazamProcessingTask.setPhotoPath(path);

                    } else {
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


    private void writeToFile(File file, String data) {


        try {
            FileOutputStream stream = new FileOutputStream(file);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(stream);
            outputStreamWriter.write(data);
            outputStreamWriter.close();
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
/*
    @Override
    protected void onPreExecute() {
        super.onPreExecute();

    }

    @Override
    protected JSONObject doInBackground(String... params) {
        JSONObject jsonResponse = null;
        try {
            List<NameValuePair> nameValuePairs = new ArrayList<>(2);
            nameValuePairs.add(new BasicNameValuePair("listskeypoints", KeyPoints.toJson(this.keyPointArray).toString()));
            nameValuePairs.add(new BasicNameValuePair("descriptors", Descriptors.toJson(this.descriptors).toString()));
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
            System.out.println("Result Identify : " + result.toString());
            jsonResponse = new JSONObject(result.toString());
        } catch (Exception e) {

        }
        return jsonResponse;
    }

    @Override
    public void onPostExecute(JSONObject result) {
        //monument doesn't exist
        Monument monument1 = new Monument();
        monument1.setDescriptors(this.descriptors);
        monument1.setKeyPoints(this.keyPointArray);
        Intent intent = new Intent(this.getActivityContext(), UnidentifiedMonument.class);
        Bundle args = new Bundle();
        args.putSerializable(Monument.NAME_SERIALIZABLE, monument1);
        intent.putExtras(args);
        getActivityContext().startActivity(intent);

        try {

            if(result != null){
                if (result.toString().equals("{}")) {
                    Toast.makeText(this.activityContext, " Monument not identify", Toast.LENGTH_LONG).show();
                    Monument monument = new Monument();
                    monument.setDescriptors(this.descriptors);
                    monument.setKeyPoints(this.keyPointArray);
                    Intent intent = new Intent(this.getActivityContext(), UnidentifiedMonument.class);
                    Bundle args = new Bundle();
                    args.putSerializable(Monument.NAME_SERIALIZABLE, monument);
                    intent.putExtras(args);
                    getActivityContext().startActivity(intent);
                } else{
                    Toast.makeText(this.activityContext, "Monument identified", Toast.LENGTH_LONG).show();
                    Toast.makeText(this.activityContext, "Result : "+result.toString(), Toast.LENGTH_LONG).show();
                }
            }else{
                Toast.makeText(this.activityContext, "Answser server NULL", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(this.activityContext, "Error : "+e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
*/

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

    public void setKeyPointArray(KeyPoint[] keyPointArray) {
        this.keyPointArray = keyPointArray;
    }


}
