package com.automato.aigerim.spor.Activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import com.automato.aigerim.spor.Models.User;
import com.automato.aigerim.spor.R;

public class EditingPageActivity extends AppCompatActivity implements View.OnClickListener {

    static final int GALLERY_REQUEST = 1;

    private RelativeLayout changePhoto;
    private ProgressBar progressBar;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private FirebaseDatabase myDatabase;
    private DatabaseReference reference;
    private FirebaseAuth mAuth;
    private ImageView userProfileImage;
    private String userID;
    private User client;
    private String userName;
    private EditText userNameEditText;
    private EditText userEmail;
    private RelativeLayout ChangePass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editing_page);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        myDatabase = FirebaseDatabase.getInstance();
        reference = myDatabase.getReference();
        mAuth = FirebaseAuth.getInstance();
        userID = mAuth.getCurrentUser().getUid();

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        myToolbar.setTitle("Редактирование");
        setSupportActionBar(myToolbar);

        myToolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_left));
        myToolbar.setTitleTextColor(Color.WHITE);
        myToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        changePhoto = (RelativeLayout) findViewById(R.id.change_photo_layout);
        userProfileImage = (ImageView) findViewById(R.id.my_photo);
        userNameEditText = (EditText) findViewById(R.id.edit_text_name);
        userEmail = (EditText) findViewById(R.id.edit_text_email);
        progressBar = (ProgressBar) findViewById(R.id.client_image_progress_bar);
        ChangePass = (RelativeLayout) findViewById(R.id.change_pass_layout);

        ChangePass.setOnClickListener(this);
        changePhoto.setOnClickListener(this);
        userNameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                userName = userNameEditText.getText().toString();
            }
        });

        progressBar.setVisibility(View.VISIBLE);
        loadingClientProfile();
    }

    public void loadingClientProfile() {
        reference.child("users").child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                client = dataSnapshot.getValue(User.class);
                userNameEditText.setText(client.name);
                userEmail.setText(client.email);
                setClientImage();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void setClientImage() {
        if (client.hasImage) {
            storageReference.child("Photos").child(userID).getBytes(1024 * 1024).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    Bitmap image = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    userProfileImage.setImageBitmap(image);
                    progressBar.setVisibility(View.GONE);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(EditingPageActivity.this, "Не могу загрузить фотографию!", Toast.LENGTH_SHORT).show();
                    Log.e("ImageLoadFailure", e.getMessage());
                }
            });
        } else
            progressBar.setVisibility(View.GONE);
    }

    private void changePassClick() {
        Intent intent = new Intent(EditingPageActivity.this, ChangePassActivity.class);
        startActivity(intent);
    }

    private void changeImageClick() {
        progressBar.setVisibility(View.VISIBLE);
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, GALLERY_REQUEST);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.change_photo_layout:
                changeImageClick();
                break;

            case R.id.change_pass_layout:
                changePassClick();
                break;

            default:
                break;
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        DatabaseReference name = myDatabase.getReference("users/" + userID + "/name");
        name.setValue(userName);
        super.onBackPressed();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        switch (requestCode) {
            case GALLERY_REQUEST:
                if (resultCode == RESULT_OK) {
                    final Uri selectedImage = imageReturnedIntent.getData();
                    galleryResponse(selectedImage);
                } else {
                    progressBar.setVisibility(View.GONE);
                }
                break;

            default:
                break;
        }
    }

    private void galleryResponse(final Uri selectedImage) {
        storageReference.child("Photos").child(userID).putFile(selectedImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                try {
                    DatabaseReference hasImage = myDatabase.getReference("users/" + userID + "/hasImage");
                    hasImage.setValue(true);

                    Toast.makeText(EditingPageActivity.this, "Изображение успешно обновлено", Toast.LENGTH_SHORT).show();
                    userProfileImage.setImageURI(selectedImage);
                } catch (Exception e) {
                    Toast.makeText(EditingPageActivity.this, "Произошла ошибка при отправке фотографии", Toast.LENGTH_LONG).show();
                    Log.e("ImageUploadFailure", e.getMessage());
                }
                progressBar.setVisibility(View.GONE);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(EditingPageActivity.this, "Произошла ошибка при отправке фотографии", Toast.LENGTH_LONG).show();
                Log.e("ImageUploadFailure", e.getMessage());
            }
        });
    }
}
