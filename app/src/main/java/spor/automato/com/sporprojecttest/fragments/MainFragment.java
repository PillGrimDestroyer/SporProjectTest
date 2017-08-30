package spor.automato.com.sporprojecttest.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Timer;

import cn.pedant.SweetAlert.SweetAlertDialog;
import spor.automato.com.sporprojecttest.Activity.MainActivity;
import spor.automato.com.sporprojecttest.Adapter.SortedDisputeAdapter;
import spor.automato.com.sporprojecttest.MyTimerTask;
import spor.automato.com.sporprojecttest.R;
import spor.automato.com.sporprojecttest.View.DisputeCell;
import spor.automato.com.sporprojecttest.models.Dispute;
import spor.automato.com.sporprojecttest.models.User;


public class MainFragment extends Fragment {

    private FirebaseDatabase database;
    private DatabaseReference reference;
    private View rootView;

    private User client;
    private String userID;
    private boolean isSorted = false;
    private String category;
    private String subCategory;

    private FirebaseRecyclerAdapter<Dispute, DisputeCell> firebaseRecyclerAdapter;
    private SortedDisputeAdapter adapter;
    private ArrayList<Dispute> mData = new ArrayList<>();
    private RecyclerView sporList;
    private Timer myTimer;
    private MyTimerTask task;
    private SweetAlertDialog mProgressDialog;

    public boolean isSorted() {
        return isSorted;
    }

    public void setSorted(boolean sorted) {
        isSorted = sorted;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCategory() {
        return this.category;
    }

    public void setSubCategory(String subCategory) {
        this.subCategory = subCategory;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView;
        if (!isSorted())
            rootView = notSortedData(inflater, container, savedInstanceState);
        else {
            if (subCategory != null)
                rootView = sortedBySubCategoryData(inflater, container, savedInstanceState);
            else
                rootView = sortedByCategoryData(inflater, container, savedInstanceState);
        }
        this.rootView = rootView;
        MainActivity.setCurentFragment(this);

        mProgressDialog = new SweetAlertDialog(rootView.getContext(), SweetAlertDialog.PROGRESS_TYPE);
        mProgressDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        mProgressDialog.setTitleText("Загрузка");
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();

        return rootView;
    }

    public View sortedByCategoryData(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootview = inflater.inflate(R.layout.fragment_main, container, false);

        this.sporList = (RecyclerView) rootview.findViewById(R.id.spor_list);
        sporList.setHasFixedSize(true);

        LinearLayoutManager llm = new LinearLayoutManager(this.getActivity());
        sporList.setLayoutManager(llm);

        database = FirebaseDatabase.getInstance();
        reference = database.getReference("spor");

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        userID = mAuth.getCurrentUser().getUid();

        reference.orderByKey().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Dispute d = ds.getValue(Dispute.class);
                    if (category.equals(d.category)) {
                        mData.add(d);
                    }
                }
                adapter = new SortedDisputeAdapter(rootView.getContext(), mData, userID, database, isSorted(), subCategory != null);
                sporList.setAdapter(adapter);
                mProgressDialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return rootview;
    }

    public View sortedBySubCategoryData(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootview = inflater.inflate(R.layout.fragment_main, container, false);

        this.sporList = (RecyclerView) rootview.findViewById(R.id.spor_list);
        sporList.setHasFixedSize(true);

        LinearLayoutManager llm = new LinearLayoutManager(this.getActivity());
        sporList.setLayoutManager(llm);

        database = FirebaseDatabase.getInstance();
        reference = database.getReference("spor");

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        userID = mAuth.getCurrentUser().getUid();

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
                mProgressDialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return rootview;
    }

    public View notSortedData(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.fragment_main, container, false);

        this.sporList = (RecyclerView) rootview.findViewById(R.id.spor_list);
        sporList.setHasFixedSize(true);

        LinearLayoutManager llm = new LinearLayoutManager(this.getActivity());
        sporList.setLayoutManager(llm);

        database = FirebaseDatabase.getInstance();
        reference = database.getReference("spor");

        Query q = reference.orderByChild("category");

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        userID = mAuth.getCurrentUser().getUid();

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
                viewHolder.setImage();

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
                    task.disputes.add(model);
                    task.disputeCells.add(viewHolder);
                }
                if (mProgressDialog != null){
                    mProgressDialog.dismiss();
                    mProgressDialog = null;
                }
            }
        };

        sporList.setAdapter(firebaseRecyclerAdapter);
        return rootview;
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























