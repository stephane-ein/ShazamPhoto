package fr.isen.shazamphoto.events;

import fr.isen.shazamphoto.ui.Home;

public class RequestIdentifyByLocalization extends RequestLocalization{

    private Home home;
    private String imagePath;

    public RequestIdentifyByLocalization(Home home, String imagePath) {
        this.home = home;
        this.imagePath = imagePath;
    }

    public Home getHome() {
        return home;
    }

    public String getImagePath() {
        return imagePath;
    }
}
