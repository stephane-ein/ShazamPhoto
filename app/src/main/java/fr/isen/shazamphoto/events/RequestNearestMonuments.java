package fr.isen.shazamphoto.events;

import java.util.ArrayList;

import fr.isen.shazamphoto.database.Localization;
import fr.isen.shazamphoto.database.Monument;
import fr.isen.shazamphoto.ui.NearestMonumentsFragment;

public class RequestNearestMonuments extends RequestLocalization {
    private NearestMonumentsFragment nearestMonumentsFragment;

    public RequestNearestMonuments(NearestMonumentsFragment nearestMonumentsFragment) {
        this.nearestMonumentsFragment = nearestMonumentsFragment;
    }

    public NearestMonumentsFragment getNearestMonumentsFragment() {
        return nearestMonumentsFragment;
    }

    @Override
    public void doPostAction(ArrayList<Monument> monuments, Localization localization) {
        nearestMonumentsFragment.setListNearestMonuments(monuments);

    }
}
