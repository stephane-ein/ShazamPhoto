package fr.isen.shazamphoto.ui;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import fr.isen.shazamphoto.R;
import fr.isen.shazamphoto.database.Monument;
import fr.isen.shazamphoto.model.ModelNavigation;

public class UnidentifiedMonument extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unidentified_monument);
        PromptNameMonument promptNameMonument = new PromptNameMonument();
        Bundle args = new Bundle();
        args.putSerializable(Monument.NAME_SERIALIZABLE, getIntent().getExtras().getSerializable(Monument.NAME_SERIALIZABLE));
        args.putSerializable(ModelNavigation.KEY, getIntent().getExtras().getSerializable(ModelNavigation.KEY));
        promptNameMonument.setArguments(args);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, promptNameMonument)
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_unidentified_monument, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

}
