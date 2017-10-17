package com.automato.aigerim.spor.Activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.automato.aigerim.spor.Models.User;
import com.automato.aigerim.spor.Other.Api;
import com.automato.aigerim.spor.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class ChangePassActivity extends AppCompatActivity implements View.OnClickListener {

    Api api;

    private EditText newPassEditText;
    private EditText oldPassEditText;
    private EditText repeatPassEditText;
    private ImageView done;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_pass);

        api = new Api(this);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        myToolbar.setTitle("Сменить пароль");
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        newPassEditText = (EditText) findViewById(R.id.new_pass_text);
        oldPassEditText = (EditText) findViewById(R.id.old_pass_text);
        repeatPassEditText = (EditText) findViewById(R.id.repeat_pass_text);
        done = (ImageView) findViewById(R.id.done);

        done.setOnClickListener(this);
    }

    private boolean isValidate() {
        boolean norm = true;

        if (TextUtils.isEmpty(oldPassEditText.getText().toString())) {
            oldPassEditText.setError("Обязательно для заполнения");
            norm = false;
        } else {
            oldPassEditText.setError(null);
        }

        if (TextUtils.isEmpty(newPassEditText.getText().toString())) {
            newPassEditText.setError("Обязательно для заполнения");
            norm = false;
        } else if (newPassEditText.getText().length() < 6) {
            newPassEditText.setError("Минимум 6 символов");
            norm = false;
        } else {
            newPassEditText.setError(null);
        }

        if (TextUtils.isEmpty(repeatPassEditText.getText().toString())) {
            repeatPassEditText.setError("Обязательно для заполнения");
            norm = false;
        } else if (repeatPassEditText.getText().length() < 6) {
            repeatPassEditText.setError("Минимум 6 символов");
            norm = false;
        } else {
            repeatPassEditText.setError(null);
        }

        if (!newPassEditText.getText().toString().equals(repeatPassEditText.getText().toString())) {
            newPassEditText.setError("Пароли не совпадают");
            repeatPassEditText.setError("Пароли не совпадают");
            norm = false;
        }
        return norm;
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.done:
                onDoneClick();
                break;

            default:
                break;
        }
    }

    private void onDoneClick() {
        if (isValidate()) {
            api.changePass(newPassEditText.getText().toString(), oldPassEditText.getText().toString());
        }
    }
}
