package fr.isen.shazamphoto.ui;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import java.util.ArrayList;

import fr.isen.shazamphoto.R;
import fr.isen.shazamphoto.database.Monument;
import fr.isen.shazamphoto.events.EventSearchMonumentByName;
import fr.isen.shazamphoto.model.ModelNavigation;
import fr.isen.shazamphoto.ui.ItemUtils.SearchableItem;
import fr.isen.shazamphoto.ui.SlidingTab.SlidingTabLayout;
import fr.isen.shazamphoto.utils.GetMonumentTask.GetMonumentSearch;
import fr.isen.shazamphoto.views.ViewDetailMonument;
import fr.isen.shazamphoto.views.ViewMonumentsResult;
import fr.isen.shazamphoto.views.ViewUndentifiedMonument;

public class Home extends ActionBarActivity implements SearchableItem {

    private SearchView searchView;
    private MenuItem searchMenuItem;
    private SlidingTabLayout mSlidingTabLayout;
    private ViewPager mViewPager;
    private SectionsPagerAdapter sectionsPagerAdapter;
    private ModelNavigation modelNavigation;

    private ListView listView;
    private NetworkInfoArea networkInfo;


    private static Handler timerHandler = new Handler();
    private static Runnable timerRunnable;
    private static long startTime = 0;
    private static boolean hiden2 = false;

    //handle the keyboard
    private InputMethodManager imm;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Set the Model of the navigation with his views
        this.modelNavigation = new ModelNavigation();
        this.modelNavigation.addView(new ViewMonumentsResult());
        this.modelNavigation.addView(new ViewDetailMonument());
        this.modelNavigation.addView(new ViewUndentifiedMonument());

        sectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), this);
        // BEGIN_INCLUDE (setup_viewpager)
        // Get the ViewPager and set it's PagerAdapter so that it can display items
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(sectionsPagerAdapter);
        // END_INCLUDE (setup_viewpager)

        // BEGIN_INCLUDE (setup_slidingtablayout)
        // Give the SlidingTabLayout the ViewPager, this must be done AFTER the ViewPager has had
        // it's PagerAdapter set.
        mSlidingTabLayout = (SlidingTabLayout) findViewById(R.id.sliding_tabs);
        mSlidingTabLayout.setViewPager(mViewPager);
        // END_INCLUDE (setup_slidingtablayout)

        networkInfo = (NetworkInfoArea) findViewById(R.id.home_info_network);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);

        searchMenuItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) searchMenuItem.getActionView();
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);

        // Assumes current activity is the searchable activity
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false); // Do not iconify the widget; expand it by default

        //Close the view results
        listView = (ListView) findViewById(R.id.listview_result_monument);

        setListenerSearchViewListener(searchView, this);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        // Set the  focus on the search bar
        switch (item.getItemId()) {
            case R.id.action_search:
                searchView.setFocusable(true);
                searchView.setIconified(false);
                searchView.requestFocusFromTouch();

                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void setListenerSearchViewListener(final SearchView searchView, final Home home) {

        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean queryTextFocused) {
                if (!queryTextFocused) {
                    // Close the keyboard
                    InputMethodManager imm = (InputMethodManager) getSystemService(
                            Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);


                    listView.setLayoutParams(new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT, 0, 0));
                    listView.setVisibility(View.INVISIBLE);
                    Shazam shazam = (Shazam) getSectionsPagerAdapter().getItem(Shazam.POSITION);
                    shazam.clearMonuments();
                }
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String newText) {
                return true;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {
                //Make de search
                GetMonumentSearch getMonumentSearch = new GetMonumentSearch(networkInfo, home, home, query);
                getMonumentSearch.execute();

                // Close the keyboard
                imm = (InputMethodManager) getSystemService(
                        Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);

                return true;
            }
        });

    }

    @Override
    public void onPostSearch(ArrayList<Monument> monuments) {
        Shazam shazam = (Shazam) getSectionsPagerAdapter().getItem(Shazam.POSITION);
        modelNavigation.changeAppView(new EventSearchMonumentByName(monuments, shazam, this));

        Toast.makeText(getApplication(), "Monument found : " + monuments.size(), Toast.LENGTH_SHORT).show();

        //Set the view on the shazam fragment
        mViewPager.setCurrentItem(Shazam.POSITION);
        sectionsPagerAdapter.getItem(Shazam.POSITION);
    }

    public SectionsPagerAdapter getSectionsPagerAdapter() {
        return sectionsPagerAdapter;
    }

    public ModelNavigation getModelNavigation() {
        return modelNavigation;
    }
}
