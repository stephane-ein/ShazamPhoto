package fr.isen.shazamphoto.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import fr.isen.shazamphoto.R;
import fr.isen.shazamphoto.database.FavouriteMonumentDAO;
import fr.isen.shazamphoto.database.Localization;
import fr.isen.shazamphoto.database.Monument;
import fr.isen.shazamphoto.database.NearestMonumentsDAO;
import fr.isen.shazamphoto.events.RequestNearestFromMonument;
import fr.isen.shazamphoto.ui.CustomAdapter.GridViewAdapter;
import fr.isen.shazamphoto.ui.Dialogs.DeleteDialog;
import fr.isen.shazamphoto.ui.ItemUtils.SearchMonumentsByLocalization;
import fr.isen.shazamphoto.utils.ConfigurationShazam;
import fr.isen.shazamphoto.utils.FunctionsDB;
import fr.isen.shazamphoto.utils.FunctionsLayout;
import fr.isen.shazamphoto.utils.GetMonumentByLocalization;


public class DetailMonument extends ActionBarActivity implements ScrollViewListener, SearchMonumentsByLocalization {

    public static final int NBMAX_MONUMENT_NEAREST_TO_DISPLAY_LANDSCAPE = 4;
    public static final int NBMAX_MONUMENT_NEAREST_TO_DISPLAY_PORTRAIT = 3;
    public static final int GRIDVIEW_SPACING = 8;
    public static final int GRIDVIEW_PADDING = 16;

    private Monument monument;
    private GridView gridView;
    private TextView nbVisitor;
    private TextView noNearestMonument;

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
        TextView nbLike = (TextView) findViewById(R.id.textView_nbLike);
        nbVisitor = (TextView) findViewById(R.id.textView_nbVisitor);
        noNearestMonument = (TextView) findViewById(R.id.adm_textview_nonearestmonument);
        final ImageView photoView = (ImageView) findViewById(R.id.imageView1);
        Button buttonFavourite = (Button) findViewById(R.id.button_add_favorite);
        Button buttonLike = (Button) findViewById(R.id.button_like);
        Button buttonPlace = (Button) findViewById(R.id.adm_button_place);
        Button buttonNavigation = (Button) findViewById(R.id.adm_button_navigation);
        Button buttonFacebook = (Button) findViewById(R.id.adm_button_sharefacebook);
        Button buttonTwitter = (Button) findViewById(R.id.adm_button_sharetwitter);

        // retrieve the monument and some element of the monument detail activity
        monument = (Monument) getIntent().getSerializableExtra(Monument.NAME_SERIALIZABLE);

        setColumnWidthView(getResources().getConfiguration().orientation);

        setTitle("");

        //Set the picture
        FunctionsLayout.setPicture(monument, photoView);

        //Set the monument information
        nbLike.setText(monument.getName());
        nbVisitor.setText("More than " + monument.getNbLike() + " likes and " + monument.getNbVisitors() + " visitors");

        // Set the several listeners
        setListenerFavorite(buttonFavourite);
        setListenerLike(buttonLike);
        setListenerPlace(buttonPlace);
        setListenerNavigation(buttonNavigation);
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
            RequestNearestFromMonument requestNearestFromMonument =
                    new RequestNearestFromMonument(this);
            GetMonumentByLocalization getMonumentByLocalization =
                    new GetMonumentByLocalization(requestNearestFromMonument, this);
            getMonumentByLocalization.execute(
                    Double.valueOf(monument.getLocalization().getLatitude()).toString(),
                    Double.valueOf(monument.getLocalization().getLongitude()).toString(),
                    ConfigurationShazam.DELTA_LOCALIZATION);
        }

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
                            "This monument has not a localization, sorry", Toast.LENGTH_LONG).show();

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
                            "This monument has not a localization, sorry", Toast.LENGTH_LONG).show();
                }

            }
        });
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setColumnWidthView(newConfig.orientation);
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
                DeleteDialog deleteDialog = new DeleteDialog();
                deleteDialog.show(getFragmentManager(), "test");
                return true;
        }
        return super.onOptionsItemSelected(item);

    }

    public void setListNearestMonuments(ArrayList<Monument> monuments) {

        if (!monuments.isEmpty()) {

            noNearestMonument.setVisibility(View.INVISIBLE);

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

            // After the list of monuments is filered, we check if
            // there is some monument around the user
            if (!monumentsFiltered.isEmpty()) {
                setGridViewArrayList(monumentsFiltered);
            } else {
                noNearestMonument.setVisibility(View.VISIBLE);
            }
        }
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

    public void setGridViewArrayList(ArrayList<Monument> m) {
        // Set the grid view of the nearests monuments
        GridViewAdapter gridViewAdapter = new GridViewAdapter(this, m);
        gridView.setAdapter(gridViewAdapter);
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

    }

    public void setListenerFavorite(final Button button) {
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
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
    }
}
