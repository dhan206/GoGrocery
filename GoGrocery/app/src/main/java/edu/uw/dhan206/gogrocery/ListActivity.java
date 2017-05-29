package edu.uw.dhan206.gogrocery;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class ListActivity extends AppCompatActivity {

    static final String TAG = "ListActivity";

    private FirebaseDatabase database;
    private Spinner spinner;
    private Map<String, String> lists;
    private ArrayAdapter spinnerAdapter;
    private ListItemAdapter adapter;
    private String currentListId;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        database = FirebaseDatabase.getInstance();
        spinner = (Spinner) findViewById(R.id.spinner);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userId = user.getUid();
        DatabaseReference userRef = database.getReference("users").child(userId);
        DatabaseReference userLists = userRef.child("lists");

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setCustomView(R.layout.spinner);
        actionBar.setDisplayShowCustomEnabled(true);
        spinner = (Spinner) actionBar.getCustomView();
        spinner.setOnItemSelectedListener(new OnSpinnerItemSelected());

        userLists.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                lists = new HashMap<String, String>();
                for (DataSnapshot listSnapshot: dataSnapshot.getChildren()) {
                    lists.put(listSnapshot.getKey(), listSnapshot.getValue().toString());
                }

                spinnerAdapter = new ArrayAdapter<String>(ListActivity.this, android.R.layout.simple_spinner_item, new ArrayList<>(lists.keySet()));
                spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(spinnerAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        });



        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.add_item_fab);
        final FragmentManager fm = this.getSupportFragmentManager();

        fab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.v(TAG, "clicked fab");
                Intent addItemIntent = new Intent(ListActivity.this, AddItemActivity.class).putExtra("listId", currentListId);
                startActivity(addItemIntent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.list_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.edit:
                if (currentListId != null) {
                    Intent intent = new Intent(this, CreatListActivity.class);
                    intent.putExtra("Type", "Edit");
                    intent.putExtra("Id", currentListId);
                    startActivity(intent);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showList(String listName) {
        String listId = lists.get(listName);
        currentListId = listId;
        DatabaseReference items = database.getReference("lists").child(listId).child("items");

        items.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<Item> itemsList = new ArrayList<>();
                for (DataSnapshot item: dataSnapshot.getChildren()) {
                    Item newItem = new Item();
                    if (item.child("name").getValue() != null) {
                        newItem.name = item.child("name").getValue().toString();
                    }
                    if (item.child("description").getValue() != null) {
                        newItem.description = item.child("description").getValue().toString();
                    }

                    if (item.child("addedBy").getValue() != null) {
                        newItem.addedBy = item.child("addedBy").getValue().toString();
                    }

                    Object address = item.child("address").getValue();
                    if (address != null) {
                        newItem.address = address.toString();
                        newItem.locationName = item.child("locationName").getValue().toString();
                    }

                    itemsList.add(newItem);
                }

                adapter = new ListItemAdapter(ListActivity.this, itemsList);
                ListView listView = (ListView) findViewById(R.id.list);
                listView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        });
    }


    public class OnSpinnerItemSelected extends Activity implements AdapterView.OnItemSelectedListener{

        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
            showList(parent.getItemAtPosition(pos).toString());
        }

        public void onNothingSelected(AdapterView<?> parent) {}

    }

    public class ListItemAdapter extends ArrayAdapter<Item> {
        public ListItemAdapter(Context context, ArrayList<Item> items) {
            super(context, 0, items);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final Item item = getItem(position);

            if(convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
            }

            TextView name = (TextView) convertView.findViewById(R.id.list_item_name);
            name.setText(item.name);

            if (item.description != null) {
                TextView description = (TextView) convertView.findViewById(R.id.list_item_description);
                description.setText(item.description);
            }

            TextView addedBy = (TextView) convertView.findViewById(R.id.addedBy);
            addedBy.setText(item.addedBy);

            if (item.address != null) {

                TextView locationName = (TextView) convertView.findViewById(R.id.locationName);
                locationName.setText(item.locationName);

                ImageView location = (ImageView) convertView.findViewById(R.id.location);
                location.setVisibility(View.VISIBLE);
                location.setTag(item.address);
                location.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String tag = v.getTag().toString();
                        Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + tag);
                        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                        mapIntent.setPackage("com.google.android.apps.maps");
                        startActivity(mapIntent);
                    }
                });
            }

            convertView.setLongClickable(true);
            convertView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                    alert.setTitle("Alert");
                    alert.setMessage("Would you like to delete this item?");
                    alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // delete
                            Log.i(TAG, "Attempting to delete item - id: " + item.id);
                            database.getReference("lists").child(currentListId).child("items").child(item.id).removeValue();
                            dialog.dismiss();
                        }
                    }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    alert.show();

                    return true;
                }
            });

            return convertView;
        }
    }
}
