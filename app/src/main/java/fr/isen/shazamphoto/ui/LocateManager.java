package fr.isen.shazamphoto.ui;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Toast;

import fr.isen.shazamphoto.database.Localization;
import fr.isen.shazamphoto.events.EventLocalizationFound;
import fr.isen.shazamphoto.events.RequestLocalization;
import fr.isen.shazamphoto.ui.ItemUtils.SearchLocalizationItem;

public class LocateManager {

    private LocationManager lm;
    private LocationListener locationListener;
    private SearchLocalizationItem searchLocalizationItem;
    public Home home;

    public LocateManager(LocationManager lm, SearchLocalizationItem searchLocalizationItem) {
        this.searchLocalizationItem = searchLocalizationItem;
        this.lm = lm;
    }

    public void startListening(final RequestLocalization requestLocalization) {

        locationListener = new LocationListener() {

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
        lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1, 1, locationListener);
    }

    public void stopListening() {
        if (locationListener != null) {
            lm.removeUpdates(locationListener);
        }
    }
}
