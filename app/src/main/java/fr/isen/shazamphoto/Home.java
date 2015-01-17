package fr.isen.shazamphoto;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;


public class Home extends ActionBarActivity {

    // Items for the menu drawer
    private String[] navigationArray;
    private DrawerLayout drawerLayout;
    private ListView drawerList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Initialization of the drawer
        navigationArray = getResources().getStringArray(
                R.array.navigation_array);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerList = (ListView) findViewById(R.id.left_drawer);

        // Set the adapter for the list view
        drawerList.setAdapter(new ArrayAdapter<String>(this,
                R.layout.drawer_list_item, navigationArray));
        // Set the list's click listener
        drawerList.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                selectItem(position);
            }
        });

        /*if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }*/

       /* button = (Button)findViewById(R.id.but_takePicture);
        mImageView = (ImageView)findViewById(R.id.img_result);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });*/

        setFragment(new Shazam(), "FRAGMENT", false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_home, container, false);
            return rootView;
        }
    }

    /** Swaps fragments in the main content view */
    private void selectItem(int position) {

        /*switch (position) {

            case About.position:
                fragment = new About();
                break;
            case FilmToSeeListFragment.POSTION:
                fragment = createFilmListFragment(toSeeDAO,
                        new FilmToSeeListFragment());
                break;

            case FilmAllListFragment.POSITION:
                fragment = createFilmListFragment(filmDAO,
                        new FilmAllListFragment());
                break;

            case FilmFavouriteListFragment.POSITION:
                fragment = createFilmListFragment(favouriteDAO,
                        new FilmFavouriteListFragment());
                break;
        }

        if (fragment != null) {
            setFragment(fragment, STACK_FILMLIST, false);
        }*/

        // Highlight the selected item and close the drawer
        drawerList.setItemChecked(position, true);
        drawerLayout.closeDrawer(drawerList);

    }

    public void setFragment(Fragment fragment, String name, boolean disableDrawer) {
        if (disableDrawer) {
            //drawerToggle.setDrawerIndicatorEnabled(false);
        }

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.addToBackStack(name);
        ft.replace(R.id.frame_fragment, fragment, name);
        ft.commit();
    }
}
