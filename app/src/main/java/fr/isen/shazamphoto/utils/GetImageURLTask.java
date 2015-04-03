package fr.isen.shazamphoto.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.io.InputStream;

import fr.isen.shazamphoto.R;

public class GetImageURLTask extends AsyncTask<String, Void, Bitmap> {

    private ImageView imageView;

    public GetImageURLTask(ImageView imageView) {
        this.imageView = imageView;
    }

    @Override
    protected Bitmap doInBackground(String... params) {
        String urldisplay = params[0];
        Bitmap result = null;
        try {
            result = LoadPicture.getPictureFromURL(urldisplay, LoadPicture.HDPI_WIDTH, LoadPicture.HDPI_HEIGHT);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("Exception in GetImageURLTask", e.getMessage());
        }
        return result;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {

        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
        }else{
            imageView.setImageResource(R.drawable.image_not_found);

        }
    }

}
