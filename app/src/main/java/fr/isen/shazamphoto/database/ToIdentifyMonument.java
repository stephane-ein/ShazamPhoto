package fr.isen.shazamphoto.database;

/**
 * Created by .Sylvain on 22/01/2015.
 */
public class ToIdentifyMonument {
    long id;
    String photoPath;
    Localization localization;

    public ToIdentifyMonument() {
        this(0, "", null);
    }

    public ToIdentifyMonument(long id, String photoPath, Localization localization) {
        this.id = id;
        this.photoPath = photoPath;
        this.localization = localization;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getPhotoPath() {
        return photoPath;
    }

    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
    }

    public Localization getLocalization() {
        return localization;
    }

    public void setLocalization(Localization localization) {
        this.localization = localization;
    }
}
