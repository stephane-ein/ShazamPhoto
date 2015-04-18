package fr.isen.shazamphoto.utils.Images;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.os.Environment;
import android.util.Log;
import android.view.Display;
import android.widget.ImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import fr.isen.shazamphoto.database.Monument;

public class LoadPicture {

    private static int HDPI_WIDTH_VERTICAL;
    private static int HDPI_HEIGHT_VERTICAL;
    private static int HDPI_WIDTH_HORIZONTAL;
    private static int HDPI_HEIGHT_HORIZONTAL;
    private static int IMAGE_PROCESS_WIDTH = 210;
    private static int IMAGE_PROCESS_HEIGHT = 260;

    public static Point getSize(Activity activity) {
        Display display = activity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size;
    }

    public static int getHdpiWidthVertical(Activity activity) {
        HDPI_WIDTH_VERTICAL = getSize(activity).x;

        return HDPI_WIDTH_VERTICAL;
    }

    public static int getHdpiHeightVertical(Activity activity) {
        HDPI_HEIGHT_VERTICAL = (int) Math.round(0.66 * getSize(activity).y);

        return HDPI_HEIGHT_VERTICAL;
    }

    public static int getHdpiWidthHorizontal(Activity activity) {
        HDPI_WIDTH_HORIZONTAL = getSize(activity).x;
        return HDPI_WIDTH_HORIZONTAL;
    }

    public static int getHdpiHeightHorizontal(Activity activity) {
        HDPI_HEIGHT_HORIZONTAL = getSize(activity).y;
        return HDPI_HEIGHT_HORIZONTAL;
    }

    public static int getImageProcessWidth() {
        return IMAGE_PROCESS_WIDTH;
    }

    public static int getImageProcessHeight() {
        return IMAGE_PROCESS_HEIGHT;
    }

    public static void setPictureFromFile(String photoPath, ImageView imageView, int reqWidth, int reqHeight) {
        // Retrieve the bitmap sampled and set the image view with the bitmap
        Bitmap bitmap = getPictureFromFile(photoPath, reqWidth, reqHeight);
        truncateBitmap(bitmap, reqWidth, reqHeight);
        imageView.setImageBitmap(bitmap);
    }

    public static void setPicture(Monument monument, int reqWidth, int reqHeight, ImageView imageView, Activity activity) {
        Boolean getFromFile = false;
        if (monument.getPhotoPathLocal() != null && !monument.getPhotoPathLocal().isEmpty()) {
            Log.v("Shazam", "LP setPicture from file... "+monument.getName());
            GetImageFromFile getImageFromFile = new GetImageFromFile(monument.getPhotoPathLocal(), reqWidth, reqHeight, imageView);
            getImageFromFile.execute();
            getFromFile = true;
        }else if(!getFromFile && monument.getPhotoPath() != null && !monument.getPhotoPath().isEmpty()) {
            Log.v("Shazam", "LP setPicture from URL..."+monument.getName());
            GetImageURLTask getImageURLTask = new GetImageURLTask(imageView, reqWidth, reqHeight, monument, activity);
            getImageURLTask.execute(monument.getPhotoPath());
        }
    }

    public static Bitmap getPictureFromFile(String photoPath, int reqWidth, int reqHeight) {
        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(photoPath, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(photoPath, options);
    }

    public static Bitmap getPictureFromURL(String url, int reqWidth, int reqHeight, Monument monument, Activity activity) {

        Bitmap bitmap = null;
        try {
            // First decode with inJustDecodeBounds=true to check dimensions
            InputStream in = new java.net.URL(url).openStream();
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(in, null, options);

            // Calculate inSampleSize
            options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

            // Decode bitmap with inSampleSize set
            in.close();
            in = new java.net.URL(url).openStream();
            options.inJustDecodeBounds = false;
            bitmap = BitmapFactory.decodeStream(in, null, options);

        } catch (Exception e) {
            e.printStackTrace();
            Log.e("Shazam", "Exception in LP getPictureFromURL : " + e.getMessage());
        }

        return bitmap;
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public static Bitmap checkRotation(Bitmap bitmap, int reqWidth, int reqHeight) {
        Bitmap bitmapFinal = bitmap;
        if (bitmap != null) {
            if (bitmap.getHeight() > bitmap.getWidth()) {
                Matrix matrix = new Matrix();
                matrix.postRotate(90);
                Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, reqWidth, reqHeight, true);
                bitmapFinal = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);
            }
        }

        return bitmapFinal;
    }

    public static Bitmap truncateBitmap(Bitmap bitmap, int widthWanted, int heightWanted) {
        Bitmap result = null;

        if (bitmap != null) {
            int widthValidate, heightValidate, x = 0, y = 0;
            //Log.v("Shazam", "Truncate widthWanted : " + widthWanted + " heightWanted : " + heightWanted + " widthBitmap : " + bitmap.getWidth() + " heightBitmap : " + bitmap.getHeight());
            if (bitmap.getWidth() < widthWanted) {
                widthValidate = bitmap.getWidth();
            } else {
                widthValidate = widthWanted;
                int diffWidth = bitmap.getWidth() - widthValidate;
                x = diffWidth / 2;
            }

            if (bitmap.getHeight() < heightWanted) {
                heightValidate = bitmap.getHeight();
            } else {
                heightValidate = heightWanted;
                int diffHeigt = bitmap.getHeight() - heightValidate;
                y = diffHeigt / 2;
            }

            result = Bitmap.createBitmap(bitmap, x, y, widthValidate, heightValidate);
        }

        return result;

    }

    public static void saveImageToFile(Bitmap image, String fileName) {
        OutputStream outputStream = null;
        try {
            String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString() + "/ShazamBuffer/";
            File file = new File(path);
            file.mkdirs();
            file = new File(path + fileName);
            outputStream = new FileOutputStream(file);

            image.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            Log.v("Shazam", "LP saveImageToFile saved : " + fileName);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("Shazam", "Exception in LP saveImageToFile : " + e.getMessage());
        } finally {
            try {
                if (outputStream != null) {
                    outputStream.flush();
                    outputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("Shazam", "LP exception in saveImageToFile finally : " + e.getMessage());
            }
        }
    }
}
