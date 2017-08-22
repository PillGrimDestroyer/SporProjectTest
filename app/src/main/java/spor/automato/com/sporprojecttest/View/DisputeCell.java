package spor.automato.com.sporprojecttest.View;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.w3c.dom.Text;

import java.util.Date;

import spor.automato.com.sporprojecttest.Activity.DisputeDetailActivity;
import spor.automato.com.sporprojecttest.R;


public class DisputeCell extends RecyclerView.ViewHolder {

    View view;

    TextView sporName;
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

    public void setOnCardListener(final Activity activity){

        this.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, DisputeDetailActivity.class);
                activity.startActivity(intent);
            }
        });

    }

    public void setSporName(String name){
        sporName = (TextView)view.findViewById(R.id.spor_subject);
        sporName.setText(name);
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
