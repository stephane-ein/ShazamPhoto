package fr.isen.shazamphoto.views;


import fr.isen.shazamphoto.events.Event;
import fr.isen.shazamphoto.events.EventSearchMonumentByName;
import fr.isen.shazamphoto.ui.Shazam;

public class ViewMonumentsResult extends View {

    @Override
    public void refresh(Event event) {

        //Diplay the result of a research by monument name
        if(event instanceof EventSearchMonumentByName){

            EventSearchMonumentByName evt = (EventSearchMonumentByName) event;
            Shazam shazam = evt.getShazam();
            shazam.setListResult(evt.getMonuments());
        }
    }
}
