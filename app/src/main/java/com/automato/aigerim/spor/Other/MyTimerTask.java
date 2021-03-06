package com.automato.aigerim.spor.Other;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimerTask;

import com.automato.aigerim.spor.View.DisputeCell;
import com.automato.aigerim.spor.Models.Dispute;

/**
 * Created by HAOR on 28.08.2017.
 */

public class MyTimerTask extends TimerTask {

    public ArrayList<DisputeCell> disputeCells;
    public ArrayList<Dispute> disputes;
    public int time = 3000;

    @Override
    public void run() {
        for (int i = 0; i < disputeCells.size(); i++) {
            Dispute d = disputes.get(i);
            final String s = d.date + " " + d.time;
            SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy HH:mm");
            try {
                Date date = format.parse(s);
                disputeCells.get(i).setProgress(date.getTime(), disputes.get(i));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }
}
