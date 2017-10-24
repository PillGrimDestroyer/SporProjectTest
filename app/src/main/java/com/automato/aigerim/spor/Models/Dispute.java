package com.automato.aigerim.spor.Models;

import java.util.HashMap;

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

    public HashMap<String, Choice> choices = new HashMap<>();
    public HashMap<String, Boolean> likes = new HashMap<>();
    public HashMap<String, Participant> participants = new HashMap<>();
}
