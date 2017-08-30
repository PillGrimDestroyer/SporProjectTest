package spor.automato.com.sporprojecttest.Activity;

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
import android.view.View;
import android.widget.Spinner;

import com.google.firebase.auth.FirebaseAuth;

import cn.pedant.SweetAlert.SweetAlertDialog;
import spor.automato.com.sporprojecttest.R;
import spor.automato.com.sporprojecttest.fragments.CabinetFragment;
import spor.automato.com.sporprojecttest.fragments.CategoryDetailFragment;
import spor.automato.com.sporprojecttest.fragments.CategoryFragment;
import spor.automato.com.sporprojecttest.fragments.DisputeDetailFragment;
import spor.automato.com.sporprojecttest.fragments.MainFragment;
import spor.automato.com.sporprojecttest.fragments.NotificationFragment;

public class MainActivity extends AppCompatActivity {

    private static FragmentManager m;
    private static MainActivity myActivity;
    private static Fragment curentFragment;
    private FirebaseAuth mAuth;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Spinner spinner = (Spinner) findViewById(R.id.spinner);
            switch (item.getItemId()) {
                case R.id.main:
                    changeFragment(0);
                    spinner.setVisibility(View.VISIBLE);
                    return true;

                case R.id.category:
                    changeFragment(1);
                    spinner.setVisibility(View.GONE);
                    return true;

                case R.id.notification:
                    changeFragment(2);
                    spinner.setVisibility(View.GONE);
                    return true;

                case R.id.cabinet:
                    changeFragment(3);
                    spinner.setVisibility(View.GONE);
                    return true;
            }
            return false;
        }

    };

    public static FragmentManager getFragmetManeger() {
        return m;
    }

    public static MainActivity getActivity() {
        return myActivity;
    }

    public static void setCurentFragment(Fragment fragment) {
        curentFragment = fragment;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        findViewById(R.id.my_toolbar).setVisibility(View.GONE);

        mAuth = FirebaseAuth.getInstance();

        m = getSupportFragmentManager();

        changeFragment(0);
        myActivity = this;
    }

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
                Log.i("info", "log out");

                Intent t = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(t);

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void changeFragment(int position) {
        Fragment newFragment = null;
        switch (position) {
            case 0:
                newFragment = new MainFragment();
                ((MainFragment) newFragment).setCategory(null);
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

    @Override
    public void onBackPressed() {
        if (curentFragment == null) {
            exitApp();
        } else {
            if (curentFragment.getClass().equals(DisputeDetailFragment.class)) {
                DisputDetailBackPress();
            } else if (curentFragment.getClass().equals(CategoryDetailFragment.class)) {
                CategoryDetailBackPress();
            } else if (curentFragment.getClass().equals(MainFragment.class)) {
                MainFragmentBackPress();
            } else {
                exitApp();
            }
        }
    }

    private void exitApp() {
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
                        MainActivity.super.onBackPressed();
                    }
                }).show();
    }

    private void CategoryDetailBackPress() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_fragment, new CategoryFragment(), "fragment")
                .setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit();
    }

    private void MainFragmentBackPress() {
        MainFragment fragment = (MainFragment) curentFragment;
        if (fragment.getCategory() != null && fragment.getCategory() != "") {
            CategoryDetailFragment CatDF = new CategoryDetailFragment();
            CatDF.setCategory(fragment.getCategory());
            Fragment newFragment = CatDF;

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.main_fragment, newFragment, "fragment")
                    .setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .commit();
        } else {
            exitApp();
        }
    }

    private void DisputDetailBackPress() {
        DisputeDetailFragment fragment = ((DisputeDetailFragment) curentFragment);
        if (fragment.isSorted() && !fragment.isSortedBySubCategory()) {
            MainFragment newFragment = new MainFragment();
            newFragment.setSorted(true);
            newFragment.setSubCategory(null);

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.main_fragment, newFragment, "fragment")
                    .setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .commit();
        } else if (fragment.isSortedBySubCategory()) {
            MainFragment newFragment = new MainFragment();
            newFragment.setSorted(true);
            newFragment.setSubCategory(fragment.getSubCategory());
            newFragment.setCategory(fragment.getCategory());

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.main_fragment, newFragment, "fragment")
                    .setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .commit();
        } else {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.main_fragment, new MainFragment(), "fragment")
                    .setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .commit();
        }
    }
}
