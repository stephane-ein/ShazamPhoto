package fr.isen.shazamphoto.events;

import android.app.Activity;

import fr.isen.shazamphoto.database.Monument;

public class EventDisplayDetailMonument extends Event{

    private Activity activity;
    private Monument monument;

    public EventDisplayDetailMonument(Activity activity, Monument monument) {
        this.activity = activity;
        this.monument = monument;
    }

    public Activity getActivity() {
        return activity;
    }

    public Monument getMonument() {
        return monument;
    }
}
