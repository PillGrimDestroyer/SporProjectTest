package com.automato.aigerim.spor.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.google.firebase.auth.FirebaseAuth;

import com.automato.aigerim.spor.R;

/**
 * Created by HAOR on 04.09.2017.
 */

public class SettingsActivity extends BaseActivity {

    RelativeLayout LogOutLayout;
    private RelativeLayout LanguageLayout;
    private RelativeLayout EditProfleLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        myToolbar.setTitle("Настройки");
        setSupportActionBar(myToolbar);

        LogOutLayout = (RelativeLayout) findViewById(R.id.log_out_layout);
        LanguageLayout = (RelativeLayout) findViewById(R.id.language_layout);
        EditProfleLayout = (RelativeLayout) findViewById(R.id.editing_layout);

        setOnclickListeners();

        if (MainActivity.isAdmin()) {
            findViewById(R.id.notif_layout).setVisibility(View.GONE);
            findViewById(R.id.editing_layout).setVisibility(View.GONE);
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    private void setOnclickListeners() {
        LanguageLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SettingsActivity.this, SelectLanguageActivity.class);
                startActivity(intent);
            }
        });

        EditProfleLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SettingsActivity.this, EditingPageActivity.class);
                startActivity(intent);
            }
        });

        LogOutLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                mAuth.signOut();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
