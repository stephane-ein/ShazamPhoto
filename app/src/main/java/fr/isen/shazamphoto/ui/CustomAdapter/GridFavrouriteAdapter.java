package fr.isen.shazamphoto.ui.CustomAdapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import fr.isen.shazamphoto.R;
import fr.isen.shazamphoto.database.Monument;
import fr.isen.shazamphoto.utils.Images.LoadPicture;

public class GridFavrouriteAdapter extends BaseAdapter {
    private final List<Monument> mItems;
    private final LayoutInflater mInflater;
    private Activity activity;

    public GridFavrouriteAdapter(Context context, ArrayList<Monument> monuments, Activity activity) {
        mInflater = LayoutInflater.from(context);
        this.mItems = monuments;
        this.activity = activity;
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public Monument getItem(int i) {
        return mItems.get(i);
    }

    @Override
    public long getItemId(int i) {
        return mItems.get(i).getId();
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View v = view;
        ImageView picture;
        TextView name;

        if (v == null) {
            v = mInflater.inflate(R.layout.grid_item, viewGroup, false);
            v.setTag(R.id.picture, v.findViewById(R.id.picture));
            v.setTag(R.id.text, v.findViewById(R.id.text));
        }

        picture = (ImageView) v.getTag(R.id.picture);
        name = (TextView) v.getTag(R.id.text);

        Monument m = getItem(i);

        /*
        if (m.getPhotoPath() != null && !m.getName().isEmpty()) {
            GetImageURLTask getImageURLTask = new GetImageURLTask(picture, LoadPicture.HDPI_WIDTH_VERTICAL, LoadPicture.HDPI_HEIGHT_VERTICAL);
            getImageURLTask.execute(m.getPhotoPath());
        }else{
            picture.setImageResource(R.drawable.image_not_found);
        }*/
        LoadPicture.setPicture(m, LoadPicture.getImageProcessWidth(activity), LoadPicture.getImageProcessHeight(activity), picture, activity);
        name.setText(m.getName());

        return v;
    }
}