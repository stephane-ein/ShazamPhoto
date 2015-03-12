package fr.isen.shazamphoto.ui.ItemUtils;


import java.util.ArrayList;

import fr.isen.shazamphoto.database.Monument;
import fr.isen.shazamphoto.events.EventOnPostLocalisationMonument;

public interface LocalizationItem {

    // If an activity or a fragment use the GetMonumentByLocalization, he has to implements this interface
    // and handle the result of the search
    public void onPostSearch(EventOnPostLocalisationMonument eventOnPostLocalisationMonument);
}
