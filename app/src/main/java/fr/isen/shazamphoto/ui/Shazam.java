package fr.isen.shazamphoto.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
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
import fr.isen.shazamphoto.ui.CustomAdapter.ResultListAdapter;
import fr.isen.shazamphoto.ui.ItemUtils.SearchLocalizationItem;
import fr.isen.shazamphoto.utils.ImageProcessing;
import fr.isen.shazamphoto.utils.ShazamProcessingTask;

public class Shazam extends Fragment implements SearchLocalizationItem {

    public static final int POSITION = 0;
    public static final int REQUEST_TAKE_PHOTO = 1;
    public static final int REQUEST_IMAGE_CAPTURE = 1;


    private Button button;
    private static LinearLayout linearLayoutResult;
    private LinearLayout progressBar;
    private TextView noMonumentFound;

    private String photoPath;
    private ArrayList<Monument> monuments;
    private static ListView listView;
    private LocateManager locateManager;
    private ModelNavigation modelNavigation;
    private ShazamProcessingTask shazamProcessingTask;
    private NetworkInfoArea networkInfo;

    public static Shazam newInstance(LocationManager locationManager,ModelNavigation modelNavigation,
                                     Activity activity, NetworkInfoArea networkInfoArea) {
        Shazam shazam = new Shazam();
        Bundle args = new Bundle();
        args.putSerializable(ModelNavigation.KEY, modelNavigation);
        shazam.setLocateManager(new LocateManager(locationManager, shazam));
        shazam.setArguments(args);
        shazam.setModelNavigation(modelNavigation);
        shazam.setShazamProcessingTask(new ShazamProcessingTask(networkInfoArea, modelNavigation, activity));
        shazam.setNetworkInfo(networkInfoArea);
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

        listView = (ListView) view.findViewById(R.id.fs_listview_result_monument);
        button = (Button) view.findViewById(R.id.but_takePicture);
        linearLayoutResult = (LinearLayout) view.findViewById(R.id.fs_linearlayout_result);
        progressBar = (LinearLayout) view.findViewById(R.id.fs_progress_bar);
        noMonumentFound = (TextView) view.findViewById(R.id.fs_textview_no_monument_found);

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
    public void foundLocalization(EventLocalizationFound eventLocalizationFound) {
        // Retrieve the localization and stop the listener on the network
        Localization localization = eventLocalizationFound.getLocalization();
        this.shazamProcessingTask.setLocalization(localization);
        this.locateManager.stopListening();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == getActivity().RESULT_OK) {
            try {
                ExifInterface exifInterface = new ExifInterface(photoPath);
                float[] localisation = new float[2];

                this.shazamProcessingTask = new ShazamProcessingTask(networkInfo, modelNavigation, getActivity());
                // Set the localization of the monument to the request to identify the monument
                if (exifInterface.getLatLong(localisation)) {
                    this.shazamProcessingTask.setLocalization(new Localization(-1,
                            Double.valueOf(localisation[0]), Double.valueOf(localisation[1])));
                } else {
                    locateManager.startListening(
                            new RequestIdentifyByLocalization((Home) getActivity(), photoPath));
                }

                // Generate the descriptors and the key points and descriptors of the picture
                ImageProcessing imageProcessing =
                        new ImageProcessing(this.getActivity(), shazamProcessingTask, photoPath);
                imageProcessing.recognise();

            } catch (Exception e) {
               Log.e("Shazam", "Exception in Shazam : "+e.getMessage());
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
        listView.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
        noMonumentFound.setVisibility(View.GONE);
        listView.setVisibility(View.GONE);

        if (!monuments.isEmpty() && listView != null) {
            this.monuments = monuments;
            ResultListAdapter adapter = new ResultListAdapter(activity, monuments);
            listView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            listView.setVisibility(View.VISIBLE);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1,
                                        int position, long arg3) {
                    modelNavigation.changeAppView(new EventDisplayDetailMonument(activity,
                            monuments.get(position)));
                }
            });
        }else if(monuments.isEmpty()){
            noMonumentFound.setVisibility(View.VISIBLE);
        }
    }

    public void clearMonuments() {
        if (this.monuments != null) this.monuments.clear();
    }

    public void setLocateManager(LocateManager locateManager) {
        this.locateManager = locateManager;
    }

    public void setModelNavigation(ModelNavigation modelNavigation) {
        this.modelNavigation = modelNavigation;
    }

    public void setShazamProcessingTask(ShazamProcessingTask shazamProcessingTask) {
        this.shazamProcessingTask = shazamProcessingTask;
    }

    public void setNetworkInfo(NetworkInfoArea networkInfo) {
        this.networkInfo = networkInfo;
    }

    public void displayLoading() {
        linearLayoutResult.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.VISIBLE);
    }

    public void hideUIResult() {
        noMonumentFound.setVisibility(View.GONE);
        listView.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
        linearLayoutResult.setVisibility(View.GONE);
    }
}

