package edu.uw.dhan206.gogrocery;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import static android.R.attr.data;
import static android.R.attr.name;
import static android.R.id.edit;

public class CreatListActivity extends AppCompatActivity {

    private ArrayAdapter<String> adapter;
    private boolean isEditting = false;
    private ArrayList<String> newMembers;

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

        if(isEditting) {
            TextView groceryListSubtext = (TextView) findViewById(R.id.createListNameSubtext);
            groceryListSubtext.setText("Edit name of grocery list");

            Button submitGroceryListButton = (Button) findViewById(R.id.createListSaveButton);
            submitGroceryListButton.setText("Save Grocery List");
            groceryListName.setText("The Test Grocery List");

            for (int i = 0; i < 5; i++) {
                adapter.add("Testing " + i);
            }
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
                    Toast.makeText(CreatListActivity.this, "New member added successfull.",
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
                if (isEditting) {

                } else {
                    for(String member : newMembers) {
                        // add each member to firebase
                    }
                    Toast.makeText(CreatListActivity.this, "All members added successfully.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
