package com.example.chat_android.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import com.example.chat_android.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.HashMap;
import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {


    TextInputEditText username, email, password;
    TextInputEditText re_password;
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
        Objects.requireNonNull(getSupportActionBar()).setTitle("Register");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        login = (TextView) findViewById(R.id.login);
        username = (TextInputEditText) findViewById(R.id.username);
        re_password = (TextInputEditText) findViewById(R.id.re_password);
        password = (TextInputEditText) findViewById(R.id.password);
        email = (TextInputEditText) findViewById(R.id.email);
        btn_register = (Button) findViewById(R.id.btn_register);

        auth = FirebaseAuth.getInstance();

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            }
        });

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String txt_username = Objects.requireNonNull(username.getText()).toString();
                String txt_email = Objects.requireNonNull(email.getText()).toString();
                String txt_password = Objects.requireNonNull(password.getText()).toString();
                String txt_re_password = Objects.requireNonNull(re_password.getText()).toString();
                if (TextUtils.isEmpty(txt_username) || TextUtils.isEmpty(txt_email) || TextUtils.isEmpty(txt_password)){
                    if (TextUtils.isEmpty(txt_username)) {
                        username.setError("Username is required");
                    }
                    if (TextUtils.isEmpty(txt_email)) {
                        email.setError("Email is required");
                    }
                    if (TextUtils.isEmpty(txt_password)) {
                        password.setError("Password is required");
                    }
                } else if (txt_password.length() < 6) {
                    password.setError("Password must be least at 6 characters");}
//                } else if (txt_re_password.equals(txt_password)) {
//                    re_password.setError("Re-Password must like password ");
//                }
                else {
                    register(txt_username, txt_email, txt_password);
                }
            }

        });

    }

    private void register(String username, String email, String password){
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
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

                            reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        Log.d("fix", email);
                                        Intent intent = new Intent(RegisterActivity.this,LoginActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                        Toast.makeText(RegisterActivity.this, "Register successfully", Toast.LENGTH_LONG ).show();
                                    }
                                }
                            });
                        } else {
                            Toast.makeText(RegisterActivity.this, "You can't register with with this email or password", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}