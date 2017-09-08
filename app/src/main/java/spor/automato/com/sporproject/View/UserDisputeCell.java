package spor.automato.com.sporproject.View;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import spor.automato.com.sporproject.R;
import spor.automato.com.sporproject.Models.Dispute;


public class UserDisputeCell extends RecyclerView.ViewHolder {

    private View rootView;
    private Dispute dispute;

    TextView disputeName;
    TextView disputeTime;

    public UserDisputeCell(View itemView) {
        super(itemView);
        rootView = itemView;
        disputeName = (TextView)itemView.findViewById(R.id.dispute_label);
        disputeTime = (TextView)itemView.findViewById(R.id.dispute_time);
    }

    private void setImage(){
        RelativeLayout sporImage = (RelativeLayout) itemView.findViewById(R.id.user_dispute_relative);

        int drawable;
        if(dispute.category.equals("Футбол"))
            drawable = R.drawable.cat_foot;
        else if(dispute.category.equals("Баскетбол"))
            drawable = R.drawable.cat_bask;
        else if(dispute.category.equals("Бокс"))
            drawable = R.drawable.cat_boxing;
        else if(dispute.category.equals("Теннис"))
            drawable = R.drawable.cat_ten;
        else if(dispute.category.equals("Борьба"))
            drawable = R.drawable.cat_wrestling;
        else
            drawable = R.drawable.cat_volleyball;

        sporImage.setBackgroundResource(drawable);
    }

    public void setData(Dispute dispute){
        this.dispute = dispute;
        setImage();

        final String s = dispute.date + " " + dispute.time;
        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        try {
            Date date = format.parse(s);
            long curUnixTime = System.currentTimeMillis();
            long progress = date.getTime() - curUnixTime;
            if (progress < 0){
                if (dispute.result.equals("")) {
                    disputeName.setText(R.string.Live);
                    disputeTime.setText(null);
                }
                else {
                    disputeName.setText(R.string.done);
                    disputeTime.setText(rootView.getResources().getString(R.string.end, dispute.result));
                }
            }
            else {
                disputeName.setText("Начало в:");
                disputeTime.setText(dispute.time);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
