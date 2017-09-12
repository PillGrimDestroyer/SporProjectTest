package com.automato.aigerim.spor.Activity;

import android.accounts.AuthenticatorException;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.automato.aigerim.spor.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthActionCodeException;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class ChangePassActivity extends AppCompatActivity {

    private EditText newPassEditText;
    private EditText repeatPassEditText;
    private ImageView done;
    private FirebaseAuth mAuth;
    private SweetAlertDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_pass);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        myToolbar.setTitle("Сменить пароль");
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mAuth = FirebaseAuth.getInstance();

        newPassEditText = (EditText) findViewById(R.id.new_pass_text);
        repeatPassEditText = (EditText) findViewById(R.id.repeat_pass_text);
        done = (ImageView) findViewById(R.id.done);

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean norm = true;

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

                if (norm) {
                    showLoader();
                    mAuth.getCurrentUser().updatePassword(newPassEditText.getText().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            dismissWithAnimationLoader();
                            SweetAlertDialog dialog = new SweetAlertDialog(ChangePassActivity.this, SweetAlertDialog.SUCCESS_TYPE);
                            dialog.setTitleText("Успех!");
                            dialog.setContentText("Пароль успешно изменен");
                            dialog.setConfirmText("Ок");
                            dialog.show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            dismissWithAnimationLoader();

                            String message = e.getMessage();
                            String code = ((FirebaseAuthException) e).getErrorCode();
                            String errorMessage = "Код ошибки - " + code + "\r\n" + message;
                            if (code.equals("ERROR_REQUIRES_RECENT_LOGIN")){
                                message = "Для данной операции требуется повторная авторизация";
                            }

                            SweetAlertDialog dialog = new SweetAlertDialog(ChangePassActivity.this, SweetAlertDialog.ERROR_TYPE);
                            dialog.setTitleText("Ошибка");
                            dialog.setContentText(message.equals(e.getMessage()) ? errorMessage : message);
                            dialog.setConfirmText("Ок");
                            dialog.show();
                        }
                    });
                }
            }
        });
    }

    public void showLoader() {
        mProgressDialog = new SweetAlertDialog(ChangePassActivity.this, SweetAlertDialog.PROGRESS_TYPE);
        mProgressDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        mProgressDialog.setTitleText("Загрузка");
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
    }

    public void dismissWithAnimationLoader() {
        if (mProgressDialog != null) {
            mProgressDialog.dismissWithAnimation();
            mProgressDialog = null;
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
