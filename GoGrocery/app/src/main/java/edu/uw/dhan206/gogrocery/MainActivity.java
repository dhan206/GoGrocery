package edu.uw.dhan206.gogrocery;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    private FirebaseAuth mAuth;
    private final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        View signUpButton = findViewById(R.id.signUpButton);
        signUpButton.setOnClickListener(this);
        View logInButton = findViewById(R.id.logInButton);
        logInButton.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case (R.id.signUpButton):
                Intent signup = new Intent(MainActivity.this, CreateAccountActivity.class);
                startActivity(signup);
                // Send to sign-up screen
                break;
            case (R.id.logInButton):
                String emailText = ((EditText)findViewById(R.id.emailText)).getText().toString();
                String passwordText = ((EditText)findViewById(R.id.passwordText)).getText().toString();
                logIn(emailText, passwordText);
                break;
        }
    }

    private void logIn(String email, String password) {
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(MainActivity.this,
                    "Please sign-up or log-in using your credentials.", Toast.LENGTH_SHORT).show();

            return;
        }

        final ProgressDialog dialog = new ProgressDialog(MainActivity.this);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage("Attempting to log you in. Please wait...");
        dialog.setIndeterminate(true);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        dialog.dismiss();
                        if (task.isSuccessful()) {
//                            // Sign in success, update UI with the information.
//                            Log.v(TAG, "signInWithEmail:success");
//                            Toast.makeText(MainActivity.this, "Log-In was successful.",
//                                    Toast.LENGTH_LONG).show();
//                            // Update UI
                            Intent login = new Intent(MainActivity.this, ListActivity.class);
                            startActivity(login);
                        } else {
                            // Sign-in failure, display a message to the user.
//                            Log.v(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(MainActivity.this, "We could not log you in with the given credentials.",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}
