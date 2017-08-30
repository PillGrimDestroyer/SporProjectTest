package spor.automato.com.sporprojecttest.View;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import spor.automato.com.sporprojecttest.R;
import spor.automato.com.sporprojecttest.models.Dispute;
import spor.automato.com.sporprojecttest.models.User;

/**
 * Created by HAOR on 30.08.2017.
 */

public class NotificationCell extends RecyclerView.ViewHolder {

    private View rootView;

    private TextView notifMessage;
    private ImageView notifImage;
    private ImageView sporImage;

    private Dispute dispute;

    public NotificationCell(View itemView) {
        super(itemView);
        rootView = itemView;

        notifMessage = (TextView) itemView.findViewById(R.id.notif_message);
        notifImage = (ImageView) itemView.findViewById(R.id.notif_image);
        sporImage = (ImageView) itemView.findViewById(R.id.spor_Image);
    }

    public void setText(String text){
        this.notifMessage.setText(text);
    }

    public void setDispute(Dispute dispute){
        this.dispute = dispute;
    }

    public void setNotifImage(){
        this.notifImage.setImageResource(R.drawable.arrow_left);
    }

    public void setSporImage(String category){
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
        else
            drawable = R.drawable.cat_volleyball;

        sporImage.setImageResource(drawable);
    }
}
