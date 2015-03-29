package fr.isen.shazamphoto.events;

import fr.isen.shazamphoto.database.Monument;

public class EventMonumentUpdated {
    private Monument monument;

    public EventMonumentUpdated(Monument monument) {
        this.monument = monument;
    }

    public Monument getMonument() {
        return monument;
    }
}
