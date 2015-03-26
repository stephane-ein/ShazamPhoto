package fr.isen.shazamphoto.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.io.InputStream;

public class GetImageURLTask extends AsyncTask<String, Void, Bitmap>{

    private ImageView imageView;
    private LinearLayout progressBar;

    public GetImageURLTask(ImageView imageView, LinearLayout progressBar) {
        this.imageView = imageView;
        this.progressBar = progressBar;
    }

    @Override
    protected Bitmap doInBackground(String... params) {
        String urldisplay = params[0];
        Bitmap result = null;
        try {
            LoadPicture.setPictureFromURL(urldisplay, imageView, LoadPicture.HDPI_WIDTH, LoadPicture.HDPI_HEIGHT);
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }
        return result;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap){
        if(bitmap != null){
            imageView.setImageBitmap(bitmap);
            imageView.setVisibility(View.VISIBLE);
            if(progressBar != null) progressBar.setVisibility(View.GONE);
        }
    }
}
