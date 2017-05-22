package edu.uw.dhan206.gogrocery;

import android.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class CreateAccountActivity extends AppCompatActivity {

    private final static String TAG = "CreateAccountActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        Button createAccount = (Button) findViewById(R.id.createAccountCreateButton);
        createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String accountEmail = ((EditText) findViewById(R.id.createAccountEmail)).getText().toString();
                String passwordFirst = ((EditText) findViewById(R.id.createAccountPasswordFieldFirst)).getText().toString();
                String passwordSecond = ((EditText) findViewById(R.id.createAccountPasswordFieldSecond)).getText().toString();

                if(passwordFirst.equals(passwordSecond)) {
                    Log.v(TAG, "passwords match");
                } else {
                    Log.v(TAG, "passwords DONT match");
//                    AlertDialog.Builder builder = new AlertDialog.Builder(getBaseContext());
//                    builder.setTitle("Passwords Do Not Match.");
//                    builder.setMessage("Please try again.");
//                    AlertDialog dialog = builder.create();
//                    dialog.show();
                }
            }
        });
    }
}
