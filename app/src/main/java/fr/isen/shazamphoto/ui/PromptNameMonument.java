package fr.isen.shazamphoto.ui;

import android.content.Context;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import fr.isen.shazamphoto.R;
import fr.isen.shazamphoto.database.Localization;
import fr.isen.shazamphoto.database.Monument;
import fr.isen.shazamphoto.events.EventLocalizationFound;
import fr.isen.shazamphoto.model.ModelNavigation;
import fr.isen.shazamphoto.ui.CustomAdapter.NearestListAdapter;
import fr.isen.shazamphoto.ui.ItemUtils.SearchLocalizationItem;
import fr.isen.shazamphoto.ui.ItemUtils.SearchMonumentsByLocalization;
import fr.isen.shazamphoto.ui.ItemUtils.SearchableItem;
import fr.isen.shazamphoto.utils.GetMonumentTask.GetMonumentByLocalization;
import fr.isen.shazamphoto.utils.GetMonumentTask.GetMonumentsByName;

public class PromptNameMonument extends Fragment implements SearchableItem, SearchMonumentsByLocalization, SearchLocalizationItem {

    private Monument monument;
    private String monumentName;
    private ListView listView;
    private LocateManager locateManager;

    public PromptNameMonument() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        View rootView = inflater.inflate(R.layout.fragment_prompt_name_monument, container, false);
        monument = (Monument) getArguments().getSerializable(Monument.NAME_SERIALIZABLE);
        listView = (ListView) rootView.findViewById(R.id.fpnm_lv_nearest_monument);

        final PromptNameMonument promptNameMonument = this;
        final EditText editText = (EditText) rootView.findViewById(R.id.editText_prompname_monument);
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_GO) {
                    //Close the keyboard
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(
                            Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);

                    // Ask the server if the monument already exist
                    // If yes, we add more information about the descriptor of the monument
                    monumentName = v.getText().toString();
                    GetMonumentsByName getMonumentsByName = new GetMonumentsByName(null, getActivity(),promptNameMonument,
                            v.getText().toString());
                    getMonumentsByName.execute();

                }
                return false;
            }
        });

        // Check if the monument retrieve has a localization
        if(monument.getLocalization() != null){
            GetMonumentByLocalization getMonumentByLocalization = new GetMonumentByLocalization(this, null, getActivity(),
                    monument.getLocalization().getLatitude(), monument.getLocalization().getLongitude());
            getMonumentByLocalization.execute();
        }else{
            // If not, we listen to the network
            locateManager = new LocateManager( (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE), this);
            locateManager.startListening();
        }
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

            getActivity().finish();
        }
    }

    @Override
    public void monumentsFoundByLocalization(ArrayList<Monument> monuments) {
        NearestListAdapter adapter = new NearestListAdapter(getActivity(), monuments,  monument.getLocalization());
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void foundLocalization(EventLocalizationFound eventLocalizationFound) {
        // Localization has been retrieved thanks to the network
        locateManager.stopListening();
        Localization localization1 = eventLocalizationFound.getLocalization();
        GetMonumentByLocalization getMonumentByLocalization = new GetMonumentByLocalization(this, null, getActivity(),
                localization1.getLatitude(),localization1.getLongitude());
        getMonumentByLocalization.execute();
    }
}