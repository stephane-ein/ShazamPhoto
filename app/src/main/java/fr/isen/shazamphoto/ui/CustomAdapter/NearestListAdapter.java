package fr.isen.shazamphoto.ui.CustomAdapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import fr.isen.shazamphoto.R;
import fr.isen.shazamphoto.database.Localization;
import fr.isen.shazamphoto.database.Monument;
import fr.isen.shazamphoto.utils.Images.LoadPicture;

public class NearestListAdapter extends BaseAdapter {

    private Activity activity;
    private LayoutInflater inflater;
    private List<Monument> monumentItems;
    private Localization localizationUser;

    public NearestListAdapter(Activity activity, List<Monument> monuments, Localization localizationUser) {
        this.activity = activity;
        this.monumentItems = monuments;
        this.localizationUser = localizationUser;
    }

    @Override
    public int getCount() {
        return monumentItems.size();
    }

    @Override
    public Object getItem(int location) {
        return monumentItems.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_row_nearest_monument, null);
        }

        // Retrieve the several items of the layout
        ImageView image = (ImageView) convertView.findViewById(R.id.lrnm_imageView);
        TextView title = (TextView) convertView.findViewById(R.id.lrnm_title);
        TextView distance = (TextView) convertView.findViewById(R.id.lrnm_distance);
        TextView visitor = (TextView) convertView.findViewById(R.id.lrnm_visitors);
        ImageView map = (ImageView) convertView.findViewById(R.id.lrnm_map);


        // Getting movie data for the row
        Monument m = monumentItems.get(position);

        // Set the title with the name of the monument
        title.setText(m.getName());

        if(m.getIdNearest()==-2){
            // Case where the monument is just the final point in the circuit
            image.setImageBitmap(BitmapFactory.decodeResource(activity.getResources(),
                    R.drawable.finish));
        }else{
            // Case where the monument is not at the end of the circuit
            // Set the image of the monument
            if (m.getPhotoPath() != null && !m.getName().isEmpty()) {
                LoadPicture.setPicture(m, LoadPicture.getImageProcessWidth(activity), LoadPicture.getImageProcessHeight(activity), image, activity);
            }else{
                // Default image
                image.setImageBitmap(BitmapFactory.decodeResource(activity.getResources(),
                        R.drawable.image_not_found));
            }

            // Set the button to display the navigation
            map.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Localization l = monumentItems.get(position).getLocalization();
                    Uri gmmIntentUri = Uri.parse("google.navigation:q=" + l.getLatitude() + "," + l.getLongitude() + "&mode=w");
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                    mapIntent.setPackage("com.google.android.apps.maps");
                    activity.startActivity(mapIntent);
                }
            });

            // Set the number of visitor and the distance between the monument and the user
            visitor.setText(Integer.valueOf(m.getNbVisitors()).toString() + " tags");
           /* Localization destionLocalization = m.getLocalization();
            float[] result = new float[3];
            if(destionLocalization != null){
                Location.distanceBetween(
                        destionLocalization.getLatitude(), destionLocalization.getLongitude(),
                        localizationUser.getLatitude(), localizationUser.getLongitude(),
                        result);
                distance.setText("to "+Integer.valueOf((int)result[0]).toString()+" m");
            }*/
            distance.setText("to "+m.getDistanceToDest()+" m");

            LinearLayout linearLayout = (LinearLayout) convertView.findViewById(R.id.lrnm_linearlayout);
            if(m.isFirstCircuit()){
                linearLayout.setBackgroundResource(R.drawable.list_row_nearest_monument_start);
            }else if(m.isSelectedCircuit()) {
                linearLayout.setBackgroundResource(R.drawable.list_row_nearestmonument_selected);
            }else{
                linearLayout.setBackgroundResource(R.drawable.list_row_bg);
            }
            notifyDataSetChanged();
        }

        return convertView;
    }

}

