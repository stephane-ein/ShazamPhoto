package fr.isen.shazamphoto.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import fr.isen.shazamphoto.R;
import fr.isen.shazamphoto.database.FavouriteMonumentDAO;
import fr.isen.shazamphoto.database.Monument;
import fr.isen.shazamphoto.database.MonumentDAO;


public class DetailMonument extends ActionBarActivity {

    private Monument monument;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_monument);

        // retrieve the monument and some element of the monument detail activity
        monument = (Monument) getIntent().getSerializableExtra(Monument.NAME_SERIALIZABLE);
        final TextView favouriteTexView = (TextView) findViewById(R.id.monument_textview_favourite);
        final Activity acticity = this;

        setTitle(monument.getName());


        LinearLayout linearLayoutLike = (LinearLayout) findViewById(R.id.linearLayoutLike);
        linearLayoutLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        LinearLayout linearLayoutFavorite = (LinearLayout) findViewById(R.id.linearLayoutFavorite);
        linearLayoutFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addMonumentToDBB();
                FavouriteMonumentDAO favouriteMonumentDAO = new FavouriteMonumentDAO(acticity);
                favouriteMonumentDAO.open();
                if (favouriteMonumentDAO.select(monument.getId()) != null) {
                    favouriteMonumentDAO.delete(monument);
                    favouriteTexView.setText("Add to favourites");
                } else {
                    favouriteMonumentDAO.insert(monument);
                    favouriteTexView.setText("Remove from favourites");
                }
                favouriteMonumentDAO.close();
            }
        });

        FavouriteMonumentDAO favouriteMonumentDAO = new FavouriteMonumentDAO(acticity);
        favouriteMonumentDAO.open();
        if (favouriteMonumentDAO.select(monument.getId()) != null) {
            favouriteTexView.setText("Remove from favourites");
        } else {
            favouriteTexView.setText("Add to favourites");
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
            case R.id.item_share:
                finish();
                return true;
            case R.id.item_delete:
                DeleteDialog deleteDialog = new DeleteDialog();
                deleteDialog.show(getFragmentManager(), "test");
                return true;
        }
        return super.onOptionsItemSelected(item);

    }

    private void addMonumentToDBB() {
        if (monument != null && monument.getId() == -1) {
            MonumentDAO dao = new MonumentDAO(this);
            dao.open();
            long id = dao.getMonumentId(monument);
            if (id == -1) {
                id = dao.insert(monument);
            }
            monument.setId(id);
            dao.close();
           /* if (film.getImage() != null) {
                saveImageToFile(film.getImage(), film.getName() + ".png");
            }*/
        }
    }
}
