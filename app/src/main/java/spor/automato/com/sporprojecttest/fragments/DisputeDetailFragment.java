package spor.automato.com.sporprojecttest.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import spor.automato.com.sporprojecttest.R;
import spor.automato.com.sporprojecttest.models.Dispute;
import spor.automato.com.sporprojecttest.models.Participant;
import spor.automato.com.sporprojecttest.models.User;

/**
 * Created by HAOR on 23.08.2017.
 */

public class DisputeDetailFragment extends Fragment {

    View rootview;

    private String date;
    private String subject;
    private String time;
    private int money;
    private int numberOfParticipant;
    private int numberOfLikes;
    private Dispute dispute;

    private String ferstTeam;
    private User client;
    private String secondTeam;

    private RadioButton fTeam;
    private RadioButton sTeam;
    private Button submit;
    private TextView rate;
    private String selectedChoice;
    private FirebaseDatabase myDatabase;

    public String getFerstTeam() {
        return ferstTeam;
    }

    public void setFerstTeam(String ferstTeam) {
        this.ferstTeam = ferstTeam;
    }

    public String getSecondTeam() {
        return secondTeam;
    }

    public void setSecondTeam(String secondTeam) {
        this.secondTeam = secondTeam;
    }

    public int getNumberOfLikes() {
        return numberOfLikes;
    }

    public void setNumberOfLikes(int numberOfLikes) {
        this.numberOfLikes = numberOfLikes;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getNumberOfParticipant() {
        return numberOfParticipant;
    }

    public void setNumberOfParticipant(int numberOfParticipant) {
        this.numberOfParticipant = numberOfParticipant;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootview = inflater.inflate(R.layout.fragment_dispute_detail,container,false);
        setSporDate(getDate());
        setSporParticipantCount(getNumberOfParticipant());
        setSporLikeCount(getNumberOfLikes());
        setSporSubject(getSubject());
        setSporStartTime(getTime());

        myDatabase = FirebaseDatabase.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String userID = mAuth.getCurrentUser().getUid();
        DatabaseReference reference = myDatabase.getReference();
        reference.child("users").child(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                client = dataSnapshot.getValue(User.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        submit = (Button) rootview.findViewById(R.id.submit);
        fTeam = (RadioButton) rootview.findViewById(R.id.ferstTeam);
        sTeam = (RadioButton) rootview.findViewById(R.id.secondTeam);
        rate = (TextView) rootview.findViewById(R.id.rate);

        fTeam.setText(getFerstTeam());
        sTeam.setText(getSecondTeam());

        fTeam.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b)
                    selectedChoice = getFerstTeam();
            }
        });

        sTeam.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b)
                    selectedChoice = getSecondTeam();
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(selectedChoice != null) {
                    if (rate.getText() != null && !rate.getText().toString().equals("")) {
                        int rateValue = Integer.parseInt(rate.getText().toString());
                        if ( rateValue <= (int) client.money) {
                            DatabaseReference refParticipant = myDatabase.getReference("spor/" + dispute.id + "/participants");
                            if(refParticipant.child(client.id) == null) {
                                Participant participant = new Participant();
                                dispute.money += rateValue;
                                dispute.participantCount += 1;

                                DatabaseReference participantCount = myDatabase.getReference("spor/" + dispute.id + "/participantCount");
                                participantCount.setValue(dispute.participantCount);

                                DatabaseReference sporMoney = myDatabase.getReference("spor/" + dispute.id + "/money");
                                sporMoney.setValue(dispute.money);

                                participant.choice = selectedChoice;
                                participant.money = rateValue;
                                participant.spor_id = dispute.id;
                                participant.user_id = client.id;
                                participant.winnings = 0;
                                
                                refParticipant.child(client.id).setValue(participant);

                                Toast.makeText(rootview.getContext(), R.string.stali_uchastnikom, Toast.LENGTH_SHORT).show();
                            }else {
                                Toast.makeText(rootview.getContext(), R.string.already_in, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(rootview.getContext(), R.string.not_enough_money, Toast.LENGTH_SHORT).show();
                        }
                    }else {
                        Toast.makeText(rootview.getContext(), R.string.money_first, Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(rootview.getContext(), R.string.choose_first, Toast.LENGTH_SHORT).show();
                }
            }
        });

        return rootview;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        // write logic here b'z it is called when fragment is visible to user

    }

    private void setSporDate(String date){
        TextView sporDate = (TextView) rootview.findViewById(R.id.spor_date);
        sporDate.setText(date);
    }

    private void setSporParticipantCount(int numberOfParticipant){
        TextView sporParticipantCount = (TextView) rootview.findViewById(R.id.viewers_count);
        sporParticipantCount.setText(Integer.toString(numberOfParticipant));
    }

    private void setSporLikeCount(int numberOfLikes){
        TextView sporLikeCount = (TextView) rootview.findViewById(R.id.like_count);
        sporLikeCount.setText(Integer.toString(numberOfLikes));
    }

    private void setSporSubject(String subject){
        TextView sporSubject = (TextView) rootview.findViewById(R.id.spor_subject);
        sporSubject.setText(subject);
    }

    private void setSporStartTime(String time){
        TextView startTime = (TextView) rootview.findViewById(R.id.spor_time);
        startTime.setText(time);
    }

    private void setImage(){
        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseStorage.getInstance().getReference().child("Photos").child(userID);
        ImageView sporImage = (ImageView)rootview.findViewById(R.id.spor_Image);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public void setDispute(Dispute dispute) {
        this.dispute = dispute;
    }
}
