package fr.isen.shazamphoto.ui.ItemUtils;

import fr.isen.shazamphoto.events.EventMonumentUpdated;

public interface UpdateMonumentItem {

    // If an activity or a fragment update a monument, he has to implements this interface
    // and handle the result of the search
    public void monumentUpdated(EventMonumentUpdated eventLocalizationFound);
}
