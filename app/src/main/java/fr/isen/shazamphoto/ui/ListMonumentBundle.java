package fr.isen.shazamphoto.ui;


import java.io.Serializable;
import java.util.ArrayList;

import fr.isen.shazamphoto.database.Monument;

public class ListMonumentBundle implements Serializable{

    private ArrayList<Monument> monuments;

    public ListMonumentBundle(ArrayList<Monument> monuments) {
        this.monuments = monuments;
    }

    public ArrayList<Monument> getMonuments() {
        return monuments;
    }

}
