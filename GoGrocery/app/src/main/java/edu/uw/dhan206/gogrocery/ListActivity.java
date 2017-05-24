package edu.uw.dhan206.gogrocery;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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

    FirebaseDatabase database;
    Spinner spinner;
    Map<String, String> lists;
    ArrayAdapter spinnerAdapter;
    ListItemAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
                    Log.v(TAG, listSnapshot.getValue().toString());
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
        fab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                FragmentManager fm = getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                AddItemFragment frag = new AddItemFragment();
                ft.add(R.id.list, frag);
                ft.commit();
            }
        });
    }

    private void showList(String listName) {
        String listId = lists.get(listName);
        DatabaseReference items = database.getReference("lists").child(listId).child("items");

        items.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<Item> itemsList = new ArrayList<>();
                for (DataSnapshot item: dataSnapshot.getChildren()) {
                    Item newItem = new Item();
                    newItem.name = item.child("name").getValue().toString();
                    newItem.description = item.child("description").getValue().toString();
                    newItem.addedBy = item.child("addedBy").getValue().toString();
                    //newItem.location = item.child("location").getValue(Place.class);
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
                // ...
            }
        });
    }


    public class OnSpinnerItemSelected extends Activity implements AdapterView.OnItemSelectedListener {

        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
            // An item was selected. You can retrieve the selected item using
            // parent.getItemAtPosition(pos)
            Log.v(TAG, "spinner item selected");
            showList(parent.getItemAtPosition(pos).toString());
        }

        public void onNothingSelected(AdapterView<?> parent) {
            // Another interface callback
        }
    }

    public class ListItemAdapter extends ArrayAdapter<Item> {
        public ListItemAdapter(Context context, ArrayList<Item> items) {
            super(context, 0, items);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Item item = getItem(position);

            if(convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
            }

            TextView name = (TextView) convertView.findViewById(R.id.list_item_name);
            name.setText(item.name);

            TextView description = (TextView) convertView.findViewById(R.id.list_item_description);
            description.setText(item.description);

            return convertView;
        }
    }
}
