package fr.isen.shazamphoto.events;

import fr.isen.shazamphoto.ui.Shazam;

public class EventLoadingSearchMonument extends Event{
    private Shazam shazam;

    public EventLoadingSearchMonument(Shazam shazam) {
        this.shazam = shazam;
    }

    public Shazam getShazam() {
        return shazam;
    }
}
