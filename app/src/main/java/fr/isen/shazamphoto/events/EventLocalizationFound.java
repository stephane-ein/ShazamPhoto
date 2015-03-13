package fr.isen.shazamphoto.events;

import fr.isen.shazamphoto.database.Localization;

public class EventLocalizationFound extends Event{

    private Localization localization;

    public Localization getLocalization() {
        return localization;
    }

    public EventLocalizationFound(Localization localization) {
        this.localization = localization;
    }
}
