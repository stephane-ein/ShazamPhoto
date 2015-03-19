package fr.isen.shazamphoto.ui.CustomAdapter;

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

public class GridFavrouriteAdapter extends BaseAdapter {
    private final List<Monument> mItems;
    private final LayoutInflater mInflater;

    public GridFavrouriteAdapter(Context context, ArrayList<Monument> monuments) {
        mInflater = LayoutInflater.from(context);
        this.mItems = monuments;
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

        if (m.getPhotoPath() != null && !m.getName().isEmpty()) {
            System.out.println("GFA : photoPath found");
           /* GetMonumentImage getMonumentImage = new GetMonumentImage(picture);
            getMonumentImage.execute(m.getPhotoPath());
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 16;
            Bitmap bitmap = BitmapFactory.decodeFile(m.getPhotoPath(), options);
            picture.setImageBitmap(bitmap);*/

            picture.setImageResource(R.drawable.monument_1);
        }else{
            System.out.println("GFA : monument_1");
            picture.setImageResource(R.drawable.monument_1);
        }
        name.setText(m.getName());

        return v;
    }
}