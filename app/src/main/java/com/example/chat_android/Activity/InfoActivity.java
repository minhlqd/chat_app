package com.example.chat_android.Activity;

import androidx.appcompat.app.AppCompatActivity;

import com.example.chat_android.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Calendar;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class InfoActivity extends AppCompatActivity {

    EditText fullName;
    EditText email;
    EditText phone;
    EditText dateOfBird;
    Button btn_confirm;

    CircleImageView profile_image;

    DatabaseReference reference;
    FirebaseDatabase firebaseDatabase;
    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        fullName = findViewById(R.id.fullName);
        dateOfBird = findViewById(R.id.dateOfBird);
        email = findViewById(R.id.email);
        phone = findViewById(R.id.phone_number);

        btn_confirm = findViewById(R.id.btn_confirm);
        profile_image = findViewById(R.id.profile_image);

        dateOfBird.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                date();
            }
        });


       btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String txt_name = fullName.getText().toString();
                String txt_date = dateOfBird.getText().toString();
                String txt_email = email.getText().toString();
                String txt_phone = phone.getText().toString();

                firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                reference = FirebaseDatabase.getInstance().getReference("Info").child(firebaseUser.getUid());

                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("name", txt_name);
                hashMap.put("DateOfBird", txt_date);
                hashMap.put("email", txt_email);
                hashMap.put("phone", txt_phone);

                reference.updateChildren(hashMap);

                Intent intent = new Intent(InfoActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });


    }

    private void date() {
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DATE);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);

        DatePickerDialog datePickerDialog = new DatePickerDialog(InfoActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            }
        }, day, month, year);
    }
}