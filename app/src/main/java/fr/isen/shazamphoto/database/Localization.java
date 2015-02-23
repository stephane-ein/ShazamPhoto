package fr.isen.shazamphoto.database;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class Localization implements Serializable{
    long id;
    float latitude;
    float longitude;

    public Localization() {
        this(0, 0, 0);
    }

    public Localization(long id, float latitude, float longitude) {
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

    public JSONObject toJson(){
        JSONObject jsonObj = new JSONObject();
        try{
            jsonObj.put("latitude", Float.valueOf(latitude).toString());
            jsonObj.put("longitude", Float.valueOf(longitude).toString());
        }
        catch (JSONException e){}

       return jsonObj;
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

    public String toString(){
        return "Lat: "+Float.valueOf(getLatitude()).toString() +" Lon: "+Float.valueOf(getLongitude()).toString();
    }
}
