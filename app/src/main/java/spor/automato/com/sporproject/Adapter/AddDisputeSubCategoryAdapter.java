package spor.automato.com.sporproject.Adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

import spor.automato.com.sporproject.Activity.MainActivity;
import spor.automato.com.sporproject.R;
import spor.automato.com.sporproject.Fragments.AddDisputeFragment;
import spor.automato.com.sporproject.Models.DisputeFromOlimp;

/**
 * Created by HAOR on 05.09.2017.
 */

public class AddDisputeSubCategoryAdapter extends BaseExpandableListAdapter {

    private Context _context;
    //Названия заголовков
    private List<String> _listDataHeader;
    //Данные для элементов подпунктов:
    private HashMap<String, List<DisputeFromOlimp>> _listDataChild;

    public AddDisputeSubCategoryAdapter(Context context, List<String> listDataHeader,
                                        HashMap<String, List<DisputeFromOlimp>> listChildData) {
        this._context = context;
        this._listDataHeader = listDataHeader;
        this._listDataChild = listChildData;
    }

    @Override
    public DisputeFromOlimp getChild(int groupPosition, int childPosititon) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                .get(childPosititon);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        final DisputeFromOlimp dispute = getChild(groupPosition, childPosition);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_item, null);
        }

        TextView subjectTextView = (TextView) convertView.findViewById(R.id.subject);
        TextView dateTimeTextView = (TextView) convertView.findViewById(R.id.date_time);

        subjectTextView.setText(dispute.rivors);
        dateTimeTextView.setText(dispute.date + " " + dispute.time);

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddDisputeFragment addDisputeFragment = new AddDisputeFragment();
                addDisputeFragment.setDisputeFromOlimp(dispute);

                MainActivity.getFragmetManeger()
                        .beginTransaction()
                        .replace(R.id.main_fragment, addDisputeFragment, "fragment")
                        .setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                        .commit();
            }
        });

        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                .size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this._listDataHeader.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this._listDataHeader.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        String headerTitle = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_group, null);
        }

        TextView lblListHeader = (TextView) convertView
                .findViewById(R.id.lblListHeader);
        lblListHeader.setTypeface(null, Typeface.BOLD);
        lblListHeader.setText(headerTitle);

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
