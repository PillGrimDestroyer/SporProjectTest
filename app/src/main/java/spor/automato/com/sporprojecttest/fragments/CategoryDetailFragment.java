package spor.automato.com.sporprojecttest.fragments;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

import spor.automato.com.sporprojecttest.Adapter.CategoryDetailAdapter;
import spor.automato.com.sporprojecttest.MainActivity;
import spor.automato.com.sporprojecttest.R;
import spor.automato.com.sporprojecttest.models.Choice;
import spor.automato.com.sporprojecttest.models.Dispute;

/**
 * Created by HAOR on 25.08.2017.
 */

public class CategoryDetailFragment extends Fragment implements CategoryDetailAdapter.ItemClickListener {

    private FirebaseDatabase database;

    private CategoryDetailAdapter adapter;
    private View rootView;
    private String category;
    private RecyclerView recyclerView;
    private HashMap<Integer, Dispute> mData = new HashMap<>();

    public void setCategory(String category) {
        this.category = category;
    }

    public void setImage(){
        FrameLayout sporImage = (FrameLayout)rootView.findViewById(R.id.imageLayout);

        int drawable;
        if(category.equals("Футбол"))
            drawable = R.drawable.football_header;
        else if(category.equals("Баскетбол"))
            drawable = R.drawable.basket_header;
        else if(category.equals("Бокс"))
            drawable = R.drawable.box_header;
        else if(category.equals("Теннис"))
            drawable = R.drawable.tennis_header;
        else if(category.equals("Борьба"))
            drawable = R.drawable.cat_wrestling;
        else
            drawable = R.drawable.cat_volleyball;

        sporImage.setBackgroundResource(drawable);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_category_detail,container,false);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.spor_list);
        recyclerView.setHasFixedSize(true);

        int numberOfColumns = 2;
        recyclerView.setLayoutManager(new GridLayoutManager(rootView.getContext(), numberOfColumns));

        database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("spor");
        setImage();

        reference.orderByKey().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int i = 0;
                for (DataSnapshot ds:dataSnapshot.getChildren()) {
                    boolean hasSubCat = ds.hasChild("subcategory");
                    boolean hasCat = ds.hasChild("category");
                    Dispute d = ds.getValue(Dispute.class);
                    if(hasSubCat && hasCat){
                        boolean alreadyHave = false;
                        for (int j = 0; j < mData.size(); j++) {
                            if (mData.get(j).subcategory.equals(d.subcategory))
                                alreadyHave = true;
                        }
                        if(!alreadyHave && category.equals(d.category)) {
                            mData.put(i, d);
                            i++;
                        }
                    }
                }
                adapter = new CategoryDetailAdapter(rootView.getContext(), category, mData);
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
        MainFragment ddf = new MainFragment();

        ddf.setCategory(adapter.getItem(position).category);
        ddf.setSubCategory(adapter.getItem(position).subcategory);
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
