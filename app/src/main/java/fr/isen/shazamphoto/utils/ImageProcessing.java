package fr.isen.shazamphoto.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.widget.Toast;

import org.apache.http.HttpResponse;
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

import fr.isen.shazamphoto.utils.Images.LoadPicture;

public class ImageProcessing {

    public static final Size size = new Size(640, 480);

    private HttpResponse response;
    private File imgFile;

    // Information about the picture
    private Mat descriptors;
    private KeyPoint[] keyPointArray;
    private String photoPath;

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

                        Bitmap myBitmap = LoadPicture.getPictureFromFile(photoPath, 640, 480);
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
                        Toast.makeText(activityContext, "Error : The picture has been removed", Toast.LENGTH_LONG).show();
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

    public HttpResponse getResponse() {
        return response;
    }

    public void setResponse(HttpResponse response) {
        this.response = response;
    }
}
