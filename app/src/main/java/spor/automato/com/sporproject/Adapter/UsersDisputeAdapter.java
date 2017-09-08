package spor.automato.com.sporproject.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import spor.automato.com.sporproject.R;
import spor.automato.com.sporproject.View.UserDisputeCell;
import spor.automato.com.sporproject.Models.Dispute;


public class UsersDisputeAdapter extends RecyclerView.Adapter<UserDisputeCell> {

    private ArrayList<Dispute> userDisputes;

    public UsersDisputeAdapter(ArrayList<Dispute> data) {
        this.userDisputes = data;
    }

    @Override
    public UserDisputeCell onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_dispute_cell, parent, false);
        UserDisputeCell userDisputeCell = new UserDisputeCell(view);
        return userDisputeCell;
    }

    @Override
    public void onBindViewHolder(UserDisputeCell holder, int position) {
        holder.setData(userDisputes.get(position));
    }

    @Override
    public int getItemCount() {
        return userDisputes.size();
    }
}
