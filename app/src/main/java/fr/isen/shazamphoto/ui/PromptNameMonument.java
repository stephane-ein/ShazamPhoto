package fr.isen.shazamphoto.ui;

import android.content.Context;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import fr.isen.shazamphoto.R;
import fr.isen.shazamphoto.database.Localization;
import fr.isen.shazamphoto.database.Monument;
import fr.isen.shazamphoto.events.EventDisplayDetailMonument;
import fr.isen.shazamphoto.events.EventLocalizationFound;
import fr.isen.shazamphoto.events.EventMonumentUpdated;
import fr.isen.shazamphoto.model.ModelNavigation;
import fr.isen.shazamphoto.ui.CustomAdapter.NearestListAdapter;
import fr.isen.shazamphoto.ui.ItemUtils.SearchLocalizationItem;
import fr.isen.shazamphoto.ui.ItemUtils.SearchMonumentsByLocalization;
import fr.isen.shazamphoto.ui.ItemUtils.SearchableItem;
import fr.isen.shazamphoto.ui.ItemUtils.UpdateMonumentItem;
import fr.isen.shazamphoto.utils.GetMonumentTask.GetMonumentByLocalization;
import fr.isen.shazamphoto.utils.GetMonumentTask.GetMonumentsByName;
import fr.isen.shazamphoto.utils.Images.LoadPicture;
import fr.isen.shazamphoto.utils.Sort;
import fr.isen.shazamphoto.utils.UpdateMonument.AddDescriptorsKeyPointsTask;

public class PromptNameMonument extends Fragment implements SearchableItem, SearchMonumentsByLocalization, SearchLocalizationItem,
        UpdateMonumentItem{

    private Monument monument;
    private String monumentName;
    private ListView listView;
    private LocateManager locateManager;
    private Button buttonSend;
    private LinearLayout llProgessBar;
    private ArrayList<Monument> monuments;
    private ModelNavigation modelNavigation;
    private Localization localization1;

    public PromptNameMonument() {
    }

    public ModelNavigation getModelNavigation() {
        return modelNavigation;
    }

    public void setModelNavigation(ModelNavigation modelNavigation) {
        this.modelNavigation = modelNavigation;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        getActivity().setTitle("Unidentified monument...");

        View rootView = inflater.inflate(R.layout.fragment_prompt_name_monument, container, false);
        monument = (Monument) getArguments().getSerializable(Monument.NAME_SERIALIZABLE);
        modelNavigation = (ModelNavigation) getArguments().getSerializable(ModelNavigation.KEY);
        listView = (ListView) rootView.findViewById(R.id.fpnm_lv_nearest_monument);
        buttonSend = (Button) rootView.findViewById(R.id.fpnm_but_send);
        llProgessBar = (LinearLayout) rootView.findViewById(R.id.fpnm_progress_bar);
        ImageView imageView = (ImageView) rootView.findViewById(R.id.fpnm_iv_monument);

        // Set the listener
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(monuments != null && !monuments.isEmpty() && monuments.get(i)!=null && modelNavigation != null){
                    modelNavigation.changeAppView(new EventDisplayDetailMonument(getActivity(), monuments.get(i), modelNavigation));
                }
            }
        });
        // Display the picture taken
        LoadPicture.setPictureFromFile(monument.getPhotoPathLocal(), imageView, LoadPicture.getHdpiWidthVertical(getActivity()), LoadPicture.getHdpiHeightVertical(getActivity()));

        final PromptNameMonument promptNameMonument = this;
        final EditText editText = (EditText) rootView.findViewById(R.id.editText_prompname_monument);
        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Ask the server if the monument already exist and disable the button to send the information
                displayLoading();
                buttonSend.setEnabled(false);
                //buttonSend.setBackgroundResource(R.drawable.button_selected);
                monumentName = editText.getText().toString();
                GetMonumentsByName getMonumentsByName = new GetMonumentsByName(getActivity(),promptNameMonument,
                        editText.getText().toString());
                getMonumentsByName.execute();

                // Close the keyboard
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(
                        Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
            }
        });

        // Check if the monument retrieve has a localization in order to retrieve the monument rear the user
        if(monument.getLocalization() != null){
            GetMonumentByLocalization getMonumentByLocalization = new GetMonumentByLocalization(this, getActivity(),
                    monument.getLocalization().getLatitude(), monument.getLocalization().getLongitude());
            getMonumentByLocalization.execute();
        }else{
            // If not, we listen to the network
            locateManager = new LocateManager( (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE), this);
            locateManager.startListening();
        }

        setRetainInstance(true);

        return rootView;
    }

    @Override
    public void onPostSearch(ArrayList<Monument> monuments, String searchName) {
        if(monuments.isEmpty()){
            // The user found a new monument
            AddMonumentFragment addMonumentFragment = new AddMonumentFragment();
            Bundle args = new Bundle();
            monument.setName(monumentName);
            args.putSerializable(Monument.NAME_SERIALIZABLE, monument);
            addMonumentFragment.setArguments(args);
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, addMonumentFragment)
                    .commit();
        }else{
            // The monument has not been identified and
            // the picture took by the user will increase the descriptor for the monument
            Monument m = monuments.get(0);
            monument.setDatabaseId(m.getDatabaseId());
            AddDescriptorsKeyPointsTask addDescriptorsKeyPointsTask = new AddDescriptorsKeyPointsTask(getActivity(), this);
            addDescriptorsKeyPointsTask.execute(monument);
        }
    }

    @Override
    public void monumentsFoundByLocalization(ArrayList<Monument> monuments) {
        if(monument.getPhotoPathLocal() != null) Sort.sortArrayMonument(monument.getLocalization(), monuments);
        this.monuments = monuments;
        NearestListAdapter adapter = new NearestListAdapter(getActivity(), monuments,  monument.getLocalization());
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void foundLocalization(EventLocalizationFound eventLocalizationFound) {
        // Localization has been retrieved thanks to the network
        locateManager.stopListening();
        localization1 = eventLocalizationFound.getLocalization();
        Log.v("Shazam", "PNM localization : "+localization1);
        GetMonumentByLocalization getMonumentByLocalization = new GetMonumentByLocalization(this, getActivity(),
                localization1.getLatitude(),localization1.getLongitude());
        getMonumentByLocalization.execute();
    }

    @Override
    public void monumentUpdated(EventMonumentUpdated eventLocalizationFound) {
        Toast.makeText(getActivity(), "The monument descriptors has been added", Toast.LENGTH_LONG).show();
        hideLoading();
    }

    public void displayLoading(){
        llProgessBar.setVisibility(View.VISIBLE);
        buttonSend.setVisibility(View.GONE);
    }

    public void hideLoading(){
        llProgessBar.setVisibility(View.GONE);
        buttonSend.setVisibility(View.VISIBLE);
    }
}