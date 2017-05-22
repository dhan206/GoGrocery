package edu.uw.dhan206.gogrocery;

import android.content.DialogInterface;
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
                    mAuth.createUserWithEmailAndPassword(accountEmail, passwordFirst)
                            .addOnCompleteListener(CreateAccountActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(task.isSuccessful()) {
                                        FirebaseUser user = mAuth.getCurrentUser();
//                                        updateUI(user);

                                        Toast.makeText(CreateAccountActivity.this, "Account successfully created.",
                                                Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(CreateAccountActivity.this, "Account creation failed.",
                                                Toast.LENGTH_SHORT).show();
//                                        updateUI(null);
                                    }
                                }
                            });

                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(CreateAccountActivity.this);
                    builder.setTitle("Passwords Do Not Match.")
                            .setMessage("Please try again.")
                            .setPositiveButton("RETRY", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // Log.v(TAG, "Close dialog");
                                }
                            });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }
        });
    }
}
