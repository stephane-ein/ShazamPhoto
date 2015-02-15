package fr.isen.shazamphoto.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import fr.isen.shazamphoto.R;
import fr.isen.shazamphoto.utils.IdentifyMonumentByLocalization;

public class Shazam extends Fragment {

    private Button button;
    private Activity activity;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private Location location;
    private String photoPath;

    public Shazam() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();


        LocationManager lm = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
        final Shazam shazam = this;

       /* LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                shazam.location = location;
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 60, 50, locationListener);*/
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_shazam, container, false);

        button = (Button) view.findViewById(R.id.but_takePicture);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == activity.RESULT_OK) {
            try {
                ExifInterface exifInterface = new ExifInterface(photoPath);
                float[] localisation = new float[2];
                if (exifInterface.getLatLong(localisation)) {
                    IdentifyMonumentByLocalization identifyMonumentByLocalization = new IdentifyMonumentByLocalization(activity, photoPath);
                    identifyMonumentByLocalization.execute("la=" + Float.valueOf(localisation[0]).toString() + "&lo=" + Float.valueOf(localisation[1]).toString() + "&o=0.001");
                } else {
                    Toast.makeText(getActivity(), "No location found", Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {}
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        photoPath = image.getAbsolutePath();
        return image;
    }

    static final int REQUEST_TAKE_PHOTO = 1;

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(activity.getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

}
