package edu.uw.dhan206.gogrocery;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AddItemActivity extends AppCompatActivity {

    private final String TAG = "AddItemActivity";
    private FirebaseAuth mAuth;
    private Place itemPlace;
    private DatabaseReference mDatabase;
    private FirebaseDatabase mDbInstance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Add Item");

        mAuth = FirebaseAuth.getInstance();

        mDbInstance = FirebaseDatabase.getInstance();
        mDatabase = mDbInstance.getReference();

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

        String listName = getIntent().getExtras().get("listName").toString();
        final Intent backToList = new Intent(AddItemActivity.this, ListActivity.class);
        backToList.putExtra("groceryListName", listName);

        Button confirmAdd = (Button)findViewById(R.id.confirmAddItemButton);
        confirmAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(TAG, "Clicked add item");
                final Item newItem = new Item();
                String itemName = ((EditText)findViewById(R.id.itemName)).getText().toString();
                String itemDesc = ((EditText)findViewById(R.id.itemDescription)).getText().toString();
                String uid = mAuth.getCurrentUser().getUid();

                newItem.name = itemName;
                newItem.description = itemDesc;

                newItem.addedBy = mDatabase.child("users").child(uid).child("name").toString();

                if (itemPlace != null) {
                    newItem.address = itemPlace.getAddress().toString();
                    newItem.locationName = itemPlace.getName().toString();
                }
                final String listId = getIntent().getExtras().get("listId").toString();

                DatabaseReference ref = mDbInstance.getReference("users").child(uid).child("name");
                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        newItem.addedBy = dataSnapshot.getValue().toString();
                        DatabaseReference itemsReference = mDatabase.child("lists").child(listId).child("items").push();
                        if (newItem.name != null && !newItem.name.isEmpty()) {
                            newItem.id = itemsReference.getKey();
                            Item.addItemToDb(itemsReference, newItem);
                            startActivity(backToList);
                        } else {
                            // give user directions to write title
                            
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


            }
        });

        Button cancelAdd = (Button)findViewById(R.id.cancelAddItemButton);
        cancelAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(TAG, "Clicked cancel");
                startActivity(backToList);
            }
        });
    }
}
