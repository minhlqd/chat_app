package com.example.chat_android.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import com.example.chat_android.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Objects;

@SuppressWarnings("ALL")
public class RegisterActivity extends AppCompatActivity {

    TextInputEditText username, email, password;
    Button btn_register;
    TextView login;

    FirebaseAuth auth;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.register);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        login = (TextView) findViewById(R.id.login);
        username = (TextInputEditText) findViewById(R.id.username);
        password = (TextInputEditText) findViewById(R.id.password);
        email = (TextInputEditText) findViewById(R.id.email);
        btn_register = (Button) findViewById(R.id.btn_register);

        auth = FirebaseAuth.getInstance();

        login.setOnClickListener(view -> startActivity(new Intent(RegisterActivity.this, LoginActivity.class)));

        btn_register.setOnClickListener(v -> {
            String txt_username = username.getText().toString();
            String txt_email = email.getText().toString();
            String txt_password = password.getText().toString();
            if (TextUtils.isEmpty(txt_username) || TextUtils.isEmpty(txt_email) || TextUtils.isEmpty(txt_password)){
                username.setError("All fields required");
            } else if (txt_password.length() < 6) {
                password.setError("Password must be least at 6 characters");
            }
            else {
                register(txt_username, txt_email, txt_password);
            }
        });

    }

    private void register(String username, String email, String password){
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("fix", email);
                        FirebaseUser firebaseUser = auth.getCurrentUser();
                        assert firebaseUser != null;
                        String user_id = firebaseUser.getUid();

                        reference = FirebaseDatabase.getInstance().getReference("Users").child(user_id);

                        HashMap<String, String> hashMap = new HashMap<>();
                        hashMap.put("id", user_id);
                        hashMap.put("username", username);
                        hashMap.put("imageURL", "default");
                        hashMap.put("status", "offline");
                        hashMap.put("search", username.toLowerCase());

                        reference.setValue(hashMap).addOnCompleteListener(task1 -> {
                            if (task1.isSuccessful()){
                                Log.d("fix", email);
                                Intent intent = new Intent(RegisterActivity.this,InfoActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                Toast.makeText(RegisterActivity.this, "Register successfully", Toast.LENGTH_LONG ).show();
                            } else {
                                Toast.makeText(RegisterActivity.this, "Signing failed,", Toast.LENGTH_LONG).show();
                                Log.d("fix", String.valueOf(task1.isSuccessful()));
                            }
                        });
                    } else {
                        Log.d("fix", String.valueOf(task.isSuccessful()));
                    }
                }).addOnFailureListener(Throwable::getMessage);
    }
}