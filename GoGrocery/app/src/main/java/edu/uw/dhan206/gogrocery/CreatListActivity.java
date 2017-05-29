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

import java.net.URLEncoder;
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
    private FirebaseUser user;
    private ArrayList<String> currentMembers;

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
            isEditting = (editType.equalsIgnoreCase("Edit"));
            database = FirebaseDatabase.getInstance();
            user = FirebaseAuth.getInstance().getCurrentUser();
            userID = user.getUid();
            if(isEditting) {
                currentListReference = database.getReference("lists").child(groceryListID);
                currentListReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        currentListName = dataSnapshot.child("name").getValue().toString();
                        if (isEditting) {
                            groceryListName.setText(currentListName);
                            groceryListName.setFocusable(false);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.v(TAG, "The read failed");
                    }
                });
            }
        }


        if(isEditting) {
            TextView groceryListSubtext = (TextView) findViewById(R.id.createListNameSubtext);
            groceryListSubtext.setText("The grocery list currently being edited");
            Button submitGroceryListButton = (Button) findViewById(R.id.createListSaveButton);
            submitGroceryListButton.setVisibility(View.INVISIBLE);

            currentMembers = new ArrayList<String>();

            DatabaseReference listOfUsersInGroceryList = database.getReference("lists").child(groceryListID).child("users");
            listOfUsersInGroceryList.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for(DataSnapshot item: dataSnapshot.getChildren()) {
                        String member = item.getKey().replace("*", ".");
                        currentMembers.add(member);
                        adapter.add(member);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        } else {
            newMembers = new ArrayList<String>();
            findViewById(R.id.createListLeaveGroupButton).setVisibility(View.INVISIBLE);
            adapter.add(user.getEmail());
        }

        Button addMemberButton = (Button) findViewById(R.id.addMemberButton);
        addMemberButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String newMemberEmail = newMemberField.getText().toString();
                if(isEditting) {
                    DatabaseReference userEmails = database.getReference("userEmails");
                    userEmails.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String encodedEmail = "";
                            try {
                                encodedEmail = newMemberEmail.replace(".", "*");
                            } catch (Exception e) {
                                Log.v(TAG, "Error " + e.toString());
                            }
                            Log.v(TAG, "encoded email: " + encodedEmail);
                            if (currentMembers.contains(newMemberEmail)) {
                                Toast.makeText(CreatListActivity.this, "The user " + newMemberEmail + " is already a member of this list.",
                                        Toast.LENGTH_SHORT).show();
                            } else if (dataSnapshot.hasChild(encodedEmail)) {
                                String newUserID = dataSnapshot.child(encodedEmail).getValue().toString();

                                // Add list to user's array of grocery list
                                DatabaseReference userReference = database.getReference("users").child(newUserID).child("lists");
                                userReference.child(currentListName).setValue(groceryListID);

                                // Add user to list's array of users
                                DatabaseReference listReference = database.getReference("lists").child(groceryListID).child("users");
                                listReference.child(encodedEmail).setValue(newUserID);
                                Toast.makeText(CreatListActivity.this, "New member added successfully.",
                                        Toast.LENGTH_SHORT).show();
                                adapter.add(newMemberEmail);
                            } else {
                                Toast.makeText(CreatListActivity.this, "The account " + newMemberEmail + " does not exist.",
                                        Toast.LENGTH_SHORT).show();
                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                } else {
                    newMembers.add(newMemberEmail);
                    adapter.add(newMemberEmail);
                }
                newMemberField.getText().clear();
            }
        });

        Button saveGroceryList = (Button) findViewById(R.id.createListSaveButton);
        saveGroceryList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String newNameInputField = groceryListName.getText().toString();
                if (!newNameInputField.isEmpty()) {
                    // Add new grocery list to array of grocery list(s)
                    final DatabaseReference newList = database.getReference("lists").push();
                    newList.child("name").setValue(newNameInputField);

                    // Add creator to grocery list's array of users
                    final DatabaseReference newListUsers = newList.child("users");
                    String encodedEmail = "";
                    try {
                        encodedEmail = user.getEmail().replace(".", "*");
                    } catch (Exception e) {
                        Log.v(TAG, "Error " + e.toString());
                    }
                    newListUsers.child(encodedEmail).setValue(userID);


                    // Add list to user's array of grocery list(s)
                    DatabaseReference userReference = database.getReference("users").child(userID).child("lists");
                    userReference.child(newNameInputField).setValue(newList.getKey());
                    DatabaseReference userEmails = database.getReference("userEmails");
                    userEmails.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            ArrayList<String> addedMembers = new ArrayList<String>();
                            for (String member : newMembers) {
                                String encodedEmail = "";
                                try {
                                    encodedEmail = member.replace(".", "*");
                                } catch (Exception e) {
                                    Log.v(TAG, "Error " + e.toString());
                                }
                                if (dataSnapshot.hasChild(encodedEmail)) {
                                    String newUserID = dataSnapshot.child(encodedEmail).getValue().toString();

                                    // Add list to user's array of grocery list
                                    DatabaseReference userReference = database.getReference("users").child(newUserID).child("lists");
                                    userReference.child(newNameInputField).setValue(newList.getKey());

                                    // Add user to list's array of users
                                    newListUsers.child(encodedEmail).setValue(newUserID);
                                    addedMembers.add(member);
                                }
                            }
                            Toast.makeText(CreatListActivity.this, "Members: " + addedMembers.toString() + " were added successfully.",
                                    Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                    Intent intent = new Intent(CreatListActivity.this, ListActivity.class);
                    intent.putExtra("groceryListName", newNameInputField);
                    startActivity(intent);
                } else {
                    Toast.makeText(CreatListActivity.this, "Please input a name for the grocery list.",
                            Toast.LENGTH_LONG).show();
                }
            }
        });

        Button leaveGroceryList = (Button) findViewById(R.id.createListLeaveGroupButton);
        leaveGroceryList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentListReference.child("users").child(user.getEmail().replace(".", "*")).removeValue();
                DatabaseReference userReference = database.getReference("users").child(userID).child("lists").child(currentListName);
                userReference.removeValue();
                startActivity(new Intent(CreatListActivity.this, ListActivity.class));
                Toast.makeText(CreatListActivity.this, "You've been successfully removed from the " + currentListName + " list.",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}
