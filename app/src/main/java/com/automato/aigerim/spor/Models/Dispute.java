package com.automato.aigerim.spor.Models;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;

@IgnoreExtraProperties


public class Dispute {

    public String id;
    public String subject;
    public int money;
    public String result;
    public boolean limit;
    public boolean calculated;
    public String category;
    public String subcategory;
    public String date;
    public String time;
    public String photo;
    public int likeCount;
    public int participantCount;
    public int viewCount;

    public HashMap<String, Choice> choices;
    public HashMap<String, Boolean> likes;
    public HashMap<String, Participant> participants;
}
