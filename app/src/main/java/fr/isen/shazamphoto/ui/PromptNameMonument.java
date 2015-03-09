package fr.isen.shazamphoto.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import fr.isen.shazamphoto.R;
import fr.isen.shazamphoto.events.SearchMonumentUnidentified;
import fr.isen.shazamphoto.utils.GetMonumentSearch;

public class PromptNameMonument  extends Fragment {

    public PromptNameMonument() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_prompt_name_monument, container, false);
        Button button = (Button) rootView.findViewById(R.id.button_no);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });

        final EditText editText = (EditText) rootView.findViewById(R.id.editText_prompname_monument);
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_GO){
                    //Close the keyboard
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(
                            Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);

                    // Ask the server if the monument already exist
                    // If yes, we add more information about the descriptor of the monument

                   // GetMonumentSearch getMonumentSearch = new GetMonumentSearch(new SearchMonumentUnidentified((UnidentifiedMonument)getActivity()));
                   // getMonumentSearch.execute(v.getText().toString());
                }
                return false;
            }
        });
        return rootView;
    }
}