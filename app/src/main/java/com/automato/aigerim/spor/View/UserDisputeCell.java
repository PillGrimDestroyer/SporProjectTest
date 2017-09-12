package com.automato.aigerim.spor.View;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.automato.aigerim.spor.Activity.MainActivity;
import com.automato.aigerim.spor.Models.Dispute;
import com.automato.aigerim.spor.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


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
        } else {
            rootView.findViewById(R.id.image_progress_bar).setVisibility(View.VISIBLE);

            StorageReference storageReference = FirebaseStorage.getInstance().getReference();
            storageReference.child("Photos").child(dispute.photo).getBytes(1024 * 1024).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    Bitmap image = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    sporImage.setImageBitmap(image);
                    rootView.findViewById(R.id.image_progress_bar).setVisibility(View.GONE);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(MainActivity.getActivity(), "Не могу загрузить фотографию!", Toast.LENGTH_SHORT).show();
                    Log.e("ImageLoadFailure", e.getMessage());
                    rootView.findViewById(R.id.image_progress_bar).setVisibility(View.GONE);
                }
            });
        }
    }

    public void setData(Dispute dispute) {
        this.dispute = dispute;
        setImage();

        final String s = dispute.date + " " + dispute.time;
        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        try {
            Date date = format.parse(s);
            long curUnixTime = System.currentTimeMillis();
            long progress = date.getTime() - curUnixTime;
            if (progress < 0) {
                if (dispute.result.equals("")) {
                    disputeName.setText(R.string.Live);
                    disputeTime.setText(null);
                } else {
                    disputeName.setText(R.string.done);
                    disputeTime.setText(rootView.getResources().getString(R.string.end, dispute.result));
                }
            } else {
                disputeName.setText("Начало в:");
                disputeTime.setText(dispute.time);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}