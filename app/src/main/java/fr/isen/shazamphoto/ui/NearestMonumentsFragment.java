package fr.isen.shazamphoto.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import fr.isen.shazamphoto.R;
import fr.isen.shazamphoto.database.Monument;
import fr.isen.shazamphoto.database.TaggedMonumentDAO;

public class NearestMonumentsFragment extends Fragment {

    private ActionMode actionMode;
    private ListView listView;

    public static NearestMonumentsFragment newInstance() {
        NearestMonumentsFragment fragment = new NearestMonumentsFragment();
        return fragment;
    }

    public NearestMonumentsFragment() {
        // Required empty public constructor
        actionMode = null;
        listView = null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_nearest_monuments, container, false);
        ListView listView = (ListView) view.findViewById(R.id.listview_nearest_monument);
        final CMNearestMonument cmNearestMonument = new CMNearestMonument((Home)getActivity());


        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                actionMode = getActivity().startActionMode(cmNearestMonument);
                return true;
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), NearestMonuments.class);
                startActivity(intent);
            }
        });

        TaggedMonumentDAO taggedMonumentDAO = new TaggedMonumentDAO(getActivity());
        taggedMonumentDAO.open();
        final List<Monument> monumentsList = taggedMonumentDAO.getAllMonuments();
        taggedMonumentDAO.close();
        final ArrayList<Monument> monuments = new ArrayList<>();
        for (Monument monument : monumentsList) {
            monuments.add(monument);
        }

        CustomListAdapter adapter = new CustomListAdapter(getActivity(), monuments);
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        return view;
    }


}
