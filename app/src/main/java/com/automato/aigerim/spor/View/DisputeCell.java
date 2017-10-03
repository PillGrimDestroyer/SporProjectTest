package com.automato.aigerim.spor.View;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.automato.aigerim.spor.Activity.MainActivity;
import com.automato.aigerim.spor.Fragments.DisputeDetailFragment;
import com.automato.aigerim.spor.Models.Choice;
import com.automato.aigerim.spor.Models.Dispute;
import com.automato.aigerim.spor.Models.User;
import com.automato.aigerim.spor.Other.TimerTask.MySingleTimerTask;
import com.automato.aigerim.spor.Other.Tools.Tools;
import com.automato.aigerim.spor.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Timer;

import cn.pedant.SweetAlert.SweetAlertDialog;


public class DisputeCell extends RecyclerView.ViewHolder {

    View view;

    TextView sporDate;
    TextView sporParticipantCount;
    TextView sporLikeCount;
    TextView sporState;
    TextView sporSubject;
    TextView startTime;
    TextView viewCount;

    private boolean isLiked;
    private boolean isSorted;
    private boolean isSortedBySubCategory;
    private User client;
    private String category;

    private StorageReference storageReference;
    private Tools tools = new Tools();
    private Timer myTimer;
    private MySingleTimerTask task;

    public DisputeCell(View itemView) {
        super(itemView);
        this.view = itemView;
        storageReference = FirebaseStorage.getInstance().getReference();
    }

    private void setOnLikeListener(final Dispute model, final FirebaseDatabase myDatabase) {
        view.findViewById(R.id.imageLike).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int likeCount;
                if (!isLiked()) {
                    likeCount = Integer.parseInt(sporLikeCount.getText().toString());
                    likeCount++;

                    if (model.likes != null) {
                        if (!model.likes.containsKey(client.id)) {
                            model.likes.put(client.id, true);
                        }
                    } else {
                        HashMap<String, Boolean> likes = new HashMap<>();
                        likes.put(client.id, true);
                        model.likes = likes;
                    }
                } else {
                    likeCount = Integer.parseInt(sporLikeCount.getText().toString());
                    likeCount--;

                    model.likes.remove(client.id);
                }

                DatabaseReference sporLikeCountInDB = myDatabase.getReference("spor/" + model.id + "/likeCount");
                sporLikeCountInDB.setValue(likeCount);

                DatabaseReference sporLikes = myDatabase.getReference("spor/" + model.id + "/likes");
                sporLikes.setValue(model.likes);

                ImageView like = (ImageView) view.findViewById(R.id.imageLike);
                if (!isLiked()) {
                    like.setImageResource(R.drawable.like);
                    setLiked(true);
                } else {
                    like.setImageResource(R.drawable.like_dark);
                    setLiked(false);
                }
                sporLikeCount.setText(Integer.toString(likeCount));
            }
        });
    }

    public void setListener(final Dispute model, final FirebaseDatabase myDatabase) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String userID = mAuth.getCurrentUser().getUid();
        DatabaseReference reference = myDatabase.getReference();
        reference.child("users").child(userID).orderByKey().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                client = dataSnapshot.getValue(User.class);
                setOnLikeListener(model, myDatabase);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void setOnCardListener(final Dispute model, final FirebaseDatabase myDatabase) {
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DisputeDetailFragment ddf = new DisputeDetailFragment();
                HashMap<String, Choice> choices = model.choices;

                String sporDate = ((TextView) view.findViewById(R.id.spor_date)).getText().toString();
                int sporParticipantCount = Integer.parseInt(((TextView) view.findViewById(R.id.viewers_count)).getText().toString());
                int sporLikeCount = Integer.parseInt(((TextView) view.findViewById(R.id.like_count)).getText().toString());

                String viewsCountMessage = ((TextView) view.findViewById(R.id.view_count)).getText().toString();
                int viewsCountNumber = Integer.parseInt(viewsCountMessage.split(" ")[0]);
                setViewCount(++viewsCountNumber);

                viewsCountMessage = ((TextView) view.findViewById(R.id.view_count)).getText().toString();
                String sporSubject = ((TextView) view.findViewById(R.id.spor_subject)).getText().toString();
                String sporStartTime = ((TextView) view.findViewById(R.id.spor_time)).getText().toString();
                int money = model.money;

                ddf.setDate(sporDate);
                ddf.setNumberOfParticipant(sporParticipantCount);
                ddf.setNumberOfLikes(sporLikeCount);
                ddf.setSubject(sporSubject);
                ddf.setTime(sporStartTime);
                ddf.setViewCount(viewsCountMessage);
                ddf.setTotalDisputeMoney(money);
                ddf.setDispute(model);
                ddf.setClient(client);
                ddf.setMyDatabase(myDatabase);
                ddf.setLikedByCurentUser(isLiked());
                ddf.setSorted(isSorted());
                ddf.setSortedBySubCategory(isSortedBySubCategory());

                ddf.setFerstTeam(choices.get("rivor1").choice);
                ddf.setSecondTeam(choices.get("rivor2").choice);

                android.support.v4.app.Fragment newFragment = ddf;

                MainActivity.getFragmetManeger()
                        .beginTransaction()
                        .replace(R.id.main_fragment, newFragment, "fragment")
                        .setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                        .commit();
            }
        });
    }

    public void disputeLiveOnCardListener() {
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new SweetAlertDialog(view.getContext(), SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Спор в прямом эфире")
                        .setContentText("На этот спор уже нельзя делать ставки")
                        .setConfirmText("Ок")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                sweetAlertDialog.dismissWithAnimation();
                            }
                        }).show();
            }
        });
    }

    public void disputeEndOnCardListener() {
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new SweetAlertDialog(view.getContext(), SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Спор завершён")
                        .setContentText("На этот спор уже нельзя делать ставки")
                        .setConfirmText("Ок")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                sweetAlertDialog.dismissWithAnimation();
                            }
                        }).show();
            }
        });
    }

    public void setSporDate(String date) {
        sporDate = (TextView) view.findViewById(R.id.spor_date);
        sporDate.setText(date);
    }

    public boolean isLiked() {
        return isLiked;
    }

    public void setLiked(boolean liked) {
        isLiked = liked;
        setLikedByCurentUser();
    }

    public boolean isSorted() {
        return isSorted;
    }

    public void setSorted(boolean sorted) {
        isSorted = sorted;
    }

    public boolean isSortedBySubCategory() {
        return isSortedBySubCategory;
    }

    public void setSortedBySubCategory(boolean sortedBySubCategory) {
        isSortedBySubCategory = sortedBySubCategory;
    }

    private void setLikedByCurentUser() {
        if (isLiked()) {
            ImageView like = (ImageView) view.findViewById(R.id.imageLike);
            like.setImageResource(R.drawable.like);
            ((TextView) view.findViewById(R.id.like_count)).setTextColor(view.getResources().getColor(R.color.grey_700));
        } else {
            ImageView like = (ImageView) view.findViewById(R.id.imageLike);
            like.setImageResource(R.drawable.like_dark);
            ((TextView) view.findViewById(R.id.like_count)).setTextColor(view.getResources().getColor(R.color.grey_300));
        }
    }

    public void setViewCount(int count) {
        viewCount = (TextView) view.findViewById(R.id.view_count);
        String endOfMessage = " просмотров";
        int lastNumber = count % 10;
        if (lastNumber == 2 || lastNumber == 3 || lastNumber == 4) {
            endOfMessage = " просмотра";
        } else if (lastNumber == 1) {
            endOfMessage = " просмотр";
        }
        if (Integer.toString(count).length() >= 2) {
            int lastTwoNumbers = count % 100;
            if (lastTwoNumbers == 11 || lastTwoNumbers == 12 || lastTwoNumbers == 13 || lastTwoNumbers == 14) {
                endOfMessage = " просмотров";
            }
        }
        this.viewCount.setText(count + endOfMessage);
    }

    public void setSporParticipantCount(int numberOfParticipant, Dispute dispute, String userId) {
        sporParticipantCount = (TextView) view.findViewById(R.id.viewers_count);
        sporParticipantCount.setText(Integer.toString(numberOfParticipant));
        if (dispute.participants != null) {
            if (dispute.participants.containsKey(userId)) {
                ImageView participant = (ImageView) view.findViewById(R.id.imageParticCount);
                participant.setImageResource(R.drawable.people_black);
                ((TextView) view.findViewById(R.id.viewers_count)).setTextColor(view.getResources().getColor(R.color.grey_700));
            } else {
                ImageView participant = (ImageView) view.findViewById(R.id.imageParticCount);
                participant.setImageResource(R.drawable.people);
                ((TextView) view.findViewById(R.id.viewers_count)).setTextColor(view.getResources().getColor(R.color.grey_300));
            }
        } else {
            ImageView participant = (ImageView) view.findViewById(R.id.imageParticCount);
            participant.setImageResource(R.drawable.people);
            ((TextView) view.findViewById(R.id.viewers_count)).setTextColor(view.getResources().getColor(R.color.grey_300));
        }
    }

    public void setSporLikeCount(int numberOfLikes) {
        this.sporLikeCount = (TextView) view.findViewById(R.id.like_count);
        sporLikeCount.setText(Integer.toString(numberOfLikes));
    }

    public void setSporSubject(String subject) {
        sporSubject = (TextView) view.findViewById(R.id.spor_subject);
        this.sporSubject.setText(subject);
    }

    public void setSporStartTime(String time) {
        startTime = (TextView) view.findViewById(R.id.spor_time);
        startTime.setText(time);
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setImage(String photo) {
        final ImageView sporImage = (ImageView) view.findViewById(R.id.spor_Image);

        if (photo == null) {
            sporImage.setBackground(null);
            int drawable;
            boolean hasImage = true;

            switch (category) {
                case "Футбол":
//                    drawable = R.drawable.cat_foot;
                    Random randomFoot = new Random();
                    int numberFoot = 1;//randomFoot.nextInt(3) + 1;
                    drawable = view.getResources().getIdentifier("foot" + numberFoot, "drawable", MainActivity.getActivity().getPackageName());
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
                    drawable = view.getResources().getIdentifier("ten" + numberTen, "drawable", MainActivity.getActivity().getPackageName());
                    break;

                case "Борьба":
                    drawable = R.drawable.cat_wrestling;
                    break;

                case "Волейбол":
                    drawable = R.drawable.cat_volleyball;
                    break;

                default:
                    drawable = R.drawable.foot1;
                    //drawable = 0;
                    //hasImage = false;
                    break;
            }

            if (hasImage)
                sporImage.setImageResource(drawable);
        } else {
            view.findViewById(R.id.client_image_progress_bar).setVisibility(View.VISIBLE);
            storageReference.child("Photos").child(photo).getBytes(1024 * 1024).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    Bitmap image = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    sporImage.setImageBitmap(image);
                    view.findViewById(R.id.client_image_progress_bar).setVisibility(View.GONE);
                    sporImage.setBackground(null);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(MainActivity.getActivity(), "Не могу загрузить фотографию!", Toast.LENGTH_SHORT).show();
                    Log.e("ImageLoadFailure", e.getMessage());
                    view.findViewById(R.id.client_image_progress_bar).setVisibility(View.GONE);
                    sporImage.setBackground(null);
                }
            });
        }
    }

    public void setSubCategory(String subCategory) {
        TextView sporSubCategory = (TextView) view.findViewById(R.id.spor_sub_category);
        sporSubCategory.setText(subCategory);
    }

    private ProgressBar getProgressBar() {
        return (ProgressBar) view.findViewById(R.id.progress_bar);
    }

    private TextView getProgressText() {
        return (TextView) view.findViewById(R.id.progress_text);
    }

    public void setProgress(long unixTime, Dispute dispute) {
        if (!MainActivity.isAdmin()) {
            setOnCardListener(dispute, FirebaseDatabase.getInstance());
        }
        ProgressBar progressBar = getProgressBar();
        TextView progressText = getProgressText();
        TextView status = getStatusTextView();

        long curUnixTime = System.currentTimeMillis();
        long progress = unixTime - curUnixTime;
        if (progress < 0) {
            //progressBar.setProgress(100);

            progressBar.setVisibility(View.GONE);
            progressText.setVisibility(View.VISIBLE);
            view.findViewById(R.id.time_layout).setVisibility(View.GONE);
            view.findViewById(R.id.spor_date).setVisibility(View.GONE);

            if (dispute.result.equals("")) {
                disputeLiveOnCardListener();
                progressText.setText(R.string.Live);
                status.setText(R.string.Live);
            } else {
                disputeEndOnCardListener();
                if (dispute.result.equals("equal")) {
                    progressText.setText(R.string.equal);
                } else {
                    progressText.setText(view.getResources().getString(R.string.end, dispute.result));
                }
                status.setText(R.string.done);
            }
        } else {
            progressBar.setVisibility(View.VISIBLE);
            progressText.setVisibility(View.GONE);
            view.findViewById(R.id.time_layout).setVisibility(View.VISIBLE);
            view.findViewById(R.id.spor_date).setVisibility(View.VISIBLE);

            if (!MainActivity.isAdmin())
                setOnCardListener(dispute, FirebaseDatabase.getInstance());
            status.setText(R.string.wait);

            progressBar.setMax(99999999);
            int progressInt = 99999999 - (int) progress;
            if (Integer.signum(progressInt) == 1) {
                progressBar.setProgress(99999999 - (int) progress);
            } else {
                progressBar.setProgress(0);
            }
        }
    }

    public TextView getStatusTextView() {
        return (TextView) view.findViewById(R.id.spor_status);
    }

    public void setRate(Dispute dispute) {
        ArrayList<Choice> arlist = new ArrayList<>(dispute.choices.values());

        TextView leftRateTextView = (TextView) view.findViewById(R.id.left_rate);
        TextView RightRateTextView = (TextView) view.findViewById(R.id.right_rate);
        TextView subjectTextView = (TextView) view.findViewById(R.id.spor_subject);

        String fTeam = tools.regex("(.*?)([ ]*?)(-)", subjectTextView.getText().toString(), 1);
        if (!fTeam.equals(arlist.get(0).choice)) {
            RightRateTextView = (TextView) view.findViewById(R.id.left_rate);
            leftRateTextView = (TextView) view.findViewById(R.id.right_rate);
        }

        leftRateTextView.setText(Integer.toString(arlist.get(0).rate));
        RightRateTextView.setText(Integer.toString(arlist.get(1).rate));
    }

    protected void finalize() throws Throwable {
        if (myTimer != null) {
            task.cancel();
            myTimer.cancel();
            myTimer.purge();
        }
        super.finalize();
    }

    public void runTimer(final Dispute model) {
        if (myTimer != null) {
            task.cancel();
            myTimer.cancel();
            myTimer.purge();
        }
        myTimer = new Timer();
        task = new MySingleTimerTask();

        task.dispute = model;
        task.disputeCell = DisputeCell.this;
        myTimer.schedule(task, 0, task.time);
    }

    public void removeTimer() {
        if (myTimer != null) {
            task.cancel();
            myTimer.cancel();
            myTimer.purge();
        }
    }
}
