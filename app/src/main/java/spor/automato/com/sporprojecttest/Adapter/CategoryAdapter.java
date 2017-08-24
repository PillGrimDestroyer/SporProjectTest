package spor.automato.com.sporprojecttest.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Map;

import spor.automato.com.sporprojecttest.R;

/**
 * Created by Apofis on 24.08.2017.
 */

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {
    Map<String, Integer> hashMapCategory;
    Context context;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case

        private CardView cardView;
        public ViewHolder(CardView cv) {
            super(cv);
            cardView = cv;
        }
    }

    public CategoryAdapter(Map<String, Integer> sortedMapAsc, Context context) {
        hashMapCategory = sortedMapAsc;
        this.context = context;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public CategoryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                         int viewType) {
        // create a new view
        CardView cv = (CardView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.design_category_row, parent, false);

        return new ViewHolder(cv);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        CardView cv = holder.cardView;

        ImageView catImage = (ImageView)cv.findViewById(R.id.cat_image);
        TextView catName = (TextView)cv.findViewById(R.id.cat_name);
        TextView catCounter = (TextView)cv.findViewById(R.id.cat_counter);

        Object firstKey = hashMapCategory.keySet().toArray()[position];
        Object valueForFirstKey = hashMapCategory.get(firstKey);

        catName.setText(firstKey.toString());
        catCounter.setText("("+valueForFirstKey.toString()+")");

        int drawable;
        final String text = catName.getText().toString();

        if(text.equals("Футбол"))
            drawable = R.drawable.cat_foot;
        else if(text.equals("Баскетбол"))
            drawable = R.drawable.cat_bask;
        else if(text.equals("Бокс"))
            drawable = R.drawable.cat_boxing;
        else if(text.equals("Теннис"))
            drawable = R.drawable.cat_ten;
        else if(text.equals("Борьба"))
            drawable = R.drawable.cat_wrestling;
        else
            drawable = R.drawable.cat_volleyball;

        catImage.setImageDrawable(ContextCompat.getDrawable(cv.getContext(),drawable));

        cv.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                //TODO: Событие клика на категорию
            }
        });
    }


    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return hashMapCategory.size();
    }
}
