package fr.isen.shazamphoto.ui;

import android.content.Context;
import android.location.LocationManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.Locale;

import fr.isen.shazamphoto.R;


public class SectionsPagerAdapter extends FragmentPagerAdapter {

    private Home home;
    private final int nbPage = 4;
    private ArrayList<Fragment> fragments;

    public SectionsPagerAdapter(FragmentManager fm, Home act) {
        super(fm);
        home = act;
        fragments = new ArrayList<>(nbPage);
        for (int i = 0; i < nbPage; i++) fragments.add(null);
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.

        Fragment result = null;

        switch (position) {
            case 0:
                if (fragments.get(Shazam.POSITION) == null) {
                    fragments.add(Shazam.POSITION, Shazam.newInstance(
                            (LocationManager) home.getSystemService(Context.LOCATION_SERVICE),
                            home.getModelNavigation(), home));
                }
                result = fragments.get(Shazam.POSITION);
                break;
            case 1:
                result = TaggedMonument.newInstance(home.getModelNavigation());
                break;
            case 2:
                if (fragments.get(NearestMonumentsFragment.POSITION) == null) {
                    fragments.add(NearestMonumentsFragment.POSITION,
                            NearestMonumentsFragment.newInstance((LocationManager)
                                    home.getSystemService(Context.LOCATION_SERVICE), home.getModelNavigation()));
                }
                result = fragments.get(NearestMonumentsFragment.POSITION);
                break;
            case 3:
                result = FavouriteMonument.newInstance(home.getModelNavigation());
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
                return home.getString(R.string.title_section1).toUpperCase(l);
            case 1:
                return home.getString(R.string.title_section2).toUpperCase(l);
            case 2:
                return home.getString(R.string.title_section3).toUpperCase(l);
            case 3:
                return home.getString(R.string.title_section4).toUpperCase(l);
        }
        return null;
    }

}