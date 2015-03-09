package fr.isen.shazamphoto.events;

import java.util.ArrayList;

import fr.isen.shazamphoto.database.Monument;
import fr.isen.shazamphoto.ui.Shazam;

public class EventSearchMonumentByName extends Event {

    private ArrayList<Monument> monuments;
    private Shazam shazam;

    public EventSearchMonumentByName(ArrayList<Monument> monuments, Shazam shazam) {
        this.monuments = monuments;
        this.shazam = shazam;
    }

    public ArrayList<Monument> getMonuments() {
        return monuments;
    }

    public Shazam getShazam() {
        return shazam;
    }
}
