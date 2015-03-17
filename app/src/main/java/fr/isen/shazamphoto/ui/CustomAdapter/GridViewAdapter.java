package fr.isen.shazamphoto.ui.CustomAdapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import fr.isen.shazamphoto.R;
import fr.isen.shazamphoto.database.Monument;

public class GridViewAdapter extends ArrayAdapter {

    private Context context;
    private ArrayList<Monument> monuments;

    public GridViewAdapter(Context context, ArrayList<Monument> monuments) {
        super(context, 0);
        this.context = context;
        this.monuments = monuments;
    }

    public int getCount() {
        return monuments.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;

        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(R.layout.grid_row, parent, false);

            TextView textViewTitle = (TextView) row.findViewById(R.id.textView_gridView);
            ImageView imageViewIte = (ImageView) row.findViewById(R.id.imageView_gridView);
            if (position < monuments.size()) {
                Monument monument = monuments.get(position);
                if (monument != null) {
                    textViewTitle.setText(monument.getName());
                }
                imageViewIte.setImageResource(R.drawable.monument_1);
            }

        }
        return row;
    }

}

