package fr.isen.shazamphoto.views;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import fr.isen.shazamphoto.database.Monument;
import fr.isen.shazamphoto.events.Event;
import fr.isen.shazamphoto.events.EventUnidentifiedMonument;
import fr.isen.shazamphoto.model.ModelNavigation;
import fr.isen.shazamphoto.ui.UnidentifiedMonument;

public class ViewUndentifiedMonument extends View{

    @Override
    public void refresh(Event event) {

        if(event instanceof EventUnidentifiedMonument){

            EventUnidentifiedMonument evt = (EventUnidentifiedMonument) event;
            Activity activity = evt.getActivity();
            Monument monument = evt.getMonument();
            ModelNavigation modelNavigation = evt.getModelNavigation();

            // Display the activity for an undidentified monument
            Intent intent = new Intent(activity, UnidentifiedMonument.class);
            Bundle args = new Bundle();
            args.putSerializable(Monument.NAME_SERIALIZABLE, monument);
            args.putSerializable(ModelNavigation.KEY, modelNavigation);
            intent.putExtras(args);
            activity.startActivity(intent);
        }
    }
}
