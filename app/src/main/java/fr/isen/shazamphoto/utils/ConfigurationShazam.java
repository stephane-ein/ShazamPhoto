package fr.isen.shazamphoto.utils;


import android.os.Environment;

public class ConfigurationShazam {

    public static final String DELTA_LOCALIZATION = ".03";
    public static final String IP_SERVER = "37.187.216.159";
    public static final String DIRECTORY_CACHE = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString() + "/ShazamBuffer";
}
