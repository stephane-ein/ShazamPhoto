package fr.isen.shazamphoto.utils;

import android.location.Location;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

import fr.isen.shazamphoto.database.Localization;
import fr.isen.shazamphoto.database.Monument;

public class Sort {

    public static void sortArrayMonument(Localization localizationUser, ArrayList<Monument> monuments){
        // Calculate the distance between the monument
        for(int i = 0; i < monuments.size(); i++){
            Monument monument = monuments.get(i);
            Localization destionLocalization = monument.getLocalization();
            float[] result = new float[3];
            if(destionLocalization != null){
                Location.distanceBetween(
                        destionLocalization.getLatitude(), destionLocalization.getLongitude(),
                        localizationUser.getLatitude(), localizationUser.getLongitude(),
                        result);
                monuments.get(i).setDistanceToDest(Integer.valueOf((int)result[0]));
            }else{
                monuments.get(i).setDistanceToDest(Integer.valueOf(-1));
            }
        }

        // Do the buble sort
        int n = monuments.size();
        boolean swaped = true;
        Monument tmp;
        while( n > 0 && swaped){
            swaped = false;
            for(int j = 0; j < n-1; j++){
                if(monuments.get(Integer.valueOf(j)).getDistanceToDest()  > monuments.get(Integer.valueOf(j+1)).getDistanceToDest()){
                    tmp = monuments.get(j);
                    monuments.set(j, monuments.get(j+1));
                    monuments.set(j+1, tmp);
                    swaped = true;
                }
            }
            n--;
        }

    }
}
