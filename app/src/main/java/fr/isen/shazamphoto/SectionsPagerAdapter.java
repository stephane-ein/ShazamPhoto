package fr.isen.shazamphoto;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.Locale;


public class SectionsPagerAdapter extends FragmentPagerAdapter {

    private Activity activity;
    private final int nbPage = 4;

    public SectionsPagerAdapter(FragmentManager fm, Activity act) {
        super(fm);
        activity = act;
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
        if (position == 0) {

            return new Shazam();
        }else{
            return new About();
        }
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