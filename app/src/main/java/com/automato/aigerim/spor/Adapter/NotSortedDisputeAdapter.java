package com.automato.aigerim.spor.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.automato.aigerim.spor.Models.Dispute;
import com.automato.aigerim.spor.R;
import com.automato.aigerim.spor.View.DisputeCell;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

/**
 * Created by HAOR on 25.08.2017.
 */

public class NotSortedDisputeAdapter extends RecyclerView.Adapter<DisputeCell> {

    private ArrayList<Dispute> mData = new ArrayList<>();
    private NotSortedDisputeAdapter.ItemClickListener mClickListener;
    private String userID;
    private FirebaseDatabase database;
    private boolean isSorted;
    private boolean isSortedBySubCategory;
    private ArrayList<DisputeCell> viewHolders = new ArrayList<>();

    // data is passed into the constructor
    public NotSortedDisputeAdapter(ArrayList<Dispute> data, String userID,
                                   FirebaseDatabase database, boolean isSorted, boolean isSortedBySubCategory) {
        this.mData = data;
        this.userID = userID;
        this.database = database;
        this.isSorted = isSorted;
        this.isSortedBySubCategory = isSortedBySubCategory;
    }

    // inflates the cell layout from xml when needed
    @Override
    public DisputeCell onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.spor_cell_layout, parent, false);
        DisputeCell viewHolder = new DisputeCell(view);
        viewHolders.add(viewHolder);
        return viewHolder;
    }

    // binds the data to the textview in each cell
    @Override
    public void onBindViewHolder(DisputeCell viewHolder, int position) {
        Dispute model = mData.get(position);

        viewHolder.setSporDate(model.date);
        viewHolder.setSporLikeCount(model.likeCount);
        viewHolder.setSporParticipantCount(model.participantCount, model, userID);
        viewHolder.setSporStartTime(model.time);
        viewHolder.setSporSubject(model.subject);
        viewHolder.setViewCount(model.viewCount);
        viewHolder.setCategory(model.category);
        viewHolder.setSubCategory(model.subcategory);
        viewHolder.setSorted(isSorted);
        viewHolder.setSortedBySubCategory(isSortedBySubCategory);
        viewHolder.setRate(model);

        boolean isLiked = false;
        if (model.likes != null) {
            isLiked = model.likes.containsKey(userID);
        }
        viewHolder.setLiked(isLiked);
        viewHolder.setListener(model, database);
        viewHolder.runTimer(model);
        viewHolder.setImage(model.photo);
    }

    // total number of cells
    @Override
    public int getItemCount() {
        return mData.size();
    }

    // convenience method for getting data at click position
    public Dispute getItem(int id) {
        return mData.get(id);
    }

    // allows clicks events to be caught
    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    public DisputeCell getView(int position) {
        return viewHolders.get(position);
    }

    public int getViewCount() {
        return viewHolders.size();
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public View view;

        public ViewHolder(View itemView) {
            super(itemView);
            this.view = itemView;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }
}
