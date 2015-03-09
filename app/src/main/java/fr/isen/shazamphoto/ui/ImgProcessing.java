package fr.isen.shazamphoto.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Base64;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.features2d.DescriptorExtractor;
import org.opencv.features2d.FeatureDetector;
import org.opencv.features2d.Features2d;
import org.opencv.features2d.KeyPoint;
import org.opencv.imgproc.Imgproc;

import java.io.File;

import fr.isen.shazamphoto.R;

/**
 * Created by carlo_000 on 09/03/2015.
 */
public class ImgProcessing {

    private Context activityContext=null;

    public Context getActivityContext() {
        return activityContext;
    }

    public void setActivityContext(Context activityContext) {
        this.activityContext = activityContext;
    }

    public static Mat matFromJson(String json){
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
    public static MatOfKeyPoint keypointsFromJson(String json){
        MatOfKeyPoint result = new MatOfKeyPoint();

        JsonParser parser = new JsonParser();
        JsonArray jsonArr = parser.parse(json).getAsJsonArray();

        int size = jsonArr.size();

        KeyPoint[] kpArray = new KeyPoint[size];

        for(int i=0; i<size; i++){
            KeyPoint kp = new KeyPoint();

            JsonObject obj = (JsonObject) jsonArr.get(i);

            Point point = new Point(
                    obj.get("x").getAsDouble(),
                    obj.get("y").getAsDouble()
            );

            kp.pt       = point;
            kp.class_id = obj.get("class_id").getAsInt();
            kp.size     =     obj.get("size").getAsFloat();
            kp.angle    =    obj.get("angle").getAsFloat();
            kp.octave   =   obj.get("octave").getAsInt();
            kp.response = obj.get("response").getAsFloat();

            kpArray[i] = kp;
        }

        result.fromArray(kpArray);

        return result;
    }
    public static String keypointsToJson(MatOfKeyPoint mat){
        if(mat!=null && !mat.empty()){
            Gson gson = new Gson();

            JsonArray jsonArr = new JsonArray();

            KeyPoint[] array = mat.toArray();
            for(int i=0; i<array.length; i++){
                KeyPoint kp = array[i];

                JsonObject obj = new JsonObject();

                obj.addProperty("class_id", kp.class_id);
                obj.addProperty("x",        kp.pt.x);
                obj.addProperty("y",        kp.pt.y);
                obj.addProperty("size",     kp.size);
                obj.addProperty("angle",    kp.angle);
                obj.addProperty("octave",   kp.octave);
                obj.addProperty("response", kp.response);

                jsonArr.add(obj);
            }

            String json = gson.toJson(jsonArr);

            return json;
        }
        return "{}";
    }
    public static String matToJson(Mat mat){
        JsonObject obj = new JsonObject();

        if(mat.isContinuous()){
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
    private BaseLoaderCallback mOpenCVCallBack = new BaseLoaderCallback(getActivityContext()) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {

                    String picturesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString();
                    File imgFile = new  File(picturesDir+"/test1.jpg");
                    File imgFile2 = new  File(picturesDir+"/test2.jpg");
                    Toast.makeText(getActivityContext(), picturesDir, Toast.LENGTH_LONG).show();

                    if(imgFile.exists()) {

                        Toast.makeText(getActivityContext(), "computing images", Toast.LENGTH_LONG).show();
                        Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                        Bitmap myBitmap2 = BitmapFactory.decodeFile(imgFile2.getAbsolutePath());

                        FeatureDetector detector = FeatureDetector.create(FeatureDetector.ORB);
                        DescriptorExtractor descriptor = DescriptorExtractor.create(DescriptorExtractor.ORB);

                        Mat img1 =new Mat();
                        Mat img2 = new Mat();
                        Mat descriptors1 = new Mat();
                        Mat descriptors2 = new Mat();
                        MatOfKeyPoint keypoints1 = new MatOfKeyPoint();
                        MatOfKeyPoint keypoints2 = new MatOfKeyPoint();

                        Utils.bitmapToMat(myBitmap, img1);
                        Utils.bitmapToMat(myBitmap2,img2);

                        Imgproc.cvtColor(img1, img1, Imgproc.COLOR_BGR2GRAY, 7);
                        Imgproc.cvtColor(img2, img2, Imgproc.COLOR_BGR2GRAY, 7);

                        detector.detect(img1, keypoints1);
                        detector.detect(img2, keypoints2);

                        descriptor.compute(img1, keypoints1, descriptors1);
                        descriptor.compute(img2, keypoints2, descriptors2);

                        System.out.println(keypointsToJson(keypoints1));
                        System.out.println(keypointsToJson(keypoints2));

                        System.out.println(matToJson(descriptors1));
                        System.out.println(matToJson(descriptors2));



                    }

                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };
}
