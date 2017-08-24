package spor.automato.com.sporprojecttest.View;

import android.app.Activity;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.HashMap;

import spor.automato.com.sporprojecttest.R;
import spor.automato.com.sporprojecttest.fragments.DisputeDetailFragment;
import spor.automato.com.sporprojecttest.models.Choice;
import spor.automato.com.sporprojecttest.models.Dispute;
import spor.automato.com.sporprojecttest.models.User;


public class DisputeCell extends RecyclerView.ViewHolder {

    View view;

    TextView sporDate;
    TextView sporParticipantCount;
    TextView sporLikeCount;
    TextView sporState;
    TextView sporSubject;
    TextView startTime;
    TextView viewCount;

    private boolean isLiked;

    public DisputeCell(View itemView) {
        super(itemView);
        this.view = itemView;
    }

    public void setOnCardListener(final Activity activity, final Dispute model, final User client, final FirebaseDatabase myDatabase){
        this.view.findViewById(R.id.imageLike).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int likeCount;
                if(!isLiked()) {
                    likeCount = Integer.parseInt(sporLikeCount.getText().toString());
                    likeCount++;

                    if(model.likes != null){
                        if(!model.likes.containsKey(client.id)) {
                            model.likes.put(client.id, true);
                        }
                    }else {
                        HashMap<String, Boolean> likes = new HashMap<>();
                        likes.put(client.id, true);
                        model.likes = likes;
                    }
                }else {
                    likeCount = Integer.parseInt(sporLikeCount.getText().toString());
                    likeCount--;

                    model.likes.remove(client.id);
                }

                DatabaseReference sporLikeCountInDB = myDatabase.getReference("spor/" + model.id + "/likeCount");
                sporLikeCountInDB.setValue(likeCount);

                DatabaseReference sporLikes = myDatabase.getReference("spor/" + model.id + "/likes");
                sporLikes.setValue(model.likes);

                ImageView like = (ImageView)view.findViewById(R.id.imageLike);
                if(!isLiked()) {
                    like.setImageResource(R.drawable.like);
                    setLiked(true);
                }else {
                    like.setImageResource(R.drawable.like_dark);
                    setLiked(false);
                }
                sporLikeCount.setText(Integer.toString(likeCount));
            }
        });

        this.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DisputeDetailFragment ddf = new DisputeDetailFragment();
                HashMap<String, Choice> choices = model.choices;

                String sporDate = ((TextView)view.findViewById(R.id.spor_date)).getText().toString();
                int sporParticipantCount = Integer.parseInt(((TextView)view.findViewById(R.id.viewers_count)).getText().toString());
                int sporLikeCount = Integer.parseInt(((TextView)view.findViewById(R.id.like_count)).getText().toString());
                String viewsCount = ((TextView)view.findViewById(R.id.view_count)).getText().toString();
                String sporSubject = ((TextView)view.findViewById(R.id.spor_subject)).getText().toString();
                String sporStartTime = ((TextView)view.findViewById(R.id.spor_time)).getText().toString();
                int money = model.money;

                ddf.setDate(sporDate);
                ddf.setNumberOfParticipant(sporParticipantCount);
                ddf.setNumberOfLikes(sporLikeCount);
                ddf.setSubject(sporSubject);
                ddf.setTime(sporStartTime);
                ddf.setViewCount(viewsCount);
                ddf.setTotalDisputeMoney(money);
                ddf.setDispute(model);
                ddf.setClient(client);
                ddf.setMyDatabase(myDatabase);
                ddf.setLikedByCurentUser(isLiked());

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

    public boolean isLiked() {
        return isLiked;
    }

    public void setLiked(boolean liked) {
        isLiked = liked;
        setLikedByCurentUser();
    }

    private void setLikedByCurentUser(){
        if (isLiked()){
            ImageView like = (ImageView)view.findViewById(R.id.imageLike);
            like.setImageResource(R.drawable.like);
        }else{
            ImageView like = (ImageView)view.findViewById(R.id.imageLike);
            like.setImageResource(R.drawable.like_dark);
        }
    }

    public void setViewCount(int count) {
        viewCount = (TextView)view.findViewById(R.id.view_count);
        //TODO: сделать так чтобы сообщение было корректным при любом колличестве
        // просмотров, просмотра, просмотр и т.д в записимости от колличества
        String endOfMessage = " просмотров";
        this.viewCount.setText(count + endOfMessage);
    }

    public void setSporParticipantCount(int numberOfParticipant){
        sporParticipantCount = (TextView)view.findViewById(R.id.viewers_count);
        this.sporParticipantCount.setText(""+ numberOfParticipant);
        if(numberOfParticipant > 0) {
            ImageView participant = (ImageView)view.findViewById(R.id.imageParticCount);
            participant.setImageResource(R.drawable.people_black);
        }
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