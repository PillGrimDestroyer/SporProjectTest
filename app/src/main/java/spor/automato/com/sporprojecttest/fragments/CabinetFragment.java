package spor.automato.com.sporprojecttest.fragments;

import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.firebase.client.annotations.Nullable;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import spor.automato.com.sporprojecttest.Adapter.UsersDisputeAdapter;
import spor.automato.com.sporprojecttest.Api.Api;
import spor.automato.com.sporprojecttest.Api.DisputeSorter;
import spor.automato.com.sporprojecttest.R;
import spor.automato.com.sporprojecttest.models.Dispute;
import spor.automato.com.sporprojecttest.models.Participant;
import spor.automato.com.sporprojecttest.models.User;

import static android.content.ContentValues.TAG;


public class CabinetFragment extends Fragment implements View.OnClickListener{

    ImageView userProfileImage;
    TextView userName;
    TextView userEmail;
    TextView userAge;
    TextView userMoney;
    TextView sporLabel;

    FloatingActionButton activ;
    FloatingActionButton finished;
    FloatingActionButton wait;


    ProgressBar progressBar;
    RecyclerView userDisputesList;


    FirebaseStorage storage;
    StorageReference storageReference;
    FirebaseDatabase myDatabase;
    DatabaseReference reference;
    FirebaseAuth mAuth;

    DisputeSorter disputeSorter;
    ArrayList<Dispute> userDisputes = new ArrayList<Dispute>();
    ArrayList<Dispute> allDisputes = new ArrayList<Dispute>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.fragment_cabinet,container,false);

        this.storage = FirebaseStorage.getInstance();
        this.storageReference = storage.getReference();
        this.myDatabase = FirebaseDatabase.getInstance();
        this.reference = myDatabase.getReference();
        this.mAuth = FirebaseAuth.getInstance();
        this.initViews(rootview);
        this.loadUserData();
        this.loading();

        return rootview;
    }

    public void loading(){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("spor");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<Dispute> disputes  = new ArrayList<Dispute>();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Dispute dispute = snapshot.getValue(Dispute.class);
                    for (DataSnapshot child : snapshot.getChildren()) {
                        if (child.getKey().equalsIgnoreCase("participants")) {
                            for (DataSnapshot part : child.getChildren()) {
                                Participant participant = part.getValue(Participant.class);
                                dispute.addParticipant(participant);
                                disputes.add(dispute);
                            }
                        }
                    }
                }
                allDisputes = disputes;
                setList(allDisputes);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void setList(ArrayList<Dispute> allDisputes){
        disputeSorter = new DisputeSorter(allDisputes);
        userDisputes = disputeSorter.getUserDisputes();
        userDisputesList.setAdapter(new UsersDisputeAdapter(userDisputes));
    }


    public void loadUserData(){
        String userID = mAuth.getCurrentUser().getUid();
        reference.child("users").child(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User client = dataSnapshot.getValue(User.class);
                userName.setText(client.name);
                userEmail.setText(client.email);
                userAge.setText(client.birthday);
                userMoney.setText(client.money+" тг");
                loadingClientProfile(client.id);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.i(TAG, "Error"+ databaseError.toException());
            }
        });
    }

    public void loadingClientProfile(String userID){
        storageReference.child("Photos").child(userID).getBytes(1024*1024).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Drawable image = new BitmapDrawable(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
                userProfileImage.setImageDrawable(image);
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    public void initViews(View rootView){
        this.userProfileImage = (ImageView)rootView.findViewById(R.id.client_profile);
        this.userName = (TextView)rootView.findViewById(R.id.client_name);
        this.userEmail = (TextView)rootView.findViewById(R.id.client_email);
        this.userAge = (TextView)rootView.findViewById(R.id.client_age);
        this.userMoney = (TextView)rootView.findViewById(R.id.client_money);
        this.sporLabel = (TextView)rootView.findViewById(R.id.client_spors_label);

        this.activ = (FloatingActionButton)rootView.findViewById(R.id.activ);
            activ.setOnClickListener(this);
        this.finished = (FloatingActionButton)rootView.findViewById(R.id.finished);
            finished.setOnClickListener(this);
        this.wait = (FloatingActionButton)rootView.findViewById(R.id.wait);
            wait.setOnClickListener(this);


        this.progressBar = (ProgressBar)rootView.findViewById(R.id.client_image_progress_bar);
        this.userDisputesList = (RecyclerView)rootView.findViewById(R.id.clientSpors);
        this.userDisputesList.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this.getActivity());
        llm.setOrientation(LinearLayoutManager.HORIZONTAL);
        userDisputesList.setLayoutManager(llm);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.activ:
                Toast.makeText(this.getActivity(), "Activ Pressed", Toast.LENGTH_SHORT).show();

            case R.id.finished:
                Toast.makeText(this.getActivity(), "Finished Pressed", Toast.LENGTH_SHORT).show();

            case R.id.wait:
                Toast.makeText(this.getActivity(), "Wait Pressed", Toast.LENGTH_SHORT).show();

            default:
                    break;
        }
    }
}


