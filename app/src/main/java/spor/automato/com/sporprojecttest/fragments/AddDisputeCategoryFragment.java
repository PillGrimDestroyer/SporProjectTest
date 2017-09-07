package spor.automato.com.sporprojecttest.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import spor.automato.com.sporprojecttest.Activity.MainActivity;
import spor.automato.com.sporprojecttest.Adapter.AddDisputeCategoryAdapter;
import spor.automato.com.sporprojecttest.R;
import spor.automato.com.sporprojecttest.models.DisputeFromOlimp;
import spor.automato.com.sporprojecttest.models.Dispute;

/**
 * Created by HAOR on 05.09.2017.
 */

public class AddDisputeCategoryFragment extends Fragment {

    private View rootView;

    private AddDisputeCategoryAdapter adapter;
    private FirebaseDatabase database;
    private DatabaseReference reference;

    private ArrayList<DisputeFromOlimp> mData = new ArrayList<>();
    private RecyclerView list;
    private Button btn;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_dispute_category, container, false);
        this.rootView = view;

        database = FirebaseDatabase.getInstance();
        reference = database.getReference("parselist");

        this.list = (RecyclerView) rootView.findViewById(R.id.list);
        this.btn = (Button) rootView.findViewById(R.id.create_dispute_button);
        list.setHasFixedSize(true);

        LinearLayoutManager llm = new LinearLayoutManager(this.getActivity());
        list.setLayoutManager(llm);

        TextView title = (TextView) getActivity().findViewById(R.id.title);
        title.setText("Добавить спор");

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddDisputeFragment addDisputeFragment = new AddDisputeFragment();

                MainActivity.getFragmetManeger()
                        .beginTransaction()
                        .replace(R.id.main_fragment, addDisputeFragment, "fragment")
                        .setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                        .commit();
            }
        });

        MainActivity.showLoader();

        reference.orderByKey().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mData.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    DisputeFromOlimp d = ds.getValue(DisputeFromOlimp.class);
                    boolean have = false;
                    for (DisputeFromOlimp dis : mData) {
                        if (dis.category.equals(d.category)) {
                            have = true;
                            break;
                        }
                    }
                    if (!have) {
                        mData.add(d);
                    }
                }
                adapter = new AddDisputeCategoryAdapter(mData);
                list.setAdapter(adapter);
                MainActivity.dismissWithAnimationLoader();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        MainActivity.setCurentFragment(this);
        return view;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        // write logic here b'z it is called when fragment is visible to user
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
