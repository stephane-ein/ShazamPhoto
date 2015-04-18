package fr.isen.shazamphoto.ui;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import fr.isen.shazamphoto.R;
import fr.isen.shazamphoto.database.Localization;
import fr.isen.shazamphoto.database.Monument;
import fr.isen.shazamphoto.utils.AddMonument;
import fr.isen.shazamphoto.utils.Images.LoadPicture;


public class AddMonumentFragment extends Fragment {

    private Monument monument;

    public static AddMonumentFragment newInstance() {
        AddMonumentFragment fragment = new AddMonumentFragment();
        return fragment;
    }

    public AddMonumentFragment() {
        // Required empty public constructor
        monument = new Monument();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        monument = (Monument) getArguments().getSerializable(Monument.NAME_SERIALIZABLE);
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        getActivity().setTitle("New monument found");
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_monument, container, false);

        // Add the listener to close the keyboard
        final EditText editTextName = (EditText) view.findViewById(R.id.editText_name_monument);
        addListenerCloseKeyboard(editTextName);
        if(monument.getName() != null && !monument.getName().isEmpty()){
            editTextName.setText(monument.getName());
        }
        final EditText editTextDate = (EditText) view.findViewById(R.id.editText_date_monument);
        addListenerCloseKeyboard(editTextDate);
        final EditText editTextDescription = (EditText) view.findViewById(R.id.editText_description_monument);
        addListenerCloseKeyboard(editTextDescription);

        // Set the picture
        ImageView imageView = (ImageView) view.findViewById(R.id.fam_imageview_monument);
        LoadPicture.setPictureFromFile(monument.getPhotoPath(), imageView, LoadPicture.getHdpiWidthVertical(getActivity()), LoadPicture.getHdpiHeightVertical(getActivity()));

        //Set the listener for the ADD button
        Button button = (Button) view.findViewById(R.id.button_add);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddMonument addMonument = new AddMonument();
                monument.setName(editTextName.getText().toString());
                monument.setDescription(editTextDescription.getText().toString());

                String year = editTextDate.getText().toString();
                if(isInteger(year)){
                    monument.setYear(Integer.valueOf(year));
                }else{
                    monument.setYear(0);
                }

                monument.setNbLike(0);
                monument.setNbVisitors(1);
                if(monument.getLocalization() == null){
                    monument.setLocalization(new Localization(0, 0.0, 0.0));
                    Log.v("Shazam", "AMF Localization was null");
                }
                addMonument.execute(monument);
                getActivity().finish();
            }
        });
        return view;
    }

    public void addListenerCloseKeyboard(final EditText editText) {

        if (editText != null) {
            editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_GO) {
                        //Close the keyboard
                        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(
                                Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);

                    }
                    return false;
                }
            });
        }
    }

    public boolean isInteger(String s) {
        try {
            Integer.parseInt(s);
        } catch(NumberFormatException e) {
            return false;
        }
        // only got here if we didn't return false
        return true;
    }


}

