package spor.automato.com.sporprojecttest.View;

import android.app.Activity;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import spor.automato.com.sporprojecttest.R;
import spor.automato.com.sporprojecttest.fragments.DisputeDetailFragment;
import spor.automato.com.sporprojecttest.models.Choice;
import spor.automato.com.sporprojecttest.models.Dispute;


public class DisputeCell extends RecyclerView.ViewHolder {

    View view;

    TextView sporDate;
    TextView sporParticipantCount;
    TextView sporLikeCount;
    TextView sporState;
    TextView sporSubject;
    TextView startTime;


    public DisputeCell(View itemView) {
        super(itemView);
        this.view = itemView;
    }

    public void setOnCardListener(final Activity activity, final Dispute model){

        this.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DisputeDetailFragment ddf = new DisputeDetailFragment();
                HashMap<String, Choice> choices = model.choices;

                String sporDate = ((TextView)view.findViewById(R.id.spor_date)).getText().toString();
                int sporParticipantCount = Integer.parseInt(((TextView)view.findViewById(R.id.viewers_count)).getText().toString());
                int sporLikeCount = Integer.parseInt(((TextView)view.findViewById(R.id.like_count)).getText().toString());
                String sporSubject = ((TextView)view.findViewById(R.id.spor_subject)).getText().toString();
                String sporStartTime = ((TextView)view.findViewById(R.id.spor_time)).getText().toString();
                int money = model.money;

                ddf.setDate(sporDate);
                ddf.setNumberOfParticipant(sporParticipantCount);
                ddf.setNumberOfLikes(sporLikeCount);
                ddf.setSubject(sporSubject);
                ddf.setTime(sporStartTime);
                ddf.setMoney(money);
                ddf.setDispute(model);

                int b = 0;
                for (Choice c : choices.values()) {
                    if(b == 0)
                        ddf.setFerstTeam(c.choice);
                    else
                        ddf.setSecondTeam(c.choice);
                    b++;
                }

                android.support.v4.app.Fragment newFragment = ddf;
                AppCompatActivity a = (AppCompatActivity) activity;

                a.getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.main_fragment, newFragment, "fragment")
                        .setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                        .commit();

                //TODO: убрать, но попозже (не, ну а вдруг пригодится?)
                /*Intent intent = new Intent(activity, DisputeDetailActivity.class);
                activity.startActivity(intent);*/
            }
        });

    }

    public void setSporDate(String date){
        sporDate = (TextView)view.findViewById(R.id.spor_date);
        this.sporDate.setText(date);
    }

    public void setSporParticipantCount(int numberOfParticipant){
        sporParticipantCount = (TextView)view.findViewById(R.id.viewers_count);
        this.sporParticipantCount.setText(""+ numberOfParticipant);
    }

    public void setSporLikeCount(int numberOfLikes){
        sporLikeCount = (TextView)view.findViewById(R.id.like_count);
        this.sporLikeCount.setText(""+ numberOfLikes);
    }

    public void setSporSubject(String subject){
        sporSubject = (TextView)view.findViewById(R.id.spor_subject);
        this.sporSubject.setText(subject);
    }

    public void setSporStartTime(String time){
        startTime = (TextView)view.findViewById(R.id.spor_time);
        this.startTime.setText(time);
    }

    public void setImage(){
        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseStorage.getInstance().getReference().child("Photos").child(userID);
        ImageView sporImage = (ImageView)view.findViewById(R.id.spor_Image);
    }
}
