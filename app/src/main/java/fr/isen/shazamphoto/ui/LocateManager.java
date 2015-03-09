package fr.isen.shazamphoto.ui;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Toast;

import fr.isen.shazamphoto.events.RequestLocalization;
import fr.isen.shazamphoto.utils.GetMonumentByLocalization;

public class LocateManager {

    private LocationManager lm;
    private LocationListener locationListener;
    public Home home;

    public LocateManager(LocationManager lm) {
        this.lm = lm;
    }

    public void startListening(final RequestLocalization requestLocalization){

        if(home != null) Toast.makeText(home, "Called Listener", Toast.LENGTH_SHORT).show();

        locationListener = new LocationListener() {

            public void onLocationChanged(Location location) {
                if(home != null) Toast.makeText(home, "localisation found", Toast.LENGTH_SHORT).show();
                GetMonumentByLocalization getMonumentByLocalization =
                        new GetMonumentByLocalization(requestLocalization);
                getMonumentByLocalization.execute(Double.valueOf(location.getLatitude()).toString(),
                        Double.valueOf(location.getLongitude()).toString(), "0.09");
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) { }
            @Override
            public void onProviderEnabled(String provider) {}
            @Override
            public void onProviderDisabled(String provider) {}
        };
        lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1, 1, locationListener);
    }

    public void stopListening() {
        if (locationListener != null) {
            if(home != null) Toast.makeText(home, "Stop Listener", Toast.LENGTH_SHORT).show();
            lm.removeUpdates(locationListener);
        }
    }
}
