package fr.isen.shazamphoto.database;

import android.widget.TextView;

import org.json.JSONObject;

import java.io.Serializable;

public class Characteristic implements Serializable{
    private String name;
    private String description;
    private Language language;

    public Characteristic(String name, String description, Language language) {
        this.name = name;
        this.description = description;
        this.language = language;
    }

    public Characteristic(JSONObject jsonCharacteristics, TextView textView) {
        try {
            name = jsonCharacteristics.getString("name");
            description = jsonCharacteristics.getString("description");
            language = new Language(jsonCharacteristics.getJSONObject("language"));
        }
        catch(Exception e) {
            textView.setText(e.getMessage());
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Language getLanguage() {
        return language;
    }

    public void setLunguage(Language lunguage) {
        this.language = lunguage;
    }
}
