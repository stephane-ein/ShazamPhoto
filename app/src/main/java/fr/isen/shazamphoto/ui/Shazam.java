package fr.isen.shazamphoto.ui;

import android.app.Activity;
import android.content.Intent;
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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import fr.isen.shazamphoto.R;
import fr.isen.shazamphoto.database.Localization;
import fr.isen.shazamphoto.database.Monument;
import fr.isen.shazamphoto.events.EventDisplayDetailMonument;
import fr.isen.shazamphoto.events.EventLocalizationFound;
import fr.isen.shazamphoto.events.RequestIdentifyByLocalization;
import fr.isen.shazamphoto.model.ModelNavigation;
import fr.isen.shazamphoto.ui.CustomAdapter.CustomListAdapter;
import fr.isen.shazamphoto.ui.ItemUtils.SearchLocalizationItem;
import fr.isen.shazamphoto.utils.ImageProcessing;
import fr.isen.shazamphoto.utils.ShazamProcessing;

public class Shazam extends Fragment implements SearchLocalizationItem {

    public static final int POSITION = 0;
    public static final int REQUEST_TAKE_PHOTO = 1;
    public static final int REQUEST_IMAGE_CAPTURE = 1;


    private Button button;
    private String photoPath;
    private ArrayList<Monument> monuments;
    private static ListView listView;
    private LocateManager locateManager;
    private ModelNavigation modelNavigation;
    private ShazamProcessing shazamProcessing;

    public static Shazam newInstance(LocationManager locationManager,
                                     ModelNavigation modelNavigation, Activity activity) {
        Shazam shazam = new Shazam();
        Bundle args = new Bundle();
        args.putSerializable(ModelNavigation.KEY, modelNavigation);
        shazam.setLocateManager(new LocateManager(locationManager, shazam));
        shazam.setArguments(args);
        shazam.setModelNavigation(modelNavigation);
        shazam.setShazamProcessing(new ShazamProcessing(modelNavigation, activity));
        return shazam;
    }

    //Require empty constructor
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
/*
        this.modelNavigation = (ModelNavigation) getArguments().getSerializable(ModelNavigation.KEY);
        this.shazamProcessing = new ShazamProcessing(modelNavigation, getActivity());*/

        //final Animation animRotate = AnimationUtils.loadAnimation(this.getActivity(), R.anim.anim_button_home);

        button.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                dispatchTakePictureIntent();
            }
        });

        if (!monuments.isEmpty()) setListResult(monuments, getActivity());

        setRetainInstance(true);

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == getActivity().RESULT_OK) {
            try {
                ExifInterface exifInterface = new ExifInterface(photoPath);
                float[] localisation = new float[2];

                this.shazamProcessing = new ShazamProcessing(modelNavigation, getActivity());
                // Set the localization of the monument to the request to identify the monument
                if (exifInterface.getLatLong(localisation)) {
                    this.shazamProcessing.setLocalization(new Localization(-1,
                            Double.valueOf(localisation[0]), Double.valueOf(localisation[1])));
                } else {
                    locateManager.startListening(
                            new RequestIdentifyByLocalization((Home) getActivity(), photoPath));
                }

                // Generate the descriptors and the key points and descriptors of the picture
                ImageProcessing imageProcessing =
                        new ImageProcessing(this.getActivity(), shazamProcessing, photoPath);
                imageProcessing.recognise();


            } catch (Exception e) {
                Toast.makeText(getActivity(), "Error: " + e.getClass().getName(), Toast.LENGTH_LONG).show();
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

    public void setListResult(final ArrayList<Monument> monuments, final Activity activity) {
        if (!monuments.isEmpty() && listView != null) {

            this.monuments = monuments;
            CustomListAdapter adapter = new CustomListAdapter(activity, monuments);
            listView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            listView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 2));
            listView.setVisibility(View.VISIBLE);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1,
                                        int position, long arg3) {
                    modelNavigation.changeAppView(new EventDisplayDetailMonument(activity,
                            monuments.get(position)));
                }
            });
        }
    }

    public void clearMonuments() {
        if (this.monuments != null) this.monuments.clear();
    }

    public void setLocateManager(LocateManager locateManager) {
        this.locateManager = locateManager;
    }

    public ModelNavigation getModelNavigation() {
        return modelNavigation;
    }

    public void setModelNavigation(ModelNavigation modelNavigation) {
        this.modelNavigation = modelNavigation;
    }

    public ShazamProcessing getShazamProcessing() {
        return shazamProcessing;
    }

    public void setShazamProcessing(ShazamProcessing shazamProcessing) {
        this.shazamProcessing = shazamProcessing;
    }

    @Override
    public void foundLocalization(EventLocalizationFound eventLocalizationFound) {
        // Retrieve the localization and stop the listener on the network
        Localization localization = eventLocalizationFound.getLocalization();
        this.shazamProcessing.setLocalization(localization);
        this.locateManager.stopListening();
    }
}

