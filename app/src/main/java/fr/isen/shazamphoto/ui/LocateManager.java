package fr.isen.shazamphoto.ui;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import fr.isen.shazamphoto.database.Localization;
import fr.isen.shazamphoto.events.EventLocalizationFound;
import fr.isen.shazamphoto.ui.ItemUtils.SearchLocalizationItem;

public class LocateManager {

    private LocationManager lm;
    private LocationListener locationListenerNetWork;
    private LocationListener locationListenerGPS;
    private SearchLocalizationItem searchLocalizationItem;
    public Home home;

    public LocateManager(LocationManager lm, SearchLocalizationItem searchLocalizationItem) {
        this.searchLocalizationItem = searchLocalizationItem;
        this.lm = lm;
    }

    public void startListening() {

        locationListenerNetWork = new LocationListener() {

            public void onLocationChanged(Location location) {
                Localization localization = new Localization(-1, location.getLatitude(),
                        location.getLongitude());
                searchLocalizationItem.foundLocalization(new EventLocalizationFound(localization));
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            @Override
            public void onProviderEnabled(String provider) {
            }

            @Override
            public void onProviderDisabled(String provider) {
            }
        };
        lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListenerNetWork);

        locationListenerGPS = new LocationListener() {

            public void onLocationChanged(Location location) {
                Localization localization = new Localization(-1, location.getLatitude(),
                        location.getLongitude());
                searchLocalizationItem.foundLocalization(new EventLocalizationFound(localization));
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            @Override
            public void onProviderEnabled(String provider) {
            }

            @Override
            public void onProviderDisabled(String provider) {
            }
        };
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListenerGPS);
    }

    public void stopListening() {
        if (locationListenerNetWork != null) {
            lm.removeUpdates(locationListenerNetWork);
            Log.v("Shazam", "LM ListenerNetwork removed");
        }
        if (locationListenerGPS != null) {
            lm.removeUpdates(locationListenerGPS);
            Log.v("Shazam", "LM ListenerGPS removed");
        }
    }
}
