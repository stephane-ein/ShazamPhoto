package fr.isen.shazamphoto.database;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class Language implements Serializable{
    private String name;
    private String value;

    public Language(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public Language(JSONObject jsonObject){
        try{
            this.name = jsonObject.getString("name");
            this.value = jsonObject.getString("value");
        }catch(Exception e){
        }
    }

    public JSONObject toJSon(){
        JSONObject jsonObj = new JSONObject();
        try{
            jsonObj.put("name", getName());
            jsonObj.put("value", getValue());
        }catch(JSONException e){}

        return jsonObj;
    }
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
