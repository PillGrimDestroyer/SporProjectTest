package com.automato.aigerim.spor.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.automato.aigerim.spor.Other.Api;
import com.automato.aigerim.spor.Other.Tools;
import com.automato.aigerim.spor.R;

/**
 * Created by HAOR on 30.08.2017.
 */

public class ForgotPassActivity extends AppCompatActivity {

    Api api;

    private EditText mail;
    private Button resetPassButton;
    private TextView register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_pass);
        api = new Api(this);

        mail = (EditText) findViewById(R.id.field_email);
        resetPassButton = (Button) findViewById(R.id.reset_pass_button);
        register = (TextView) findViewById(R.id.register);

        resetPassButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changePass();
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ForgotPassActivity.this, RegistrationActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    private void changePass() {
        if (!Tools.isNullOrWhitespace(mail.getText().toString())) {
            mail.setError(null);
            api.forgotPass(mail.getText().toString());
        } else {
            mail.setError("Обязательно для заполнения");
        }
    }
}
