package fr.isen.shazamphoto.database;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class Country implements Serializable{
    private String name;

    public Country(){
        this("Name");
    }

    public Country(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public JSONObject toJSon(){
        JSONObject jsonObject = new JSONObject();
        try{
            jsonObject.put("name", getName());
        }catch(JSONException e) {}
        return jsonObject;
    }
}
