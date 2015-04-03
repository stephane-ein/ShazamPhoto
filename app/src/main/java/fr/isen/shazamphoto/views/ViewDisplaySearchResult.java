package fr.isen.shazamphoto.views;


import java.util.ArrayList;

import fr.isen.shazamphoto.database.Monument;
import fr.isen.shazamphoto.events.Event;
import fr.isen.shazamphoto.events.EventResultSearchMonument;
import fr.isen.shazamphoto.ui.Shazam;

public class ViewDisplaySearchResult extends View{

    @Override
    public void refresh(Event event) {
        if(event instanceof EventResultSearchMonument){
            EventResultSearchMonument src = (EventResultSearchMonument) event;
            Shazam shazam = src.getShazam();
            ArrayList<Monument> monuments = src.getMonuments();
            // Adapt the view if any monuments was found
            if(monuments.isEmpty()){
                shazam.displayNoMonumentFound();
            }else{
                shazam.displayMonumentFound(monuments, src.getQuery());
            }
        }

    }
}
