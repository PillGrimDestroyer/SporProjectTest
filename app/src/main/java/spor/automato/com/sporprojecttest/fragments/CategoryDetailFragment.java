package spor.automato.com.sporprojecttest.fragments;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import spor.automato.com.sporprojecttest.Adapter.GridAdapter;
import spor.automato.com.sporprojecttest.R;
import spor.automato.com.sporprojecttest.View.CategoryDetailCell;
import spor.automato.com.sporprojecttest.View.DisputeCell;
import spor.automato.com.sporprojecttest.models.Dispute;

/**
 * Created by Apofis on 25.08.2017.
 */

public class CategoryDetailFragment extends Fragment implements GridAdapter.ItemClickListener {

    private FirebaseDatabase database;

    private GridAdapter adapter;
    private View rootView;
    private String category;
    private RecyclerView recyclerView;
    private FirebaseRecyclerAdapter<Dispute, CategoryDetailCell> firebaseRecyclerAdapter;
    private ArrayList<String> mData = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_category_detail,container,false);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.spor_list);
        recyclerView.setHasFixedSize(true);

        int numberOfColumns = 2;
        recyclerView.setLayoutManager(new GridLayoutManager(rootView.getContext(), numberOfColumns));

        /*database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("spor");
        Query q = reference.orderByChild("category").equalTo(category);
        this.firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Dispute, CategoryDetailCell>(Dispute.class, R.layout.design_category_detail_cell, CategoryDetailCell.class, q) {
            @Override
            protected void populateViewHolder(CategoryDetailCell viewHolder, Dispute model, int position) {
                if(!viewHolder.getData().contains(model.subcategory) && category.equals(model.category)) {
                    viewHolder.addData(model.subcategory);
                    viewHolder.setText(model.subcategory);
                }
            }
        };

        recyclerView.setAdapter(firebaseRecyclerAdapter);*/

        database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("spor");

        reference.orderByKey().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds:dataSnapshot.getChildren()) {
                    boolean hasSubCat = ds.hasChild("subcategory");
                    boolean hasCat = ds.hasChild("category");
                    Dispute d = ds.getValue(Dispute.class);
                    if(hasSubCat && hasCat){
                        if(!mData.contains(d.subcategory) && category.equals(d.category))
                            mData.add(d.subcategory);
                    }
                }
                adapter = new GridAdapter(rootView.getContext(), category, mData);
                adapter.setClickListener(CategoryDetailFragment.this);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return rootView;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        // write logic here b'z it is called when fragment is visible to user

    }

    @Override
    public void onItemClick(View view, int position) {
        String str = "You clicked " + adapter.getItem(position) + ", which is at cell position " + position;
        Toast.makeText(rootView.getContext(), str, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
