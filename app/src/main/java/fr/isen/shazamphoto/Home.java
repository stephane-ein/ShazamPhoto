package fr.isen.shazamphoto;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;


public class Home extends ActionBarActivity {

    // Items for the menu drawer
    private String[] navigationArray;
    private DrawerLayout drawerLayout;
    private ListView drawerList;

    static private String FRAGMENT_STACK = "fragmentStack";

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

        selectItem(0);
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

    /** Swaps fragments in the main content view */
    private void selectItem(int position) {
        Fragment fragment = null;
        switch (position) {

            case Shazam.POSITION:
                fragment = new Shazam();
                break;
            case About.POSITION:
                fragment = new About();
                setTitle("About selected");
                break;
        }

        if (fragment != null) {
            setFragment(fragment, FRAGMENT_STACK, false);
        }

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
