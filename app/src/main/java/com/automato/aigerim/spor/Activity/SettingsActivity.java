package com.automato.aigerim.spor.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.automato.aigerim.spor.Models.User;
import com.automato.aigerim.spor.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by HAOR on 04.09.2017.
 */

public class
SettingsActivity extends BaseActivity {

    TextView LogOutLayout;
    private RelativeLayout LanguageLayout;
    private RelativeLayout EditProfleLayout;
    private Switch notifSwitch;
    private FirebaseAuth mAuth;
    private FirebaseDatabase databese;
    private User client;
    private RelativeLayout aboutDisputeLayout;
    private RelativeLayout privacyPolicyLayout;
    private RelativeLayout termsOfUseLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        myToolbar.setTitle("Настройки");
        setSupportActionBar(myToolbar);

        mAuth = FirebaseAuth.getInstance();
        databese = FirebaseDatabase.getInstance();

        databese.getReference("users").child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                client = dataSnapshot.getValue(User.class);

                if (client.receiveNotifications) {
                    notifSwitch.setChecked(true);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        LogOutLayout = (TextView) findViewById(R.id.log_out);
        LanguageLayout = (RelativeLayout) findViewById(R.id.language_layout);
        EditProfleLayout = (RelativeLayout) findViewById(R.id.editing_layout);
        notifSwitch = (Switch) findViewById(R.id.notif_swithc);
        aboutDisputeLayout = (RelativeLayout) findViewById(R.id.about_dispute_layout);
        privacyPolicyLayout = (RelativeLayout) findViewById(R.id.privacy_policy_layout);
        termsOfUseLayout = (RelativeLayout) findViewById(R.id.terms_of_use_layout);

        setOnclickListeners();

        if (MainActivity.isAdmin()) {
            findViewById(R.id.notif_layout).setVisibility(View.GONE);
            findViewById(R.id.linear_profile).setVisibility(View.GONE);
            findViewById(R.id.settings).setVisibility(View.GONE);
            findViewById(R.id.profile).setVisibility(View.GONE);
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    private void setOnclickListeners() {
        aboutDisputeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SettingsActivity.this, AboutDisputeActivity.class);
                startActivity(intent);
            }
        });

        privacyPolicyLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SettingsActivity.this, PrivacyPolicyActivity.class);
                startActivity(intent);
            }
        });

        termsOfUseLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SettingsActivity.this, TermsOfUseActivity.class);
                startActivity(intent);
            }
        });

        notifSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                DatabaseReference recNotif = databese.getReference("users/" + mAuth.getCurrentUser().getUid() + "/receiveNotifications");
                if (b) {
                    recNotif.setValue(true);
                } else {
                    recNotif.setValue(false);
                }
            }
        });

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
                Intent intent = new Intent(SettingsActivity.this, EditingProfilePageActivity.class);
                startActivity(intent);
            }
        });

        LogOutLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                mAuth.signOut();
                Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
