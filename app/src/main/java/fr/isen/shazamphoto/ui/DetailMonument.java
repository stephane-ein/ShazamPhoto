package fr.isen.shazamphoto.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import fr.isen.shazamphoto.R;
import fr.isen.shazamphoto.database.FavouriteMonumentDAO;
import fr.isen.shazamphoto.database.Localization;
import fr.isen.shazamphoto.database.Monument;
import fr.isen.shazamphoto.database.NearestMonumentsDAO;
import fr.isen.shazamphoto.events.EventDisplayDetailMonument;
import fr.isen.shazamphoto.events.EventMonumentUpdated;
import fr.isen.shazamphoto.model.ModelNavigation;
import fr.isen.shazamphoto.ui.CustomAdapter.GridFavrouriteAdapter;
import fr.isen.shazamphoto.ui.Dialogs.DeleteDialog;
import fr.isen.shazamphoto.ui.ItemUtils.SearchMonumentsByLocalization;
import fr.isen.shazamphoto.ui.ItemUtils.SearchableItem;
import fr.isen.shazamphoto.ui.ItemUtils.UpdateMonumentItem;
import fr.isen.shazamphoto.ui.ScrollView.ADMScrollView;
import fr.isen.shazamphoto.ui.ScrollView.ScrollViewListener;
import fr.isen.shazamphoto.utils.FunctionsDB;
import fr.isen.shazamphoto.utils.GetMonumentTask.GetMonumentByLocalization;
import fr.isen.shazamphoto.utils.GetMonumentTask.GetMonumentsByName;
import fr.isen.shazamphoto.utils.Images.LoadPicture;
import fr.isen.shazamphoto.utils.Images.SaveImageURLTask;
import fr.isen.shazamphoto.utils.UpdateMonument.AddLikeTask;


public class DetailMonument extends ActionBarActivity implements ScrollViewListener,
        SearchMonumentsByLocalization, SearchableItem, UpdateMonumentItem {

    public static final int NBMAX_MONUMENT_NEAREST_TO_DISPLAY_LANDSCAPE = 4;
    public static final int NBMAX_MONUMENT_NEAREST_TO_DISPLAY_PORTRAIT = 3;
    public static final int GRIDVIEW_SPACING = 8;
    public static final int GRIDVIEW_PADDING = 16;

    private Monument monument;

    private ModelNavigation modelNavigation;

    private GridView gridView;
    private TextView nbVisitors;
    private TextView nbLikes;
    private TextView noNearestMonument;
    private Button buttonLike;
    private ImageView photoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_monument);
        final Activity activity = this;
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.argb(0, 0, 0, 0)));

        //Retrieve the several items
        ADMScrollView scrollView = (ADMScrollView) findViewById(R.id.adm_scrollview);
        gridView = (GridView) findViewById(R.id.gridView_nearestMonuments);
        TextView title = (TextView) findViewById(R.id.adm_title);
        TextView description = (TextView) findViewById(R.id.adm_textview_description);
        nbVisitors = (TextView) findViewById(R.id.adm_nb_visitor);
        nbLikes = (TextView) findViewById(R.id.adm_nb_likes);
        noNearestMonument = (TextView) findViewById(R.id.adm_textview_nonearestmonument);
        photoView = (ImageView) findViewById(R.id.imageView1);
        Button buttonFavourite = (Button) findViewById(R.id.button_add_favorite);
        buttonLike = (Button) findViewById(R.id.button_like);
        Button buttonPlace = (Button) findViewById(R.id.adm_button_place);
        Button buttonNavigation = (Button) findViewById(R.id.adm_button_navigation);
        Button buttonFacebook = (Button) findViewById(R.id.adm_button_sharefacebook);
        Button buttonTwitter = (Button) findViewById(R.id.adm_button_sharetwitter);

        monument = (Monument) getIntent().getSerializableExtra(Monument.NAME_SERIALIZABLE);
        modelNavigation = (ModelNavigation) getIntent().getSerializableExtra(ModelNavigation.KEY);
        setColumnWidthView(getResources().getConfiguration().orientation);

        setTitle("");
        description.setText(monument.getDescription());

        if(monument.getPhotoPathLocal() == null || monument.getPhotoPathLocal().isEmpty()){
            SaveImageURLTask saveImageURLTask;
            Log.v("Shazam", "DM save the image...");
            if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
                saveImageURLTask = new SaveImageURLTask(this, monument, LoadPicture.getHdpiWidthVertical(this), LoadPicture.getHdpiHeightVertical(this), photoView);
            }else{
                saveImageURLTask = new SaveImageURLTask(this, monument, LoadPicture.getHdpiWidthHorizontal(this), LoadPicture.getHdpiHeightHorizontal(this), photoView);
            }
            saveImageURLTask.execute();
        }else{
            //Set the picture
            if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
                LoadPicture.setPicture(monument, LoadPicture.getHdpiWidthVertical(this), LoadPicture.getHdpiHeightVertical(this), photoView, this);
            }else{
                LoadPicture.setPicture(monument, LoadPicture.getHdpiWidthHorizontal(this), LoadPicture.getHdpiHeightHorizontal(this), photoView, this);
            }
        }




        //Set the monument information
        title.setText(monument.getName());
        upDateMonument();

        // Set the several listeners
        setListenerFavorite(buttonFavourite);
        setListenerLike(buttonLike);
        setListenerPlace(buttonPlace);
        setListenerNavigation(buttonNavigation);
        setListenerShareFacebook(buttonFacebook);
        setListenerShareTwitter(buttonTwitter);
        scrollView.setScrollViewListener(this);


        FavouriteMonumentDAO favouriteMonumentDAO = new FavouriteMonumentDAO(activity);
        favouriteMonumentDAO.open();
        if (favouriteMonumentDAO.select(monument.getId()) != null) {
            buttonFavourite.setText("REMOVE FROM FAV.");
        } else {
            buttonFavourite.setText("ADD TO FAVORITE");
        }
        favouriteMonumentDAO.close();

        // Get the nearest monuments
        NearestMonumentsDAO nearestMonumentsDAO = new NearestMonumentsDAO(this);
        nearestMonumentsDAO.open();
        ArrayList<Monument> monuments =
                nearestMonumentsDAO.getNearestMonuments(this.monument.getId());
        // Set the nearest monuments in the gridView
        if (!monuments.isEmpty()) {
            setGridViewArrayList(monuments);
        } else if (monument.getLocalization() != null) {
            Localization l = monument.getLocalization();
            GetMonumentByLocalization getMonumentByLocalization =
                    new GetMonumentByLocalization(this, this, l.getLatitude(), l.getLongitude());
            getMonumentByLocalization.execute();
        } else {
            noNearestMonument.setVisibility(View.VISIBLE);
        }

        updateButtonLike();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setColumnWidthView(newConfig.orientation);

        //Set the picture
        if(newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
            LoadPicture.setPicture(monument, LoadPicture.getHdpiWidthVertical(this), LoadPicture.getHdpiHeightVertical(this), photoView, this);
        }else{
            LoadPicture.setPicture(monument, LoadPicture.getHdpiWidthHorizontal(this), LoadPicture.getHdpiHeightHorizontal(this), photoView, this);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detail_monument, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                finish();
                return true;
            case R.id.item_delete:
                Bundle bundle = new Bundle();
                bundle.putSerializable(Monument.NAME_SERIALIZABLE, monument);
                DeleteDialog deleteDialog = new DeleteDialog();
                deleteDialog.setArguments(bundle);
                deleteDialog.show(getFragmentManager(), "Delete the monument tagged");
                return true;
        }
        return super.onOptionsItemSelected(item);

    }

    public void upDateMonument() {
        GetMonumentsByName getMonumentsByName =
                new GetMonumentsByName(this, this, monument.getName());
        getMonumentsByName.execute();
    }

    public void setListNearestMonuments(ArrayList<Monument> monuments) {

        if (!monuments.isEmpty()) {

            noNearestMonument.setVisibility(View.GONE);

            ArrayList<Monument> monumentsFiltered = new ArrayList<>();
            int size = (monuments.size() > 4) ? NBMAX_MONUMENT_NEAREST_TO_DISPLAY_LANDSCAPE : monuments.size();
            NearestMonumentsDAO nearestMonumentsDAO = new NearestMonumentsDAO(this);
            nearestMonumentsDAO.open();

            // Add the nearest monument in the database in order to work off line
            for (int i = 0; i < size; i++) {
                Monument m = monuments.get(i);
                if (!m.getName().equals(monument.getName())) {
                    monumentsFiltered.add(m);
                    FunctionsDB.addMonumentToDB(m, this);
                    nearestMonumentsDAO.insert(this.monument.getId(), m.getId());
                }
            }

            nearestMonumentsDAO.close();

            // After the list of monuments is filtered, we check if
            // there is some monument around the user
            if (!monumentsFiltered.isEmpty()) {
                setGridViewArrayList(monumentsFiltered);
            } else {
                noNearestMonument.setVisibility(View.VISIBLE);
            }
        }
    }

    public void setGridViewArrayList(final ArrayList<Monument> m) {
        // Set the grid view of the nearest monuments
        GridFavrouriteAdapter gridViewAdapter = new GridFavrouriteAdapter(this, m, this);
        gridView.setAdapter(gridViewAdapter);
        gridViewAdapter.notifyDataSetChanged();

        // Set the listener
        final DetailMonument detailMonument = this;
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Add the monument o the DB in order to know if the user has liked the monument
                // and to retrieve the result of hte search off line
                Monument monument = m.get(position);
                FunctionsDB.addMonumentToDB(monument, getApplication());
                Monument mDB = FunctionsDB.getMonument(monument, getApplication());
                GetMonumentsByName getMonumentsByName =
                        new GetMonumentsByName(detailMonument, detailMonument, mDB.getName());
                getMonumentsByName.execute();

            }
        });
    }

    public void setColumnWidthView(int orientation) {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int columnWidth = 0;

        // Checks the orientation of the screen
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            columnWidth = ((width - 2 * GRIDVIEW_PADDING - 2 * GRIDVIEW_SPACING) / (NBMAX_MONUMENT_NEAREST_TO_DISPLAY_LANDSCAPE + 1));

        } else if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            columnWidth = ((width - 2 * GRIDVIEW_PADDING - 2 * GRIDVIEW_SPACING) / (NBMAX_MONUMENT_NEAREST_TO_DISPLAY_PORTRAIT + 1));
        }

        gridView.setColumnWidth(columnWidth);
    }

    @Override
    public void onScrollChanged(ADMScrollView scrollView, int x, int y, int oldx, int oldy) {
        // Handle the fadding
        View view = scrollView.getChildAt(scrollView.getChildCount() - 1);

        double downScrolled = (view.getBottom() - (scrollView.getHeight() + scrollView.getScrollY()));
        double startShowMenu = (view.getBottom() * 0.3);
        double fullShowMenu = (view.getBottom() * 0.6);
        double lengthArea = fullShowMenu - startShowMenu;

        if (startShowMenu >= downScrolled && downScrolled <= fullShowMenu) {
            double deltaScrolled = startShowMenu - downScrolled;
            int newAlpha = (int) ((deltaScrolled * 255.0) / lengthArea);
            int alpha = newAlpha > 255 ? 255 : newAlpha;
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.argb(alpha, 0, 0, 0)));
        } else if (downScrolled > startShowMenu) {
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.argb(0, 0, 0, 0)));
        }
    }

    @Override
    public void monumentsFoundByLocalization(ArrayList<Monument> monuments) {
        if (!monuments.isEmpty()) {
            noNearestMonument.setVisibility(View.GONE);
            setListNearestMonuments(monuments);
        } else {
            noNearestMonument.setVisibility(View.VISIBLE);
        }
    }

    public void setListenerFavorite(final Button button) {
        final DetailMonument detailMonument = this;
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FunctionsDB.addMonumentToDB(monument, getApplicationContext());
                FavouriteMonumentDAO favouriteMonumentDAO = new FavouriteMonumentDAO(getApplicationContext());
                favouriteMonumentDAO.open();
                if (favouriteMonumentDAO.select(monument.getId()) != null) {
                    favouriteMonumentDAO.delete(monument);
                    button.setText("ADD TO FAVORITE");
                } else {
                    favouriteMonumentDAO.insert(monument);
                    button.setText("REMOVE FROM FAV.");

                }
                favouriteMonumentDAO.close();
            }
        });
    }

    public void setListenerLike(Button button) {
        final DetailMonument detailMonument = this;
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                monument.setLiked(1);
                FunctionsDB.editMonument(monument, getApplication());
                updateButtonLike();
                AddLikeTask task = new AddLikeTask(detailMonument, detailMonument);
                task.execute(monument);
            }
        });
    }

    @Override
    public void onPostSearch(ArrayList<Monument> monuments, String query) {
        if (monuments != null && !monuments.isEmpty()) {
            // Add the monument in the DB in order to know if the user has liked the monument
            // and to retrieve the result of the search off line
            FunctionsDB.addMonumentToDB(monuments.get(0), getApplicationContext());
            Monument m = FunctionsDB.getMonument(monuments.get(0), getApplicationContext());

            if (m.getName().equals(monument.getName())) {
                // Update the number of like and visitor; because the user clicked on the like button
                upDateStatisticsMonument(m);
                updateButtonLike();
            } else {
                // User click a the monument near the current monument (In the grid view)
                // Display the detail about the monument and start a new activity
                modelNavigation.changeAppView(new EventDisplayDetailMonument(this, m, modelNavigation));
                // Close this activity
                finish();
            }


        }
    }

    @Override
    public void monumentUpdated(EventMonumentUpdated eventLocalizationFound) {
        if (eventLocalizationFound != null) {
            Monument m = eventLocalizationFound.getMonument();
            upDateStatisticsMonument(m);
            updateButtonLike();
        }
    }

    private void updateButtonLike() {
        if (monument.getLiked() == 1) {
            buttonLike.setEnabled(false);
            buttonLike.setBackgroundResource(R.drawable.button_selected);
        } else {
            buttonLike.setEnabled(true);
            buttonLike.setBackgroundResource(R.drawable.button_unselected);
        }
    }

    public void upDateStatisticsMonument(Monument m) {
        int nbLike = m.getNbLike();
        int nbVisitor = m.getNbVisitors();
        monument.setNbLike(nbLike);
        monument.setNbVisitors(nbVisitor);
        FunctionsDB.editMonument(monument, this);
        nbLikes.setText(Integer.valueOf(monument.getNbLike()).toString());
        nbVisitors.setText(Integer.valueOf(monument.getNbVisitors()).toString());
    }

    private void setListenerShareTwitter(Button buttonTwitter) {
        buttonTwitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareItTwitter();
            }
        });
    }

    private void setListenerShareFacebook(Button buttonFacebook) {
        buttonFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareItFacebook();
            }
        });
    }

    private void setListenerNavigation(Button buttonNavigation) {
        buttonNavigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Localization l = monument.getLocalization();
                if (l != null) {
                    Uri gmmIntentUri = Uri.parse("google.navigation:q=" + l.getLatitude() + "," + l.getLongitude() + "&mode=w");
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                    mapIntent.setPackage("com.google.android.apps.maps");
                    startActivity(mapIntent);
                } else {
                    Toast.makeText(getApplicationContext(),
                            "This monument has not a localization", Toast.LENGTH_LONG).show();

                }
            }
        });
    }

    private void setListenerPlace(Button buttonPlace) {
        buttonPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Localization l = monument.getLocalization();
                if (l != null) {
                    Uri gmmIntentUri = Uri.parse("geo:" + l.getLatitude() + "," + l.getLongitude());
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                    mapIntent.setPackage("com.google.android.apps.maps");
                    startActivity(mapIntent);
                } else {
                    Toast.makeText(getApplicationContext(),
                            "This monument has not a localization", Toast.LENGTH_LONG).show();
                }

            }
        });
    }

    private void shareItTwitter() {
        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString();

        File toto = new File(path + "/test1.jpg"); // A content Uri to the image you would like to share.
        share("twitter", toto.toString(), "waza");
    }

    public void shareItFacebook() {

        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString();

        File toto = new File(path + "/test1.jpg"); // A content Uri to the image you would like to share.
        share("facebook", toto.toString(), "waza");


    }

    public void share(String nameApp, String imagePath, String message) {
        try {
            List<Intent> targetedShareIntents = new ArrayList<Intent>();
            Intent share = new Intent(android.content.Intent.ACTION_SEND);
            share.setType("image/*");
            List<ResolveInfo> resInfo = getPackageManager()
                    .queryIntentActivities(share, 0);
            if (!resInfo.isEmpty()) {
                for (ResolveInfo info : resInfo) {
                    Intent targetedShare = new Intent(
                            android.content.Intent.ACTION_SEND);
                    targetedShare.setType("image/jpeg"); // put here your mime
                    // type
                    if (info.activityInfo.packageName.toLowerCase().contains(
                            nameApp)
                            || info.activityInfo.name.toLowerCase().contains(
                            nameApp)) {
                        targetedShare.putExtra(Intent.EXTRA_SUBJECT,
                                "Sample Photo");
                        targetedShare.putExtra(Intent.EXTRA_TEXT, message);
                        targetedShare.putExtra(Intent.EXTRA_STREAM,
                                Uri.fromFile(new File(imagePath)));
                        targetedShare.setPackage(info.activityInfo.packageName);
                        targetedShareIntents.add(targetedShare);
                    }
                }
                Intent chooserIntent = Intent.createChooser(
                        targetedShareIntents.remove(0), "Select app to share");
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS,
                        targetedShareIntents.toArray(new Parcelable[]{}));
                startActivity(chooserIntent);
            }
        } catch (Exception e) {
            Log.v("Shazam",
                    "DM Exception while sending image on" + nameApp + " "
                            + e.getMessage());
        }
    }
}
