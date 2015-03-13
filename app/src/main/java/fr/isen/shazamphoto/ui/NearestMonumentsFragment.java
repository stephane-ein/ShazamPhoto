package fr.isen.shazamphoto.ui;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import fr.isen.shazamphoto.R;
import fr.isen.shazamphoto.database.Localization;
import fr.isen.shazamphoto.database.Monument;
import fr.isen.shazamphoto.events.EventLocalizationFound;
import fr.isen.shazamphoto.events.RequestNearestMonuments;
import fr.isen.shazamphoto.ui.ItemUtils.SearchLocalizationItem;
import fr.isen.shazamphoto.ui.ItemUtils.SearchMonumentsByLocalization;
import fr.isen.shazamphoto.utils.ConfigurationShazam;
import fr.isen.shazamphoto.utils.GetMonumentByLocalization;

public class NearestMonumentsFragment extends Fragment implements SearchLocalizationItem, SearchMonumentsByLocalization {

    public static final int POSITION = 1;
    public static final String NMF_NEATREST_MONUMENT_LIST =
            "fr.sein.shazamphoto.ui.nearestmonumentsfragment";

    private ListView listView;
    private ArrayList<Monument> monuments;
    private Localization localization;
    private LocateManager locateManager;

    private TextView textViewInformation;
    private TextView textViewTable;
    private Button buttonNearestMonuments;
    private Button buttonMakeCircuit;
    private Button buttonCancelCircuit;

    public static NearestMonumentsFragment newInstance(LocationManager locationManager) {
        NearestMonumentsFragment fragment = new NearestMonumentsFragment();
        fragment.setLocateManager(new LocateManager(locationManager, fragment));
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

        // Retrieve the different item
        listView = (ListView) view.findViewById(R.id.listview_nearest_monument);
        buttonNearestMonuments = (Button) view.findViewById(R.id.fnm_button_searchnearestmonument);
        buttonMakeCircuit = (Button) view.findViewById(R.id.fnm_button_makecircuit);
        buttonCancelCircuit = (Button) view.findViewById(R.id.fnm_button_cancelcircuit);
        textViewTable = (TextView) view.findViewById(R.id.fnm_textview_table);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), DetailMonument.class);
                intent.putExtra(Monument.NAME_SERIALIZABLE, monuments.get(position));
                startActivity(intent);
            }
        });



        final NearestMonumentsFragment fragment = this;

        buttonNearestMonuments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Search a localization if we don't have it
                /*if (localization == null) {
                    locateManager.startListening(new RequestNearestMonuments(fragment));
                } else {
                    GetMonumentByLocalization getMonumentByLocalization =
                            new GetMonumentByLocalization(new RequestNearestMonuments(fragment),
                                    fragment);
                    getMonumentByLocalization.execute(
                            Double.valueOf(localization.getLatitude()).toString(),
                            Double.valueOf(localization.getLongitude()).toString(),
                            ConfigurationShazam.DELTA_LOCALIZATION);
                }*/
                // Attribute to the little algo
                Geocoder geocoder = new Geocoder(fragment.getActivity(), Locale.getDefault());
                List<Address> addresses = null;

                // Information for the little matrix
                ArrayList<Monument> list = new ArrayList<>(5);
                list.add( new Monument(1, "Lille", "", "", 2000, 0, 0, new Localization(-1, 50.615966, 3.054250)));
                list.add( new Monument(2, "Amiens", "", "", 2000, 0, 0, new Localization(-1, 49.885387, 2.296194)));
                list.add( new Monument(3, "Paris", "", "", 2000, 0, 0, new Localization(-1, 48.855271, 2.345632)));
                list.add( new Monument(4, "Nantes", "", "", 2000, 0, 0, new Localization(-1, 547.224742, -1.581980)));
                list.add( new Monument(5, "Marseille", "", "", 2000, 0, 0, new Localization(-1, 43.394618, 5.361379)));
                int size = list.size()+1;
                String[][] littleMatrix = new String[size][size];
                int index = 1;

                littleMatrix[0][0] = "0";
                littleMatrix[size-1][0] = "0";
                littleMatrix[size-1][size-1] = "0";
                littleMatrix[0][size-1] = "0";

                for(int i=0; i < list.size(); i++)
                {
                        Monument monument = list.get(i);
                        Localization startLocalization = monument.getLocalization();
                        // Put the id of the monument in the matrix
                        littleMatrix[index][0] = monument.getName();
                        littleMatrix[0][index] = monument.getName();

                        //Get the distance between this monument and the other
                        for(int j = 0 ; j<list.size(); j++){
                            if( j != i){
                                Monument nextMonument = list.get(j);
                                Localization endLocalization = nextMonument.getLocalization();
                                float[] result = new float[3];
                                Location.distanceBetween(
                                        startLocalization.getLatitude(), startLocalization.getLongitude(),
                                        endLocalization.getLatitude(), endLocalization.getLongitude(),
                                        result);
                                littleMatrix[index][j+1] = Float.toString(result[0]);
                            }

                        }

                        littleMatrix[index][index] = "-1";
                        littleMatrix[size-1][index] = "0";
                        littleMatrix[index][size-1] = "0";
                        index++;
                }



                textViewTable.setText(toStringArray(littleMatrix, size));

            }
        });

        buttonMakeCircuit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        textViewInformation = (TextView) view.findViewById(R.id.fnm_textview_informationfdm);

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
        buttonNearestMonuments.setVisibility(View.INVISIBLE);
        locateManager.stopListening();
    }

    public ArrayList<Monument> getMonuments() {
        return monuments;
    }

    public void setLocateManager(LocateManager locateManager) {
        this.locateManager = locateManager;
    }

    @Override
    public void foundLocalization(EventLocalizationFound eventLocalizationFound) {
        // Retrieve the localization found
        localization = eventLocalizationFound.getLocalization();
        locateManager.stopListening();
    }

    @Override
    public void monumentsFoundByLocalization(ArrayList<Monument> monuments) {
        //Set the several nearest monuments found
        setListNearestMonuments(monuments);
        listView.setVisibility(View.VISIBLE);
        // Display the button to make a circuit
        buttonMakeCircuit.setVisibility(View.VISIBLE);
        textViewInformation.setText("Chose the monument to see and we're going to display the best circuit");
    }

    public String toStringArray(String[][] array, int size) {
        String result = "";

            for(int i = 0 ; i< size; i++){
                for(int j = 0 ; j< size; j++){
                    result += array[i][j] +"    ";
                }
                result += "\n";
            }

        return result;
    }
}
