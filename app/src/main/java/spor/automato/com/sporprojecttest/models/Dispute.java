package spor.automato.com.sporprojecttest.models;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.ArrayList;

@IgnoreExtraProperties


public class Dispute {

    public String id;
    public String subject;
    public int money;
    public String result;
    public boolean limit;
    public String category;
    public String subcategory;
    public String date;
    public String time;
    public int likeCount;
    public int participantCount;
    public int viewCount;

    public ArrayList<Participant> participant;

    public Dispute(){
        this.participant = new ArrayList<Participant>();
    }

    public void addParticipant(Participant participant){
        this.participant.add(participant);
    }

    public ArrayList<Participant> getParticp(){
        return this.participant;
    }
}
