package fr.isen.shazamphoto.events;

import fr.isen.shazamphoto.ui.NearestMonumentsFragment;

public class RequestNearestMonuments extends RequestLocalization {
    private NearestMonumentsFragment nearestMonumentsFragment;

    public RequestNearestMonuments(NearestMonumentsFragment nearestMonumentsFragment) {
        this.nearestMonumentsFragment = nearestMonumentsFragment;
    }

    public NearestMonumentsFragment getNearestMonumentsFragment() {
        return nearestMonumentsFragment;
    }
}
