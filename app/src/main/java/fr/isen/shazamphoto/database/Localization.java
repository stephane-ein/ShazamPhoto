package fr.isen.shazamphoto.database;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class Localization implements Serializable{
    private long id;
    private double latitude;
    private double longitude;

    public Localization() {
        this(0, 0.0, 0.0);
    }

    public Localization(long id, Double latitude, Double longitude) {
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Localization(JSONObject jsonObject){
        try{
            this.id = -1;
            this.latitude = Double.valueOf(jsonObject.getString("latitude")).floatValue();
            this.longitude =  Double.valueOf(jsonObject.getString("longitude")).floatValue();
        }catch(Exception e){
        }
    }

    public JSONObject toJson(){
        JSONObject jsonObj = new JSONObject();
        try{
            jsonObj.put("latitude", Double.valueOf(latitude).toString());
            jsonObj.put("longitude", Double.valueOf(longitude).toString());
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

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String toString(){
        return "Lat: "+Double.valueOf(getLatitude()).toString() +" Lon: "+Double.valueOf(getLongitude()).toString();
    }
}
