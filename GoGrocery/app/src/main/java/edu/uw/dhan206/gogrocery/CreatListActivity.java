package edu.uw.dhan206.gogrocery;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static android.R.attr.data;
import static android.R.attr.name;
import static android.R.attr.type;
import static android.R.id.edit;
import static android.webkit.ConsoleMessage.MessageLevel.LOG;

public class CreatListActivity extends AppCompatActivity {

    private final static String TAG = "CREATELISTACTIVITY";
    private ArrayAdapter<String> adapter;
    private boolean isEditting;
    private ArrayList<String> newMembers;
    private String groceryListID;
    private String editType;
    private FirebaseDatabase database;
    private String userID;
    private String currentListName;
    private DatabaseReference currentListReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creat_list);

        ArrayList<String> data = new ArrayList<String>();

        adapter = new ArrayAdapter<String>(this, R.layout.activity_create_list_item, R.id.createListMemberListItem, data);
        ListView listView = (ListView) findViewById(R.id.memberListView);
        listView.setAdapter(adapter);

        final EditText groceryListName = (EditText) findViewById(R.id.createListName);
        final EditText newMemberField = (EditText) findViewById(R.id.createListNewMemberName);

        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            editType = extras.getString("Type");
            groceryListID = extras.getString("Id");
            isEditting = !(editType.equalsIgnoreCase("Edit"));
            database = FirebaseDatabase.getInstance();
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            userID = user.getUid();
            currentListReference = database.getReference("lists").child(groceryListID);
            currentListReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    currentListName = dataSnapshot.child("name").getValue().toString();
                    if(isEditting) {
                        groceryListName.setText(currentListName);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.v(TAG, "The read failed");
                }
            });
//            Log.v(TAG, currentList.child("name").getValue().toString());
        }


        if(isEditting) {
            TextView groceryListSubtext = (TextView) findViewById(R.id.createListNameSubtext);
            groceryListSubtext.setText("Edit name of grocery list");

            Button submitGroceryListButton = (Button) findViewById(R.id.createListSaveButton);
            submitGroceryListButton.setText("Save Grocery List");


        } else {
            newMembers = new ArrayList<String>();
            findViewById(R.id.createListLeaveGroupButton).setVisibility(View.INVISIBLE);
        }

        Button addMemberButton = (Button) findViewById(R.id.addMemberButton);
        addMemberButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newMemberEmail = newMemberField.getText().toString();
                if(isEditting) {
                    // add member to firebase
                    Toast.makeText(CreatListActivity.this, "New member added successfully.",
                            Toast.LENGTH_SHORT).show();
                } else {
                    newMembers.add(newMemberEmail);
                }
                adapter.add(newMemberEmail);
                newMemberField.getText().clear();
            }
        });

        Button saveGroceryList = (Button) findViewById(R.id.createListSaveButton);
        saveGroceryList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newNameInputField = groceryListName.getText().toString();
                if (!newNameInputField.isEmpty()) {
                    if (isEditting) {
                        if (!currentListName.equals(newNameInputField)) {
                            currentListReference.child("name").setValue(newNameInputField);
                        }
                    } else {
                        DatabaseReference newList = database.getReference("lists").push();
                        newList.child("name").setValue(newNameInputField);
                        newList.child("users");
                        newList.child("items");

                        DatabaseReference userReference = database.getReference("users").child(userID).child("lists");
                        userReference.child(newNameInputField).setValue(newList.getKey());

                        for (String member : newMembers) {

                        }
                        Toast.makeText(CreatListActivity.this, "All members added successfully.",
                                Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(CreatListActivity.this, ListActivity.class));
                        Toast.makeText(CreatListActivity.this, "New List Added",
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(CreatListActivity.this, "Please input a name for the grocery list.",
                            Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
