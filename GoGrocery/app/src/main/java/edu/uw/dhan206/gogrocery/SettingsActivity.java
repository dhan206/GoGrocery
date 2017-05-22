package edu.uw.dhan206.gogrocery;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by joseph on 2017-05-22.
 */

public class SettingsActivity extends AppCompatActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        android.app.FragmentManager fm = getFragmentManager();
        android.app.FragmentTransaction ft = fm.beginTransaction();

        SettingsFragment settingsFragment = new SettingsFragment();

        ft.replace(android.R.id.content, settingsFragment, "SettingsFragment");
        ft.commit();
    }
}
