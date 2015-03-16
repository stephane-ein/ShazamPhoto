package fr.isen.shazamphoto.events;

import android.app.Activity;

import java.util.ArrayList;

import fr.isen.shazamphoto.database.Monument;
import fr.isen.shazamphoto.ui.Shazam;

public class EventSearchMonumentByName extends Event {

    private ArrayList<Monument> monuments;
    private Shazam shazam;
    private Activity activity;

    public EventSearchMonumentByName(ArrayList<Monument> monuments, Shazam shazam, Activity activity) {
        this.monuments = monuments;
        this.shazam = shazam;
        this.activity = activity;
    }

    public ArrayList<Monument> getMonuments() {
        return monuments;
    }

    public Shazam getShazam() {
        return shazam;
    }

    public Activity getActivity() {
        return activity;
    }
}
