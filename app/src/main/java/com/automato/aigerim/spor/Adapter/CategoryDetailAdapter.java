package com.automato.aigerim.spor.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import com.automato.aigerim.spor.R;
import com.automato.aigerim.spor.Models.Dispute;
import com.makeramen.roundedimageview.RoundedImageView;

/**
 * Created by HAOR on 25.08.2017.
 */

public class CategoryDetailAdapter extends RecyclerView.Adapter<CategoryDetailAdapter.ViewHolder> {

    private ArrayList<String> mData = new ArrayList<>();
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private String category;

    // data is passed into the constructor
    public CategoryDetailAdapter(final Context context, final String category, ArrayList<String> data) {
        this.mInflater = LayoutInflater.from(context);
        this.category = category;
        this.mData = data;
    }

    // inflates the cell layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.category_detail_cell, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    // binds the data to the textview in each cell
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String animal = mData.get(position);
        holder.myTextView.setText(animal);
    }

    // total number of cells
    @Override
    public int getItemCount() {
        return mData.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView myTextView;
        View itemView;

        public ViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            myTextView = (TextView) itemView.findViewById(R.id.info_text);
            itemView.setOnClickListener(this);
            setImage();
        }

        public void setImage(){
            RoundedImageView sporImage = (RoundedImageView) itemView.findViewById(R.id.cat_image);

            int drawable;
            if(category.equals("Футбол"))
                drawable = R.drawable.football_subcategory;
            else if(category.equals("Баскетбол"))
                drawable = R.drawable.basket_subcategory;
            else if(category.equals("Бокс"))
                drawable = R.drawable.box_subcategory;
            else if(category.equals("Теннис"))
                drawable = R.drawable.tennis_subcategory;
            else if(category.equals("Борьба"))
                drawable = R.drawable.cat_wrestling;
            else if(category.equals("Волейбол"))
                drawable = R.drawable.cat_volleyball;
            else
                drawable = R.drawable.foot3;

            sporImage.setImageResource(drawable);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // convenience method for getting data at click position
    public String getItem(int id) {
        return mData.get(id);
    }

    // allows clicks events to be caught
    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}
