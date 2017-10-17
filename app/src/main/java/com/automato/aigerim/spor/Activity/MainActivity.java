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

import com.automato.aigerim.spor.Fragments.AddDisputeFragment;
import com.automato.aigerim.spor.Fragments.CabinetFragment;
import com.automato.aigerim.spor.Fragments.CategoryDetailFragment;
import com.automato.aigerim.spor.Fragments.CategoryFragment;
import com.automato.aigerim.spor.Fragments.DisputeDetailFragment;
import com.automato.aigerim.spor.Fragments.MainFragment;
import com.automato.aigerim.spor.Fragments.NotificationFragment;
import com.automato.aigerim.spor.R;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class MainActivity extends AppCompatActivity {

    private static FragmentManager m;
    private static MainActivity myActivity;
    private static boolean ferstOpened = false;
    private static Fragment curentFragment;
    private static SweetAlertDialog mProgressDialog;
    private static boolean isAdmin = false;
    private static Context context;
    private Spinner spinner;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            if (spinner == null)
                spinner = (Spinner) findViewById(R.id.spinner);
            switch (item.getItemId()) {
                case R.id.main:
                    changeFragment(0);
                    spinner.setVisibility(View.VISIBLE);
                    findViewById(R.id.my_toolbar).setVisibility(View.VISIBLE);
                    findViewById(R.id.shadow).setVisibility(View.VISIBLE);
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
                    findViewById(R.id.my_toolbar).setVisibility(View.VISIBLE);
                    findViewById(R.id.shadow).setVisibility(View.VISIBLE);
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
                    findViewById(R.id.my_toolbar).setVisibility(View.VISIBLE);
                    findViewById(R.id.shadow).setVisibility(View.VISIBLE);
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
                    findViewById(R.id.my_toolbar).setVisibility(View.VISIBLE);
                    findViewById(R.id.shadow).setVisibility(View.VISIBLE);
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
                    findViewById(R.id.my_toolbar).setVisibility(View.VISIBLE);
                    findViewById(R.id.shadow).setVisibility(View.GONE);
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

    public static boolean isAdmin() {
        return isAdmin;
    }

    public static void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public static void showLoader() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mProgressDialog == null) {
                    mProgressDialog = new SweetAlertDialog(myActivity, SweetAlertDialog.PROGRESS_TYPE);
                    mProgressDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                    mProgressDialog.setTitleText("Загрузка");
                    mProgressDialog.setCancelable(false);
                    mProgressDialog.show();
                }
            }
        });
    }

    public static boolean isFerstOpened() {
        return ferstOpened;
    }

    public static void setFerstOpened(boolean bool) {
        ferstOpened = bool;
    }

    public static void dismissWithAnimationLoader() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mProgressDialog != null) {
                    mProgressDialog.dismissWithAnimation();
                    mProgressDialog = null;
                }
            }
        });
    }

    public static Context getContext() {
        return context;
    }

    public static Fragment getCurentFragment() {
        return curentFragment;
    }

    public static void setCurentFragment(Fragment fragment) {
        curentFragment = fragment;
    }

    public static void CategoryDetailBackPress() {
        MainActivity.getFragmetManeger()
                .beginTransaction()
                .replace(R.id.main_fragment, CategoryFragment.getInstance(), "fragment")
                .setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myActivity = this;
        context = MainActivity.this;
        ferstOpened = false;

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

        m = getSupportFragmentManager();

        changeFragment(0);
    }

    private void changeFragment(int position) {
        Fragment newFragment = null;
        switch (position) {
            case 0:
                newFragment = MainFragment.getInstance(false, null, null);
                ((MainFragment) newFragment).setCategory(null);
                break;
            case 1:
                newFragment = CategoryFragment.getInstance();
                break;
            case 2:
                newFragment = new NotificationFragment();
                break;
            case 3:
                newFragment = new CabinetFragment();
                break;
            case 4:
                newFragment = new AddDisputeFragment();
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
            }  else {
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
                finishAffinity();
            }
        }).show();
    }

    private void MainFragmentBackPress() {
        MainFragment fragment = (MainFragment) curentFragment;
        if (fragment.getCategory() != null && fragment.getCategory() != "") {
            CategoryDetailFragment CatDF = CategoryDetailFragment.getInstance();
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
            MainFragment newFragment = MainFragment.getInstance(true, null, null);
            newFragment.setSorted(true);
            newFragment.setSubCategory(null);

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.main_fragment, newFragment, "fragment")
                    .setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .commit();
        } else if (fragment.isSortedBySubCategory()) {
            MainFragment newFragment = MainFragment.getInstance(true, fragment.getCategory(), fragment.getSubCategory());
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
                    .replace(R.id.main_fragment, MainFragment.getInstance(false, null, null), "fragment")
                    .setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .commit();
        }
    }
}
