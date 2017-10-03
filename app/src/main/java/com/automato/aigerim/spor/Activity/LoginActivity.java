package com.automato.aigerim.spor.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.automato.aigerim.spor.Models.User;
import com.automato.aigerim.spor.R;
import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class LoginActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "EmailPassword";
    public ProgressDialog mProgressDialog;
    private EditText mEmailField;
    private EditText mPasswordField;
    private TextView forgotPass;
    private Button register;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private boolean mainLogin = false;
    private FirebaseDatabase database;
    private DatabaseReference reference;
    private User client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Firebase.setAndroidContext(this);
        FirebaseApp.initializeApp(this);
        database = FirebaseDatabase.getInstance();
        reference = database.getReference("users");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mEmailField = (EditText) findViewById(R.id.field_email);
        mPasswordField = (EditText) findViewById(R.id.field_password);
        forgotPass = (TextView) findViewById(R.id.forgot_password);

        forgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, ForgotPassActivity.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.sign_in_button).setOnClickListener(this);
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull final FirebaseAuth firebaseAuth) {
                final FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    if (!mainLogin) {
                        showProgressDialog();
                        reference.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                client = dataSnapshot.getValue(User.class);
                                if (client.confirmed) {
                                    user.reload().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            if (user.isEmailVerified()) {
                                                Intent t = new Intent(LoginActivity.this, MainActivity.class);
                                                t.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                                                hideProgressDialog();

                                                boolean isAdmin = mAuth.getCurrentUser().getEmail().equals("automato.android@yandex.ru");
                                                MainActivity.setAdmin(isAdmin);
                                                startActivity(t);

                                                Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                                            }
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            String message = e.getMessage();
                                            if (e instanceof FirebaseAuthInvalidUserException) {
                                                message = "Данный пользователь отсутсвует в системе";
                                            }
                                            hideProgressDialog();
                                            new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.ERROR_TYPE)
                                                    .setTitleText("Ошибка")
                                                    .setContentText(message)
                                                    .setConfirmText("Ок")
                                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                        @Override
                                                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                            sweetAlertDialog.dismissWithAnimation();
                                                            firebaseAuth.signOut();
                                                        }
                                                    }).show();
                                            Log.d(TAG, "authError:" + e.getMessage());
                                        }
                                    });
                                } else {
                                    hideProgressDialog();
                                    Toast.makeText(LoginActivity.this, "Для автоматического входа требуется верификация аккаунта", Toast.LENGTH_LONG).show();
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                } else {
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
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

    private void signIn(String email, String password) {
        Log.d(TAG, "signIn:" + email);
        if (!validateForm()) {
            return;
        }

        mainLogin = true;
        showProgressDialog();
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithEmail:success");

                            boolean isAdmin = mAuth.getCurrentUser().getEmail().equals("automato.android@yandex.ru");
                            MainActivity.setAdmin(isAdmin);

                            hideProgressDialog();

                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        } else {

                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Не верный логин или пароль", Toast.LENGTH_SHORT).show();

                        }
                        hideProgressDialog();
                    }
                });
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
