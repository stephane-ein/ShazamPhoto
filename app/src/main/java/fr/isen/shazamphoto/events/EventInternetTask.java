package fr.isen.shazamphoto.events;

import org.json.JSONObject;

public class EventInternetTask extends Event{

    private boolean internetfound;
    private JSONObject jsonResponse;

    public EventInternetTask(boolean internetfound, JSONObject jsonResponse) {
        this.internetfound = internetfound;
        this.jsonResponse = jsonResponse;
    }

    public boolean isInternetfound() {
        return internetfound;
    }

    public JSONObject getJsonResponse() {
        return jsonResponse;
    }
}
