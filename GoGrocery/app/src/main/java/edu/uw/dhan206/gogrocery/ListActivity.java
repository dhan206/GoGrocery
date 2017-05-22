package edu.uw.dhan206.gogrocery;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class ListActivity extends AppCompatActivity {

    static final String TAG = "ListActivity";

    FirebaseDatabase database;
    Spinner spinner;

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
                ArrayList<String> lists = new ArrayList<String>();
                for (DataSnapshot listSnapshot: dataSnapshot.getChildren()) {
                    lists.add(listSnapshot.getKey());
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(ListActivity.this, android.R.layout.simple_spinner_item, lists);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(adapter);
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
        }

        public void onNothingSelected(AdapterView<?> parent) {
            // Another interface callback
        }
    }
}
