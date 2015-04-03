package fr.isen.shazamphoto.ui.Dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import fr.isen.shazamphoto.database.Monument;
import fr.isen.shazamphoto.utils.FunctionsDB;

public class DeleteDialog extends DialogFragment {

    public DeleteDialog() {
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Retrieve the monument
        Bundle bundle = getArguments();
        final Monument monument = (Monument) bundle.getSerializable(Monument.NAME_SERIALIZABLE);
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Remove this monument from your TAG ?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        FunctionsDB.removeMonumentFromTaggedMonument(monument, getActivity());
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });

        return builder.create();
    }

}
