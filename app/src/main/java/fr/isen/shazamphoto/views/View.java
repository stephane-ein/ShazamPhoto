package fr.isen.shazamphoto.views;


import java.io.Serializable;

import fr.isen.shazamphoto.events.Event;

public abstract class View implements Serializable{

    public abstract void refresh(Event event);
}
