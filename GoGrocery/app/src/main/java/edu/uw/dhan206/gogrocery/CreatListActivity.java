package edu.uw.dhan206.gogrocery;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import static android.R.id.edit;

public class CreatListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creat_list);

        // get intent from list view. Store the message, should be either "new" or "edit"
//        if (new) {
//            // hide leave group button
//            findViewById(R.id.createListLeaveGroupButton).setVisibility(View.INVISIBLE);
//
//            // move save group button to bottom of the page
//            findViewById(R.id.createListSaveButton)
//        }
    }
}
