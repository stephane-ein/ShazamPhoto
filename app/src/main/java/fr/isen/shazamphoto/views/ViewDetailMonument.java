package fr.isen.shazamphoto.views;

import android.app.Activity;
import android.content.Intent;

import fr.isen.shazamphoto.database.Monument;
import fr.isen.shazamphoto.events.Event;
import fr.isen.shazamphoto.events.EventDisplayDetailMonument;
import fr.isen.shazamphoto.model.ModelNavigation;
import fr.isen.shazamphoto.ui.DetailMonument;

public class ViewDetailMonument extends View{

    @Override
    public void refresh(Event event) {

        //Display the detail view of a monument
        if(event instanceof EventDisplayDetailMonument){

            EventDisplayDetailMonument evt = (EventDisplayDetailMonument) event;

            Activity activity = evt.getActivity();
            Monument monument = evt.getMonument();
            ModelNavigation modelNavigation = evt.getModelNavigation();
            Intent intent = new Intent(activity, DetailMonument.class);
            intent.putExtra(Monument.NAME_SERIALIZABLE, monument);
            intent.putExtra(ModelNavigation.KEY, modelNavigation);
            activity.startActivity(intent);
        }
    }
}
