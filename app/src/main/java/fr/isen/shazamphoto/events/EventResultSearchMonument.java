package fr.isen.shazamphoto.events;

import android.app.Activity;

import java.util.ArrayList;

import fr.isen.shazamphoto.database.Monument;
import fr.isen.shazamphoto.ui.Shazam;

public class EventResultSearchMonument extends Event{
    private Shazam shazam;
    private ArrayList<Monument> monuments;
    private String query;
    private Activity activity;

    public EventResultSearchMonument(Shazam shazam, ArrayList<Monument> monuments, String query, Activity activity) {
        this.shazam = shazam;
        this.monuments = monuments;
        this.query = query;
        this.activity = activity;
    }

    public Shazam getShazam() {
        return shazam;
    }

    public ArrayList<Monument> getMonuments() {
        return monuments;
    }

    public String getQuery() {
        return query;
    }

    public Activity getActivity() {
        return activity;
    }
}
