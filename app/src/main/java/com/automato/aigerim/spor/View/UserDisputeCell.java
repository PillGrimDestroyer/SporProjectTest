package com.automato.aigerim.spor.View;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.automato.aigerim.spor.Activity.MainActivity;
import com.automato.aigerim.spor.Models.Dispute;
import com.automato.aigerim.spor.Other.Tools;
import com.automato.aigerim.spor.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;


public class UserDisputeCell extends RecyclerView.ViewHolder {

    TextView disputeName;
    TextView disputeTime;
    private View rootView;
    private Dispute dispute;

    public UserDisputeCell(View itemView) {
        super(itemView);
        rootView = itemView;
        disputeName = (TextView) itemView.findViewById(R.id.dispute_label);
        disputeTime = (TextView) itemView.findViewById(R.id.dispute_time);
    }

    private void setImage() {
        final ImageView sporImage = (ImageView) rootView.findViewById(R.id.spor_Image);

        if (dispute.photo == null) {
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

                case "Волейбол":
                    drawable = R.drawable.cat_volleyball;
                    break;

                default:
                    drawable = R.drawable.foot1;
                    break;
            }

            sporImage.setImageResource(drawable);
        } else {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    Bitmap bitmap = Tools.downloadDisputePhoto(MainActivity.getContext(), dispute.photo);
                    if (bitmap != null) {
                        sporImage.setImageBitmap(bitmap);
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                rootView.findViewById(R.id.image_progress_bar).setVisibility(View.GONE);
                            }
                        });
                    }
                }
            });
            thread.setDaemon(false);
            thread.start();
        }
    }

    public void setData(Dispute dispute) {
        this.dispute = dispute;
        setImage();

        String s = dispute.date + " " + dispute.time;
        s = s.replace("/", ".");
        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        try {
            Date date = format.parse(s);
            long curUnixTime = System.currentTimeMillis();
            long progress = date.getTime() - curUnixTime;
            disputeName.setText(dispute.subject);
            if (progress < 0) {
                if (dispute.result.equals("")) {
                    disputeTime.setText(R.string.Live);
                } else {
                    if (dispute.result.equals("equal")) {
                        disputeTime.setText(R.string.equal);
                    } else {
                        disputeTime.setText(rootView.getResources().getString(R.string.end, dispute.result));
                    }
                }
            } else {
                disputeTime.setText("Начало в " + dispute.time);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
