package spor.automato.com.sporprojecttest.Activity;

import android.app.DialogFragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.soundcloud.android.crop.Crop;

import java.io.FileNotFoundException;
import java.io.InputStream;

import spor.automato.com.sporprojecttest.R;
import spor.automato.com.sporprojecttest.fragments.DatePickerFragment;
import spor.automato.com.sporprojecttest.models.User;

public class RegistrationActivity extends BaseActivity  implements View.OnClickListener{

    private EditText mEmailField;
    private EditText mPasswordField;
    private EditText mName;
    private EditText mPasswordRepeat;
    ImageView imageCrop;
    private String gender;
    private FirebaseAuth mAuth;
    private StorageReference mStorage;
    private Uri imageUriCrop = null;
    private Uri imageUriFull = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        mStorage = FirebaseStorage.getInstance().getReference();

        imageUriCrop = null;


        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final Drawable upArrow = ContextCompat.getDrawable(this, R.drawable.back_white);
        upArrow.setColorFilter(ContextCompat.getColor(this, R.color.grey_100), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);


        mEmailField = (EditText) findViewById(R.id.field_email);
        mPasswordField = (EditText) findViewById(R.id.field_password);
        mName = (EditText) findViewById(R.id.field_name);
        mPasswordRepeat = (EditText) findViewById(R.id.field_password_confirm);
        gender="";

        findViewById(R.id.email_sign_in_button).setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        switch(v.getId()){
            case R.id.datePicker:

                DialogFragment newFragment = new DatePickerFragment();
                newFragment.show(getFragmentManager(),"datePicker");
                break;
            case R.id.email_sign_in_button:
                boolean valid = true;

                TextView birthday = (TextView)findViewById(R.id.datePicker);
                if(mEmailField.getText().toString().equals("")){
                    mEmailField.setError(getString(R.string.required));
                    valid = false;
                }
                if(mPasswordField.getText().toString().equals("") ){
                    mPasswordField.setError(getString(R.string.required));
                    valid = false;
                }
                if(mName.getText().toString().equals("")){
                    mName.setError(getString(R.string.required));
                    valid = false;
                }
                if(mPasswordRepeat.getText().toString().equals("")) {
                    mPasswordRepeat.setError(getString(R.string.required));
                    valid = false;
                    mPasswordRepeat.setError(getString(R.string.required));
                }

                if(gender.equals("")){
                    Toast.makeText(RegistrationActivity.this, R.string.required_gender, Toast.LENGTH_SHORT).show();
                    valid = false;
                }
                if(birthday.getText().toString().equals("")){
                    Toast.makeText(RegistrationActivity.this, R.string.required_birthday, Toast.LENGTH_SHORT).show();
                    valid = false;
                }

                if(!mPasswordField.getText().toString().equals( mPasswordRepeat.getText().toString())){
                    Toast.makeText(RegistrationActivity.this, R.string.mismatch, Toast.LENGTH_SHORT).show();
                    valid = false;
                }
                else if(mPasswordField.getText().toString().length()<6 || mPasswordRepeat.getText().toString().length()<6 ){
                    Toast.makeText(RegistrationActivity.this, R.string.password_length_min, Toast.LENGTH_SHORT).show();
                    valid = false;
                }

                if(valid == true){
                    createAccount(
                            mName.getText().toString(),
                            mEmailField.getText().toString(),
                            mPasswordField.getText().toString(),
                            birthday.getText().toString(),
                            gender);
                }
                break;
            case R.id.female_wrapper:
                TextView text = (TextView)findViewById(R.id.female_text);
                ImageView image = (ImageView)findViewById(R.id.female_image);
                text.setTextColor(ContextCompat.getColor(this, R.color.orange));
                image.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.female_or));

                text = (TextView)findViewById(R.id.male_text);
                image = (ImageView)findViewById(R.id.male_image);
                text.setTextColor(ContextCompat.getColor(this, R.color.black));
                image.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.male));

                gender = "female";

                break;
            case R.id.male_wrapper:
                TextView textMale = (TextView)findViewById(R.id.male_text);
                ImageView imageMale = (ImageView)findViewById(R.id.male_image);
                textMale.setTextColor(ContextCompat.getColor(this, R.color.orange));
                imageMale.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.male_or));

                textMale = (TextView)findViewById(R.id.female_text);
                imageMale = (ImageView)findViewById(R.id.female_image);
                textMale.setTextColor(ContextCompat.getColor(this, R.color.black));
                imageMale.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.female));

                gender = "male";
                break;
            case R.id.upload_photo_imageview:

                imageCrop = (ImageView) findViewById(R.id.upload_photo_imageview);
                Crop.pickImage(this);
                break;
        }
    }

    private void createAccount(final String name,
                               final String email,
                               final String password,
                               final String birthday,
                               final String gender) {

        Log.d("info", "createAccount:" + email);
        if (!validateForm()) {
            return;
        }

        showProgressDialog();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            Log.d("info", "createUserWithEmail:success");
                            final FirebaseUser user = mAuth.getCurrentUser();

                            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("users");
                            final String userId = user.getUid();
                            User p1 = new User(userId, name, user.getEmail(), birthday,gender );
                            mDatabase.child(userId).setValue(p1);

                            if(imageUriCrop!=null){
                                final StorageReference filepath = mStorage.child("Photos").child(userId);
                                filepath.putFile(imageUriCrop).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                        final long ONE_MEGABYTE = 1024 * 1024;
                                        filepath.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                            @Override
                                            public void onSuccess(byte[] bytes) {
                                                DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("users");
                                                final StorageReference filepath = mStorage.child("Photos").child(userId);

                                                mDatabase.child(userId+"/hasImage").setValue(true);

                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception exception) {
                                            }
                                        });

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(RegistrationActivity.this, "Photo was NOT uploaded!", Toast.LENGTH_LONG).show();
                                    }
                                });

                            }
                            if(imageUriFull!=null){
                                final StorageReference filepath = mStorage.child("Photos").child(userId+"-full");
                                filepath.putFile(imageUriFull);
                            }
                            sendEmailVerification();
                            Intent intent = new Intent(RegistrationActivity.this, LoginActivity.class);
                            startActivity(intent);

                        }
                        hideProgressDialog();
                    }
                });
    }

    private void sendEmailVerification() {

        final FirebaseUser user = mAuth.getCurrentUser();
        user.sendEmailVerification()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()) {
                            Toast.makeText(RegistrationActivity.this,
                                    "Verification email sent to " + user.getEmail(),
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Log.e("info", "sendEmailVerification", task.getException());
                            Toast.makeText(RegistrationActivity.this,
                                    "Failed to send verification email.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    protected void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);


        if (resultCode == RESULT_OK) {
            try {
                this.imageUriCrop = data.getData();
                final InputStream imageStream = getContentResolver().openInputStream(imageUriCrop);
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                imageCrop.setImageBitmap(selectedImage);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(RegistrationActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
            }

        }else {
            Toast.makeText(RegistrationActivity.this, "You haven't picked Image",Toast.LENGTH_LONG).show();
        }
    }

    private boolean validateForm() {
        boolean valid = true;

        String email = mEmailField.getText().toString();
        if (TextUtils.isEmpty(email)) {
            mEmailField.setError("Required.");
            valid = false;
        } else {
            mEmailField.setError(null);
        }

        String password = mPasswordField.getText().toString();
        if (TextUtils.isEmpty(password)) {
            mPasswordField.setError("Required.");
            valid = false;
        } else {
            mPasswordField.setError(null);
        }

        return valid;
    }

}
