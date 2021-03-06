package com.automato.aigerim.spor.Fragments;

import android.animation.Animator;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.automato.aigerim.spor.Activity.MainActivity;
import com.automato.aigerim.spor.Adapter.NotSortedDisputeAdapter;
import com.automato.aigerim.spor.Adapter.SortedDisputeAdapter;
import com.automato.aigerim.spor.Models.Dispute;
import com.automato.aigerim.spor.Other.Api;
import com.automato.aigerim.spor.Other.Tools;
import com.automato.aigerim.spor.R;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class MainFragment extends Fragment {

    private static MainFragment instance;
    private static Api api;

    private static View rootView;
    private static boolean isSorted = false;
    private static String category;
    private static String subCategory;
    private static SortedDisputeAdapter adapter;
    private static ArrayList<Dispute> mData = new ArrayList<>();
    private static RecyclerView sporList;
    private static LinearLayoutManager llm;
    private static boolean isLoading = false;
    private static NotSortedDisputeAdapter notSortedAdapter;
    private static boolean fLoad = false;
    private static Spinner spinner;
    private static ImageView searchRight;
    private static ImageView searchLeft;
    private static EditText searchField;
    private static ImageView close;
    private static TextView no_disputes;

    public static MainFragment getInstance(boolean isSorted, String category, String subCategory) {
        MainFragment.category = category;
        MainFragment.isSorted = isSorted;
        MainFragment.subCategory = subCategory;
        if (instance == null) {
            instance = new MainFragment();
        } else {
//            MainActivity.showLoader();
            if (spinner.getSelectedItemPosition() != 0)
                MainActivity.setFerstOpened(false);
            spinner.setSelection(0, false);
            if (!isSorted)
                notSortedSetAdapter();
            else {
                sortedBySubCategorySetAdapter();
            }
        }
        MainActivity.setCurentFragment(instance);
        return instance;
    }

    public static boolean isSorted() {
        return isSorted;
    }

    public void setSorted(boolean sorted) {
        isSorted = sorted;
    }

    private static void sortedBySubCategorySetAdapter() {
        if (Build.VERSION.SDK_INT >= 23)
            sporList.setOnScrollChangeListener(null);
        else
            sporList.setOnScrollListener(null);

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    mData = api.getSortedBySubCategoryDisputes(category, subCategory);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                /*mData.clear();
                for (Dispute d : sortedDisputes) {
                    if (category != null && subCategory != null) {
                        if (category.equals(d.category) && subCategory.equals(d.subcategory)) {
                            mData.add(d);
                        }
                    }
                }*/
                adapter = new SortedDisputeAdapter(rootView.getContext(), mData, isSorted(), subCategory != null);

                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        sporList.swapAdapter(adapter, true);
                    }
                });
//                MainActivity.dismissWithAnimationLoader();
            }
        });
        thread.setDaemon(false);
        thread.start();
    }

    private static void notSortedSetAdapter() {
        if (Build.VERSION.SDK_INT >= 23)
            sporList.setOnScrollChangeListener(null);
        else
            sporList.setOnScrollListener(null);

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                mData = api.getAllDisputes(10);
                notSortedAdapter = new NotSortedDisputeAdapter(mData, isSorted(), subCategory != null);

                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        sporList.swapAdapter(notSortedAdapter, true);
                        if (mData.size() == 0){
                            no_disputes.setText(MainActivity.getActivity().getString(R.string.no_disputes));
                            no_disputes.setVisibility(View.VISIBLE);
                        }else {
                            no_disputes.setVisibility(View.GONE);
                        }
                    }
                });
//                MainActivity.dismissWithAnimationLoader();
            }
        });
        thread.setDaemon(false);
        thread.start();

        if (Build.VERSION.SDK_INT >= 23) {
            RecyclerView.OnScrollChangeListener scrollChangeListener = new RecyclerView.OnScrollChangeListener() {
                @Override
                public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                    if (llm != null) {
                        int visibleItemCount = llm.getChildCount();//смотрим сколько элементов на экране
                        int totalItemCount = llm.getItemCount();//сколько всего элементов
                        int firstVisibleItems = llm.findFirstVisibleItemPosition();//какая позиция первого элемента

                        Log.w("visibleItemCount", Integer.toString(visibleItemCount));
                        Log.w("firstVisibleItems", Integer.toString(firstVisibleItems));
                        Log.w("totalItemCount", Integer.toString(totalItemCount));

                        if (!isLoading) {//проверяем, грузим мы что-то или нет, эта переменная должна быть вне класса  OnScrollListener
                            if ((visibleItemCount + firstVisibleItems) >= totalItemCount) {
                                isLoading = true;//ставим флаг что мы попросили еще элемены
                                notSortedLoadMoreItems(totalItemCount);//тут я использовал калбэк который просто говорит наружу что нужно еще элементов и с какой позиции начинать загрузку
                            }
                        }
                    }
                }
            };
            sporList.setOnScrollChangeListener(scrollChangeListener);
        } else {
            RecyclerView.OnScrollListener scrollListener = new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    if (llm != null) {
                        int visibleItemCount = llm.getChildCount();//смотрим сколько элементов на экране
                        int totalItemCount = llm.getItemCount();//сколько всего элементов
                        int firstVisibleItems = llm.findFirstVisibleItemPosition();//какая позиция первого элемента

                        Log.w("visibleItemCount", Integer.toString(visibleItemCount));
                        Log.w("firstVisibleItems", Integer.toString(firstVisibleItems));
                        Log.w("totalItemCount", Integer.toString(totalItemCount));

                        if (!isLoading) {//проверяем, грузим мы что-то или нет, эта переменная должна быть вне класса  OnScrollListener
                            if ((visibleItemCount + firstVisibleItems) >= totalItemCount) {
                                isLoading = true;//ставим флаг что мы попросили еще элемены
                                notSortedLoadMoreItems(totalItemCount);//тут я использовал калбэк который просто говорит наружу что нужно еще элементов и с какой позиции начинать загрузку
                            }
                        }
                    }
                }
            };
            sporList.setOnScrollListener(scrollListener);
        }
    }

    private static void notSortedLoadMoreItems(final int itemCount) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                mData.addAll(api.getAllDisputes(10 + itemCount));

                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        notSortedAdapter.notifyDataSetChanged();
                    }
                });
                isLoading = false;
            }
        });
        thread.setDaemon(false);
        thread.start();
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        MainFragment.category = category;
    }

    public void setSubCategory(String subCategory) {
        MainFragment.subCategory = subCategory;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (notSortedAdapter != null) {
            for (int i = 0; i < notSortedAdapter.getViewCount(); i++) {
                notSortedAdapter.getView(i).removeTimer();
            }
        }
        if (adapter != null) {
            for (int i = 0; i < adapter.getViewCount(); i++) {
                adapter.getView(i).removeTimer();
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_main, container, false);

        api = new Api(getActivity());

        sporList = (RecyclerView) rootView.findViewById(R.id.spor_list);
        sporList.setHasFixedSize(true);
        llm = new LinearLayoutManager(this.getActivity());
        sporList.setLayoutManager(llm);

        if (!isSorted())
            notSortedSetAdapter();
        else {
            sortedBySubCategorySetAdapter();
        }
        MainActivity.setCurentFragment(this);

        getActivity().findViewById(R.id.my_toolbar).setVisibility(View.VISIBLE);
        getActivity().findViewById(R.id.shadow).setVisibility(View.VISIBLE);

        spinner = (Spinner) getActivity().findViewById(R.id.spinner);
        searchRight = (ImageView) getActivity().findViewById(R.id.search_right);
        searchLeft = (ImageView) getActivity().findViewById(R.id.search_left);
        searchField = (EditText) getActivity().findViewById(R.id.search_field);
        close = (ImageView) getActivity().findViewById(R.id.close);
        no_disputes = (TextView) rootView.findViewById(R.id.no_disputes);

        if (!isSorted()) {
            spinner.setVisibility(View.VISIBLE);
            getActivity().findViewById(R.id.title).setVisibility(View.GONE);
        }
        searchField.setVisibility(View.GONE);
        searchRight.setVisibility(View.VISIBLE);
        searchLeft.setVisibility(View.GONE);
        close.setVisibility(View.GONE);

        setSearchListeners();
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (MainActivity.isFerstOpened()) {
//                    MainActivity.showLoader();
                    if (!spinner.getSelectedItem().toString().equals("Все споры"))
                        sortedByTypeSetAdapter(spinner.getSelectedItem().toString());
                    else
                        notSortedSetAdapter();
                } else
                    MainActivity.setFerstOpened(true);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spinner.setSelection(0, false);

        return rootView;
    }

    private void setSearchListeners() {
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((EditText) MainActivity.getActivity().findViewById(R.id.search_field)).getText().clear();
                searchLeft.performClick();
                if (!isSorted()) {
                    if (spinner.getSelectedItem().toString().equals("Все споры")) {
                        notSortedSetAdapter();
                    } else {
                        spinner.setSelection(0, false);
                    }
                } else {
                    sortedBySubCategorySetAdapter();
                }
            }
        });

        searchField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                View focus = MainActivity.getActivity().getCurrentFocus();
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    //прячу клаву и не только
                    if (focus != null) {
                        InputMethodManager imm = (InputMethodManager) MainActivity.getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(focus.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                        if (searchField.getText().length() > 0) {
                            sortedBySearchSetAdapter();
                        }
                    }
                }
                return false;
            }
        });

        searchLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Animation animationRight = AnimationUtils.loadAnimation(MainActivity.getContext(), R.anim.mytrans_right_back);
                Animation animationLeft = AnimationUtils.loadAnimation(MainActivity.getContext(), R.anim.mytrans_left_back);
                Animation animationScale = AnimationUtils.loadAnimation(MainActivity.getContext(), R.anim.myscale_back);
                Animation animationClose = AnimationUtils.loadAnimation(MainActivity.getContext(), R.anim.show_close_back);

                searchRight.clearAnimation();
                searchLeft.clearAnimation();
                searchField.clearAnimation();
                close.clearAnimation();

                searchRight.setAnimation(animationRight);
                searchLeft.setAnimation(animationLeft);
                searchField.setAnimation(animationScale);
                close.setAnimation(animationClose);

                searchLeft.animate().setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animator) {
                        close.animate().setListener(new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationStart(Animator animator) {
                                close.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onAnimationEnd(Animator animator) {
                                close.setVisibility(View.GONE);
                            }

                            @Override
                            public void onAnimationCancel(Animator animator) {

                            }

                            @Override
                            public void onAnimationRepeat(Animator animator) {

                            }
                        }).start();
                    }

                    @Override
                    public void onAnimationEnd(Animator animator) {
                        if (MainActivity.getCurentFragment().getClass().equals(CategoryFragment.class)) {
                            MainActivity.getActivity().findViewById(R.id.title).setVisibility(View.VISIBLE);
                        } else if (MainActivity.getCurentFragment().getClass().equals(MainFragment.class)) {
                            if (isSorted()) {
                                getActivity().findViewById(R.id.title).setVisibility(View.VISIBLE);
                            } else {
                                getActivity().findViewById(R.id.spinner).setVisibility(View.VISIBLE);
                            }
                        }
                        searchLeft.setVisibility(View.GONE);
                        searchRight.setVisibility(View.VISIBLE);
                        searchField.setVisibility(View.GONE);
                        searchRight.animate().setListener(new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationStart(Animator animator) {

                            }

                            @Override
                            public void onAnimationEnd(Animator animator) {

                            }

                            @Override
                            public void onAnimationCancel(Animator animator) {

                            }

                            @Override
                            public void onAnimationRepeat(Animator animator) {

                            }
                        }).start();
                    }

                    @Override
                    public void onAnimationCancel(Animator animator) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animator) {

                    }
                }).start();
            }
        });
        searchRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                spinner = (Spinner) MainActivity.getActivity().findViewById(R.id.spinner);
                searchRight = (ImageView) MainActivity.getActivity().findViewById(R.id.search_right);
                searchLeft = (ImageView) MainActivity.getActivity().findViewById(R.id.search_left);
                searchField = (EditText) MainActivity.getActivity().findViewById(R.id.search_field);
                close = (ImageView) MainActivity.getActivity().findViewById(R.id.close);

                Animation animationRight = AnimationUtils.loadAnimation(MainActivity.getContext(), R.anim.mytrans_right);
                Animation animationLeft = AnimationUtils.loadAnimation(MainActivity.getContext(), R.anim.mytrans_left);
                Animation animationScale = AnimationUtils.loadAnimation(MainActivity.getContext(), R.anim.myscale);
                Animation animationShow = AnimationUtils.loadAnimation(MainActivity.getContext(), R.anim.show_close);

                searchRight.clearAnimation();
                searchLeft.clearAnimation();
                searchField.clearAnimation();
                close.clearAnimation();

                searchRight.setAnimation(animationRight);
                searchLeft.setAnimation(animationLeft);
                searchField.setAnimation(animationScale);
                close.setAnimation(animationShow);

                searchRight.animate().setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animator) {
                        if (MainActivity.getCurentFragment().getClass().equals(CategoryFragment.class)) {
                            MainActivity.getActivity().findViewById(R.id.title).setVisibility(View.GONE);
                        } else if (MainActivity.getCurentFragment().getClass().equals(MainFragment.class)) {
                            if (isSorted()) {
                                MainActivity.getActivity().findViewById(R.id.title).setVisibility(View.GONE);
                            } else {
                                MainActivity.getActivity().findViewById(R.id.spinner).setVisibility(View.VISIBLE);
                            }
                        }
                        searchField.setVisibility(View.VISIBLE);
                        MainActivity.getActivity().findViewById(R.id.spinner).setVisibility(View.GONE);
                        searchField.animate().start();
                        close.animate().setListener(new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationStart(Animator animator) {
                                close.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onAnimationEnd(Animator animator) {

                            }

                            @Override
                            public void onAnimationCancel(Animator animator) {

                            }

                            @Override
                            public void onAnimationRepeat(Animator animator) {

                            }
                        }).start();
                    }

                    @Override
                    public void onAnimationEnd(Animator animator) {
                        searchRight.setVisibility(View.GONE);
                        searchLeft.animate().setListener(new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationStart(Animator animator) {
                                searchLeft.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onAnimationEnd(Animator animator) {

                            }

                            @Override
                            public void onAnimationCancel(Animator animator) {

                            }

                            @Override
                            public void onAnimationRepeat(Animator animator) {

                            }
                        }).start();
                    }

                    @Override
                    public void onAnimationCancel(Animator animator) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animator) {

                    }
                }).start();
            }
        });
    }

    private void sortedByTypeSetAdapter(final String selType) {
        if (Build.VERSION.SDK_INT >= 23)
            sporList.setOnScrollChangeListener(null);
        else
            sporList.setOnScrollListener(null);

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                ArrayList<Dispute> allDisputes = api.getAllDisputes();
                mData.clear();

                for (Dispute d : allDisputes) {
                    String s = d.date + " " + d.time;
                    s = s.replace("/", ".");
                    SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy HH:mm");
                    try {
                        Date date = format.parse(s);
                        long curUnixTime = System.currentTimeMillis();
                        long progress = date.getTime() - curUnixTime;

                        String type;
                        if (progress < 0 && d.result.equals("")) {
                            type = "Споры в прямом эфире";
                        } else if (progress < 0 && !d.result.equals("")) {
                            type = "Завершённые споры";
                        } else {
                            type = "Ожидаемые споры";
                        }

                        if (selType.equals(type)) {
                            mData.add(d);
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                adapter = new SortedDisputeAdapter(rootView.getContext(), mData, isSorted(), subCategory != null);

                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        sporList.swapAdapter(adapter, true);
                        if (mData.size() == 0){
                            no_disputes.setText(getActivity().getString(R.string.no_disputes_type));
                            no_disputes.setVisibility(View.VISIBLE);
                        }else {
                            no_disputes.setVisibility(View.GONE);
                        }
                    }
                });
//                MainActivity.dismissWithAnimationLoader();
            }
        });
        thread.setDaemon(false);
        thread.start();
    }

    private void sortedBySearchSetAdapter() {
        if (Build.VERSION.SDK_INT >= 23)
            sporList.setOnScrollChangeListener(null);
        else
            sporList.setOnScrollListener(null);

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                ArrayList<Dispute> allDisputes = api.getAllDisputes();

                mData.clear();
                for (Dispute d : allDisputes) {
                    if (Tools.regex(searchField.getText().toString().toLowerCase(), d.subject.toLowerCase()))
                        mData.add(d);
                }
                adapter = new SortedDisputeAdapter(rootView.getContext(), mData, isSorted(), subCategory != null);

                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        sporList.swapAdapter(adapter, true);
                        if (mData.size() == 0){
                            no_disputes.setText(getActivity().getString(R.string.no_disputes_search));
                            no_disputes.setVisibility(View.VISIBLE);
                        }else {
                            no_disputes.setVisibility(View.GONE);
                        }
                    }
                });
//                MainActivity.dismissWithAnimationLoader();
            }
        });
        thread.setDaemon(false);
        thread.start();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
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























