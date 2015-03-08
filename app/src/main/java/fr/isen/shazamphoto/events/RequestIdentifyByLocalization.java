package fr.isen.shazamphoto.events;

import android.content.Intent;

import java.util.ArrayList;

import fr.isen.shazamphoto.database.Localization;
import fr.isen.shazamphoto.database.Monument;
import fr.isen.shazamphoto.ui.DetailMonument;
import fr.isen.shazamphoto.ui.Home;
import fr.isen.shazamphoto.ui.NearestMonumentsFragment;
import fr.isen.shazamphoto.ui.UnidentifiedMonument;
import fr.isen.shazamphoto.utils.FunctionsDB;

public class RequestIdentifyByLocalization extends RequestLocalization{

    private Home home;
    private String imagePath;

    public RequestIdentifyByLocalization(Home home, String imagePath) {
        this.home = home;
        this.imagePath = imagePath;
    }

    public Home getHome() {
        return home;
    }

    public String getImagePath() {
        return imagePath;
    }

    @Override
    public void doPostAction(ArrayList<Monument> monuments, Localization localization) {

        if (!monuments.isEmpty()) {
            Intent intent = new Intent(home, DetailMonument.class);
            monuments.get(0).setPhotoPath(imagePath);
            FunctionsDB.addMonumentToDB(monuments.get(0), home);
            FunctionsDB.addMonumentToTaggedMonument(monuments.get(0), home);
            intent.putExtra(Monument.NAME_SERIALIZABLE, monuments.get(0));
            home.startActivity(intent);

            // Set the position, in order to not use the NETWORK_PROVIDER
            NearestMonumentsFragment nearestMonumentsFragment = (NearestMonumentsFragment)
                    home.getSectionsPagerAdapter().getItem(NearestMonumentsFragment.POSITION);
            nearestMonumentsFragment.setLocalization(localization);

        } else {

            //Unidentified Monument
            Intent intent = new Intent(home, UnidentifiedMonument.class);
            home.startActivity(intent);
        }
    }
}
