package database;

import android.graphics.Bitmap;

import org.json.JSONObject;

import java.io.Serializable;
import java.sql.Date;

public class Monument implements Serializable {

    private static final long serialVersionUID = 1L;
    // Notez que l'identifiant est un long
    private long id;
    private String name;
    private String year;
    private String description;
    private Bitmap image;

    public Monument(long id, String name) {
        this.id = id;
        this.name = name;
        this.image = null;
    }

    public Monument(long id, String name, String year, Date releaseDate, String runtime, String director, String story, String imageUrl, String imdbRating, String imdbVotes, String actors) {
        this.id = id;
        this.name = name;
        this.year = year;
        this.description = story;
        this.image = null;
    }

    public Monument(JSONObject jsonFilm) {
        try {
            id = -1;
            name = jsonFilm.getString("Title");
            year = jsonFilm.getString("Year");
            description = jsonFilm.getString("Plot");
        }
        catch(Exception e) {}
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }
}
