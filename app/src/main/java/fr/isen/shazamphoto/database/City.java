package fr.isen.shazamphoto.database;

import org.json.JSONException;
import org.json.JSONObject;

public class City {
    private String name;
    private Country country;

    public City(){
        this("name", new Country());
    }

    public City(String name, Country country) {
        this.name = name;
        this.country = country;
    }

    public String getName() {
        return name;
    }

    public Country getCountry() {
        return country;
    }

    public JSONObject toJson() {
        JSONObject jsonObject = new JSONObject();
        try{
            jsonObject.put("name", getName());
            jsonObject.put("country", getCountry().toJSon());

        }catch(JSONException e) {}

        return jsonObject;
    }

}
