package spor.automato.com.sporprojecttest.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashMap;

import spor.automato.com.sporprojecttest.R;
import spor.automato.com.sporprojecttest.View.NotificationCell;
import spor.automato.com.sporprojecttest.models.Dispute;
import spor.automato.com.sporprojecttest.models.Notification;

/**
 * Created by HAOR on 25.08.2017.
 */

public class NotificationAdapter extends RecyclerView.Adapter<NotificationCell> {

    private HashMap<String, Notification> mData = new HashMap<>();
    private View view;
    private LayoutInflater mInflater;
    private ArrayList<Dispute> disputes;

    // data is passed into the constructor
    public NotificationAdapter(final Context context, HashMap<String, Notification> data, ArrayList<Dispute> disputes) {
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
        String text;
        ArrayList<Notification> n = new ArrayList<>(mData.values());
        holder.setDispute(disputes.get(position));
        if (n.get(position).winnings > 0) {
            text = view.getResources().getString(R.string.youWin, disputes.get(position).result, Integer.toString(n.get(position).winnings));
        }else {
            text = view.getResources().getString(R.string.youLose);
        }
        holder.setText(text);
        holder.setNotifImage();
        holder.setSporImage(disputes.get(position).category);
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
