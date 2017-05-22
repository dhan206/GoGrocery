package edu.uw.dhan206.gogrocery;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        View signUpButton = findViewById(R.id.signUpButton);
        signUpButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case (R.id.signUpButton):
                Intent signup = new Intent(MainActivity.this, CreateAccountActivity.class);
                startActivity(signup);
                break;
        }
    }
}
