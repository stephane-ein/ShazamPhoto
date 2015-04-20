package fr.isen.shazamphoto.utils.Images;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import fr.isen.shazamphoto.R;

public class GetImageFromFile extends AsyncTask<String, Void, Bitmap>{

    private String photoPath;
    private int reqHeight;
    private int reqWidth;
    private ImageView imageView;

    public GetImageFromFile(String photoPath, int reqWidth, int reqHeight, ImageView imageView) {
        this.photoPath = photoPath;
        this.reqHeight = reqHeight;
        this.reqWidth = reqWidth;
        this.imageView = imageView;
    }

    @Override
    protected Bitmap doInBackground(String... strings) {
        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(photoPath, options);

        // Calculate inSampleSize
        options.inSampleSize = LoadPicture.calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(photoPath, options);
    }

    @Override
    protected void onPostExecute(Bitmap bitmap){
        if(bitmap != null){
            bitmap = LoadPicture.truncateBitmap(bitmap, reqWidth, reqHeight);
            imageView.setImageBitmap(bitmap);
        }else{
            imageView.setImageResource(R.drawable.image_not_found);
        }
        //Log.v("Shazam", "LP setPicture from file... done");
    }
}
