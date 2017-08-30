package spor.automato.com.sporprojecttest.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Timer;

import spor.automato.com.sporprojecttest.MyTimerTask;
import spor.automato.com.sporprojecttest.R;
import spor.automato.com.sporprojecttest.View.DisputeCell;
import spor.automato.com.sporprojecttest.models.Dispute;

/**
 * Created by HAOR on 25.08.2017.
 */

public class SortedDisputeAdapter extends RecyclerView.Adapter<DisputeCell> {

    private ArrayList<Dispute> mData = new ArrayList<>();
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private String userID;
    private FirebaseDatabase database;
    private Timer myTimer;
    private MyTimerTask task;
    private boolean isSorted;
    private boolean isSortedBySubCategory;

    // data is passed into the constructor
    public SortedDisputeAdapter(final Context context, ArrayList<Dispute> data, String userID,
                                FirebaseDatabase database, boolean isSorted, boolean isSortedBySubCategory) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.userID = userID;
        this.database = database;
        this.isSorted = isSorted;
        this.isSortedBySubCategory = isSortedBySubCategory;
    }

    // inflates the cell layout from xml when needed
    @Override
    public DisputeCell onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.spor_cell_layout, parent, false);
        DisputeCell viewHolder = new DisputeCell(view);
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
        viewHolder.setImage();

        boolean isLiked = false;
        if (model.likes != null) {
            isLiked = model.likes.containsKey(userID);
        }
        viewHolder.setLiked(isLiked);
        viewHolder.setOnCardListener(model, database);
        if (myTimer == null) {
            myTimer = new Timer();
            task = new MyTimerTask();

            task.disputes = new ArrayList<>();
            task.disputeCells = new ArrayList<>();

            task.disputeCells.add(viewHolder);
            task.disputes.add(model);
            myTimer.schedule(task, 0, task.time);
        } else {
            task.disputes.add(model);
            task.disputeCells.add(viewHolder);
        }
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

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView myTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            myTextView = (TextView) itemView.findViewById(R.id.info_text);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }
}
