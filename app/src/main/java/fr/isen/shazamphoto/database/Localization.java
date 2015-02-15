package fr.isen.shazamphoto.database;

import org.json.JSONObject;

import java.io.Serializable;

public class Localization implements Serializable{
    long id;
    float latitude;
    float longitude;

    public Localization() {
        this(0, 0, 0);
    }

    public Localization(long id, int latitude, int longitude) {
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Localization(JSONObject jsonObject){
        try{
            this.id = -1;
            this.latitude = Float.valueOf(jsonObject.getString("latitude")).floatValue();
            this.longitude =  Float.valueOf(jsonObject.getString("longitude")).floatValue();
        }catch(Exception e){
        }
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public float getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }
}
