package fr.isen.shazamphoto.utils;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import fr.isen.shazamphoto.R;
import fr.isen.shazamphoto.ui.NetworkInfoArea;


public abstract class InternetTask<Params, Progress, Result> extends AsyncTask<Params, Progress, Result> {

    private NetworkInfoArea networkInfo;
    private Activity activity;

    private Handler timerHandler = new Handler();
    private Runnable timerRunnable;
    private long startTime = 0;
    private boolean sendHideAnimation;

    protected InternetTask(NetworkInfoArea networkInfo, Activity activity) {
        this.networkInfo = networkInfo;
        this.activity = activity;
        this.sendHideAnimation = false;
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
        boolean result = netInfo != null && netInfo.isConnectedOrConnecting();

        return result;
    }

    public void updateUI(boolean isInternetFound) {
        if (isInternetFound == false) {
            displayUINoNetwork();
        }
    }

    public void displayUINoNetwork() {
        // Set the animation to display the network info
        Animation animation = AnimationUtils.loadAnimation(activity, R.anim.abc_fade_in);
        animation.setDuration(2000);
        networkInfo.setVisibility(View.VISIBLE);
        networkInfo.startAnimation(animation);

        // Reset the boolean
        sendHideAnimation = false;

        // Set the timer start eh animation after 5 seconds
        timerRunnable = new Runnable() {

            @Override
            public void run() {
                long millis = System.currentTimeMillis() - startTime;
                int seconds = (int) (millis / 1000);
                seconds = seconds % 60;
                if (seconds >= 5 && sendHideAnimation == false) {
                    sendHideAnimation = true;
                    // Remove the timer
                    timerHandler.removeCallbacks(timerRunnable);
                    // hide the info
                    networkInfo.setVisibility(View.GONE);

                }
                timerHandler.postDelayed(this, 500);
            }
        };

        // Start the timer
        startTime = System.currentTimeMillis();
        timerHandler.postDelayed(timerRunnable, 0);
    }
}
