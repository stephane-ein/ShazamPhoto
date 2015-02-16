package fr.isen.shazamphoto.ui;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.Locale;

import fr.isen.shazamphoto.R;
import fr.isen.shazamphoto.database.Monument;


public class SectionsPagerAdapter extends FragmentPagerAdapter {

    private Activity activity;
    private final int nbPage = 4;
    private Shazam shazam;

    public SectionsPagerAdapter(FragmentManager fm, Activity act) {
        super(fm);
        activity = act;
        shazam = null;
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.

        Fragment result = null;

        switch (position) {
            case 0:
                result = new Shazam();
                break;
            case 1:
                result = new NearestMonumentsFragment();
                break;
            case 2:
                result = TaggedMonument.newInstance();
                break;
            case 3:
                result = FavouriteMonument.newInstance();
                break;
            default:
                result = new About();
        }

        return result;
    }

    @Override
    public int getCount() {
        return nbPage;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        Locale l = Locale.getDefault();
        switch (position) {
            case 0:
                return activity.getString(R.string.title_section1).toUpperCase(l);
            case 1:
                return activity.getString(R.string.title_section2).toUpperCase(l);
            case 2:
                return activity.getString(R.string.title_section3).toUpperCase(l);
            case 3:
                return activity.getString(R.string.title_section4).toUpperCase(l);
        }
        return null;
    }
}