package com.automato.aigerim.spor.Other.TimerTask;

import com.automato.aigerim.spor.Activity.MainActivity;
import com.automato.aigerim.spor.Models.Dispute;
import com.automato.aigerim.spor.View.DisputeCell;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimerTask;

/**
 * Created by HAOR on 16.09.2017.
 */

public class MySingleTimerTask extends TimerTask {

    public Dispute dispute;
    public DisputeCell disputeCell;
    public long time = 3000;

    @Override
    public void run() {
        String s = dispute.date + " " + dispute.time;
        s = s.replace("/", ".");
        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        try {
            final Date date = format.parse(s);
            MainActivity.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    disputeCell.setProgress(date.getTime(), dispute);
                }
            });
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
