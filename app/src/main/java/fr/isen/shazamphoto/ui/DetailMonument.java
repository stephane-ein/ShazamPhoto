package fr.isen.shazamphoto.ui;

import android.app.Activity;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
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

import java.util.ArrayList;

import fr.isen.shazamphoto.R;
import fr.isen.shazamphoto.database.FavouriteMonumentDAO;
import fr.isen.shazamphoto.database.Monument;
import fr.isen.shazamphoto.database.NearestMonumentsDAO;
import fr.isen.shazamphoto.events.RequestNearestFromMonument;
import fr.isen.shazamphoto.utils.ConfigurationShazam;
import fr.isen.shazamphoto.utils.FunctionsDB;
import fr.isen.shazamphoto.utils.GetMonumentByLocalization;


public class DetailMonument extends ActionBarActivity implements ScrollViewListener {

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

        //Retrieve the TextView, the buttons and the imageView
        gridView = (GridView) findViewById(R.id.gridView_nearestMonuments);
        TextView nbLike = (TextView) findViewById(R.id.textView_nbLike);
        nbVisitor = (TextView) findViewById(R.id.textView_nbVisitor);
        noNearestMonument = (TextView) findViewById(R.id.adm_textview_nonearestmonument);
        final ImageView photoView = (ImageView) findViewById(R.id.imageView1);
        ADMScrollView scrollView = (ADMScrollView) findViewById(R.id.adm_scrollview);

        // retrieve the monument and some element of the monument detail activity
        monument = (Monument) getIntent().getSerializableExtra(Monument.NAME_SERIALIZABLE);

        setColumnWidthView(getResources().getConfiguration().orientation);

        //localization.setText(monument.getLocalization().toString());

        setTitle("");

        //Set the picture
        if (monument.getPhotoPath() != null && !monument.getPhotoPath().isEmpty()) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 4;
            Bitmap bitmap = BitmapFactory.decodeFile(monument.getPhotoPath(), options);
            photoView.setImageBitmap(bitmap);
        }


        //Set the monument information
        nbLike.setText(monument.getName());
        nbVisitor.setText("More than " + monument.getNbLike() + " likes and " + monument.getNbVisitors() + " visitors");

        Button button = (Button) findViewById(R.id.button_like);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        // Set the button favourite
        final Button favouriteButton = (Button) findViewById(R.id.button_add_favorite);
        favouriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FunctionsDB.addMonumentToDB(monument, getApplicationContext());
                FavouriteMonumentDAO favouriteMonumentDAO = new FavouriteMonumentDAO(activity);
                favouriteMonumentDAO.open();
                if (favouriteMonumentDAO.select(monument.getId()) != null) {
                    favouriteMonumentDAO.delete(monument);
                    favouriteButton.setText("ADD TO FAVORITE");
                } else {
                    favouriteMonumentDAO.insert(monument);
                    favouriteButton.setText("REMOVE FROM FAV.");
                }
                favouriteMonumentDAO.close();
            }
        });
        FavouriteMonumentDAO favouriteMonumentDAO = new FavouriteMonumentDAO(activity);
        favouriteMonumentDAO.open();
        if (favouriteMonumentDAO.select(monument.getId()) != null) {
            favouriteButton.setText("REMOVE FROM FAV.");
        } else {
            favouriteButton.setText("ADD TO FAVORITE");
        }
        favouriteMonumentDAO.close();

        // Get the nearest monuments
        NearestMonumentsDAO nearestMonumentsDAO = new NearestMonumentsDAO(this);
        nearestMonumentsDAO.open();
        ArrayList<Monument> monuments = nearestMonumentsDAO.getNearestMonuments(this.monument.getId());
        if (!monuments.isEmpty()) {
            setGridViewArrayList(monuments);
        } else {
            RequestNearestFromMonument requestNearestFromMonument = new RequestNearestFromMonument(this);
            GetMonumentByLocalization getMonumentByLocalization = new GetMonumentByLocalization(requestNearestFromMonument);
            getMonumentByLocalization.execute(Double.valueOf(monument.getLocalization().getLatitude()).toString(),
                    Double.valueOf(monument.getLocalization().getLongitude()).toString(), ConfigurationShazam.DELTA_LOCALIZATION);
        }

        scrollView.setScrollViewListener(this);
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
}
