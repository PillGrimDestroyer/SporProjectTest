package com.automato.aigerim.spor.Fragments;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.automato.aigerim.spor.Activity.MainActivity;
import com.automato.aigerim.spor.Adapter.CategoryDetailAdapter;
import com.automato.aigerim.spor.Models.Dispute;
import com.automato.aigerim.spor.Other.Api;
import com.automato.aigerim.spor.R;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by HAOR on 25.08.2017.
 */

public class CategoryDetailFragment extends Fragment implements CategoryDetailAdapter.ItemClickListener {

    static Api api;

    private static CategoryDetailFragment instance;
    private static CategoryDetailAdapter adapter;
    private static View rootView;
    private static String category;
    private static RecyclerView recyclerView;
    private static ArrayList<String> mData = new ArrayList<>();
//    private static HashMap<Integer, Dispute> mData = new HashMap<>();
    //    private static DatabaseReference reference;
    private static Toolbar myToolbar;
    private static TextView title;
//    private FirebaseDatabase database;

    public static CategoryDetailFragment getInstance() {
        /*if (instance == null) {
            instance = new CategoryDetailFragment();
        } else {
            adapter = null;
            setImage();
            myToolbar.setTitle(category);
            title.setText(category);
            loadData();
        }*/
        instance = new CategoryDetailFragment();

        return instance;
    }

    public static void setImage() {
        FrameLayout sporImage = (FrameLayout) rootView.findViewById(R.id.imageLayout);

        int drawable;

        switch (category) {
            case "Футбол":
                drawable = R.drawable.football_header;
                break;

            case "Баскетбол":
                drawable = R.drawable.basket_header;
                break;

            case "Бокс":
                drawable = R.drawable.box_header;
                break;

            case "Теннис":
                drawable = R.drawable.tennis_header;
                break;

            case "Борьба":
                drawable = R.drawable.cat_wrestling;
                break;

            case "Волейбол":
                drawable = R.drawable.cat_volleyball;
                break;

            default:
                drawable = R.drawable.football_header;
                break;
        }

        sporImage.setBackgroundResource(drawable);
    }

    private void loadData() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    mData = api.getSortedByCategoryDisputes(category);
                    adapter = new CategoryDetailAdapter(rootView.getContext(), category, mData);
                    adapter.setClickListener(instance);
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            recyclerView.setAdapter(adapter);
                        }
                    });
                }catch (Exception ex){
                    ex.printStackTrace();
                }
            }
        });
        thread.setDaemon(false);
        thread.start();
    }

    public void setCategory(String category) {
        this.category = category;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_category_detail, container, false);

        api = new Api(getActivity());

        recyclerView = (RecyclerView) rootView.findViewById(R.id.spor_list);
        recyclerView.setHasFixedSize(true);

        int numberOfColumns = 2;
        recyclerView.setLayoutManager(new GridLayoutManager(rootView.getContext(), numberOfColumns));

        setImage();

        getActivity().findViewById(R.id.my_toolbar).setVisibility(View.GONE);
        getActivity().findViewById(R.id.shadow).setVisibility(View.GONE);

        myToolbar = (Toolbar) rootView.findViewById(R.id.my_transparent_toolbar);
        myToolbar.setTitle(category);
        myToolbar.setTitleTextColor(getResources().getColor(R.color.white));
        MainActivity.getActivity().setSupportActionBar(myToolbar);

        myToolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_left));
        myToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.CategoryDetailBackPress();
            }
        });

        title = (TextView) getActivity().findViewById(R.id.title);
        title.setText(category);

        loadData();

        MainActivity.setCurentFragment(this);
        return rootView;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        // write logic here b'z it is called when fragment is visible to user

    }

    @Override
    public void onItemClick(View view, int position) {
        String category = CategoryDetailFragment.category;
        String subCategory = adapter.getItem(position);
        MainFragment ddf = MainFragment.getInstance(true, category, subCategory);

        ddf.setCategory(category);
        ddf.setSubCategory(subCategory);
        ddf.setSorted(true);

        android.support.v4.app.Fragment newFragment = ddf;

        MainActivity.getFragmetManeger()
                .beginTransaction()
                .replace(R.id.main_fragment, newFragment, "fragment")
                .setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }
}
