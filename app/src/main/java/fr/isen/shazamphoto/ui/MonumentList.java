package fr.isen.shazamphoto.ui;


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
import fr.isen.shazamphoto.database.FavouriteMonumentDAO;
import fr.isen.shazamphoto.database.Monument;
import fr.isen.shazamphoto.database.TaggedMonumentDAO;

public abstract class MonumentList extends Fragment {
    private View view;
    private String typeOfList;

    public MonumentList(String typeOfList) {
        // Required empty public constructor
        this.typeOfList = typeOfList;
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
        setView();
        setRetainInstance(true);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() != null) {
            setView();
            setRetainInstance(true);
        }
    }

    public void setView(){
        if(typeOfList.equals(FavouriteMonument.class.getSimpleName())){
            setFavouriteMonuments();
        }else if(typeOfList.equals(TaggedMonument.class.getSimpleName())){
            setTaggedMonuments();
        }
    }

    public void setFavouriteMonuments(){
        FavouriteMonumentDAO favouriteMonumentDAO = new FavouriteMonumentDAO(getActivity());
        favouriteMonumentDAO.open();
        final List<Monument> monumentsList = favouriteMonumentDAO.getAllMonuments();
        favouriteMonumentDAO.close();
        final ArrayList<Monument> monuments = new ArrayList<>();
        for (Monument monument : monumentsList) {
            monuments.add(monument);
        }
        setListMonuments(monuments);
    }

    public void setTaggedMonuments(){
        TaggedMonumentDAO taggedMonumentDAO = new TaggedMonumentDAO(getActivity());
        taggedMonumentDAO.open();
        final List<Monument> monumentsList = taggedMonumentDAO.getAllMonuments();
        taggedMonumentDAO.close();
        final ArrayList<Monument> monuments = new ArrayList<>();
        for (Monument monument : monumentsList) {
            monuments.add(monument);
        }
        setListMonuments(monuments);
    }

    public void setListMonuments(final ArrayList<Monument> monuments){
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
    }
}
