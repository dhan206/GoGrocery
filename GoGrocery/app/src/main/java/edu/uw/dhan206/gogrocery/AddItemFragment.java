package edu.uw.dhan206.gogrocery;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.EditText;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class AddItemFragment extends DialogFragment {
    private final String TAG = "AddItemFragment";
    private FirebaseAuth mAuth;
    private Place itemPlace;
    private DatabaseReference mDatabase;
    private String listId;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceSate) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                        getActivity().getFragmentManager()
                                .findFragmentById(R.id.place_autocomplete_fragment);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();


        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                Log.i(TAG, "Place: " + place.getName());
                itemPlace = place;
            }

            @Override
            public void onError(Status status) {
                Log.i(TAG, "An error occured: " + status);
            }
        });

        builder.setView(inflater.inflate(R.layout.fragment_add_item, null))
                .setTitle("Add a new item to the list")
                .setPositiveButton("Add Item", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Add item to grocery list
                        Item newItem = new Item();
                        String itemName = ((EditText)getActivity().findViewById(R.id.itemName)).getText().toString();
                        String itemDesc = ((EditText)getActivity().findViewById(R.id.itemDescription)).getText().toString();
                        String addedBy = mAuth.getCurrentUser().getUid();

                        newItem.name = itemName;
                        newItem.description = itemDesc;
                        newItem.addedBy = addedBy;
                        newItem.location = itemPlace;

                        // TODO: change magic string "1" into listId accepeted as parameter from ListActivity
                        mDatabase.child("lists").child("1").child("items").push().setValue(newItem);

                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        AddItemFragment.this.getDialog().cancel();
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }
}