package fr.isen.shazamphoto.events;

import android.app.Activity;

import fr.isen.shazamphoto.database.Monument;

public class EventDisplayDetailMonument extends EventMonument{

    public EventDisplayDetailMonument(Activity activity, Monument monument) {
        super(activity, monument);
    }
}
