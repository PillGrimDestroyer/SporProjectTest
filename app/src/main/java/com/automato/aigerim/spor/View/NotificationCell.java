package com.automato.aigerim.spor.View;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.automato.aigerim.spor.Activity.MainActivity;
import com.automato.aigerim.spor.Models.Dispute;
import com.automato.aigerim.spor.Models.Notification;
import com.automato.aigerim.spor.Models.User;
import com.automato.aigerim.spor.Other.Api;
import com.automato.aigerim.spor.R;

import java.util.Random;

/**
 * Created by HAOR on 30.08.2017.
 */

public class NotificationCell extends RecyclerView.ViewHolder {

    Api api;
    private View rootView;
    private TextView notifMessage;
    private ImageView notifImage;
    private ImageView sporImage;

    private Dispute dispute;
    private Notification notification;

    public NotificationCell(View itemView) {
        super(itemView);
        rootView = itemView;

        api = new Api(rootView.getContext());

        notifMessage = (TextView) itemView.findViewById(R.id.notif_message);
        notifImage = (ImageView) itemView.findViewById(R.id.notif_image);
        sporImage = (ImageView) itemView.findViewById(R.id.spor_Image);
    }

    private void setText(String text) {
        this.notifMessage.setText(text);
    }

    public void setDispute(Dispute dispute) {
        this.dispute = dispute;
    }

    private void setNotifImage() {
        if (notification.winnings > 0) {
            this.notifImage.setImageResource(R.drawable.trophy);
        } else {
            this.notifImage.setImageResource(R.drawable.sad);
        }
    }

    private void setSporImage() {
        int drawable;

        switch (dispute.category) {
            case "Футбол":
//                    drawable = R.drawable.cat_foot;
                Random randomFoot = new Random();
                int numberFoot = 1;//randomFoot.nextInt(3) + 1;
                drawable = rootView.getResources().getIdentifier("foot" + numberFoot, "drawable", MainActivity.getActivity().getPackageName());
                break;

            case "Баскетбол":
                drawable = R.drawable.cat_bask;
                break;

            case "Бокс":
                drawable = R.drawable.cat_boxing;
                break;

            case "Теннис":
//                    drawable = R.drawable.cat_ten;
                Random randomTen = new Random();
                int numberTen = 2;//randomTen.nextInt(3) + 1;
                drawable = rootView.getResources().getIdentifier("ten" + numberTen, "drawable", MainActivity.getActivity().getPackageName());
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

    public void setNotification(Notification notification) {
        this.notification = notification;
        String text;
        if (notification.winnings > 0) {
            text = rootView.getResources().getString(R.string.youWin, dispute.subject, Double.toString(notification.winnings));
        } else {
            text = rootView.getResources().getString(R.string.youLose, dispute.subject);
        }
        if (oneOfChoiceIsZero()) {
            text = rootView.getResources().getString(R.string.dispute_stoped);
        }
        setText(text);
        setNotifImage();
        setSporImage();
        notification.checked = true;

        User.history.get(dispute.id).checked = true;
        api.updateUser();
        /*DatabaseReference sporLikes = myDatabase.getReference("users/" + userId + "/history/" + dispute.id + "/checked");
        sporLikes.setValue(notification.checked);*/
    }

    private boolean oneOfChoiceIsZero() {
        boolean ret = false;
        if (dispute.choices.get("rivor1").rate == 0 || dispute.choices.get("rivor2").rate == 0) {
            ret = true;
        }
        return ret;
    }
}
