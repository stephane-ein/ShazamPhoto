package fr.isen.shazamphoto.database;

public class Monument {
    long id;
    String name;
    String photoPath;
    String description;
    int year;
    int nbVisitors;
    int nbVisited;
    Localization localization;

    public Monument() {
        this(0, "", "", "", 0, 0, 0, null);
    }

    public Monument(long id, String name, String photoPath, String description, int year, int nbVisitors, int nbVisited, Localization localization) {
        this.id = id;
        this.name = name;
        this.photoPath = photoPath;
        this.description = description;
        this.year = year;
        this.nbVisitors = nbVisitors;
        this.nbVisited = nbVisited;
        this.localization = localization;
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
