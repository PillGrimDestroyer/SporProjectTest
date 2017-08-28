package spor.automato.com.sporprojecttest.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

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
    private String viewCount;
    private String ferstTeam;
    private String secondTeam;
    private int totalDisputeMoney;
    private int numberOfParticipant;
    private int numberOfLikes;
    private boolean isLikedByCurentUser;
    private Dispute dispute;

    private User client;
    private FirebaseDatabase myDatabase;

    private RadioButton fTeam;
    private RadioButton sTeam;
    private Button submit;
    private TextView rate;
    private String selectedChoice;

    public String getFerstTeam() {
        return ferstTeam;
    }

    public void setFerstTeam(String ferstTeam) {
        this.ferstTeam = ferstTeam;
    }

    public String getViewCount() {
        return viewCount;
    }

    public boolean isLikedByCurentUser() {
        return isLikedByCurentUser;
    }

    public void setLikedByCurentUser(boolean likedByCurentUser) {
        isLikedByCurentUser = likedByCurentUser;
    }

    public void setViewCount(String viewCount) {
        this.viewCount = viewCount;
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
        rootview = inflater.inflate(R.layout.fragment_dispute_detail, container, false);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String userID = mAuth.getCurrentUser().getUid();
        DatabaseReference reference = myDatabase.getReference();
        reference.child("users").child(userID).orderByKey().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                client = dataSnapshot.getValue(User.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        setSporDate(getDate());
        setSporParticipantCount(getNumberOfParticipant());
        setSporLikeCount(getNumberOfLikes());
        setSporSubject(getSubject());
        setSporStartTime(getTime());
        setViewsCount(getViewCount());
        setLikeImage(isLikedByCurentUser());
        setImage();

        dispute.viewCount += 1;

        DatabaseReference viewCount = myDatabase.getReference("spor/" + dispute.id + "/viewCount");
        viewCount.setValue(dispute.viewCount);

        submit = (Button) rootview.findViewById(R.id.submit);
        fTeam = (RadioButton) rootview.findViewById(R.id.ferstTeam);
        sTeam = (RadioButton) rootview.findViewById(R.id.secondTeam);
        rate = (TextView) rootview.findViewById(R.id.rate);
        ProgressBar progressBar = (ProgressBar) rootview.findViewById(R.id.progress_bar);

        fTeam.setText(getFerstTeam());
        sTeam.setText(getSecondTeam());

        final String s = dispute.date + " " + dispute.time;
        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        try {
            Date date = format.parse(s);
            long curUnixTime = System.currentTimeMillis();
            long progress = date.getTime() - curUnixTime;
            int progressInt = 0;
            if (progress < 0){
                TextView progressText = (TextView) rootview.findViewById(R.id.progress_text);
                progressBar.setProgress(100);
                progressBar.setVisibility(View.GONE);
                progressText.setVisibility(View.VISIBLE);
                if (dispute.result.equals(""))
                    progressText.setText("Live");
                else
                    progressText.setText(dispute.result);
            }
            else {
                progressInt = Integer.parseInt(Long.toString(progress / 100000000));
                progressBar.setProgress(100 - progressInt);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }


        fTeam.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b)
                    selectedChoice = getFerstTeam();
            }
        });

        sTeam.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b)
                    selectedChoice = getSecondTeam();
            }
        });

        rootview.findViewById(R.id.imageLike).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView sporLikeCount = (TextView) rootview.findViewById(R.id.like_count);
                int likeCount;
                if(!isLikedByCurentUser()) {
                    likeCount = Integer.parseInt(sporLikeCount.getText().toString());
                    likeCount++;

                    if(dispute.likes != null){
                        if(!dispute.likes.containsKey(client.id)) {
                            dispute.likes.put(client.id, true);
                        }
                    }else {
                        HashMap<String, Boolean> likes = new HashMap<>();
                        likes.put(client.id, true);
                        dispute.likes = likes;
                    }
                }else {
                    likeCount = Integer.parseInt(sporLikeCount.getText().toString());
                    likeCount--;

                    dispute.likes.remove(client.id);
                }

                DatabaseReference sporLikeCountInDB = myDatabase.getReference("spor/" + dispute.id + "/likeCount");
                sporLikeCountInDB.setValue(likeCount);

                DatabaseReference sporLikes = myDatabase.getReference("spor/" + dispute.id + "/likes");
                sporLikes.setValue(dispute.likes);

                ImageView like = (ImageView)view.findViewById(R.id.imageLike);
                if(!isLikedByCurentUser()) {
                    like.setImageResource(R.drawable.like);
                    setLikedByCurentUser(true);
                }else {
                    like.setImageResource(R.drawable.like_dark);
                    setLikedByCurentUser(false);
                }
                sporLikeCount.setText(Integer.toString(likeCount));
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedChoice != null) {
                    if (rate.getText() != null && !rate.getText().toString().equals("")) {
                        int rateValue = Integer.parseInt(rate.getText().toString());
                        if (rateValue <= (int) client.money) {
                            if(dispute.participants == null){
                                dispute.participants = new HashMap<>();
                            }
                            if (!dispute.participants.containsKey(client.id)) {
                                Participant participant = new Participant();
                                dispute.money += rateValue;
                                client.money -= rateValue;
                                dispute.participantCount += 1;

                                DatabaseReference participantCount = myDatabase.getReference("spor/" + dispute.id + "/participantCount");
                                participantCount.setValue(dispute.participantCount);

                                DatabaseReference sporMoney = myDatabase.getReference("spor/" + dispute.id + "/totalDisputeMoney");
                                sporMoney.setValue(dispute.money);

                                DatabaseReference clientMoney = myDatabase.getReference("users/" + client.id + "/money");
                                clientMoney.setValue(client.money);

                                participant.choice = selectedChoice;
                                participant.money = rateValue;
                                participant.spor_id = dispute.id;
                                participant.user_id = client.id;
                                participant.winnings = 0;

                                DatabaseReference refParticipant = myDatabase.getReference("spor/" + dispute.id + "/participants");
                                refParticipant.child(client.id).setValue(participant);

                                Toast.makeText(rootview.getContext(), R.string.stali_uchastnikom, Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(rootview.getContext(), R.string.already_in, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(rootview.getContext(), R.string.not_enough_money, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(rootview.getContext(), R.string.money_first, Toast.LENGTH_SHORT).show();
                    }
                } else {
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

    private void setViewsCount(String viewsCount) {
        TextView viewCount = (TextView) rootview.findViewById(R.id.view_count);
        viewCount.setText(viewsCount);
    }

    private void setSporParticipantCount(int numberOfParticipant){
        TextView sporParticipantCount = (TextView) rootview.findViewById(R.id.viewers_count);
        sporParticipantCount.setText(Integer.toString(numberOfParticipant));
        if (numberOfParticipant > 0) {
            ImageView participant = (ImageView)rootview.findViewById(R.id.imageParticCount);
            participant.setImageResource(R.drawable.people_black);
        }
    }

    private void setSporLikeCount(int numberOfLikes){
        TextView sporLikeCount = (TextView) rootview.findViewById(R.id.like_count);
        sporLikeCount.setText(Integer.toString(numberOfLikes));
        if(numberOfLikes > 0) {
            ImageView like = (ImageView)rootview.findViewById(R.id.imageLike);
            like.setImageResource(R.drawable.like);
        }
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
        ImageView sporImage = (ImageView)rootview.findViewById(R.id.spor_Image);

        int drawable;
        if(dispute.category.equals("Футбол"))
            drawable = R.drawable.cat_foot;
        else if(dispute.category.equals("Баскетбол"))
            drawable = R.drawable.cat_bask;
        else if(dispute.category.equals("Бокс"))
            drawable = R.drawable.cat_boxing;
        else if(dispute.category.equals("Теннис"))
            drawable = R.drawable.cat_ten;
        else if(dispute.category.equals("Борьба"))
            drawable = R.drawable.cat_wrestling;
        else
            drawable = R.drawable.cat_volleyball;

        sporImage.setImageResource(drawable);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    public void setTotalDisputeMoney(int totalDisputeMoney) {
        this.totalDisputeMoney = totalDisputeMoney;
    }

    public void setDispute(Dispute dispute) {
        this.dispute = dispute;
    }

    public void setClient(User client) {
        this.client = client;
    }


    public void setMyDatabase(FirebaseDatabase myDatabase) {
        this.myDatabase = myDatabase;
    }

    public void setLikeImage(boolean likeImage) {
        ImageView like = (ImageView)rootview.findViewById(R.id.imageLike);
        if (likeImage){
            like.setImageResource(R.drawable.like);
        }else {
            like.setImageResource(R.drawable.like_dark);
        }
    }
}
