package com.example.chat_android.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.chat_android.Models.Info;
import com.example.chat_android.Models.User;
import com.example.chat_android.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import org.jetbrains.annotations.NotNull;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

@SuppressWarnings("ALL")
public class ProfileActivity extends AppCompatActivity {

    CircleImageView profile_image;
    CircleImageView edit;
    TextView username;
    TextView name;
    TextView email;
    TextView phone;
    TextView date;

    DatabaseReference reference;
    FirebaseUser firebaseUser;
    FirebaseAuth auth;

    StorageReference storageReference;

    private static final int IMAGE_REQUEST = 1;
    private Uri imageUri;
    private StorageTask<UploadTask.TaskSnapshot> uploadTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.profile);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        profile_image = findViewById(R.id.profile_image);
        username = findViewById(R.id.username);
        edit = findViewById(R.id.edit);
        name = findViewById(R.id.fullName);
        email = findViewById(R.id.email);
        phone = findViewById(R.id.phone_number);
        date = findViewById(R.id.dateOfBird);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

        storageReference = FirebaseStorage.getInstance().getReference("Images");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);

                assert user != null;
                username.setText(user.getUsername());

                if (user.getImageURL().equals("default")) {
                    profile_image.setImageResource(R.drawable.ic_name);
                } else {
                    profile_image.setImageResource(R.drawable.ic_name);
                    //Glide.with(ProfileActivity.this).load(user.getImageURL()).into(profile_image);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        reference = FirebaseDatabase.getInstance().getReference("Info").child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Info info = snapshot.getValue(Info.class);
                if (info != null) {
                    name.setText(info.getName());
                    email.setText(info.getEmail());
                    phone.setText(info.getPhone());
                    Log.d("aaa", info.getPhone().toString());
                    date.setText((CharSequence) info.getDate());
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });


        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogUsername();
            }
        });

        profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImage();
            }
        });
    }

    private void DialogUsername() {
        Dialog dialog = new Dialog(this);
        dialog.setTitle("Username");
        dialog.setContentView(R.layout.dialog_edit_profile);
        dialog.setCanceledOnTouchOutside(false);

        EditText username_edit = dialog.findViewById(R.id.username_edit);
        EditText name_edit = dialog.findViewById(R.id.fullName);
        EditText email_edit = dialog.findViewById(R.id.email);
        EditText phone_edit = dialog.findViewById(R.id.phone_number);
        EditText date_edit = dialog.findViewById(R.id.dateOfBird);
        Button confirm = dialog.findViewById(R.id.confirm);
        Button cancel = dialog.findViewById(R.id.cancel);


        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);

                assert user != null;
                username_edit.setText(user.getUsername());

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        reference =  FirebaseDatabase.getInstance().getReference("Info").child(firebaseUser.getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                Info info =  snapshot.getValue(Info.class);

                name_edit.setText(info.getName());
                email_edit.setText(info.getEmail());
                phone_edit.setText(info.getPhone());
                date_edit.setText((CharSequence) info.getDate());
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });


        cancel.setOnClickListener(v -> {
            dialog.dismiss();
        });
        confirm.setOnClickListener(v -> {
            firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            String edit_user = username_edit.getText().toString();
            reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("username", edit_user);
            hashMap.put("search", edit_user.toLowerCase().toString());
            reference.updateChildren(hashMap);

            date_edit.setOnClickListener(v1 -> {
                Calendar calendar = Calendar.getInstance();
                int day = calendar.get(Calendar.DATE);
                int month = calendar.get(Calendar.MONTH);
                int year = calendar.get(Calendar.YEAR);
                DatePickerDialog datePickerDialog = new DatePickerDialog(ProfileActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        calendar.set(year,month,dayOfMonth);
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd//MM//YYYY");
                        date_edit.setText(simpleDateFormat.format(calendar.getTime()));
                    }
                },year,month,day);
            });

            String txt_name = name_edit.getText().toString();
            String txt_date = date_edit.getText().toString();
            String txt_email = email_edit.getText().toString();
            String txt_phone = phone_edit.getText().toString();

            reference = FirebaseDatabase.getInstance().getReference("Info").child(firebaseUser.getUid());
            HashMap<String, Object> hashMapInfo = new HashMap<>();
            hashMapInfo.put("name", txt_name);
            hashMapInfo.put("dateOfBird", txt_date);
            hashMapInfo.put("email", txt_email);
            hashMapInfo.put("phone", txt_phone);
            reference.updateChildren(hashMapInfo);

            dialog.dismiss();
        });
        dialog.show();
    }

    private void openImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, IMAGE_REQUEST);
    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = ProfileActivity.this.getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void uploadImage(){
        final ProgressDialog progressDialog = new ProgressDialog(ProfileActivity.this);
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
                    assert downloadUri != null;
                    String mUri = downloadUri.toString();

                    reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

                    HashMap<String, Object> map = new HashMap<>();
                    map.put("imageURL",mUri);
                    reference.updateChildren(map);
                } else {
                    Toast.makeText(ProfileActivity.this, "Failed", Toast.LENGTH_LONG).show();
                }
                progressDialog.dismiss();
            }).addOnFailureListener(e -> {
                Toast.makeText(ProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            });
        } else {
            Toast.makeText(ProfileActivity.this, "No image selected", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null){
            imageUri = data.getData();
            if (uploadTask !=null && uploadTask.isInProgress()) {
                Toast.makeText(ProfileActivity.this, "Upload image successful", Toast.LENGTH_SHORT).show();
            } else {
                uploadImage();
            }
        }
    }
}