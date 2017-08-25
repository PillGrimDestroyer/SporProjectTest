package spor.automato.com.sporprojecttest.View;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

import spor.automato.com.sporprojecttest.R;

/**
 * Created by Apofis on 25.08.2017.
 */

public class CategoryDetailCell extends RecyclerView.ViewHolder {

    View view;
    ArrayList<String> data = new ArrayList<>();

    public CategoryDetailCell(View itemView) {
        super(itemView);
        this.view = itemView;
    }

    public void setText(String text) {
        TextView t = (TextView) view.findViewById(R.id.info_text);
        t.setText(text);
    }

    public void addData(String data) {
        this.data.add(data);
    }

    public ArrayList<String> getData() {
        return data;
    }
}
