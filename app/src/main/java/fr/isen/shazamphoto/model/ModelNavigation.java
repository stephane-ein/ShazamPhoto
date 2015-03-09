package fr.isen.shazamphoto.model;


import java.io.Serializable;
import java.util.LinkedList;

import fr.isen.shazamphoto.events.Event;
import fr.isen.shazamphoto.views.View;

//Class representing the different view that can be displayed
public class ModelNavigation extends Model implements Serializable{

    public static final String KEY = "fr.isen.shazamphoto.modelnavigation";

    private LinkedList<View> views;

    public ModelNavigation() {
        this.views = new LinkedList<>();
    }

    public void addView(View view){
        this.views.add(view);
    }

    public void changeAppView(Event event){
        for(View view : this.views){
            view.refresh(event);
        }
    }
}
