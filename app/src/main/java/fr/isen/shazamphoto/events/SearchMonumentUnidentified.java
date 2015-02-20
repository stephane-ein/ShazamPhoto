package fr.isen.shazamphoto.events;

import fr.isen.shazamphoto.ui.UnidentifiedMonument;

public class SearchMonumentUnidentified extends SearchMonument {

    private UnidentifiedMonument unidentifiedMonument;

    public SearchMonumentUnidentified(UnidentifiedMonument unidentifiedMonument) {
        this.unidentifiedMonument = unidentifiedMonument;
    }

    public UnidentifiedMonument getUnidentifiedMonument() {
        return unidentifiedMonument;
    }
}
