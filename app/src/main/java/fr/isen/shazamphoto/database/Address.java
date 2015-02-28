package fr.isen.shazamphoto.database;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class Address implements Serializable{
    private int id;
    private String number;
    private String street;
    private City city;

    public Address(){
        this(-1, "42", "Street", new City());
    }

    public Address(int id, String number, String street, City city) {
        this.id = id;
        this.number = number;
        this.street = street;
        this.city = city;
    }

    public int getId() {
        return id;
    }

    public String getNumber() {
        return number;
    }

    public String getStreet() {
        return street;
    }

    public City getCity() {
        return city;
    }

    public JSONObject toJson(){
        JSONObject jsonObject = new JSONObject();

        try{
            jsonObject.put("id", Integer.valueOf(getId()).toString());
            jsonObject.put("number", getNumber());
            jsonObject.put("street", getStreet());
            jsonObject.put("city", getCity().toJson());
        }catch(JSONException e) {}

        return jsonObject;
    }
}
