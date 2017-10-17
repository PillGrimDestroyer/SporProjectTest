package com.automato.aigerim.spor.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.automato.aigerim.spor.Other.Api;
import com.automato.aigerim.spor.Other.Tools;
import com.automato.aigerim.spor.R;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "EmailPassword";
    private EditText mEmailField;
    private EditText mPasswordField;
    private TextView forgotPass;
    private Button register;
    private Button signInButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mEmailField = (EditText) findViewById(R.id.field_email);
        mPasswordField = (EditText) findViewById(R.id.field_password);
        forgotPass = (TextView) findViewById(R.id.forgot_password);
        signInButton = (Button) findViewById(R.id.sign_in_button);

        forgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, ForgotPassActivity.class);
                startActivity(intent);
            }
        });

        signInButton.setOnClickListener(this);
        SharedPreferences sp = getPreferences(MODE_PRIVATE);

        String login = sp.getString("MyStore_login", "");
        String password = sp.getString("MyStore_password", "");

        Log.v("SharedPref_login", login);
        Log.v("SharedPref_password", password);

        if (!Tools.isNullOrWhitespace(login) && !Tools.isNullOrWhitespace(password)) {
            mEmailField.setText(login);
            mPasswordField.setText(password);
            signInButton.performClick();
        }
    }

    public void sharedPref() {
        SharedPreferences.Editor prefEditor = getPreferences(MODE_PRIVATE).edit();
        prefEditor.putString("MyStore_login", mEmailField.getText().toString());
        prefEditor.putString("MyStore_password", mPasswordField.getText().toString());
        prefEditor.apply();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button:
                String emailText = mEmailField.getText().toString();
                String passText = mPasswordField.getText().toString();

                signIn(emailText, passText);
                break;

            case R.id.register:
                Intent intent = new Intent(LoginActivity.this, RegistrationActivity.class);
                startActivity(intent);
                break;
        }
    }

    private void signIn(final String email, final String password) {
        Log.d(TAG, "signIn:" + email);
        if (validateForm()) {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    Api api = new Api(LoginActivity.this);
                    if (api.logIn(email, password)) {
                        sharedPref();

                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                }
            });
            thread.setDaemon(false);
            thread.start();
        }
    }

    private boolean validateForm() {
        boolean valid = true;

        String email = mEmailField.getText().toString();
        if (TextUtils.isEmpty(email)) {
            mEmailField.setError("Обязательно для заполнения");
            valid = false;
        } else {
            mEmailField.setError(null);
        }

        String password = mPasswordField.getText().toString();
        if (TextUtils.isEmpty(password)) {
            mPasswordField.setError("Обязательно для заполнения");
            valid = false;
        } else {
            mPasswordField.setError(null);
        }

        return valid;
    }

    @Override
    public void onBackPressed() {
        new SweetAlertDialog(this, SweetAlertDialog.NORMAL_TYPE)
                .setTitleText("Выход")
                .setContentText("Вы хотите выйти из приложения?")
                .setCancelText("Нет")
                .setConfirmText("Да")
                .showCancelButton(true)
                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.cancel();
                    }
                }).setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                LoginActivity.super.onBackPressed();
            }
        }).show();
    }
}
