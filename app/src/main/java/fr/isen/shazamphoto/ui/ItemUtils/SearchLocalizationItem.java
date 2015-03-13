package fr.isen.shazamphoto.ui.ItemUtils;


import fr.isen.shazamphoto.events.EventLocalizationFound;

public interface SearchLocalizationItem {

    // If an activity or a fragment use the LocateManager, he has to implements this interface
    // and handle the result of the search
    public void foundLocalization(EventLocalizationFound eventLocalizationFound);
}
