package fr.isen.shazamphoto.ui;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import fr.isen.shazamphoto.R;
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
    public View getView(int position, View convertView, ViewGroup parent) {

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

        // getting movie data for the row
        Monument m = monumentItems.get(position);

        title.setText(m.getName());
        visitor.setText(Integer.valueOf(m.getNbVisitors()).toString() +" visitors");

        if (m.getPhotoPath() != null && !m.getName().isEmpty()) {
            GetMonumentImage getMonumentImage = new GetMonumentImage(image);
            getMonumentImage.execute(m.getPhotoPath());
        }

        return convertView;
    }

}

