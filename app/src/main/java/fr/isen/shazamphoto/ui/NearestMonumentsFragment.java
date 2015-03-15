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

    private ArrayList<Monument> monumentsNearest;
    private ArrayList<Monument> monumentCircuit;

    private HashMap<Long, Monument> monumentsForCircuit;
    private Monument startMonument;
    private boolean isCircuitMode;
    private final long idUser = -2;

    private Localization localization;
    private LocateManager locateManager;

    private TextView textViewInformation;
    private TextView textViewTable;
    private Button buttonNearestMonuments;
    private Button buttonMakeCircuit;
    private Button buttonCancelCircuit;
    private Button buttonModeCircuit;
    private Button buttonBack;
    private LinearLayout linearLayoutActionsCircuit;

    public static NearestMonumentsFragment newInstance(LocationManager locationManager) {
        NearestMonumentsFragment fragment = new NearestMonumentsFragment();
        fragment.setLocateManager(new LocateManager(locationManager, fragment));
        return fragment;
    }

    public NearestMonumentsFragment() {
        listView = null;
        monumentsNearest = new ArrayList<>();
        monumentCircuit = new ArrayList<>();
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
        buttonBack = (Button) view.findViewById(R.id.fnm_button_back);
        buttonMakeCircuit = (Button) view.findViewById(R.id.fnm_button_makecircuit);
        buttonCancelCircuit = (Button) view.findViewById(R.id.fnm_button_cancelcircuit);
        textViewTable = (TextView) view.findViewById(R.id.fnm_textview_table);
        buttonModeCircuit = (Button) view.findViewById(R.id.fnm_button_modecircuit);
        textViewInformation = (TextView) view.findViewById(R.id.fnm_textview_informationfdm);
        linearLayoutActionsCircuit = (LinearLayout) view.findViewById(R.id.fnm_linearlayout_actionscircuit);

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

        setRetainInstance(true);
        return view;
    }

    public Localization getLocalization() {
        return localization;
    }

    public void setLocalization(Localization localization) {
        this.localization = localization;
    }

    public ArrayList<Monument> getMonumentsNearest() {
        return monumentsNearest;
    }

    public void setLocateManager(LocateManager locateManager) {
        this.locateManager = locateManager;
    }

    public void setListNearestMonuments(ArrayList<Monument> monuments) {
        // Stop he listener on the network
        locateManager.stopListening();

        // Display the list view
        this.monumentsNearest = monuments;
        CustomListAdapter adapter = new CustomListAdapter(getActivity(), monuments);
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        buttonNearestMonuments.setVisibility(View.INVISIBLE);
        listView.setVisibility(View.VISIBLE);

        // Display the button to make a circuit
        buttonModeCircuit.setVisibility(View.VISIBLE);

        // Display the information
        textViewInformation.setText("Create a circuit by choosing the monumentsNearest");
        textViewInformation.setVisibility(View.VISIBLE);
    }

    public void setListCircuitMonuments(ArrayList<Monument> monuments) {
        this.monumentCircuit = monuments;
        CustomListAdapter adapter = new CustomListAdapter(getActivity(), monuments);
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        listView.setVisibility(View.VISIBLE);
        // Display the button back and hide the action for the mode circuit
        buttonBack.setVisibility(View.VISIBLE);
        linearLayoutActionsCircuit.setVisibility(View.INVISIBLE);
    }

    @Override
    public void foundLocalization(EventLocalizationFound eventLocalizationFound) {
        // Retrieve the localization found
        localization = eventLocalizationFound.getLocalization();
        locateManager.stopListening();
        // Retrieve the nearest monumentsNearest
        executeGetMonumentByLocalization();
       // textViewTable.setText(localization.toString());
    }

    @Override
    public void monumentsFoundByLocalization(ArrayList<Monument> monuments) {
        //Set the several nearest monument found
        setListNearestMonuments(monuments);
        Toast.makeText(getActivity(), "Monuments found", Toast.LENGTH_SHORT).show();
    }

    private void setListenerListView(ListView listView) {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Monument m = monumentsNearest.get(position);
                if (!isCircuitMode) {
                    // If the user did not activate the circuit mode
                    // we just display the detail about the monument selected
                    Intent intent = new Intent(getActivity(), DetailMonument.class);
                    intent.putExtra(Monument.NAME_SERIALIZABLE, m);
                    startActivity(intent);
                } else {
                    // The circuit is activated, we add the monument to visit in the hasp map
                    monumentsForCircuit.put(m.getId(), m);
                    if (startMonument == null){
                        // Add the position of the user in the monument to visit (To modify)
                        startMonument = new Monument(idUser, localization);
                        monumentsForCircuit.put(startMonument.getId(), startMonument);
                    }
                    Toast.makeText(getActivity(), "Monument selected: "+m.toString(),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void displayDefaultUI(){
        // Deactivate the circuit mode and remove the first monument selected
        // and clearing the list of monument selected
        isCircuitMode = false;
        startMonument = null;
        monumentsForCircuit.clear();
        // Set the default UI for the fragment
        linearLayoutActionsCircuit.setVisibility(View.INVISIBLE);
        buttonBack.setVisibility(View.INVISIBLE);
        buttonMakeCircuit.setVisibility(View.VISIBLE);
        setListNearestMonuments(monumentsNearest);
    }

    public void displayModeCircuit(){
        // Display the different action to create a circuit
        linearLayoutActionsCircuit.setVisibility(View.VISIBLE);
        // hide the button to pass on mode circuit
        buttonModeCircuit.setVisibility(View.INVISIBLE);
        // Change the mode of the listener on the list view, in order to retrieve
        // the monument selected for the circuit
        isCircuitMode = true;
        textViewInformation.setText("Please select the monument to visit");
    }

    public void executeGetMonumentByLocalization() {
        // Retrieve the nearest monuments with the localization of the user
        GetMonumentByLocalization getMonumentByLocalization =
                new GetMonumentByLocalization(new RequestNearestMonuments(this),
                        this);
        getMonumentByLocalization.execute(
                Double.valueOf(localization.getLatitude()).toString(),
                Double.valueOf(localization.getLongitude()).toString(),
                ConfigurationShazam.DELTA_LOCALIZATION);
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

    public void setListenerNearestMonuments(Button button, final NearestMonumentsFragment fragment) {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Search a localization if we don't have it
                if (localization == null) {
                    locateManager.startListening(new RequestNearestMonuments(fragment));
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

                for (long i : monumentsForCircuit.keySet()) {
                    Monument monument = monumentsForCircuit.get(i);
                    Localization startLocalization = monument.getLocalization();

                    // Put the id of the monument in the matrix
                    littleMatrix[index][0] = (int) monument.getId();
                    littleMatrix[0][index] = (int) monument.getId();

                    int jIndex = 1;

                    //Get the distance between two monumentsNearest
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
                            littleMatrix[index][jIndex] = Math.round(result[0]);
                        }

                        jIndex++;
                    }

                    littleMatrix[index][index] = -1;
                    littleMatrix[size - 1][index] = 0;
                    littleMatrix[index][size - 1] = 0;
                    index++;
                }

                System.out.println(toStringArray(littleMatrix, size));

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
                    if (p.getFrom() == idUser) {
                        start = p;
                    }
                    i++;
                }

                if (start != null) {
                    // Set the circuit for the monumentsNearest in order
                    monumentPath.add(monumentsForCircuit.get(start.getTo()));
                    start = start.getNext();
                    while (start.getTo() != startMonument.getId()) {
                        monumentPath.add(monumentsForCircuit.get(start.getTo()));
                        start = start.getNext();
                    }

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
                displayModeCircuit();
            }
        });
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
            result += (p == null ? "null " : p.toString());
        }

        return result;
    }
}
