package fr.isen.shazamphoto.database;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.HashMap;

public class Monument implements Serializable {
    private long id;
    private HashMap<String, Characteristic> characteristics; // Value of the langue is the key
    private String photoPath;
    private int year;
    private int nbVisitors;
    private int nbLike;
    private Localization localization;

    public static final String NAME_SERIALIZABLE = "fr.isen.shazamphoto.database.monument_serializable";

    public Monument() {
        this(0, "", "", "", 0, 0, 0, null);
    }

    public Monument(long id, String name, String photoPath, String description, int year, int nbVisitors, int nbLike, Localization localization) {
        this.id = id;
        this.characteristics = new HashMap<>();
        Characteristic characteristic = new Characteristic(name, description, new Language(LanguageAvailable.DEFAULT_NAME, LanguageAvailable.DEFAULT_VALUE));
        this.characteristics.put(characteristic.getLanguage().getValue(), characteristic);
        this.photoPath = photoPath;
        this.year = year;
        this.nbVisitors = nbVisitors;
        this.nbLike = nbLike;
        this.localization = localization;
    }
    public Monument(JSONObject jsonMonument) {
        try {
            id = -1;
            year = Integer.valueOf(jsonMonument.getString("year")).intValue();
            photoPath = jsonMonument.getString("photoPath");
            nbVisitors =  Integer.valueOf(jsonMonument.getInt("nbVisitors")).intValue();
            nbLike =  Integer.valueOf(jsonMonument.getInt("nbLikes")).intValue();
            localization = new Localization(jsonMonument.getJSONObject("localization"));

            //Get all the characteristic monument
            this.characteristics = new HashMap<>();
            JSONArray characteristicsJSON = jsonMonument.getJSONArray("characteristics");
            int nbCharacteristic = characteristicsJSON.length();
            for(int i = 0 ; i<nbCharacteristic; i++){
                JSONObject characteristicJSON = characteristicsJSON.getJSONObject(i);
                Characteristic characteristic = new Characteristic(characteristicJSON);
                this.characteristics.put(characteristic.getLanguage().getValue(), characteristic);
            }

        } catch (Exception e) {
        }

    }

    public HashMap<String, Characteristic> getCharacteristics() {
        return characteristics;
    }
    public Characteristic getCharacteristicByLanguage(String languageValue){
        return this.characteristics.get(languageValue);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return characteristics.get(LanguageAvailable.DEFAULT_VALUE).getName();
    }

    public void setName(String name) {
        this.characteristics.get(LanguageAvailable.DEFAULT_VALUE).setName(name);
    }

    public String getPhotoPath() {
        return photoPath;
    }

    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
    }

    public String getDescription() {
        return characteristics.get(LanguageAvailable.DEFAULT_VALUE).getDescription();
    }

    public void setDescription(String description) {
        this.characteristics.get(LanguageAvailable.DEFAULT_VALUE).setDescription(description);
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getNbVisitors() {
        return nbVisitors;
    }

    public void setNbVisitors(int nbVisitors) {
        this.nbVisitors = nbVisitors;
    }

    public int getNbLike() {
        return nbLike;
    }

    public void setNbLike(int nbLike) {
        this.nbLike = nbLike;
    }

    public Localization getLocalization() {
        return localization;
    }

    public void setLocalization(Localization localization) {
        this.localization = localization;
    }
}
