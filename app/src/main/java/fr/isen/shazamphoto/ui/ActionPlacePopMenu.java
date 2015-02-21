package fr.isen.shazamphoto.ui;

import android.content.Context;
import android.content.Intent;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.Toast;


import fr.isen.shazamphoto.R;


public class ActionPlacePopMenu extends PopupMenu implements PopupMenu.OnMenuItemClickListener{

    private View menuItemView;
    private Home home;

    public ActionPlacePopMenu(Context context, View anchor) {
        super(context, anchor);
        this.menuItemView = anchor;
        this.home = context instanceof Home ? (Home) context : null;
        setOnMenuItemClickListener(this);
    }

    public void showPopup() {
        inflate(R.menu.menu_action_place);
        show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.display_monuments:
                home.getmViewPager().setCurrentItem(NearestMonumentsFragment.POSITION);
                home.getSectionsPagerAdapter().getItem(NearestMonumentsFragment.POSITION);
                NearestMonumentsFragment nearestMonumentsFragment = (NearestMonumentsFragment)
                        home.getSectionsPagerAdapter().getItem(NearestMonumentsFragment.POSITION);
                nearestMonumentsFragment.getButton().performClick();
                Intent intent = new Intent(home, NearestMonuments.class);
                home.startActivity(intent);
                return true;
            case R.id.route_nearest_monuments:
                home.getmViewPager().setCurrentItem(NearestMonumentsFragment.POSITION);
                home.getSectionsPagerAdapter().getItem(NearestMonumentsFragment.POSITION);
                return true;
            default:
                return false;
        }
    }
}
