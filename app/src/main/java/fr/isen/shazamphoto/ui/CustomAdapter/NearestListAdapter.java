package fr.isen.shazamphoto.ui.CustomAdapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import fr.isen.shazamphoto.R;
import fr.isen.shazamphoto.database.Localization;
import fr.isen.shazamphoto.database.Monument;
import fr.isen.shazamphoto.utils.GetMonumentImage;

public class NearestListAdapter extends BaseAdapter {

    private Activity activity;
    private LayoutInflater inflater;
    private List<Monument> monumentItems;

    public NearestListAdapter(Activity activity, List<Monument> monuments) {
        this.activity = activity;
        this.monumentItems = monuments;
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


        ImageView image = (ImageView) convertView.findViewById(R.id.lrnm_imageView);
        TextView title = (TextView) convertView.findViewById(R.id.lrnm_title);
        TextView distance = (TextView) convertView.findViewById(R.id.lrnm_distance);
        TextView visitor = (TextView) convertView.findViewById(R.id.lrnm_visitors);
        ImageView map = (ImageView) convertView.findViewById(R.id.lrnm_map);


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

        // getting movie data for the row
        Monument m = monumentItems.get(position);

        title.setText(m.getName());
        visitor.setText(Integer.valueOf(m.getNbVisitors()).toString() + " visitors");

        if (m.getPhotoPath() != null && !m.getName().isEmpty()) {
            GetMonumentImage getMonumentImage = new GetMonumentImage(image);
            getMonumentImage.execute(m.getPhotoPath());
        }


        return convertView;
    }

}

