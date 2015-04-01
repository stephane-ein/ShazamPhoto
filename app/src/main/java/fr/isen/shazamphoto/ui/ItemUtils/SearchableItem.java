package fr.isen.shazamphoto.ui.ItemUtils;


import java.util.ArrayList;

import fr.isen.shazamphoto.database.Monument;

public interface SearchableItem {

    // If an activity or a fragment use the GetMonumentsByName, he has to implements this interface
    // and handle the result of the search
    public void onPostSearch(ArrayList<Monument> monuments, String searchName);
}
