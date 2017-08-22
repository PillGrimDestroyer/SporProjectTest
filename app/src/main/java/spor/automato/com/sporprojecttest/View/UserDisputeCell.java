package spor.automato.com.sporprojecttest.View;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import spor.automato.com.sporprojecttest.R;
import spor.automato.com.sporprojecttest.models.Dispute;


public class UserDisputeCell extends RecyclerView.ViewHolder {

    ImageView disputeImage;
    TextView disputeName;
    TextView disputeTime;

    public UserDisputeCell(View itemView) {
        super(itemView);
        disputeImage = (ImageView)itemView.findViewById(R.id.dispute_img);
        disputeName = (TextView)itemView.findViewById(R.id.dispute_label);
        disputeTime = (TextView)itemView.findViewById(R.id.dispute_time);
    }

    public void setData(Dispute dispute){
        this.disputeName.setText(dispute.subject);
        this.disputeTime.setText(dispute.time);
    }
}
