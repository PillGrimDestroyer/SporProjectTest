package com.automato.aigerim.spor.Activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Spinner;

import com.google.firebase.auth.FirebaseAuth;

import cn.pedant.SweetAlert.SweetAlertDialog;
import com.automato.aigerim.spor.R;
import com.automato.aigerim.spor.Fragments.AddDisputeCategoryFragment;
import com.automato.aigerim.spor.Fragments.AddDisputeSubCategoryFragment;
import com.automato.aigerim.spor.Fragments.CabinetFragment;
import com.automato.aigerim.spor.Fragments.CategoryDetailFragment;
import com.automato.aigerim.spor.Fragments.CategoryFragment;
import com.automato.aigerim.spor.Fragments.DisputeDetailFragment;
import com.automato.aigerim.spor.Fragments.MainFragment;
import com.automato.aigerim.spor.Fragments.NotificationFragment;

public class MainActivity extends AppCompatActivity {

    private static FragmentManager m;
    private static MainActivity myActivity;
    private static Fragment curentFragment;
    private static SweetAlertDialog mProgressDialog;
    private static boolean isAdmin = false;
    private static Context context;
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
                    findViewById(R.id.title).setVisibility(View.GONE);
                    findViewById(R.id.settings_image).setVisibility(View.GONE);
                    findViewById(R.id.done).setVisibility(View.GONE);
                    findViewById(R.id.search_field).setVisibility(View.GONE);
                    findViewById(R.id.search_right).setVisibility(View.VISIBLE);
                    findViewById(R.id.search_left).setVisibility(View.GONE);
                    findViewById(R.id.close).setVisibility(View.GONE);
                    return true;

                case R.id.category:
                    changeFragment(1);
                    findViewById(R.id.title).setVisibility(View.VISIBLE);
                    spinner.setVisibility(View.GONE);
                    findViewById(R.id.settings_image).setVisibility(View.GONE);
                    findViewById(R.id.done).setVisibility(View.GONE);
                    findViewById(R.id.search_field).setVisibility(View.GONE);
                    findViewById(R.id.search_right).setVisibility(View.VISIBLE);
                    findViewById(R.id.search_left).setVisibility(View.GONE);
                    findViewById(R.id.close).setVisibility(View.GONE);
                    return true;

                case R.id.add_spor:
                    changeFragment(4);
                    spinner.setVisibility(View.GONE);
                    findViewById(R.id.settings_image).setVisibility(View.GONE);
                    findViewById(R.id.done).setVisibility(View.GONE);
                    findViewById(R.id.title).setVisibility(View.VISIBLE);
                    findViewById(R.id.search_field).setVisibility(View.GONE);
                    findViewById(R.id.search_right).setVisibility(View.GONE);
                    findViewById(R.id.search_left).setVisibility(View.GONE);
                    findViewById(R.id.close).setVisibility(View.GONE);
                    return true;

                case R.id.notification:
                    changeFragment(2);
                    spinner.setVisibility(View.GONE);
                    findViewById(R.id.settings_image).setVisibility(View.GONE);
                    findViewById(R.id.done).setVisibility(View.GONE);
                    findViewById(R.id.title).setVisibility(View.VISIBLE);
                    findViewById(R.id.search_field).setVisibility(View.GONE);
                    findViewById(R.id.search_right).setVisibility(View.GONE);
                    findViewById(R.id.search_left).setVisibility(View.GONE);
                    findViewById(R.id.close).setVisibility(View.GONE);
                    return true;

                case R.id.cabinet:
                    changeFragment(3);
                    spinner.setVisibility(View.GONE);
                    findViewById(R.id.done).setVisibility(View.GONE);
                    findViewById(R.id.title).setVisibility(View.VISIBLE);
                    findViewById(R.id.settings_image).setVisibility(View.VISIBLE);
                    findViewById(R.id.search_right).setVisibility(View.GONE);
                    findViewById(R.id.search_left).setVisibility(View.GONE);
                    findViewById(R.id.search_field).setVisibility(View.GONE);
                    findViewById(R.id.close).setVisibility(View.GONE);
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

    public static boolean isAdmin() {
        return isAdmin;
    }

    public static void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public static void showLoader() {
        mProgressDialog = new SweetAlertDialog(myActivity, SweetAlertDialog.PROGRESS_TYPE);
        mProgressDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        mProgressDialog.setTitleText("Загрузка");
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
    }

    public static void dismissWithAnimationLoader() {
        if (mProgressDialog != null) {
            mProgressDialog.dismissWithAnimation();
            mProgressDialog = null;
        }
    }

    public static Context getContext() {
        return context;
    }

    public static Fragment getCurentFragment() {
        return curentFragment;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myActivity = this;
        context = MainActivity.this;

        ImageView settings = (ImageView) getActivity().findViewById(R.id.settings_image);
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        if (isAdmin) {
            navigation.inflateMenu(R.menu.navigation_admin);
        } else {
            navigation.inflateMenu(R.menu.navigation);
        }
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        mAuth = FirebaseAuth.getInstance();

        m = getSupportFragmentManager();

        changeFragment(0);
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
            case 4:
                newFragment = new AddDisputeCategoryFragment();
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
            } else if (curentFragment.getClass().equals(AddDisputeSubCategoryFragment.class)) {
                AddDisputeSubCategoryBackPress();
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

    private void AddDisputeSubCategoryBackPress() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_fragment, new AddDisputeCategoryFragment(), "fragment")
                .setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit();
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
