package fr.isen.shazamphoto.database;

public class Localization {
    long id;
    int latitude;
    int longitude;

    public Localization() {
        this(0, 0, 0);
    }

    public Localization(long id, int latitude, int longitude) {
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getLatitude() {
        return latitude;
    }

    public void setLatitude(int latitude) {
        this.latitude = latitude;
    }

    public int getLongitude() {
        return longitude;
    }

    public void setLongitude(int longitude) {
        this.longitude = longitude;
    }
}
