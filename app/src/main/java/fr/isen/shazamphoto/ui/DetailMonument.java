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
import android.widget.Toast;

import java.util.ArrayList;

import fr.isen.shazamphoto.R;
import fr.isen.shazamphoto.database.FavouriteMonumentDAO;
import fr.isen.shazamphoto.database.Monument;
import fr.isen.shazamphoto.database.MonumentDAO;
import fr.isen.shazamphoto.events.RequestNearestFromMonument;
import fr.isen.shazamphoto.utils.GetMonumentByLocalization;


public class DetailMonument extends ActionBarActivity {

    public static final int NBMAX_MONUMENT_NEAREST_TO_DISPLAY_LANDSCAPE = 4;
    public static final int NBMAX_MONUMENT_NEAREST_TO_DISPLAY_PORTRAIT = 3;
    public static final int GRIDVIEW_SPACING = 8;
    public static final int GRIDVIEW_PADDING = 16;

    private Monument monument;
    private GridView gridView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_monument);
        final Activity activity = this;
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.argb(0, 0, 0, 0)));

        //Retrieve the TextView, the buttons and the imageView
        TextView nbLike = (TextView) findViewById(R.id.textView_nbLike);
        TextView nbVisitor = (TextView) findViewById(R.id.textView_nbVisitor);
        TextView localization = (TextView) findViewById(R.id.textview_localization);
        final ImageView photoView = (ImageView) findViewById(R.id.imageView1);
        gridView = (GridView)findViewById(R.id.gridView_nearestMonuments);

        // retrieve the monument and some element of the monument detail activity
        monument = (Monument) getIntent().getSerializableExtra(Monument.NAME_SERIALIZABLE);

        setColumnWidthView(getResources().getConfiguration().orientation);

        localization.setText(monument.getLocalization().toString());

        setTitle("");

        //Set the picture
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 4;
        Bitmap bitmap = BitmapFactory.decodeFile(monument.getPhotoPath(), options);
        photoView.setImageBitmap(bitmap);

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
                addMonumentToDB();
                FavouriteMonumentDAO favouriteMonumentDAO = new FavouriteMonumentDAO(activity);
                favouriteMonumentDAO.open();
                if (favouriteMonumentDAO.select(monument.getId()) != null) {
                    favouriteMonumentDAO.delete(monument);
                    favouriteButton.setText("ADD TO FAVORITE");
                } else {
                    favouriteMonumentDAO.insert(monument);
                    favouriteButton.setText("REMOVE FROM FAVOURITE");
                }
                favouriteMonumentDAO.close();
            }
        });
        FavouriteMonumentDAO favouriteMonumentDAO = new FavouriteMonumentDAO(activity);
        favouriteMonumentDAO.open();
        if (favouriteMonumentDAO.select(monument.getId()) != null) {
            favouriteButton.setText("REMOVE FROM FAVORITE");
        } else {
            favouriteButton.setText("ADD TO FAVORITE");
        }
        favouriteMonumentDAO.close();

        // Get the nearest monuments
        RequestNearestFromMonument requestNearestFromMonument = new RequestNearestFromMonument(this);
        GetMonumentByLocalization getMonumentByLocalization = new GetMonumentByLocalization(requestNearestFromMonument);
        getMonumentByLocalization.execute(Float.valueOf(monument.getLocalization().getLatitude()).toString(),
                Float.valueOf(monument.getLocalization().getLongitude()).toString(), "0.09");

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
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

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

    private void addMonumentToDB() {
        if (monument != null && monument.getId() == -1) {
            MonumentDAO dao = new MonumentDAO(this);
            dao.open();
            long id = dao.getMonumentId(monument);
            if (id == -1) {
                id = dao.insert(monument);
            }
            monument.setId(id);
            dao.close();
        }
    }

    public void setListNearestMonuments(ArrayList<Monument> monuments){
        if(!monuments.isEmpty()){

            ArrayList<Monument> monumentsFiltered = new ArrayList<>();
            int size = (monuments.size() > 4) ? NBMAX_MONUMENT_NEAREST_TO_DISPLAY_LANDSCAPE : monuments.size();
            for(int i = 0; i<size; i++){
                Monument m = monuments.get(i);
                if(!m.getName().equals(monument.getName())){
                    monumentsFiltered.add(m);
                }
            }

            if(!monumentsFiltered.isEmpty()){
                // Set the grid view of the nearests monuments
                GridViewAdapter gridViewAdapter = new GridViewAdapter(this, monumentsFiltered);
                gridView.setAdapter(gridViewAdapter);
            }
        }
    }

    public void setColumnWidthView(int orientation){
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int columnWidth = 0;

        // Checks the orientation of the screen
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            columnWidth = (int) ((width - 2*GRIDVIEW_PADDING -2*GRIDVIEW_SPACING)/(NBMAX_MONUMENT_NEAREST_TO_DISPLAY_LANDSCAPE+1)) ;

        } else if (orientation == Configuration.ORIENTATION_PORTRAIT){
            columnWidth = (int) ((width - 2*GRIDVIEW_PADDING -2*GRIDVIEW_SPACING)/(NBMAX_MONUMENT_NEAREST_TO_DISPLAY_PORTRAIT+1)) ;
        }

        gridView.setColumnWidth(columnWidth);
    }
}
