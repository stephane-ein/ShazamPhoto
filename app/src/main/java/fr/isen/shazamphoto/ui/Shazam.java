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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import fr.isen.shazamphoto.R;
import fr.isen.shazamphoto.database.Localization;
import fr.isen.shazamphoto.database.Monument;
import fr.isen.shazamphoto.database.MonumentSearchDAO;
import fr.isen.shazamphoto.events.EventDisplayDetailMonument;
import fr.isen.shazamphoto.events.EventLocalizationFound;
import fr.isen.shazamphoto.model.ModelNavigation;
import fr.isen.shazamphoto.ui.CustomAdapter.ResultListAdapter;
import fr.isen.shazamphoto.ui.ItemUtils.SearchLocalizationItem;
import fr.isen.shazamphoto.utils.FunctionsDB;
import fr.isen.shazamphoto.utils.ImageProcessing;
import fr.isen.shazamphoto.utils.ShazamProcessingTask;

public class Shazam extends Fragment implements SearchLocalizationItem {

    public static final int POSITION = 0;
    public static final int REQUEST_TAKE_PHOTO = 1;
    public static final int REQUEST_IMAGE_CAPTURE = 1;

    private Button butTakePicture;
    private static LinearLayout llActionSearch;
    private static LinearLayout progressBar;
    private static TextView noMonumentFound;
    private static TextView descriptionTakePicture;
    private static TextView spaceButton;
    private static Button butRemoveResult;
    private static TextView tvDescriptionResult;
    private static LinearLayout llMonumentSearch;

    private String photoPath;
    private static ListView listView;
    private LocateManager locateManager;
    private ModelNavigation modelNavigation;
    private ShazamProcessingTask shazamProcessingTask;

    private static String query;

    private Activity activity;

    public static Shazam newInstance(LocationManager locationManager, ModelNavigation modelNavigation,
                                     Activity activity) {
        Shazam shazam = new Shazam();
        Bundle args = new Bundle();
        args.putSerializable(ModelNavigation.KEY, modelNavigation);
        shazam.setLocateManager(new LocateManager(locationManager, shazam));
        shazam.setArguments(args);
        shazam.setModelNavigation(modelNavigation);
        shazam.setShazamProcessingTask(new ShazamProcessingTask(modelNavigation, activity));
        shazam.setActivity(activity);
        return shazam;
    }

    //Require empty constructor
    public Shazam() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_shazam, container, false);

        butTakePicture = (Button) view.findViewById(R.id.but_takePicture);
        llActionSearch = (LinearLayout) view.findViewById(R.id.fs_ll_action_search);
        progressBar = (LinearLayout) view.findViewById(R.id.fs_progress_bar);
        noMonumentFound = (TextView) view.findViewById(R.id.fs_textview_no_monument_found);
        descriptionTakePicture = (TextView) view.findViewById(R.id.fs_tv_title_button);
        spaceButton = (TextView) view.findViewById(R.id.fs_tv_space_button);
        butRemoveResult = (Button) view.findViewById(R.id.fs_button_remove_result);
        listView = (ListView) view.findViewById(R.id.fs_listview_result_monument);
        tvDescriptionResult = (TextView) view.findViewById(R.id.fs_tv_description_result);
        llMonumentSearch = (LinearLayout) view.findViewById(R.id.fs_ll_monuments_search);

        butTakePicture.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                dispatchTakePictureIntent();
            }
        });

        setListenerRemoveResult(butRemoveResult);

        // Retrieve the monument found
        ArrayList<Monument> monuments = getMonumentSearch();
        if (!monuments.isEmpty()) displayMonumentFound(monuments, query);

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

                this.shazamProcessingTask = new ShazamProcessingTask(modelNavigation, getActivity());
                // Set the localization of the monument to the request to identify the monument
                if (exifInterface.getLatLong(localisation)) {
                    this.shazamProcessingTask.setLocalization(new Localization(-1,
                            Double.valueOf(localisation[0]), Double.valueOf(localisation[1])));
                } else {
                    locateManager.startListening();
                }

                // Generate the descriptors and the key points and descriptors of the picture
                ImageProcessing imageProcessing =
                        new ImageProcessing(this.getActivity(), shazamProcessingTask, photoPath);
                imageProcessing.recognise();

            } catch (Exception e) {
                e.printStackTrace();
                Log.e("Shazam", "Exception in Shazam : " + e.getMessage());
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
                takePictureIntent.putExtra("outputX", 700);
                takePictureIntent.putExtra("outputY", 700);
                takePictureIntent.putExtra("aspectX", 1);
                takePictureIntent.putExtra("aspectY", 1);
                takePictureIntent.putExtra("scale", true);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    public void setListResult(final ArrayList<Monument> monuments, String query) {
        // Remove all the monument previously found
        FunctionsDB.removeAllMonumentSearch(activity);

        // Retrieve the monument and store them in the db
        for (Monument m : monuments) {
            FunctionsDB.addMonumentToDB(m, activity);
            FunctionsDB.addMonumentToMonumentSearch(m, activity, query);
        }

        // Display the monuments found
        final ArrayList<Monument> monumentsDB = FunctionsDB.getAllMonumentSearch(activity);
        ResultListAdapter adapter = new ResultListAdapter(activity, monumentsDB);
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        // Set the listener on the monuments
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    int position, long arg3) {
                MonumentSearchDAO monumentSearchDAO = new MonumentSearchDAO(activity);
                monumentSearchDAO.open();
                Monument mDB = monumentSearchDAO.select(monumentsDB.get(position).getId());
                // Change the view by displaying the detail about monument retrieved in the db

                modelNavigation.changeAppView(new EventDisplayDetailMonument(activity,
                        mDB, modelNavigation));
                monumentSearchDAO.close();
            }
        });
    }

    public void displayDefaultUI(){
        spaceButton.setVisibility(View.VISIBLE);
        descriptionTakePicture.setVisibility(View.VISIBLE);
        llActionSearch.setVisibility(View.GONE);
    }

    public void displayMonumentFound(ArrayList<Monument> monuments, String query) {
        hideLoading();
        hideNoMonumentFound();
        hideDescriptionButton();
        llActionSearch.setVisibility(View.VISIBLE);
        setListResult(monuments, query);
        tvDescriptionResult.setText("Results for " + query);
        llMonumentSearch.setVisibility(View.VISIBLE);
    }

    public void displayLoading() {
        hideDescriptionButton();
        llActionSearch.setVisibility(View.VISIBLE);
        if (progressBar != null) progressBar.setVisibility(View.VISIBLE);
    }

    public void displayNoMonumentFound() {
        hideLoading();
        hideDescriptionButton();
        llActionSearch.setVisibility(View.VISIBLE);
        llMonumentSearch.setVisibility(View.GONE);
        noMonumentFound.setVisibility(View.VISIBLE);
    }


    private void hideNoMonumentFound(){
        noMonumentFound.setVisibility(View.GONE);
    }

    private void hideDescriptionButton() {
        descriptionTakePicture.setVisibility(View.GONE);
        spaceButton.setVisibility(View.GONE);
    }

    private void hideLoading() {
        if (progressBar != null) progressBar.setVisibility(View.GONE);
    }

    private void setListenerRemoveResult(Button button){
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Clear the DB
                FunctionsDB.removeAllMonumentSearch(getActivity());
                llActionSearch.setVisibility(View.GONE);
                displayDefaultUI();
            }
        });
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

    public ArrayList<Monument> getMonumentSearch() {
        MonumentSearchDAO monumentSearchDAO = new MonumentSearchDAO(getActivity());
        monumentSearchDAO.open();
        List<Monument> monumentsList = monumentSearchDAO.getAllMonuments();
        query = monumentSearchDAO.query;
        monumentSearchDAO.close();
        ArrayList<Monument> monuments = new ArrayList<>();
        for (Monument monument : monumentsList) {
            monuments.add(monument);
        }

        return monuments;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }
}

