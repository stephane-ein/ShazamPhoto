package fr.isen.shazamphoto.events;

import android.app.Activity;
import android.content.Intent;
import android.widget.Toast;

import java.util.ArrayList;

import fr.isen.shazamphoto.database.Localization;
import fr.isen.shazamphoto.database.Monument;
import fr.isen.shazamphoto.ui.Home;
import fr.isen.shazamphoto.ui.ListMonumentBundle;
import fr.isen.shazamphoto.ui.NearestMonuments;
import fr.isen.shazamphoto.ui.NearestMonumentsFragment;

public class RequestNearestMonumentsAndMap extends RequestLocalization {

    private Home home;
    private NearestMonumentsFragment nearestMonumentsFragment;

    public RequestNearestMonumentsAndMap(Home home, NearestMonumentsFragment nearestMonumentsFragment) {
        this.home = home;
        this.nearestMonumentsFragment = nearestMonumentsFragment;
    }

    @Override
    public void doPostAction(ArrayList<Monument> monuments, Localization localization) {
        //super.doPostAction(monuments, localization);

        Toast.makeText(home, "Monument and a Map", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(home, NearestMonuments.class);
        intent.putExtra(NearestMonumentsFragment.NMF_NEATREST_MONUMENT_LIST,
               monuments);
        home.startActivity(intent);
    }
}
