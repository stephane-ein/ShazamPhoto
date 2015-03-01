package fr.isen.shazamphoto.ui;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import fr.isen.shazamphoto.R;

public class GridViewAdapter extends ArrayAdapter {

    Context context;

    public GridViewAdapter(Context context) {
        super(context, 0);
        this.context = context;
    }

    public int getCount() {
        return 3;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;

        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(R.layout.grid_row, parent, false);

            TextView textViewTitle = (TextView) row.findViewById(R.id.textView_gridView);
            ImageView imageViewIte = (ImageView) row.findViewById(R.id.imageView_gridView);

            if (position % 2 == 0) {
                textViewTitle.setText("Monument 1");
                imageViewIte.setImageResource(R.drawable.monument_1);
            } else {
                textViewTitle.setText("Monument 2");
                imageViewIte.setImageResource(R.drawable.no_picture);
            }
        }
        return row;
    }

}

