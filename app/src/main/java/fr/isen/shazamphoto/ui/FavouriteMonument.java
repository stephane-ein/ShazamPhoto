package fr.isen.shazamphoto.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import fr.isen.shazamphoto.R;
import fr.isen.shazamphoto.database.FavouriteMonumentDAO;
import fr.isen.shazamphoto.database.Monument;

public class FavouriteMonument extends Fragment {

    private View view;

    public static FavouriteMonument newInstance() {
        FavouriteMonument fragment = new FavouriteMonument();
        return fragment;
    }

    public FavouriteMonument() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_favourite_monument, container, false);

        FavouriteMonumentDAO favouriteMonumentDAO = new FavouriteMonumentDAO(getActivity());
        favouriteMonumentDAO.open();
        final List<Monument> monumentsList = favouriteMonumentDAO.getAllMonuments();
        favouriteMonumentDAO.close();
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

                Intent intent = new Intent(getActivity(), DetailMonument.class);

                intent.putExtra(Monument.NAME_SERIALIZABLE, monuments.get(position));
                startActivity(intent);
            }
        });
        setRetainInstance(true);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() != null) {
            this.onCreate(null);
            FavouriteMonumentDAO favouriteMonumentDAO = new FavouriteMonumentDAO(getActivity());
            favouriteMonumentDAO.open();
            final List<Monument> monumentsList = favouriteMonumentDAO.getAllMonuments();
            favouriteMonumentDAO.close();
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

                    Intent intent = new Intent(getActivity(), DetailMonument.class);

                    intent.putExtra(Monument.NAME_SERIALIZABLE, monuments.get(position));
                    startActivity(intent);
                }
            });
            setRetainInstance(true);
        }
    }
}
