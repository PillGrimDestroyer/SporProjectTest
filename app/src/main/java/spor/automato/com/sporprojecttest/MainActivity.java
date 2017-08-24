package spor.automato.com.sporprojecttest;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import spor.automato.com.sporprojecttest.fragments.CabinetFragment;
import spor.automato.com.sporprojecttest.fragments.CategoryFragment;
import spor.automato.com.sporprojecttest.fragments.MainFragment;
import spor.automato.com.sporprojecttest.fragments.NotificationFragment;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        mAuth = FirebaseAuth.getInstance();
        changeFragment(0);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.main:
                    changeFragment(0);
                    Log.i("info","main");
                    return true;

                case R.id.category:
                    changeFragment(1);
                    return true;

                case R.id.notification:
                    changeFragment(2);
                    return true;

                case R.id.cabinet:
                    changeFragment(3);
                    return true;
            }
            return false;
        }

    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);

        // return true so that the menu pop up is opened
        return true;
    }

    private void signOut() {
        mAuth.signOut();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.log_out:
                signOut();
                Log.i("info","log out");

                Intent t = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(t);

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void changeFragment(int position) {
        Fragment newFragment = null;
        switch(position) {
            case 0:
                newFragment = new MainFragment();
                break;
            case 1:
                newFragment = new CategoryFragment();
                break;
            case 2:
                newFragment = new NotificationFragment();
                break;
            case 3:
                newFragment = new CabinetFragment();
                break;
        }

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_fragment, newFragment, "fragment")
                .setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit();
    }
}