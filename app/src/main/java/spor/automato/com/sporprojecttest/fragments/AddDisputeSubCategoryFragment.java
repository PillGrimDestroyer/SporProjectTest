package spor.automato.com.sporprojecttest.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import spor.automato.com.sporprojecttest.Activity.MainActivity;
import spor.automato.com.sporprojecttest.Adapter.AddDisputeSubCategoryAdapter;
import spor.automato.com.sporprojecttest.R;
import spor.automato.com.sporprojecttest.models.Dispute;
import spor.automato.com.sporprojecttest.models.DisputeFromOlimp;

/**
 * Created by HAOR on 05.09.2017.
 */

public class AddDisputeSubCategoryFragment extends Fragment {

    private View rootView;
    private ExpandableListView expListView;
    private AddDisputeSubCategoryAdapter listAdapter;
    private List<String> listDataHeader;
    private HashMap<String, List<DisputeFromOlimp>> listDataChild;
    private FirebaseDatabase database;
    private DatabaseReference reference;
    private String category;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_dispute_choice, container, false);
        this.rootView = view;

        expListView = (ExpandableListView) rootView.findViewById(R.id.lvExp);

        TextView title = (TextView) getActivity().findViewById(R.id.title);
        title.setText("Выберите из списка");

        MainActivity.showLoader();
        writeData();

        MainActivity.setCurentFragment(this);
        return view;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    private void writeData() {
        listDataHeader = new ArrayList<>();
        listDataChild = new HashMap<>();

        database = FirebaseDatabase.getInstance();
        reference = database.getReference("parselist");

        reference.orderByKey().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    DisputeFromOlimp dispute = child.getValue(DisputeFromOlimp.class);
                    if (dispute.category.equals(category)) {
                        if (listDataChild.containsKey(dispute.subcategory)) {
                            listDataChild.get(dispute.subcategory).add(dispute);
                        } else {
                            List<DisputeFromOlimp> list = new ArrayList<>();
                            list.add(dispute);

                            listDataHeader.add(dispute.subcategory);
                            listDataChild.put(dispute.subcategory, list);
                        }
                    }
                }

                listAdapter = new AddDisputeSubCategoryAdapter(rootView.getContext(), listDataHeader, listDataChild);
                expListView.setAdapter(listAdapter);
                MainActivity.dismissWithAnimationLoader();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
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
