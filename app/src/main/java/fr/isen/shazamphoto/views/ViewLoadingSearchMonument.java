package fr.isen.shazamphoto.views;


import fr.isen.shazamphoto.events.Event;
import fr.isen.shazamphoto.events.EventLoadingSearchMonument;
import fr.isen.shazamphoto.ui.Shazam;

public class ViewLoadingSearchMonument extends View{

    @Override
    public void refresh(Event event) {
        if(event instanceof EventLoadingSearchMonument){
            EventLoadingSearchMonument src = (EventLoadingSearchMonument) event;
            Shazam shazam = src.getShazam();
            shazam.displayLoading();
        }

    }
}
