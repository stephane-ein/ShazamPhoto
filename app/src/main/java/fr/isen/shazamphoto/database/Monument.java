package fr.isen.shazamphoto.database;

public class Monument {
    int id;
    String photoPath;
    String description;
    int year;
    int nbVisitors;
    int nbVisited;
    Localization localization;

    public Monument() {
        this(null, 0, "", "", 0, 0, 0);
    }

    public Monument(Localization localization, int id, String photoPath, String description, int year, int nbVisitors, int nbVisited) {
        this.localization = localization;
        this.id = id;
        this.photoPath = photoPath;
        this.description = description;
        this.year = year;
        this.nbVisitors = nbVisitors;
        this.nbVisited = nbVisited;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPhotoPath() {
        return photoPath;
    }

    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getNbVisitors() {
        return nbVisitors;
    }

    public void setNbVisitors(int nbVisitors) {
        this.nbVisitors = nbVisitors;
    }

    public int getNbVisited() {
        return nbVisited;
    }

    public void setNbVisited(int nbVisited) {
        this.nbVisited = nbVisited;
    }

    public Localization getLocalization() {
        return localization;
    }

    public void setLocalization(Localization localization) {
        this.localization = localization;
    }
}
