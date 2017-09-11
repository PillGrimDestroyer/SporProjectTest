package com.automato.aigerim.spor.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;

import com.automato.aigerim.spor.R;

/**
 * Created by HAOR on 30.08.2017.
 */

public class ForgotPassActivity extends BaseActivity{

    private EditText mail;
    private Button resetPassButton;
    private TextView register;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_pass);

        mail = (EditText) findViewById(R.id.field_email);
        resetPassButton = (Button) findViewById(R.id.reset_pass_button);
        register = (TextView) findViewById(R.id.register);

        mAuth = FirebaseAuth.getInstance();

        resetPassButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showProgressDialog();
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
        mAuth.sendPasswordResetEmail(mail.getText().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(ForgotPassActivity.this, "На вашу почту отправлено сообщение", Toast.LENGTH_SHORT).show();
                hideProgressDialog();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("Exception", e.getMessage());
                Toast.makeText(ForgotPassActivity.this, "Данный почтовый ящик не зарегистрирован", Toast.LENGTH_SHORT).show();
                hideProgressDialog();
            }
        });
    }
}
