package fr.isen.shazamphoto.utils.Images;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

public class GetMonumentImage extends AsyncTask<String, Void, Bitmap> {

    private ImageView imageView;

    public GetMonumentImage(ImageView imageView) {
        if (imageView != null) {
            this.imageView = imageView;
        } else {
            this.imageView = null;
        }
    }

    public Bitmap doInBackground(String... params) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 16;
        Bitmap bitmap = BitmapFactory.decodeFile(params[0], options);
        return bitmap;
    }

    public void onPostExecute(Bitmap bitmap) {
        if (this.imageView != null && bitmap != null) {
            this.imageView.setImageBitmap(bitmap);
        }
    }
}
