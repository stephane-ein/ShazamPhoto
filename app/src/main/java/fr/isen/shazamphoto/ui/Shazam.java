package fr.isen.shazamphoto.ui;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import fr.isen.shazamphoto.R;
import fr.isen.shazamphoto.database.Monument;
import fr.isen.shazamphoto.events.RequestIdentifyByLocalization;
import fr.isen.shazamphoto.events.RequestNearestMonuments;
import fr.isen.shazamphoto.utils.GetMonumentByLocalization;

public class Shazam extends Fragment {

    public static final int POSITION = 0;
    public static final int REQUEST_TAKE_PHOTO = 1;
    public static final int REQUEST_IMAGE_CAPTURE = 1;

    private Button button;
    private String photoPath;
    private ArrayList<Monument> monuments;
    private ListView listView;
    private LocationManager lm;
    private LocationListener locationListener;

    public static Shazam newInstance() {
        return new Shazam();
    }

    public Shazam() {
        this.monuments = new ArrayList<>();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_shazam, container, false);
        listView = (ListView) view.findViewById(R.id.listview_result_monument);
        button = (Button) view.findViewById(R.id.but_takePicture);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });

        if (!monuments.isEmpty()) setListResult(monuments);

        setRetainInstance(true);

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == getActivity().RESULT_OK) {
            try {
                ExifInterface exifInterface = new ExifInterface(photoPath);
                float[] localisation = new float[2];
                lm = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

                if (exifInterface.getLatLong(localisation)) {
                    GetMonumentByLocalization getMonumentByLocalization = new GetMonumentByLocalization(new RequestIdentifyByLocalization((Home) getActivity(), photoPath));
                    getMonumentByLocalization.execute(Float.valueOf(localisation[0]).toString(), Float.valueOf(localisation[1]).toString(), "0.09");
                } else {
                    locationListener = new LocationListener() {
                        public void onLocationChanged(Location location) {
                            GetMonumentByLocalization getMonumentByLocalization = new GetMonumentByLocalization(new RequestIdentifyByLocalization((Home) getActivity(), photoPath));
                            getMonumentByLocalization.execute(Double.valueOf(location.getLatitude()).toString(), Double.valueOf(location.getLongitude()).toString(), "0.09");

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
                    lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 60, 500, locationListener);
                }
            } catch (Exception e) {
                Toast.makeText(getActivity(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
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

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
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

    public void setListResult(ArrayList<Monument> monuments) {
        if (!monuments.isEmpty()) {
            if(locationListener != null) lm.removeUpdates(locationListener);
            this.monuments = monuments;
            CustomListAdapter adapter = new CustomListAdapter(getActivity(), monuments);
            listView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            listView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 2));
            listView.setVisibility(View.VISIBLE);
        }
    }

    public void clearMonuments() {
        if (this.monuments != null) this.monuments.clear();
    }

}

