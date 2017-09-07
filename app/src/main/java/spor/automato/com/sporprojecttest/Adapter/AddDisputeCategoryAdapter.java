package spor.automato.com.sporprojecttest.Adapter;

import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import spor.automato.com.sporprojecttest.Activity.MainActivity;
import spor.automato.com.sporprojecttest.R;
import spor.automato.com.sporprojecttest.fragments.AddDisputeSubCategoryFragment;
import spor.automato.com.sporprojecttest.models.Dispute;
import spor.automato.com.sporprojecttest.models.DisputeFromOlimp;

/**
 * Created by HAOR on 05.09.2017.
 */

public class AddDisputeCategoryAdapter extends RecyclerView.Adapter<AddDisputeCategoryAdapter.ViewHolder> {

    private ArrayList<DisputeFromOlimp> disputes;

    public AddDisputeCategoryAdapter(ArrayList<DisputeFromOlimp> disputes) {
        this.disputes = disputes;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public AddDisputeCategoryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                                   int viewType) {
        // create a new view
        RelativeLayout rl = (RelativeLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.add_dispute_cell, parent, false);

        return new ViewHolder(rl);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        RelativeLayout rl = holder.relativeLayout;

        ((TextView) rl.findViewById(R.id.category)).setText(disputes.get(position).category);

        rl.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                AddDisputeSubCategoryFragment DSCF = new AddDisputeSubCategoryFragment();
                DSCF.setCategory(disputes.get(position).category);
                android.support.v4.app.Fragment newFragment = DSCF;

                MainActivity.getFragmetManeger().beginTransaction()
                        .replace(R.id.main_fragment, newFragment, "fragment")
                        .setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                        .commit();
            }
        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return disputes.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private RelativeLayout relativeLayout;

        public ViewHolder(RelativeLayout rl) {
            super(rl);
            relativeLayout = rl;
        }
    }
}
