package edu.uw.dhan206.gogrocery;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class CreateAccountActivity extends AppCompatActivity {

    private final static String TAG = "CreateAccountActivity";
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        mAuth = FirebaseAuth.getInstance();

        Button createAccount = (Button) findViewById(R.id.createAccountCreateButton);
        createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String accountEmail = ((EditText) findViewById(R.id.createAccountEmail)).getText().toString();
                String accountName = ((EditText) findViewById(R.id.createAccountName)).getText().toString();
                String passwordFirst = ((EditText) findViewById(R.id.createAccountPasswordFieldFirst)).getText().toString();
                String passwordSecond = ((EditText) findViewById(R.id.createAccountPasswordFieldSecond)).getText().toString();
                if(passwordFirst.equals(passwordSecond)) {
                    final ProgressDialog dialog = new ProgressDialog(CreateAccountActivity.this);
                    dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    dialog.setMessage("Attemtping to create your account. Please wait...");
                    dialog.setIndeterminate(true);
                    dialog.setCanceledOnTouchOutside(false);
                    dialog.show();
                    mAuth.createUserWithEmailAndPassword(accountEmail, passwordFirst)
                            .addOnCompleteListener(CreateAccountActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    dialog.dismiss();
                                    if(task.isSuccessful()) {
                                        FirebaseUser user = mAuth.getCurrentUser();
                                        startActivity(new Intent(CreateAccountActivity.this, ListActivity.class));

                                        Toast.makeText(CreateAccountActivity.this, "Account successfully created.",
                                                Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(CreateAccountActivity.this, "Account creation failed. Please try another email/password combination.",
                                                Toast.LENGTH_LONG).show();
                                    }
                                }
                            });

                } else if (!passwordFirst.equals(passwordSecond)){
                    Toast.makeText(CreateAccountActivity.this, "Passwords do not match. Please try again.",
                            Toast.LENGTH_LONG).show();
                } else if (accountName.isEmpty()){
                    Toast.makeText(CreateAccountActivity.this, "Please enter an email.",
                            Toast.LENGTH_LONG).show();
                    findViewById(R.id.createAccountEmail).requestFocus();
                } else if (passwordFirst.isEmpty() || passwordSecond.isEmpty()) {
                    Toast.makeText(CreateAccountActivity.this, "Please enter a password.",
                            Toast.LENGTH_LONG).show();
                } else if (accountName.isEmpty()) {
                    Toast.makeText(CreateAccountActivity.this, "Please enter your name.",
                            Toast.LENGTH_LONG).show();
                    findViewById(R.id.createAccountName).requestFocus();
                }
            }
        });
    }
}
