package spor.automato.com.sporprojecttest.models;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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

    public ArrayList<Participant> participant; //TODO: убрать это и всё связанное с ним
    public HashMap<String, Choice> choices;
    public HashMap<String, Boolean> likes;
    public HashMap<String, Participant> participants;

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
