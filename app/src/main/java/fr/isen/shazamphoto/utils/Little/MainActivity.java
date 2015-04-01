package fr.isen.shazamphoto.utils.Little;

import android.os.Bundle;


public class MainActivity{

    protected void onCreate(Bundle savedInstanceState) {
        int[][] test = {{0,1,2,3,4,5,6,0},
                        {1,-1,780,320,580,480,660,0},
                        {2,780,-1,700,460,300,200,0},
                        {3,320,700,-1,380,820,630,0},
                        {4,580,460,380,-1,750,310,0},
                        {5,480,300,820,750,-1,500,0},
                        {6,660,200,630,310,500,-1,0},
                        {0,0,0,0,0,0,0,0}};
        Little little = new Little(6,test);
        little.doLittle();
        int toto=1;
    }


}
