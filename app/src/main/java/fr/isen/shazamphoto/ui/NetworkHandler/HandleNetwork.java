package fr.isen.shazamphoto.ui.NetworkHandler;


import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import fr.isen.shazamphoto.R;

public class HandleNetwork {


    private static Handler timerHandler = new Handler();
    private static Runnable timerRunnable;
    private static long startTime = 0;
    private static boolean hiden = false;
    private static boolean hiden2 = false;

    public static boolean checkNetwork(TextView textView, Activity activity) {

        ConnectivityManager cm =
                (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        boolean result = netInfo != null && netInfo.isConnectedOrConnecting();

        if(result==false){
            displayUINoNetwork(textView, activity);
        }else{
           hideUINiNetwork(textView, activity);
        }

        return result;

    }

    public static void displayUINoNetwork(final TextView textView, final Activity activity){
        //Display the text view that indicate that internet is not available
        Animation animation = AnimationUtils.loadAnimation(activity, R.anim.abc_fade_in);
        animation.setDuration(2000);
        textView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 2));
        textView.startAnimation(animation);
        textView.setVisibility(View.VISIBLE);
        hiden = false;

        // Set the timer start eh animation after 5 seconds
        timerRunnable = new Runnable() {

            @Override
            public void run() {
                long millis = System.currentTimeMillis() - startTime;
                int seconds = (int) (millis / 1000);
                seconds = seconds % 60;
                if (seconds >= 5 && hiden == false) {
                    hiden = true;
                    // Remove the timer
                    timerHandler.removeCallbacks(timerRunnable);
                    // Start the animation
                    HandleNetwork.hideUINiNetwork(textView, activity);

                }
                timerHandler.postDelayed(this, 500);
            }
        };

        // Start the timer
        startTime = System.currentTimeMillis();
        timerHandler.postDelayed(timerRunnable, 0);
    }

    public static void hideUINiNetwork(final TextView textView, Activity activity){
        Animation animation = AnimationUtils.loadAnimation(activity, R.anim.abc_fade_out);
        animation.setDuration(2000);
        textView.startAnimation(animation);
        hiden2 = false;

        // Set the timer to hide the text view after 2 seconds
        timerRunnable = new Runnable() {

            @Override
            public void run() {
                long millis = System.currentTimeMillis() - startTime;
                int seconds = (int) (millis / 1000);
                seconds = seconds % 60;
                if (seconds >= 2 && hiden2 == false) {
                    hiden2 = true;
                    // Remove the timer
                    timerHandler.removeCallbacks(timerRunnable);
                    // Hide the textview
                    textView.setVisibility(View.INVISIBLE);
                    textView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 0));
                }
                timerHandler.postDelayed(this, 500);
            }
        };

        // Start the timer
        startTime = System.currentTimeMillis();
        timerHandler.postDelayed(timerRunnable, 0);

    }
}
