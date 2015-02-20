package fr.isen.shazamphoto.events;


import fr.isen.shazamphoto.ui.Shazam;

public class SearchMonumentByName extends SearchMonument {

    private Shazam shazam;

    public SearchMonumentByName(Shazam shazam) {
        this.shazam = shazam;
    }

    public Shazam getShazam() {
        return shazam;
    }
}
