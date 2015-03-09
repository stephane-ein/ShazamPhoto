package fr.isen.shazamphoto.ui;

import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;

import fr.isen.shazamphoto.R;
import fr.isen.shazamphoto.events.RequestNearestMonumentsAndMap;
import fr.isen.shazamphoto.utils.GetMonumentByLocalization;


public class ActionPlacePopMenu extends PopupMenu implements PopupMenu.OnMenuItemClickListener{

    private View menuItemView;
    private Home home;
    private LocateManager locateManager;

    public ActionPlacePopMenu(Context context, View anchor, LocationManager locationManager) {
        super(context, anchor);
        this.menuItemView = anchor;
        this.home = context instanceof Home ? (Home) context : null;
        setOnMenuItemClickListener(this);
        this.locateManager = new LocateManager(locationManager);
        this.locateManager.home = home;
    }

    public void showPopup() {
        inflate(R.menu.menu_action_place);
        show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {

        switch (item.getItemId()) {

            // Display the nearest monument on the google map
            case R.id.display_monuments:
                home.getmViewPager().setCurrentItem(NearestMonumentsFragment.POSITION);
                NearestMonumentsFragment nearestMonumentsFragment = (NearestMonumentsFragment)
                        home.getSectionsPagerAdapter().getItem(NearestMonumentsFragment.POSITION);
                this.locateManager.startListening(new RequestNearestMonumentsAndMap(home, nearestMonumentsFragment));
                return true;

            // Drawn the shortest path between n monuments
            case R.id.route_nearest_monuments:
                home.getmViewPager().setCurrentItem(NearestMonumentsFragment.POSITION);
                home.getSectionsPagerAdapter().getItem(NearestMonumentsFragment.POSITION);
                return true;

            default:
                return false;
        }
    }
}
