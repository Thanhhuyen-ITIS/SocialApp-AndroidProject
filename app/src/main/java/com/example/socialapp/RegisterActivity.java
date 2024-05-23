package com.example.socialapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {
    private EditText username;
    private EditText email;
    private EditText password;
    private EditText confirmPassword;
    private EditText name;

    private Button registerButton;
    private TextView loginUSer;

    ProgressDialog pd;

    private DatabaseReference dReference;
    private FirebaseAuth fAuth;
//    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        username = findViewById(R.id.username);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        name = findViewById(R.id.name);
        confirmPassword = findViewById(R.id.cf_password);

        dReference = FirebaseDatabase.getInstance().getReference();
        fAuth = FirebaseAuth.getInstance();

        pd = new ProgressDialog(this);

        loginUSer = findViewById(R.id.login_user);
        registerButton = findViewById(R.id.register);

        loginUSer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class ));
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String usernameText = username.getText().toString();
                String emailText = email.getText().toString();
                String passwordText = password.getText().toString();
                String nameText = name.getText().toString();
                String confirmPasswordText = confirmPassword.getText().toString();

                if (usernameText.isEmpty() || emailText.isEmpty() || passwordText.isEmpty() || nameText.isEmpty() || confirmPasswordText.isEmpty()) {
                    if (usernameText.isEmpty()) {
                        username.setError("Username is required");
                    }
                    if (emailText.isEmpty()) {
                        email.setError("Email is required");
                    }
                    if (passwordText.isEmpty()) {
                        password.setError("Password is required");
                    }
                    if (nameText.isEmpty()) {
                        name.setError("Name is required");
                    }
                    if (confirmPasswordText.isEmpty()) {
                        confirmPassword.setError("Confirm Password is required");
                    }
                } else {
                    if (passwordText.length() < 6) {
                        password.setError("Password must be at least 6 characters");
                    }
                     else if (!passwordText.equals(confirmPasswordText)) {
                        confirmPassword.setError("Password does not match");
                    } else {
                        registerUSer(usernameText, emailText, passwordText, nameText);
                    }
                }
            }
        });


    }

    private void registerUSer(String usernameText, String emailText, String passwordText, String nameText) {
        pd.setMessage("Please wait...");
        pd.show();
        fAuth.createUserWithEmailAndPassword(emailText, passwordText).addOnSuccessListener(authResult -> {
            HashMap<String, Object> map = new HashMap<>();
            map.put("username", usernameText);
            map.put("email", emailText);
            map.put("name", nameText);
            map.put("id", fAuth.getCurrentUser().getUid());
            map.put("bio", "");
            map.put("imageUrl", "default");
            dReference.child("Users").child(fAuth.getCurrentUser().getUid()).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    pd.dismiss();
                    if (task.isSuccessful()) {
                        Toast.makeText(RegisterActivity.this, "User created successfully", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(RegisterActivity.this, StartActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    }
                }
            });
        }).addOnFailureListener(e -> {
            Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            pd.dismiss();
        });
    }
}