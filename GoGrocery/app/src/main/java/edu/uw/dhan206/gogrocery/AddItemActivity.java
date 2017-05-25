package edu.uw.dhan206.gogrocery;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddItemActivity extends AppCompatActivity {

    private final String TAG = "AddItemActivity";
    private FirebaseAuth mAuth;
    private Place itemPlace;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                Log.i(TAG, "Place: " + place.getName());
                itemPlace = place;
            }

            @Override
            public void onError(Status status) {
                Log.e(TAG, "Place could not be selected due to error: " + status);
            }
        });

        Button confirmAdd = (Button)findViewById(R.id.confirmAddItemButton);
        confirmAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(TAG, "Clicked add item");
                Item newItem = new Item();
                String itemName = ((EditText)findViewById(R.id.itemName)).getText().toString();
                String itemDesc = ((EditText)findViewById(R.id.itemDescription)).getText().toString();
                String addedBy = mAuth.getCurrentUser().getUid();

                newItem.name = itemName;
                newItem.description = itemDesc;
                newItem.addedBy = addedBy;
                newItem.address = itemPlace.getAddress().toString();
                newItem.locationName = itemPlace.getName().toString();
                DatabaseReference itemsReference = mDatabase.child("lists").child("1").child("items").push();

                Item.addItemToDb(itemsReference, newItem);
            }
        });

        Button cancelAdd = (Button)findViewById(R.id.cancelAddItemButton);
        cancelAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(TAG, "Clicked cancel");
            }
        });

    }
}
