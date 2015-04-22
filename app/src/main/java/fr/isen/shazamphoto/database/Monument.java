package fr.isen.shazamphoto.database;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.opencv.core.Mat;
import org.opencv.features2d.KeyPoint;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;

public class Monument implements Serializable {
    private long id;
    private HashMap<String, Characteristic> characteristics; // Value of the langue is the key
    private String photoPath;
    private String photoPathLocal;
    private int year;
    private int nbVisitors;
    private int nbLike;
    private Localization localization;
    private Address address;
    private KeyPoint[] keyPoints;
    private Mat descriptors;
    private int idNearest;
    private int databaseId;
    private int liked;
    private boolean isFirstCircuit = false;
    private boolean isSelectedCircuit = false;
    private int distanceToDest = 0;

    public static final String NAME_SERIALIZABLE = "fr.isen.shazamphoto.database.monument_serializable";

    public Monument() {
        this(0, "", "", "", 0, 0, 0, null, new Address());
    }

    public Monument(KeyPoint[] keyPoints, Mat descriptors){
        this();
        this.descriptors = descriptors;
        this.keyPoints = keyPoints;
    }

    public Monument(long id, Localization localization, String name){
        this();
        this.id = id;
        this.idNearest = (int)id;
        this.localization = localization;
        this.setName(name);
    }

    public Monument(KeyPoint[] keyPoints, Mat descriptors, Localization localization, String photoPath){
        this(keyPoints, descriptors);
        this.localization = localization;
        this.photoPath = photoPath;
    }
    public Monument(long id, String name, String photoPath, String description, int year, int nbVisitors, int nbLike, Localization localization) {
        this(id, name, photoPath, description, year, nbVisitors, nbLike, localization, new Address());
    }

    public Monument(long id, int databaseId, String name, String photoPath, String description, int year, int nbVisitor, int nbLike, int liked, Address address) {
        this(id, databaseId, name, photoPath, description, year, nbVisitor, nbLike, liked, null, address, null, null);
    }

    public Monument(long id, int dataBaseId, String name, String photPath, String description, int year, int nbVisitor, int nbLike, int liked, Localization localization) {
        this(id, dataBaseId, name, photPath, description, year, nbVisitor, nbLike, liked, localization, null, null, null);
    }

    public Monument(long id, int databaseId, String name, String photoPath, String description, int year, int nbVisitors, int nbLike, Localization localization, Address address) {
        this(id, databaseId, name, photoPath, description, year, nbVisitors, nbLike, localization, address, null, null);
    }

    public Monument(long id, String name, String photoPath, String description, int year, int nbVisitors, int nbLike, Localization localization, Address address) {
        this(id, name, photoPath, description, year, nbVisitors, nbLike, localization, address, null, null);
    }

    public Monument(long id, String name, String photoPath, String description, int year, int nbVisitors, int nbLike, Localization localization, Address address, KeyPoint[] keyPoints, Mat descriptors) {
        this(id, 0, name, photoPath, description, year, nbVisitors, nbLike, localization, address, keyPoints, descriptors);
    }

    public Monument(long id, int databaseId, String name, String photoPath, String description, int year, int nbVisitors, int nbLike, Localization localization, Address address, KeyPoint[] keyPoints, Mat descriptors) {
        this(id, databaseId, name, photoPath, description, year, nbVisitors, nbLike, 0, localization, address, keyPoints, descriptors);
    }

    public Monument(long id, int databaseId, String name, String photoPath, String description, int year, int nbVisitors, int nbLike, int liked, Localization localization, Address address, KeyPoint[] keyPoints, Mat descriptors) {
       this(id, databaseId, name, photoPath, description, year, nbVisitors, nbLike, liked, localization, address, keyPoints, descriptors, null);
    }

    public Monument(long id, int dataBaseId, String name, String photoPath, String description, int year, int nbVisitors, int nbLike, int liked, Localization localization, String photoPathLocal) {
        this(id, dataBaseId, name, photoPath, description, year, nbVisitors, nbLike, liked, localization, null, null, null, photoPathLocal);
    }

    public Monument(long id, int databaseId, String name, String photoPath, String description, int year, int nbVisitors, int nbLike, int liked, Localization localization,
                    Address address, KeyPoint[] keyPoints, Mat descriptors, String photoPathLocal) {
        this.id = id;
        this.characteristics = new HashMap<>();
        Characteristic characteristic = new Characteristic(name, description, new Language(LanguageAvailable.DEFAULT_NAME, LanguageAvailable.DEFAULT_VALUE));
        this.characteristics.put(characteristic.getLanguage().getValue(), characteristic);
        this.photoPath = photoPath;
        this.year = year;
        this.nbVisitors = nbVisitors;
        this.nbLike = nbLike;
        this.localization = localization;
        this.address = address;
        this.keyPoints = keyPoints;
        this.descriptors = descriptors;
        this.liked = liked;
        this.databaseId = databaseId;
        this.photoPathLocal = photoPathLocal;
    }

    public Monument(JSONObject jsonMonument) {
        try {
            id = -1;
            year = jsonMonument.getInt("year");
            photoPath = jsonMonument.getString("photopath");
            nbVisitors = jsonMonument.getInt("nbvisitors");
            nbLike = jsonMonument.getInt("nblikes");
            localization = new Localization(jsonMonument.getJSONObject("localization"));
            databaseId = jsonMonument.getInt("id");

            //Get all the characteristic monument
            this.characteristics = new HashMap<>();
            JSONArray characteristicsJSON = jsonMonument.getJSONArray("characteristics");
            int nbCharacteristic = characteristicsJSON.length();
            for (int i = 0; i < nbCharacteristic; i++) {
                JSONObject characteristicJSON = characteristicsJSON.getJSONObject(i);
                Characteristic characteristic = new Characteristic(characteristicJSON);
                this.characteristics.put(characteristic.getLanguage().getValue(), characteristic);
            }

        } catch (Exception e) {
            Log.e("Shazam", "Exception in Monument Monument(JSON): " + e.getMessage());
        }

    }

    public JSONObject toJSON() {
        JSONObject jsonObj = new JSONObject();
        try {
            // Here we convert Java Object to JSON
            jsonObj.put("photopath", getPhotoPath());
            jsonObj.put("year", Integer.valueOf(getYear()).toString());
            jsonObj.put("nbvisitors", Integer.valueOf(getNbVisitors()).toString());
            jsonObj.put("nblikes", Integer.valueOf(getNbLike()).toString());

            JSONArray jsonArray = new JSONArray();
            Iterator<HashMap.Entry<String, Characteristic>> it = characteristics.entrySet().iterator();
            while (it.hasNext()) {
                HashMap.Entry<String, Characteristic> entry = it.next();
                jsonArray.put(entry.getValue().toJson());
            }
            jsonObj.put("characteristics", jsonArray);

            jsonObj.put("localization", localization.toJson());
            jsonObj.put("address", address.toJson());

            // Parse the JSON keypoints and the descriptors
            jsonObj.put(KeyPoints.KEY, KeyPoints.toJson(keyPoints));
            jsonObj.put(Descriptors.KEY, Descriptors.toJson(descriptors));

        } catch (JSONException ex) {
        }

        return jsonObj;
    }

    public HashMap<String, Characteristic> getCharacteristics() {
        return characteristics;
    }

    public Characteristic getCharacteristicByLanguage(String languageValue) {
        return this.characteristics.get(languageValue);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        if (characteristics == null || (characteristics != null &&
                characteristics.get(LanguageAvailable.DEFAULT_VALUE) == null)) {
            return "Null";
        } else {
            return characteristics.get(LanguageAvailable.DEFAULT_VALUE).getName();
        }
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

    public void setCharacteristics(HashMap<String, Characteristic> characteristics) {
        this.characteristics = characteristics;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public KeyPoint[] getKeyPoints() {
        return keyPoints;
    }

    public void setKeyPoints(KeyPoint[] keyPoints) {
        this.keyPoints = keyPoints;
    }

    public Mat getDescriptors() {
        return descriptors;
    }

    public void setDescriptors(Mat descriptors) {
        this.descriptors = descriptors;
    }

    @Override
    public String toString(){
        return getName() +" "+Long.valueOf(getId()).toString() +" id nearest: " +getIdNearest();
    }

    public int getIdNearest() {
        return idNearest;
    }

    public void setIdNearest(int idNearest) {
        this.idNearest = idNearest;
    }

    public int getDatabaseId() {
        return databaseId;
    }

    public void setDatabaseId(int databaseId) {
        this.databaseId = databaseId;
    }

    public int getLiked() {
        return liked;
    }

    public void setLiked(int liked) {
        this.liked = liked;
    }

    public String getPhotoPathLocal() {
        return photoPathLocal;
    }

    public void setPhotoPathLocal(String photoPathLocal) {
        this.photoPathLocal = photoPathLocal;
    }

    public boolean isFirstCircuit() {
        return isFirstCircuit;
    }

    public void setFirstCircuit(boolean isFirstCircuit) {
        this.isFirstCircuit = isFirstCircuit;
    }

    public boolean isSelectedCircuit() {
        return isSelectedCircuit;
    }

    public void setSelectedCircuit(boolean isSelectedCircuit) {
        this.isSelectedCircuit = isSelectedCircuit;
    }

    public int getDistanceToDest() {
        return distanceToDest;
    }

    public void setDistanceToDest(int distanceToDest) {
        this.distanceToDest = distanceToDest;
    }
}

