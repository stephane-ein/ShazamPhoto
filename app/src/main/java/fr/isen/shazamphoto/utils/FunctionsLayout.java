package fr.isen.shazamphoto.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

import fr.isen.shazamphoto.database.Monument;

public class FunctionsLayout {

    public static void setPicture(Monument monument, ImageView imageView){
        //Set the picture
        if (monument.getPhotoPath() != null && !monument.getPhotoPath().isEmpty()) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 4;
            Bitmap bitmap = BitmapFactory.decodeFile(monument.getPhotoPath(), options);
            imageView.setImageBitmap(bitmap);
        }
    }

}
