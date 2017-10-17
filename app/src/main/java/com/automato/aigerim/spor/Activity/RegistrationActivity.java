package com.automato.aigerim.spor.Activity;

import android.app.DialogFragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.automato.aigerim.spor.Fragments.DatePickerFragment;
import com.automato.aigerim.spor.Other.Api;
import com.automato.aigerim.spor.Other.Tools;
import com.automato.aigerim.spor.R;
import com.soundcloud.android.crop.Crop;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class RegistrationActivity extends AppCompatActivity implements View.OnClickListener {

    Api api;

    ImageView imageCrop;
    Tools tools = new Tools();
    private EditText mEmailField;
    private EditText mPasswordField;
    private EditText mName;
    private EditText mPasswordRepeat;
    private String gender;
    private Uri imageUri = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        api = new Api(this);

        imageUri = null;

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        myToolbar.setTitle("Регистрация");
        setSupportActionBar(myToolbar);

        myToolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_left));
        myToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mEmailField = (EditText) findViewById(R.id.field_email);
        mPasswordField = (EditText) findViewById(R.id.field_password);
        mName = (EditText) findViewById(R.id.field_name);
        mPasswordRepeat = (EditText) findViewById(R.id.field_password_confirm);
        gender = "";

        findViewById(R.id.email_sign_in_button).setOnClickListener(this);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        switch (i) {
            case R.id.datePicker:
                DialogFragment newFragment = new DatePickerFragment();
                newFragment.show(getFragmentManager(), "datePicker");
                break;

            case R.id.email_sign_in_button:
                TextView birthday = (TextView) findViewById(R.id.datePicker);

                if (checkValidate()) {
                    createAccount(
                            mName.getText().toString(),
                            mEmailField.getText().toString(),
                            mPasswordField.getText().toString(),
                            birthday.getText().toString(),
                            gender);
                }
                break;

            case R.id.female_wrapper:
                TextView text = (TextView) findViewById(R.id.female_text);
                ImageView image = (ImageView) findViewById(R.id.female_image);
                text.setTextColor(ContextCompat.getColor(this, R.color.orange));
                image.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.female_or));

                text = (TextView) findViewById(R.id.male_text);
                image = (ImageView) findViewById(R.id.male_image);
                text.setTextColor(ContextCompat.getColor(this, R.color.black));
                image.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.male));

                gender = "female";
                break;

            case R.id.male_wrapper:
                TextView textMale = (TextView) findViewById(R.id.male_text);
                ImageView imageMale = (ImageView) findViewById(R.id.male_image);
                textMale.setTextColor(ContextCompat.getColor(this, R.color.orange));
                imageMale.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.male_or));

                textMale = (TextView) findViewById(R.id.female_text);
                imageMale = (ImageView) findViewById(R.id.female_image);
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

    private boolean checkValidate() {
        TextView birthday = (TextView) findViewById(R.id.datePicker);
        boolean valid = true;
        if (tools.isNullOrWhitespace(mEmailField.getText().toString())) {
            mEmailField.setError(getString(R.string.required));
            valid = false;
        } else {
            mEmailField.setError(null);
        }

        if (tools.isNullOrWhitespace(mPasswordField.getText().toString())) {
            mPasswordField.setError(getString(R.string.required));
            valid = false;
        } else {
            mPasswordField.setError(null);
        }

        if (tools.isNullOrWhitespace(mName.getText().toString())) {
            mName.setError(getString(R.string.required));
            valid = false;
        } else {
            mName.setError(null);
        }

        if (tools.isNullOrWhitespace(mPasswordRepeat.getText().toString())) {
            mPasswordRepeat.setError(getString(R.string.required));
            valid = false;
        } else {
            mPasswordRepeat.setError(null);
        }

        if (tools.isNullOrWhitespace(gender)) {
            Toast.makeText(RegistrationActivity.this, R.string.required_gender, Toast.LENGTH_SHORT).show();
            valid = false;
        }

        if (tools.isNullOrWhitespace(birthday.getText().toString()) || birthday.getText().toString().equals("ДД/MM/ГГ")) {
            birthday.setError(getString(R.string.required_birthday));
            valid = false;
        } else if (checkAge(birthday.getText().toString())) {
            Toast.makeText(RegistrationActivity.this, getString(R.string.smallAge), Toast.LENGTH_SHORT).show();
            valid = false;
        } else {
            birthday.setError(null);
        }

        if (!mPasswordField.getText().toString().equals(mPasswordRepeat.getText().toString())) {
            mPasswordField.setError(getString(R.string.mismatch));
            mPasswordRepeat.setError(getString(R.string.mismatch));
            valid = false;
        } else if (mPasswordField.getText().toString().length() < 6 || mPasswordRepeat.getText().toString().length() < 6) {
            if (mPasswordField.getText().toString().length() < 6) {
                mPasswordField.setError(getString(R.string.mismatch));
            }
            if (mPasswordRepeat.getText().toString().length() < 6) {
                mPasswordRepeat.setError(getString(R.string.mismatch));
            }
            valid = false;
        } else {
            mPasswordField.setError(null);
            mPasswordRepeat.setError(null);
        }

        return valid;
    }

    private boolean checkAge(String date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/mm/yyyy");
        Date yearOfBirth;
        boolean bb = false;
        try {
            yearOfBirth = simpleDateFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return true;
        }
        int currentYear = new Date().getYear();
        int birthYear = yearOfBirth.getYear();
        int diference = currentYear - birthYear;
        if (diference < 18) {
            bb = true;
        }
        return bb;
    }

    private void createAccount(final String name, final String email, final String password,
                               final String birthday, final String gender) {

        Log.d("info", "createAccount:" + email);

        api.signUp(email, password, name, birthday, gender, imageUri);
    }

    @Override
    protected void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            try {
                this.imageUri = data.getData();
                final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                imageCrop.setImageBitmap(selectedImage);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                String message = e.getMessage();
                String title = "Ошибка";

                SweetAlertDialog dialog = new SweetAlertDialog(RegistrationActivity.this, SweetAlertDialog.ERROR_TYPE);
                dialog.setTitleText(title);
                dialog.setConfirmText("Ок");
                dialog.setContentText(message);
                dialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.dismissWithAnimation();
                    }
                });
                dialog.show();
            }
        }
    }
}
