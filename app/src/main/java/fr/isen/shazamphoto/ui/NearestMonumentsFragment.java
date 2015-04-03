package fr.isen.shazamphoto.ui;

import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import fr.isen.shazamphoto.R;
import fr.isen.shazamphoto.database.Localization;
import fr.isen.shazamphoto.database.Monument;
import fr.isen.shazamphoto.events.EventLocalizationFound;
import fr.isen.shazamphoto.events.RequestNearestMonuments;
import fr.isen.shazamphoto.ui.CustomAdapter.NearestListAdapter;
import fr.isen.shazamphoto.ui.ItemUtils.SearchLocalizationItem;
import fr.isen.shazamphoto.ui.ItemUtils.SearchMonumentsByLocalization;
import fr.isen.shazamphoto.utils.GetMonumentTask.GetMonumentByLocalization;
import fr.isen.shazamphoto.utils.Little.Little;
import fr.isen.shazamphoto.utils.Little.Point;

public class NearestMonumentsFragment extends Fragment implements SearchLocalizationItem,
        SearchMonumentsByLocalization {

    public static final int POSITION = 2;
    public static final String NMF_NEATREST_MONUMENT_LIST =
            "fr.sein.shazamphoto.ui.nearestmonumentsfragment";

    private ListView listView;

    private ArrayList<Monument> monumentsNearest;   // List correspond to the nearest monuments
    private ArrayList<Monument> monuments;          // List correspond to the current monument in the list view

    private HashMap<Integer, Monument> monumentsForCircuit;
    private int i;

    private Monument startMonument;
    private boolean startedCircuitMode;

    private Localization localization;
    private LocateManager locateManager;

    private TextView textViewInformation;
    private Button buttonNearestMonuments;
    private Button buttonMakeCircuit;
    private Button buttonCancelCircuit;
    private Button buttonModeCircuit;
    private Button buttonBack;
    private LinearLayout linearLayoutActionsCircuit;
    private NetworkInfoArea networkInfo;

    public static NearestMonumentsFragment newInstance(LocationManager locationManager) {
        NearestMonumentsFragment fragment = new NearestMonumentsFragment();
        fragment.setLocateManager(new LocateManager(locationManager, fragment));
        return fragment;
    }

    public NearestMonumentsFragment() {
        listView = null;
        monumentsNearest = new ArrayList<>();
        monuments = new ArrayList<>();
        localization = null;
        startedCircuitMode = false;
        startMonument = null;
        monumentsForCircuit = new HashMap<>();
        i = 1;
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
        buttonBack = (Button) view.findViewById(R.id.fnm_button_back);
        buttonMakeCircuit = (Button) view.findViewById(R.id.fnm_button_makecircuit);
        buttonCancelCircuit = (Button) view.findViewById(R.id.fnm_button_cancelcircuit);
        buttonModeCircuit = (Button) view.findViewById(R.id.fnm_button_modecircuit);
        textViewInformation = (TextView) view.findViewById(R.id.fnm_textview_informationfdm);
        linearLayoutActionsCircuit = (LinearLayout) view.findViewById(R.id.fnm_linearlayout_actionscircuit);
        networkInfo = (NetworkInfoArea) getActivity().findViewById(R.id.home_info_network);
        // Set the several listeners
        setListenerListView(listView);
        setListenerNearestMonuments(buttonNearestMonuments, this);
        setListenerMakeCircuit(buttonMakeCircuit);
        setListenerModeCircuit(buttonModeCircuit);
        setListenerCancelCircuit(buttonCancelCircuit);
        setListenerBack(buttonBack);

        // Check if we have a monumentsForCircuit of nearest monument already found
        // Case for sweeping fragment
        if (!monumentsNearest.isEmpty()) setListNearestMonuments(monumentsNearest);

        reset();
        setRetainInstance(true);
        return view;
    }

    private void reset(){
        // Restore the default value
        startedCircuitMode = false;
        startMonument = null;
        monumentsForCircuit = new HashMap<>();
        i = 1;
    }

    @Override
    public void foundLocalization(EventLocalizationFound eventLocalizationFound) {
        // Retrieve the localization found
        localization = eventLocalizationFound.getLocalization();
        locateManager.stopListening();

        // Retrieve the nearest monumentsNearest
        executeGetMonumentByLocalization();
    }

    @Override
    public void monumentsFoundByLocalization(ArrayList<Monument> monuments) {
        //Set the several nearest monument found
        setListNearestMonuments(monuments);
    }

    public void executeGetMonumentByLocalization() {
        // Retrieve the nearest monuments with the localization of the user
        GetMonumentByLocalization getMonumentByLocalization =
                new GetMonumentByLocalization(this, networkInfo, getActivity(),localization.getLatitude(), localization.getLongitude());
       /* getMonumentByLocalization.execute(
                Double.valueOf(localization.getLatitude()).toString(),
                Double.valueOf(localization.getLongitude()).toString(),
                ConfigurationShazam.DELTA_LOCALIZATION);*/
        getMonumentByLocalization.execute();
    }

    public void setListNearestMonuments(ArrayList<Monument> monuments) {
        // Stop he listener on the network
        locateManager.stopListening();

        // Display the list view
        this.monumentsNearest = monuments;
        this.monuments = monuments;
        NearestListAdapter adapter = new NearestListAdapter(getActivity(), monuments, localization);
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        listView.setVisibility(View.VISIBLE);

        // Change the UI
        displayModeCircuit();
    }

    public void setListCircuitMonuments(ArrayList<Monument> monuments) {
        this.monuments = monuments;
        NearestListAdapter adapter = new NearestListAdapter(getActivity(), monuments, localization);
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        listView.setVisibility(View.VISIBLE);
        displayCircuitActivated();
    }

    private void setListenerListView(ListView listView) {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Monument m = monuments.get(position);
                if (!startedCircuitMode) {
                    // If the user did not activate the circuit mode
                    // we just display the detail about the monument selected
                    Intent intent = new Intent(getActivity(), DetailMonument.class);
                    intent.putExtra(Monument.NAME_SERIALIZABLE, m);
                    startActivity(intent);
                } else {
                    // Initialise the color of the background
                    int background = R.drawable.list_row_nearestmonument_selected;

                    // Retrieve the first monument to visit
                    if (startMonument == null){
                        startMonument = m;
                        background = R.drawable.list_row_nearest_monument_start;
                    }

                    textViewInformation.setText("... and the other monuments to visit");

                    // We add the monument to visit in the hasp map
                    m.setIdNearest(i);
                    monumentsForCircuit.put(m.getIdNearest(), m);
                    i++;

                    LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.lrnm_linearlayout);
                    linearLayout.setBackgroundResource(background);
                }
            }
        });
    }

    public void displayDefaultUI(){
        // Deactivate the circuit mode and remove the first monument selected
        // and clearing the list of monument selected
        startedCircuitMode = false;
        startMonument = null;
        monumentsForCircuit.clear();

        // Set the default UI for the fragment
        linearLayoutActionsCircuit.setVisibility(View.INVISIBLE);
        buttonBack.setVisibility(View.INVISIBLE);
        buttonMakeCircuit.setVisibility(View.VISIBLE);
        setListNearestMonuments(monumentsNearest);
    }

    public void displayModeCircuit(){
        // Hide the button to retrieve all the nearest monuments
        buttonNearestMonuments.setVisibility(View.INVISIBLE);

        // Display the button to make a circuit
        buttonModeCircuit.setVisibility(View.VISIBLE);

        // Display the information
        textViewInformation.setText("Create a circuit by choosing the monument to visit");
        textViewInformation.setVisibility(View.VISIBLE);
    }

    public void displayCreateCircuit(){
        // Display the different action to create a circuit
        linearLayoutActionsCircuit.setVisibility(View.VISIBLE);

        // hide the button to pass on mode circuit
        buttonModeCircuit.setVisibility(View.INVISIBLE);

        // Change the mode of the listener on the list view, in order to retrieve
        // the monument selected for the circuit
        startedCircuitMode = true;
        textViewInformation.setText("Please select the first monument to visit...");
    }

    public void displayCircuitActivated(){
        // Disable the "starter" to create a circuit, in order to get the detail about a monument
        startedCircuitMode = false;

        // Display the button back and hide the action for the mode circuit
        buttonBack.setVisibility(View.VISIBLE);
        linearLayoutActionsCircuit.setVisibility(View.INVISIBLE);
    }

    private void setListenerBack(Button buttonBack) {
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayDefaultUI();
            }
        });
    }

    private void setListenerCancelCircuit(Button buttonCancelCircuit) {
        buttonCancelCircuit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayDefaultUI();
            }
        });
    }

    private void setListenerNearestMonuments(Button button, final NearestMonumentsFragment fragment) {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Search a localization if we don't have it
                if (localization == null) {
                    locateManager.startListening();
                } else {
                    executeGetMonumentByLocalization();
                }
            }
        });
    }

    public void setListenerMakeCircuit(Button button) {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Initialise the little matrix
                int size = monumentsForCircuit.size() + 2;
                int[][] littleMatrix = new int[size][size];
                int index = 1;

                // Set the several corner to 0
                littleMatrix[0][0] = 0;
                littleMatrix[size - 1][0] = 0;
                littleMatrix[size - 1][size - 1] = 0;
                littleMatrix[0][size - 1] = 0;

                for (Integer i : monumentsForCircuit.keySet()) {
                    Monument monument = monumentsForCircuit.get(i);

                    Localization startLocalization = monument.getLocalization();

                    // Put the id of the monument in the matrix
                    littleMatrix[index][0] =  monument.getIdNearest();
                    littleMatrix[0][index] =  monument.getIdNearest();

                    int jIndex = 1;

                    //Get the distance between two monumentsNearest
                    for (Integer j : monumentsForCircuit.keySet()) {

                        if (j != i) {
                            Monument nextMonument = monumentsForCircuit.get(j);
                            Localization endLocalization = nextMonument.getLocalization();
                            float[] result = new float[3];
                            Location.distanceBetween(
                                    startLocalization.getLatitude(), startLocalization.getLongitude(),
                                    endLocalization.getLatitude(), endLocalization.getLongitude(),
                                    result);
                            // Set the distance found
                            littleMatrix[index][jIndex] = Math.round(result[0]);
                        }

                        jIndex++;
                    }

                    littleMatrix[index][index] = -1;
                    littleMatrix[size - 1][index] = 0;
                    littleMatrix[index][size - 1] = 0;
                    index++;
                }

                // Do the little
                Little little = new Little(monumentsForCircuit.size(), littleMatrix);
                little.doLittle();
                List<Point> shortPath = little.getShortPath();
                ArrayList<Monument> monumentPath = new ArrayList<>(monumentsForCircuit.size());
                Point start = null;
                int i = 0;

                // Retrieve the first monument to visit
                while (start == null && i < shortPath.size()) {
                    Point p = shortPath.get(i);
                    if (p.getFrom() == startMonument.getIdNearest()) {
                        start = p;
                    }
                    i++;
                }

                if (start != null) {
                    // Set the circuit for the monumentsNearest in order
                    monumentPath.add(monumentsForCircuit.get((int)start.getFrom()));
                    start = start.getNext();
                    while ((int)(start.getFrom()) != startMonument.getIdNearest()) {
                        monumentPath.add(monumentsForCircuit.get((int)start.getFrom()));
                        start = start.getNext();
                    }

                    // Add the first monument to the end of the circuit with a specific id
                    Monument end = new Monument(-2, startMonument.getLocalization(), startMonument.getName());
                    monumentPath.add(end);

                    // Display the result
                    setListCircuitMonuments(monumentPath);
                }
            }
        });
    }

    public void setListenerModeCircuit(final Button button_) {
        button_.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayCreateCircuit();
            }
        });
    }

    public Localization getLocalization() {
        return localization;
    }

    public void setLocalization(Localization localization) {
        this.localization = localization;
    }

    public void setLocateManager(LocateManager locateManager) {
        this.locateManager = locateManager;
    }

    public String toStringArray(int[][] array, int size) {
        String result = "";

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                result += Integer.valueOf(array[i][j]).toString() + "    ";
            }
            result += "\n";
        }

        return result;
    }

    public String toStringPath(List<Monument> arrayList) {
        String result = "";

        for (Monument p : arrayList) {
            result += (p == null ? "null " : p.getName()+ " "+p.getIdNearest() +"   ");
        }

        return result;
    }
}
