package fr.isen.shazamphoto.utils.Images;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import fr.isen.shazamphoto.R;
import fr.isen.shazamphoto.database.Monument;

public class GetImageURLTask extends AsyncTask<String, Void, Bitmap> {

    private ImageView imageView;
    private int reqWith;
    private int reqHeight;
    private Monument monument;
    private Activity activity;

    public GetImageURLTask(ImageView imageView, int reqWith, int reqHeight, Monument monument, Activity activity) {
        this.imageView = imageView;
        this.reqWith = reqWith;
        this.reqHeight = reqHeight;
        this.monument = monument;
        this.activity = activity;
    }

    @Override
    protected Bitmap doInBackground(String... params) {
        String urldisplay = params[0];
        Bitmap result = null;
        try {
            result = LoadPicture.getPictureFromURL(urldisplay, reqWith, reqHeight, monument, activity);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("Exception in GetImageURLTask", e.getMessage());
        }
        return result;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {

        if (bitmap != null) {
            bitmap = LoadPicture.truncateBitmap(bitmap, reqWith, reqHeight);
            imageView.setImageBitmap(bitmap);
           Log.v("Shazam", "GIURLT setPiture from URL... done");
        } else {
            imageView.setImageResource(R.drawable.image_not_found);

        }

    }

}
