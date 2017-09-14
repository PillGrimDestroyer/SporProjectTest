package com.automato.aigerim.spor.Fragments;

import android.animation.Animator;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;

import com.automato.aigerim.spor.Activity.MainActivity;
import com.automato.aigerim.spor.Adapter.SortedDisputeAdapter;
import com.automato.aigerim.spor.Models.Dispute;
import com.automato.aigerim.spor.Models.User;
import com.automato.aigerim.spor.Other.TimerTask.MyTimerTask;
import com.automato.aigerim.spor.Other.Tools.Tools;
import com.automato.aigerim.spor.R;
import com.automato.aigerim.spor.View.DisputeCell;


public class MainFragment extends Fragment {

    private Tools tools = new Tools();

    private FirebaseDatabase database;
    private DatabaseReference reference;
    private FirebaseAuth mAuth;
    private View rootView;

    private User client;
    private String userID;
    private boolean isSorted = false;
    private String category;
    private String subCategory;
    private boolean fLoad = false;

    private FirebaseRecyclerAdapter<Dispute, DisputeCell> firebaseRecyclerAdapter;
    private SortedDisputeAdapter adapter;
    private ArrayList<Dispute> mData = new ArrayList<>();
    private RecyclerView sporList;
    private Timer myTimer;
    private MyTimerTask task;
    private Spinner spinner;
    private ImageView searchRight;
    private ImageView searchLeft;
    private EditText searchField;
    private ImageView close;

    public boolean isSorted() {
        return isSorted;
    }

    public void setSorted(boolean sorted) {
        isSorted = sorted;
    }

    public String getCategory() {
        return this.category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setSubCategory(String subCategory) {
        this.subCategory = subCategory;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        MainActivity.showLoader();

        database = FirebaseDatabase.getInstance();
        reference = database.getReference("spor");
        mAuth = FirebaseAuth.getInstance();
        userID = mAuth.getCurrentUser().getUid();
        myTimer = null;

        final View rootView;
        if (!isSorted())
            rootView = notSortedData(inflater, container, savedInstanceState);
        else {
            rootView = sortedBySubCategoryData(inflater, container, savedInstanceState);
        }
        this.rootView = rootView;
        MainActivity.setCurentFragment(this);

        spinner = (Spinner) getActivity().findViewById(R.id.spinner);
        searchRight = (ImageView) getActivity().findViewById(R.id.search_right);
        searchLeft = (ImageView) getActivity().findViewById(R.id.search_left);
        searchField = (EditText) getActivity().findViewById(R.id.search_field);
        close = (ImageView) getActivity().findViewById(R.id.close);

        if (!isSorted()){
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
                    MainActivity.showLoader();
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
                if (!isSorted()){
                    if(spinner.getSelectedItem().toString().equals("Все споры")){
                        notSortedSetAdapter();
                    }else {
                        spinner.setSelection(0, false);
                    }
                }else {
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

    public View sortedBySubCategoryData(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootview = inflater.inflate(R.layout.fragment_main, container, false);

        this.sporList = (RecyclerView) rootview.findViewById(R.id.spor_list);
        sporList.setHasFixedSize(true);

        LinearLayoutManager llm = new LinearLayoutManager(this.getActivity());
        sporList.setLayoutManager(llm);

        sortedBySubCategorySetAdapter();

        return rootview;
    }

    public View notSortedData(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.fragment_main, container, false);

        this.sporList = (RecyclerView) rootview.findViewById(R.id.spor_list);
        sporList.setHasFixedSize(true);

        LinearLayoutManager llm = new LinearLayoutManager(this.getActivity());
        sporList.setLayoutManager(llm);
        notSortedSetAdapter();

        return rootview;
    }

    private void sortedByTypeSetAdapter(final String selType) {
        if (mData != null)
            mData.clear();
        reference.orderByKey().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Dispute d = ds.getValue(Dispute.class);
                    final String s = d.date + " " + d.time;
                    SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy HH:mm");
                    try {
                        Date date = format.parse(s);
                        long curUnixTime = System.currentTimeMillis();
                        long progress = date.getTime() - curUnixTime;

                        String type;
                        if (progress < 0 && d.result.equals("")) {
                            type = "Активные споры";
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
                adapter = new SortedDisputeAdapter(rootView.getContext(), mData, userID, database, isSorted(), subCategory != null);
                sporList.setAdapter(adapter);
                MainActivity.dismissWithAnimationLoader();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void sortedBySearchSetAdapter() {
        MainActivity.showLoader();
        reference.orderByKey().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mData.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Dispute d = ds.getValue(Dispute.class);
                    boolean p = tools.regex(searchField.getText().toString().toLowerCase(), d.subject.toLowerCase());
                    if (p) {
                        mData.add(d);
                    }
                }
                adapter = new SortedDisputeAdapter(rootView.getContext(), mData, userID, database, isSorted(), subCategory != null);
                sporList.setAdapter(adapter);
                MainActivity.dismissWithAnimationLoader();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void sortedBySubCategorySetAdapter() {
        reference.orderByKey().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mData.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Dispute d = ds.getValue(Dispute.class);
                    if (category.equals(d.category) && subCategory.equals(d.subcategory)) {
                        mData.add(d);
                    }
                }
                adapter = new SortedDisputeAdapter(rootView.getContext(), mData, userID, database, isSorted(), subCategory != null);
                sporList.setAdapter(adapter);
                MainActivity.dismissWithAnimationLoader();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void notSortedSetAdapter() {
        Query q = reference.orderByChild("category");

        this.firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Dispute, DisputeCell>(Dispute.class, R.layout.spor_cell_layout, DisputeCell.class, q) {
            @Override
            protected void populateViewHolder(DisputeCell viewHolder, Dispute model, int position) {
                viewHolder.setSporDate(model.date);
                viewHolder.setSporLikeCount(model.likeCount);
                viewHolder.setSporParticipantCount(model.participantCount, model, userID);
                viewHolder.setSporStartTime(model.time);
                viewHolder.setSporSubject(model.subject);
                viewHolder.setViewCount(model.viewCount);
                viewHolder.setCategory(model.category);
                viewHolder.setSubCategory(model.subcategory);
                viewHolder.setSorted(isSorted());
                viewHolder.setSortedBySubCategory(subCategory != null);
                viewHolder.setImage(model.photo);
                viewHolder.setRate(model);

                boolean isLiked = false;
                if (model.likes != null) {
                    isLiked = model.likes.containsKey(userID);
                }
                viewHolder.setLiked(isLiked);
                viewHolder.setOnCardListener(model, database);
                if (myTimer == null) {
                    myTimer = new Timer();
                    task = new MyTimerTask();

                    task.disputes = new ArrayList<>();
                    task.disputeCells = new ArrayList<>();

                    task.disputeCells.add(viewHolder);
                    task.disputes.add(model);
                    myTimer.schedule(task, 0, task.time);
                } else {
                    try {
                        task.disputes.remove(position);
                        task.disputeCells.remove(position);
                    } catch (Exception e) {

                    }

                    task.disputes.add(position, model);
                    task.disputeCells.add(position, viewHolder);
                }
                MainActivity.dismissWithAnimationLoader();
            }
        };

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() == null)
                    MainActivity.dismissWithAnimationLoader();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        sporList.setAdapter(firebaseRecyclerAdapter);
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























