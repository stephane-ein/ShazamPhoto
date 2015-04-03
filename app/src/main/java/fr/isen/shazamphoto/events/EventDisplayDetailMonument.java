package fr.isen.shazamphoto.events;

import android.app.Activity;

import fr.isen.shazamphoto.database.Monument;
import fr.isen.shazamphoto.model.ModelNavigation;

public class EventDisplayDetailMonument extends EventMonument{

    private ModelNavigation modelNavigation;

    public EventDisplayDetailMonument(Activity activity, Monument monument, ModelNavigation modelNavigation) {
        super(activity, monument);
        this.modelNavigation = modelNavigation;
    }

    public ModelNavigation getModelNavigation() {
        return modelNavigation;
    }
}
