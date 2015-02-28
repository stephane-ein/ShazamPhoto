package fr.isen.shazamphoto.ui;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.SearchView;
import android.widget.Toast;

import fr.isen.shazamphoto.R;
import fr.isen.shazamphoto.events.SearchMonumentByName;
import fr.isen.shazamphoto.utils.GetMonumentSearch;

public class Home extends ActionBarActivity {

    private SearchView searchView;
    private MenuItem searchMenuItem;
    private SlidingTabLayout mSlidingTabLayout;
    private ViewPager mViewPager;
    private SectionsPagerAdapter sectionsPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
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

        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean queryTextFocused) {
                if (!queryTextFocused) {
                    // Close the keyboard
                    InputMethodManager imm = (InputMethodManager) getSystemService(
                            Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);

                    //Close the view results
                    View listView = findViewById(R.id.listview_result_monument);
                    listView.setLayoutParams(new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT, 0, 0));
                    listView.setVisibility(View.INVISIBLE);
                    Shazam shazam = (Shazam) getSectionsPagerAdapter().getItem(Shazam.POSITION);
                    shazam.clearMonuments();
                }
            }
        });

        final Home home = this;
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String newText) {
                return true;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {

                //Set the view on the shazam fragment
                mViewPager.setCurrentItem(0);
                sectionsPagerAdapter.getItem(0);

                // Close the keyboard
                InputMethodManager imm = (InputMethodManager) getSystemService(
                        Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);

                //Make de search
                GetMonumentSearch getMonumentSearch = new GetMonumentSearch(
                        new SearchMonumentByName((Shazam)getSectionsPagerAdapter().getItem(
                                Shazam.POSITION)));
                getMonumentSearch.execute(query);

                return true;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        // Set the  focus on the search bar
        switch(item.getItemId()){
            case  R.id.action_search :
                searchView.setFocusable(true);
                searchView.setIconified(false);
                searchView.requestFocusFromTouch();
                break;
            case R.id.action_place :
                //Show the pop menu
                ActionPlacePopMenu popupMenu = new ActionPlacePopMenu(this, findViewById(R.id.action_place));
                popupMenu.showPopup();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public SectionsPagerAdapter getSectionsPagerAdapter() {
        return sectionsPagerAdapter;
    }

    public ViewPager getmViewPager() {
        return mViewPager;
    }
}
