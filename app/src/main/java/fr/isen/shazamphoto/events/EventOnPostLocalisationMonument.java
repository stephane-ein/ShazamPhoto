package fr.isen.shazamphoto.events;


import java.util.ArrayList;

import fr.isen.shazamphoto.database.Localization;
import fr.isen.shazamphoto.database.Monument;

public class EventOnPostLocalisationMonument extends Event {

    private Localization localization;
    private ArrayList<Monument> monuments;

    public EventOnPostLocalisationMonument(Localization localization, ArrayList<Monument> monuments) {
        this.localization = localization;
        this.monuments = monuments;
    }

    public Localization getLocalization() {
        return localization;
    }

    public void setLocalization(Localization localization) {
        this.localization = localization;
    }
}
