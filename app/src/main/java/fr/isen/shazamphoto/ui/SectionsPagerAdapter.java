package fr.isen.shazamphoto.ui;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.widget.Toast;

import java.util.Locale;

import fr.isen.shazamphoto.R;


public class SectionsPagerAdapter extends FragmentPagerAdapter {

    private Home home;
    private final int nbPage = 4;
    private Shazam shazam;

    public SectionsPagerAdapter(FragmentManager fm, Home act) {
        super(fm);
        home = act;
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
                result = NearestMonumentsFragment.newInstance();
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