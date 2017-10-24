package com.automato.aigerim.spor.Activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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

import com.automato.aigerim.spor.Models.User;
import com.automato.aigerim.spor.Other.Api;
import com.automato.aigerim.spor.Other.FTP;
import com.automato.aigerim.spor.Other.Tools;
import com.automato.aigerim.spor.R;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class EditingProfilePageActivity extends AppCompatActivity implements View.OnClickListener {

    static final int GALLERY_REQUEST = 1;
    Api api;

    private RelativeLayout changePhoto;
    private ProgressBar progressBar;
    private ImageView userProfileImage;
    private EditText userNameEditText;
    private EditText userEmail;
    private RelativeLayout ChangePass;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editing_profile_page);

        api = new Api(this);

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
                User.name = userNameEditText.getText().toString();
            }
        });

        progressBar.setVisibility(View.VISIBLE);
        loadingClientProfile();
    }

    public void loadingClientProfile() {
        userNameEditText.setText(User.name);
        userEmail.setText(User.email);
        setClientImage();
    }

    private void setClientImage() {
        if (User.hasImage) {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    final Bitmap bitmap = Tools.downloadUserPhoto(EditingProfilePageActivity.this);
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            userProfileImage.setImageBitmap(bitmap);
                            progressBar.setVisibility(View.GONE);
                        }
                    });
                }
            });
            thread.setDaemon(false);
            thread.start();
        } else
            progressBar.setVisibility(View.GONE);
    }

    private void changePassClick() {
        Intent intent = new Intent(EditingProfilePageActivity.this, ChangePassActivity.class);
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
        api.updateUser();
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
        String selfile = Tools.getRealPathFromUri(EditingProfilePageActivity.this, selectedImage);
        String filetype = selfile.substring(selfile.lastIndexOf(".") + 1);
        if (!filetype.equals("jpg") && !filetype.equals("jpeg")){
            Toast.makeText(EditingProfilePageActivity.this, "Изображение должно быть в формате jpg или jpeg", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
            return;
        }
        userProfileImage.setImageURI(selectedImage);
        Tools.uploadUserPhoto(selectedImage, EditingProfilePageActivity.this, progressBar, false);
    }
}
