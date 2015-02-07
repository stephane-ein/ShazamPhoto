package fr.isen.shazamphoto.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import fr.isen.shazamphoto.R;
import fr.isen.shazamphoto.database.Monument;
import fr.isen.shazamphoto.database.TaggedMonumentDAO;

public class FavouriteMonument extends Fragment {

    private Activity activity;

    public static FavouriteMonument newInstance(Activity act) {
        FavouriteMonument fragment = new FavouriteMonument();
        fragment.setActivity(act);
        return fragment;
    }

    public FavouriteMonument() {
        // Required empty public constructor
    }

    public void setActivity(Activity act){
        this.activity = act;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_favourite_monument, container, false);

        TaggedMonumentDAO taggedMonumentDAO = new TaggedMonumentDAO(getActivity());
        taggedMonumentDAO.open();
        final List<Monument> monumentsList = taggedMonumentDAO.getAllMonuments();
        taggedMonumentDAO.close();
        Bundle args = null;
        final ArrayList<Monument> monuments = new ArrayList<Monument>();
        for (Monument monument : monumentsList) {
            monuments.add(monument);
           /* FileInputStream in = null;
            try {
                in = openFileInput(monument.getName() + ".png");
                monument.setImage(BitmapFactory.decodeStream(in));
            } catch (Exception e) {}
            finally {
                try {
                    if (in != null) {
                        in.close();
                    }
                } catch (IOException e) {}
            }*/
        }

        CustomListAdapter adapter = new CustomListAdapter(getActivity(), monuments);
        ListView listview = (ListView) view.findViewById(R.id.listview_favourite_monument);
        listview.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    int position, long arg3) {

                Intent intent = new Intent(activity, DetailMonument.class);

                intent.putExtra(Monument.NAME_SERIALIZABLE, monuments.get(position));
                startActivity(intent);
            }
        });
        setRetainInstance(true);

        return view;
    }
}
