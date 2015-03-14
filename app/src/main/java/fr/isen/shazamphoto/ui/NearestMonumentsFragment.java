package fr.isen.shazamphoto.ui;

import android.content.Intent;
import android.graphics.Color;
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
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import fr.isen.shazamphoto.R;
import fr.isen.shazamphoto.database.Localization;
import fr.isen.shazamphoto.database.Monument;
import fr.isen.shazamphoto.events.EventLocalizationFound;
import fr.isen.shazamphoto.events.RequestNearestMonuments;
import fr.isen.shazamphoto.ui.ItemUtils.SearchLocalizationItem;
import fr.isen.shazamphoto.ui.ItemUtils.SearchMonumentsByLocalization;
import fr.isen.shazamphoto.utils.ConfigurationShazam;
import fr.isen.shazamphoto.utils.GetMonumentByLocalization;
import fr.isen.shazamphoto.utils.little.Little;
import fr.isen.shazamphoto.utils.little.Point;

public class NearestMonumentsFragment extends Fragment implements SearchLocalizationItem,
        SearchMonumentsByLocalization {

    public static final int POSITION = 1;
    public static final String NMF_NEATREST_MONUMENT_LIST =
            "fr.sein.shazamphoto.ui.nearestmonumentsfragment";

    private ListView listView;

    private ArrayList<Monument> monuments;

    private HashMap<Long, Monument> monumentsForCircuit;
    private Monument startMonument;
    private boolean isCircuitMode;

    private Localization localization;
    private LocateManager locateManager;

    private TextView textViewInformation;
    private TextView textViewTable;
    private Button buttonNearestMonuments;
    private Button buttonMakeCircuit;
    private Button buttonCancelCircuit;
    private Button buttonModeCircuit;
    private LinearLayout linearLayoutActionsCircuit;

    public static NearestMonumentsFragment newInstance(LocationManager locationManager) {
        NearestMonumentsFragment fragment = new NearestMonumentsFragment();
        fragment.setLocateManager(new LocateManager(locationManager, fragment));
        return fragment;
    }

    public NearestMonumentsFragment() {
        listView = null;
        monuments = new ArrayList<>();
        localization = null;
        isCircuitMode = false;
        startMonument = null;
        monumentsForCircuit = new HashMap<>();
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
        buttonModeCircuit = (Button) view.findViewById(R.id.fnm_button_modecircuit);
        textViewInformation = (TextView) view.findViewById(R.id.fnm_textview_informationfdm);
        linearLayoutActionsCircuit = (LinearLayout) view.findViewById(R.id.fnm_linearlayout_actionscircuit);

        // Set the several listeners
        /*setListenerListView(listView);
        setListenerNearestMonuments(buttonNearestMonuments, this);
        setListenerMakeCircuit(buttonMakeCircuit);
        setListenerModeCircuit(buttonModeCircuit);
        setListenerCancelCircuit(buttonCancelCircuit);
*/
        // Check if we have a monumentsForCircuit of nearest monument already found
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

    public ArrayList<Monument> getMonuments() {
        return monuments;
    }

    public void setLocateManager(LocateManager locateManager) {
        this.locateManager = locateManager;
    }

    public void setListNearestMonuments(ArrayList<Monument> monuments) {
        this.monuments = monuments;
        CustomListAdapter adapter = new CustomListAdapter(getActivity(), monuments);
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        buttonNearestMonuments.setVisibility(View.INVISIBLE);
        locateManager.stopListening();
    }

    public void setListCircuitMonuments(ArrayList<Monument> monuments) {
        this.monuments = monuments;
        CustomListAdapter adapter = new CustomListAdapter(getActivity(), monuments);
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        listView.setVisibility(View.VISIBLE);
        buttonNearestMonuments.setVisibility(View.INVISIBLE);
    }

    @Override
    public void foundLocalization(EventLocalizationFound eventLocalizationFound) {
        // Retrieve the localization found
        localization = eventLocalizationFound.getLocalization();
        locateManager.stopListening();
        Toast.makeText(getActivity(), "Localization found", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void monumentsFoundByLocalization(ArrayList<Monument> monuments) {
        Toast.makeText(getActivity(), "Monuments found", Toast.LENGTH_SHORT).show();
        //Set the several nearest monument found
        setListNearestMonuments(monuments);
        listView.setVisibility(View.VISIBLE);
        // Display the button to make a circuit
        buttonModeCircuit.setVisibility(View.VISIBLE);
        // Display the information
        textViewInformation.setText("Create a circuit by choosing the monuments");
        textViewInformation.setVisibility(View.VISIBLE);
    }

    private void setListenerListView(ListView listView) {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Monument m =  monuments.get(position);
                if(!isCircuitMode){
                    // If the user did not activate the circuit mode
                    // we just display the detail about the monument selected
                    Intent intent = new Intent(getActivity(), DetailMonument.class);
                    intent.putExtra(Monument.NAME_SERIALIZABLE, m);
                    startActivity(intent);
                }else{
                    // The circuit is activated, we add the monument to visit in the hasp map
                    monumentsForCircuit.put(m.getId(), m);
                    if(startMonument == null) startMonument = m;
                    view.setBackgroundColor(Color.BLUE);
                    Toast.makeText(getActivity(), "Size mouments : "+monumentsForCircuit.size()+ " m: "+m.toString(), Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void setListenerCancelCircuit(Button buttonCancelCircuit) {
        buttonCancelCircuit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Desactivate the circuit mode and remove the first monument selected
                // and clearing the list of monument selected
                isCircuitMode = false;
                startMonument = null;
                monumentsForCircuit.clear();
                // Set the default UI for the fragment
                linearLayoutActionsCircuit.setVisibility(View.INVISIBLE);
                buttonMakeCircuit.setVisibility(View.VISIBLE);
            }
        });
    }

    public void setListenerNearestMonuments(Button button, final NearestMonumentsFragment fragment) {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Search a localization if we don't have it
                if (localization == null) {
                    locateManager.startListening(new RequestNearestMonuments(fragment));
                } else {
                    GetMonumentByLocalization getMonumentByLocalization =
                            new GetMonumentByLocalization(new RequestNearestMonuments(fragment),
                                    fragment);
                    getMonumentByLocalization.execute(
                            Double.valueOf(localization.getLatitude()).toString(),
                            Double.valueOf(localization.getLongitude()).toString(),
                            ConfigurationShazam.DELTA_LOCALIZATION);
                    buttonModeCircuit.setVisibility(View.VISIBLE);
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

                for (long i : monumentsForCircuit.keySet()) {
                    Monument monument = monumentsForCircuit.get(i);
                    Localization startLocalization = monument.getLocalization();

                    // Put the id of the monument in the matrix
                    littleMatrix[index][0] = (int) monument.getId();
                    littleMatrix[0][index] = (int) monument.getId();

                    //Get the distance between two monuments
                    for (long j : monumentsForCircuit.keySet()) {

                        if (j != i) {
                            Monument nextMonument = monumentsForCircuit.get(j);
                            Localization endLocalization = nextMonument.getLocalization();
                            float[] result = new float[3];
                            Location.distanceBetween(
                                    startLocalization.getLatitude(), startLocalization.getLongitude(),
                                    endLocalization.getLatitude(), endLocalization.getLongitude(),
                                    result);
                            // Set the distance found
                            littleMatrix[index][(int) (j + 1)] = Math.round(result[0]);
                        }

                    }

                    littleMatrix[index][index] = -1;
                    littleMatrix[size - 1][index] = 0;
                    littleMatrix[index][size - 1] = 0;
                    index++;
                }

                Little little = new Little(monumentsForCircuit.size(), littleMatrix);
                little.doLittle();
                List<Point> shortPath = little.getShortPath();
                ArrayList<Monument> monumentPath = new ArrayList<>(monumentsForCircuit.size());
                Point start = null;
                int i = 0;

                Toast.makeText(getActivity(), "Size path : "+Integer.valueOf(shortPath.size()).toString(), Toast.LENGTH_LONG).show();
                // Retrieve the first monument to visit
                while (start == null && i < shortPath.size()) {
                    Point p = shortPath.get(i);
                    Toast.makeText(getActivity(), "Point: "+Long.valueOf(p.getFrom()).toString() +" m: "+Long.valueOf(startMonument.getId()).toString(), Toast.LENGTH_LONG).show();
                    if (p.getFrom() == startMonument.getId()){
                        start = p;
                        Toast.makeText(getActivity(), "Start point found", Toast.LENGTH_LONG).show();
                    }
                    i++;
                }

                if(start != null){
                    // Set the circuit for the monuments in order
                    monumentPath.add(monumentsForCircuit.get(start.getFrom()));
                    start = start.getNext();
                    while (start.getFrom() != startMonument.getId()) {
                        System.out.println(Long.valueOf(start.getFrom()).toString() + " " + monumentsForCircuit.get(start.getFrom()));
                        monumentPath.add(monumentsForCircuit.get(start.getFrom()));
                        start = start.getNext();
                    }

                    // Display the result
                    setListCircuitMonuments(monumentPath);
                }

            }
        });
    }

    public void setListenerModeCircuit(final Button button_){
        button_.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Display the different action to create a circuit
                linearLayoutActionsCircuit.setVisibility(View.VISIBLE);
                // Make the current button
                button_.setVisibility(View.INVISIBLE);
                // Change the mode of the listener on the list view, in order to retrieve
                // the monument selected for the circuit
                isCircuitMode = true;
                textViewInformation.setText("Please select the monument to visit");
            }
        });
    }


    public String toStringArray(String[][] array, int size) {
        String result = "";

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                result += array[i][j] + "    ";
            }
            result += "\n";
        }

        return result;
    }

    public String toStringPath(List<Monument> arrayList) {
        String result = "";

        for (Monument p : arrayList) {
            result += (p == null ? "null " : p.toString());
        }

        return result;
    }
}
