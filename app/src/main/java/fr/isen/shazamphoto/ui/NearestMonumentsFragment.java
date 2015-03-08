package fr.isen.shazamphoto.ui;

import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;

import fr.isen.shazamphoto.R;
import fr.isen.shazamphoto.database.Localization;
import fr.isen.shazamphoto.database.Monument;
import fr.isen.shazamphoto.events.RequestNearestMonuments;
import fr.isen.shazamphoto.utils.GetMonumentByLocalization;

public class NearestMonumentsFragment extends Fragment {

    public static final int POSITION = 1;
    public static final String NMF_NEATREST_MONUMENT_LIST =
            "fr.sein.shazamphoto.ui.nearestmonumentsfragment";

    private ListView listView;
    private Button button;
    private ArrayList<Monument> monuments;
    private Localization localization;
    private LocateManager locateManager;

    public static NearestMonumentsFragment newInstance(LocationManager locationManager) {
        NearestMonumentsFragment fragment = new NearestMonumentsFragment();
        fragment.setLocateManager(new LocateManager(locationManager));
        return fragment;
    }

    public NearestMonumentsFragment() {
        listView = null;
        monuments = new ArrayList<>();
        localization = null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_nearest_monuments, container, false);
        listView = (ListView) view.findViewById(R.id.listview_nearest_monument);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), NearestMonuments.class);
                startActivity(intent);
            }
        });

        final NearestMonumentsFragment fragment = this;
        button = (Button) view.findViewById(R.id.button_search_nearest_monuments);


        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                // Search a localization if we don't have one
                if (localization == null) {
                    locateManager.startListening(new RequestNearestMonuments(fragment));
                } else {
                    GetMonumentByLocalization getMonumentByLocalization =
                            new GetMonumentByLocalization(new RequestNearestMonuments(fragment));
                    getMonumentByLocalization.execute(
                            Double.valueOf(localization.getLatitude()).toString(),
                            Double.valueOf(localization.getLongitude()).toString(), "0.09");
                }
            }
        });

        // Check if we have a list of nearest monument already found
        // Case for sweeping fragment
        if (!monuments.isEmpty()) {
            setListNearestMonuments(monuments);
        }

        setRetainInstance(true);
        return view;
    }

    public Localization getLocalization() {
        return localization;
    }

    public void setLocalization(Localization localization) {
        this.localization = localization;
    }

    public void setListNearestMonuments(ArrayList<Monument> monuments) {
        this.monuments = monuments;
        CustomListAdapter adapter = new CustomListAdapter(getActivity(), monuments);
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        button.setVisibility(View.INVISIBLE);
        locateManager.stopListening();
    }

    public ArrayList<Monument> getMonuments(){ return monuments; }

    public Button getButton() {
        return button;
    }

    public void setLocateManager(LocateManager locateManager) {
        this.locateManager = locateManager;
    }
}
