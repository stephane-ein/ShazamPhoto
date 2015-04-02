package fr.isen.shazamphoto.utils;

import android.content.Context;
import android.util.Log;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import fr.isen.shazamphoto.database.FavouriteMonumentDAO;
import fr.isen.shazamphoto.database.Monument;
import fr.isen.shazamphoto.database.MonumentDAO;
import fr.isen.shazamphoto.database.MonumentSearchDAO;
import fr.isen.shazamphoto.database.TaggedMonumentDAO;

public class FunctionsDB {

    public static void addMonumentToDB(Monument monument, Context context) {
        if (monument != null && monument.getId() == -1) {
            MonumentDAO dao = new MonumentDAO(context);
            dao.open();
            long id = dao.getMonumentId(monument);
            if (id == -1) {
                id = dao.insert(monument);
            }
            monument.setId(id);
            dao.close();
        }
    }

    public static void addMonumentToTaggedMonument(Monument monument, Context context) {
        TaggedMonumentDAO taggedMonumentDAO = new TaggedMonumentDAO(context);
        taggedMonumentDAO.open();
        if (taggedMonumentDAO.select(monument.getId()) == null) {
            taggedMonumentDAO.insert(monument);
        }
        taggedMonumentDAO.close();
    }

    public static void addMonumentToMonumentSearch(Monument monument, Context context) {
        MonumentSearchDAO monumentSearchDAO = new MonumentSearchDAO(context);
        monumentSearchDAO.open();
        if (monumentSearchDAO.select(monument.getId()) == null) {
            monumentSearchDAO.insert(monument);
        }
        monumentSearchDAO.close();
    }

    public static ArrayList<Monument> getAllMonumentSearch(Context context) {
        MonumentSearchDAO monumentSearchDAO = new MonumentSearchDAO(context);
        monumentSearchDAO.open();
        List<Monument> monumentList = monumentSearchDAO.getAllMonuments();
        monumentSearchDAO.close();

        ArrayList<Monument> monumentsArrayList = new ArrayList<>();
        for (Monument m : monumentList) monumentsArrayList.add(m);

        return monumentsArrayList;

    }

    public static void removeAllMonumentSearch(Context context) {
        MonumentSearchDAO monumentSearchDAO = new MonumentSearchDAO(context);
        monumentSearchDAO.open();
        List<Monument> monuments = monumentSearchDAO.getAllMonuments();

        for (Monument m : monuments) {
            monumentSearchDAO.delete(m);
        }

        monumentSearchDAO.close();
    }

    public static void removeMonumentFromTaggedMonument(Monument monument, Context context) {
        TaggedMonumentDAO taggedMonumentDAO = new TaggedMonumentDAO(context);
        taggedMonumentDAO.open();
        if (taggedMonumentDAO.select(monument.getId()) != null) {
            taggedMonumentDAO.delete(monument);
        }
        taggedMonumentDAO.close();
    }

    public static void removeMonumentFromFavouriteMonument(Monument monument, Context context) {
        FavouriteMonumentDAO favouriteMonumentDAO = new FavouriteMonumentDAO(context);
        favouriteMonumentDAO.open();
        if (favouriteMonumentDAO.select(monument.getId()) != null) {
            favouriteMonumentDAO.delete(monument);
        }
        favouriteMonumentDAO.close();
    }

    public static void editMonument(Monument monument, Context context) {
        Log.v("Shazam", "FunctionDB editMonument before if: " + monument.getLiked() + " monument id: " + monument.getId());
        Log.v("Shazam", "FunctionDB editMonument in if: " + monument.getLiked());
        MonumentDAO dao = new MonumentDAO(context);
        dao.open();
        dao.edit(monument);
        dao.close();

    }


}
