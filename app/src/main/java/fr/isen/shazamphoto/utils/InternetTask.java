package fr.isen.shazamphoto.utils;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.widget.Toast;


public abstract class InternetTask<Params, Progress, Result> extends AsyncTask<Params, Progress, Result> {

    private Activity activity;


    protected InternetTask(Activity activity) {
        this.activity = activity;
    }

    public Activity getActivity() {
        return activity;
    }

    @Override
    protected abstract Result doInBackground(Params... params);

    public boolean checkNetwork() {

        // Check the status of the network
        ConnectivityManager cm =
                (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public void updateUI(boolean isInternetFound) {
        if (!isInternetFound) {
            displayUINoNetwork();
        }
    }

    public void displayUINoNetwork() {
        if(getActivity() != null) Toast.makeText(getActivity(), "No internet found", Toast.LENGTH_LONG).show();
    }
}
