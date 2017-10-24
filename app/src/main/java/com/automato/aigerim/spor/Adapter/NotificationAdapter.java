package com.automato.aigerim.spor.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashMap;

import com.automato.aigerim.spor.R;
import com.automato.aigerim.spor.View.NotificationCell;
import com.automato.aigerim.spor.Models.Dispute;
import com.automato.aigerim.spor.Models.Notification;

/**
 * Created by HAOR on 25.08.2017.
 */

public class NotificationAdapter extends RecyclerView.Adapter<NotificationCell> {

    private HashMap<String, Notification> mData = new HashMap<>();
    private View view;
    private LayoutInflater mInflater;
    private ArrayList<Dispute> disputes;

    // data is passed into the constructor
    public NotificationAdapter(final Context context, HashMap<String, Notification> data,
                               ArrayList<Dispute> disputes) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.disputes = disputes;
    }

    // inflates the cell layout from xml when needed
    @Override
    public NotificationCell onCreateViewHolder(ViewGroup parent, int viewType) {
        this.view = mInflater.inflate(R.layout.notification_cell, parent, false);
        NotificationCell viewHolder = new NotificationCell(view);
        return viewHolder;
    }

    // binds the data to the textview in each cell
    @Override
    public void onBindViewHolder(NotificationCell holder, int position) {
        ArrayList<Notification> n = new ArrayList<>(mData.values());

        Notification notification = n.get(position);
        Dispute dispute = disputes.get(position);

        holder.setDispute(dispute);
        holder.setNotification(notification);
    }

    // total number of cells
    @Override
    public int getItemCount() {
        return mData.size();
    }

    // convenience method for getting data at click position
    public Notification getItem(String id) {
        return mData.get(id);
    }
}
