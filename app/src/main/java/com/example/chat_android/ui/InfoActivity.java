package com.example.chat_android.ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.chat_android.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Objects;

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

    StorageReference storageReference;

    private static final int IMAGE_REQUEST = 1;
    private Uri imageUri;
    private StorageTask<UploadTask.TaskSnapshot> uploadTask;

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

//        profile_image.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                openImage();
//            }
//        });

       btn_confirm.setOnClickListener(v -> {
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

    private void openImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, IMAGE_REQUEST);
    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = InfoActivity.this.getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void uploadImage(){
        final ProgressDialog progressDialog = new ProgressDialog(InfoActivity.this);
        progressDialog.setMessage("Uploading...");
        progressDialog.show();

        if (imageUri !=null) {
            final StorageReference fileReference = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(imageUri));
            uploadTask = fileReference.putFile(imageUri);
            uploadTask.continueWithTask(task -> {
                if (!task.isSuccessful()){
                    throw Objects.requireNonNull(task.getException());
                }
                return fileReference.getDownloadUrl();
            }).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    String mUri = downloadUri.toString();

                    reference = FirebaseDatabase.getInstance().getReference("Info").child(firebaseUser.getUid());

                    HashMap<String, Object> map = new HashMap<>();
                    map.put("imageURL",mUri);
                    reference.updateChildren(map);
                } else {
                    Toast.makeText(InfoActivity.this, "Failed", Toast.LENGTH_LONG).show();
                }
                progressDialog.dismiss();
            }).addOnFailureListener(e -> {
                Toast.makeText(InfoActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            });
        } else {
            Toast.makeText(InfoActivity.this, "No image selected", Toast.LENGTH_SHORT).show();

        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null){
            imageUri = data.getData();
            if (uploadTask !=null && uploadTask.isInProgress()) {
                Toast.makeText(InfoActivity.this, "Upload image successful", Toast.LENGTH_SHORT).show();
            } else {
                uploadImage();
            }
        }
    }
}
