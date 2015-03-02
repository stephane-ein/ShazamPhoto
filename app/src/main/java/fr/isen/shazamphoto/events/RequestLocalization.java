package fr.isen.shazamphoto.events;


import java.util.ArrayList;

import fr.isen.shazamphoto.database.Localization;
import fr.isen.shazamphoto.database.Monument;

public abstract class RequestLocalization {

    public abstract void doPostAction(ArrayList<Monument> monuments, Localization localization);
}
