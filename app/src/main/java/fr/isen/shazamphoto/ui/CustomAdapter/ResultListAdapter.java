package fr.isen.shazamphoto.ui.CustomAdapter;


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
import fr.isen.shazamphoto.utils.Images.LoadPicture;

public class ResultListAdapter extends BaseAdapter {

    private Activity activity;
    private LayoutInflater inflater;
    private List<Monument> monumentItems;

    public ResultListAdapter(Activity activity, List<Monument> monuments) {
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
            convertView = inflater.inflate(R.layout.list_row_result_search, null);
        }

        // Retrieve the several items of the layout
        ImageView image = (ImageView) convertView.findViewById(R.id.lrrs_imageView);
        TextView title = (TextView) convertView.findViewById(R.id.lrrs_title);
        TextView visitor = (TextView) convertView.findViewById(R.id.lrrs_nb_visitor);
        TextView like = (TextView) convertView.findViewById(R.id.lrrs_nb_like);

        // Getting movie data for the row
        Monument m = monumentItems.get(position);

        // Set the several about the monument
        title.setText(m.getName());
        visitor.setText(Integer.valueOf(m.getNbVisitors()).toString());
        like.setText(Integer.valueOf(m.getNbLike()).toString());

        LoadPicture.setPicture(m, LoadPicture.getImageProcessWidth(activity), LoadPicture.getImageProcessHeight(activity), image, activity);

        return convertView;
    }

}
