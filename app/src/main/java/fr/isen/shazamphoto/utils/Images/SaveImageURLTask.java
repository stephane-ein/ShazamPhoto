package fr.isen.shazamphoto.utils.Images;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.ImageView;

import fr.isen.shazamphoto.R;
import fr.isen.shazamphoto.database.Monument;
import fr.isen.shazamphoto.utils.FunctionsDB;

public class SaveImageURLTask extends AsyncTask<String, Void, Bitmap> {

    private Activity activity;
    private Monument monument;
    private int reqWidth;
    private int reqHeight;
    private ImageView imageView;

    public SaveImageURLTask(Activity activity, Monument monument, int reqWidth, int reqHeight, ImageView imageView) {
        this.activity = activity;
        this.monument = monument;
        this.reqWidth = reqWidth;
        this.reqHeight = reqHeight;
        this.imageView = imageView;
    }

    @Override
    protected Bitmap doInBackground(String... params) {
        String urldisplay = monument.getPhotoPath();
        Bitmap result = null;
        try {
            result = LoadPicture.getPictureFromURL(urldisplay, LoadPicture.getHdpiWidthHorizontal(activity), LoadPicture.getHdpiHeightHorizontal(activity), monument, activity);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("Shazam", "Exception in SIURLT : "+e.getMessage());
        }
        return result;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if(bitmap != null){
            String fileName = monument.getName();//.replace(" ", "")+".png";
            monument.setPhotoPathLocal(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString() +"/ShazamBuffer/"+fileName);
            LoadPicture.saveImageToFile(bitmap, fileName);
            FunctionsDB.editMonument(monument, activity);
            bitmap = LoadPicture.truncateBitmap(bitmap, reqWidth, reqHeight);
            imageView.setImageBitmap(bitmap);
            Log.v("Shazam", "DM save the image... done");

        }else{

            imageView.setImageResource(R.drawable.image_not_found);
        }
    }

}
