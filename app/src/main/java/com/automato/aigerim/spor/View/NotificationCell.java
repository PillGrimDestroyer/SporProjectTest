package com.automato.aigerim.spor.View;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.automato.aigerim.spor.R;
import com.automato.aigerim.spor.Models.Dispute;
import com.automato.aigerim.spor.Models.Notification;

/**
 * Created by HAOR on 30.08.2017.
 */

public class NotificationCell extends RecyclerView.ViewHolder {

    private View rootView;

    private TextView notifMessage;
    private ImageView notifImage;
    private ImageView sporImage;

    private Dispute dispute;
    private Notification notification;
    private FirebaseDatabase myDatabase;
    private String userId;

    public NotificationCell(View itemView) {
        super(itemView);
        rootView = itemView;

        notifMessage = (TextView) itemView.findViewById(R.id.notif_message);
        notifImage = (ImageView) itemView.findViewById(R.id.notif_image);
        sporImage = (ImageView) itemView.findViewById(R.id.spor_Image);
    }

    private void setText(String text){
        this.notifMessage.setText(text);
    }

    public void setDispute(Dispute dispute){
        this.dispute = dispute;
    }

    private void setNotifImage(){
        if (notification.winnings > 0){
            this.notifImage.setImageResource(R.drawable.trophy);
        }else {
            this.notifImage.setImageResource(R.drawable.sad);
        }
    }

    private void setSporImage(){
        int drawable;

        switch (dispute.category){
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

            default:
                drawable = R.drawable.cat_volleyball;
                break;
        }

        sporImage.setImageResource(drawable);
    }

    public void setDataBase(FirebaseDatabase dataBase) {
        this.myDatabase = dataBase;
    }

    public void setUserId(String userId){
        this.userId = userId;
    }

    public void setNotification(Notification notification) {
        this.notification = notification;
        String text;
        if (notification.winnings > 0) {
            text = rootView.getResources().getString(R.string.youWin, dispute.subject, Integer.toString(notification.winnings));
        }else {
            text = rootView.getResources().getString(R.string.youLose);
        }
        setText(text);
        setNotifImage();
        setSporImage();
        notification.checked = true;

        DatabaseReference sporLikes = myDatabase.getReference("users/" + userId + "/history/" + dispute.id + "/checked");
        sporLikes.setValue(notification.checked);
    }
}
