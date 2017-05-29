package edu.uw.dhan206.gogrocery;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;
import android.widget.ListView;
import android.widget.Button;

import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by joseph on 2017-05-22.
 */


public class SettingsFragment extends PreferenceFragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }
}
