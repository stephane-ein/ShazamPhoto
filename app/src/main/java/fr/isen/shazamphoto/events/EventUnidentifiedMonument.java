package fr.isen.shazamphoto.events;


import android.app.Activity;

import fr.isen.shazamphoto.database.Monument;

public class EventUnidentifiedMonument extends EventMonument {

    public EventUnidentifiedMonument(Activity activity, Monument monument) {
        super(activity, monument);
    }
}
