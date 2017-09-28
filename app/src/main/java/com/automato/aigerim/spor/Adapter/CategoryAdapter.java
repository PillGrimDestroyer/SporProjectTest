package com.automato.aigerim.spor.Adapter;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.automato.aigerim.spor.Fragments.CategoryDetailFragment;
import com.automato.aigerim.spor.R;

import java.util.Map;

/**
 * Created by HAOR on 24.08.2017.
 */

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {
    Map<String, Integer> hashMapCategory;
    Context context;
    FragmentManager manager;
    View rootView;

    public CategoryAdapter(Map<String, Integer> sortedMapAsc, Context context, FragmentManager manager, View rootView) {
        hashMapCategory = sortedMapAsc;
        this.context = context;
        this.manager = manager;
        this.rootView = rootView;

    }

    // Create new views (invoked by the layout manager)
    @Override
    public CategoryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                         int viewType) {
        // create a new view
        RelativeLayout cv = (RelativeLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.category_cell, parent, false);

        return new ViewHolder(cv);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        RelativeLayout cv = holder.cardView;

        ImageView catImage = (ImageView) cv.findViewById(R.id.cat_image);
        TextView catName = (TextView) cv.findViewById(R.id.cat_name);
        TextView catCounter = (TextView) cv.findViewById(R.id.cat_counter);

        Object firstKey = hashMapCategory.keySet().toArray()[position];
        Object valueForFirstKey = hashMapCategory.get(firstKey);

        catName.setText(firstKey.toString());
        catCounter.setText("(" + valueForFirstKey.toString() + ")");

        int drawable;
        final String text = catName.getText().toString();

        switch (text) {
            case "Футбол":
                drawable = R.drawable.cat_foot;
                break;

            case "Баскетбол":
                drawable = R.drawable.cat_bask;
                break;

            case "Бокс":
                drawable = R.drawable.cat_boxing;
                break;

            case "Теннис":
                drawable = R.drawable.cat_ten;
                break;

            case "Борьба":
                drawable = R.drawable.cat_wrestling;
                break;

            case "Волейбол":
                drawable = R.drawable.cat_volleyball;
                break;

            default:
                drawable = R.drawable.foot1;
                break;
        }

        catImage.setImageDrawable(ContextCompat.getDrawable(cv.getContext(), drawable));

        cv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CategoryDetailFragment CatDF = new CategoryDetailFragment();
                String category = ((TextView) v.findViewById(R.id.cat_name)).getText().toString();
                CatDF.setCategory(category);
                android.support.v4.app.Fragment newFragment = CatDF;

                manager.beginTransaction()
                        .replace(R.id.main_fragment, newFragment, "fragment")
                        .setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                        .commit();
            }
        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return hashMapCategory.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case

        private RelativeLayout cardView;

        public ViewHolder(RelativeLayout cv) {
            super(cv);
            cardView = cv;
        }
    }
}
