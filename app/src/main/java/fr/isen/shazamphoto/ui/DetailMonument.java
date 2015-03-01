package fr.isen.shazamphoto.ui;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import fr.isen.shazamphoto.R;
import fr.isen.shazamphoto.database.FavouriteMonumentDAO;
import fr.isen.shazamphoto.database.Monument;
import fr.isen.shazamphoto.database.MonumentDAO;


public class DetailMonument extends ActionBarActivity {

    private Monument monument;

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
        final ImageView photoView = (ImageView) findViewById(R.id.imageView1);
        GridView gridView=(GridView)findViewById(R.id.gridView_nearestMonuments);

        // Create the Custom Adapter Object
        GridViewAdapter gridViewAdapter = new GridViewAdapter(this);
        // Set the Adapter to GridView
        gridView.setAdapter(gridViewAdapter);

        // retrieve the monument and some element of the monument detail activity
        monument = (Monument) getIntent().getSerializableExtra(Monument.NAME_SERIALIZABLE);

        setTitle("");

        //Set the picture
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 4;
        Bitmap bitmap = BitmapFactory.decodeFile(monument.getPhotoPath(), options);
        photoView.setImageBitmap(bitmap);

        //Set the informations
        nbLike.setText(monument.getName());
        nbVisitor.setText("More than " + monument.getNbLike() + " likes and " + monument.getNbVisitors() + " visitors");

        Button button = (Button) findViewById(R.id.button_like);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

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
}
