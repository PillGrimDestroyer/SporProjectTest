package spor.automato.com.sporprojecttest.fragments;

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

import spor.automato.com.sporprojecttest.View.DisputeCell;
import spor.automato.com.sporprojecttest.R;
import spor.automato.com.sporprojecttest.models.Choice;
import spor.automato.com.sporprojecttest.models.Dispute;
import spor.automato.com.sporprojecttest.models.User;


public class MainFragment extends Fragment {

    FirebaseDatabase database;
    DatabaseReference reference;

    private User client;
    private String userID;

    FirebaseRecyclerAdapter<Dispute, DisputeCell> firebaseRecyclerAdapter;
    RecyclerView sporList;

    @Nullable
    @Override
     public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.fragment_main,container,false);

        this.sporList = (RecyclerView)rootview.findViewById(R.id.spor_list);
        sporList.setHasFixedSize(true);

        LinearLayoutManager llm = new LinearLayoutManager(this.getActivity());
        sporList.setLayoutManager(llm);

        database = FirebaseDatabase.getInstance();
        reference = database.getReference("spor");

        //TODO: это надо удалить (но только если это никому не нужно)
        /*reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot child : dataSnapshot.getChildren()){
                    Dispute dispute = child.getValue(Dispute.class);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });*/

        Query q = reference.orderByChild("category");

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        userID = mAuth.getCurrentUser().getUid();
        reference = database.getReference();
        reference.child("users").child(userID).orderByKey().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                client = dataSnapshot.getValue(User.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        this.firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Dispute, DisputeCell>(Dispute.class, R.layout.spor_cell_layout, DisputeCell.class, q) {
            @Override
            protected void populateViewHolder(DisputeCell viewHolder, Dispute model, int position) {
                viewHolder.setSporDate(model.date);
                viewHolder.setSporLikeCount(model.likeCount);
                viewHolder.setSporParticipantCount(model.participantCount);
                viewHolder.setSporStartTime(model.time);
                viewHolder.setSporSubject(model.subject);
                viewHolder.setViewCount(model.viewCount);

                boolean isLiked = false;
                if(model.likes != null) {
                    isLiked = model.likes.containsKey(userID);
                }
                viewHolder.setLiked(isLiked);
                viewHolder.setOnCardListener(getActivity(), model, client, database);
            }
        };

        sporList.setAdapter(firebaseRecyclerAdapter);
        return rootview;
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























