package fr.isen.shazamphoto.events;

import java.util.ArrayList;

import fr.isen.shazamphoto.database.Localization;
import fr.isen.shazamphoto.database.Monument;
import fr.isen.shazamphoto.ui.DetailMonument;

public class RequestNearestFromMonument extends RequestLocalization {

    private DetailMonument detailMonument;

    public RequestNearestFromMonument(DetailMonument detailMonument) {
        this.detailMonument = detailMonument;
    }

    @Override
    public void doPostAction(ArrayList<Monument> monuments, Localization localization) {
        detailMonument.setListNearestMonuments(monuments);
    }

    public DetailMonument getDetailMonument() {
        return detailMonument;
    }

}
