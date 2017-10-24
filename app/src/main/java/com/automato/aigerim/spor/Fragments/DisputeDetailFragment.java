package com.automato.aigerim.spor.Fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.automato.aigerim.spor.Activity.MainActivity;
import com.automato.aigerim.spor.Models.Choice;
import com.automato.aigerim.spor.Models.Dispute;
import com.automato.aigerim.spor.Models.Participant;
import com.automato.aigerim.spor.Models.User;
import com.automato.aigerim.spor.Other.Api;
import com.automato.aigerim.spor.Other.Tools;
import com.automato.aigerim.spor.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by HAOR on 23.08.2017.
 */

public class DisputeDetailFragment extends Fragment {

    View rootview;
    Api api;

    private String date;
    private String subject;
    private String time;
    private String viewCount;
    private String ferstTeam;
    private String secondTeam;
    private int totalDisputeMoney;
    private int numberOfParticipant;
    private int numberOfLikes;
    private boolean isLikedByCurentUser;
    private Dispute dispute;
    private boolean isSorted = false;
    private boolean isSortedBySubCategory = false;
    private ProgressBar progressBar;
    private TextView title;

    private RadioButton fTeam;
    private RadioButton sTeam;
    private Button submit;
    private EditText rate;
    private String selectedChoice;
    private Tools tools = new Tools();

    public String getFerstTeam() {
        return ferstTeam;
    }

    public void setFerstTeam(String ferstTeam) {
        this.ferstTeam = ferstTeam;
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

    public String getSubCategory() {
        return dispute.subcategory;
    }

    public String getCategory() {
        return dispute.category;
    }

    public String getViewCount() {
        return viewCount;
    }

    public void setViewCount(String viewCount) {
        this.viewCount = viewCount;
    }

    public boolean isLikedByCurentUser() {
        return isLikedByCurentUser;
    }

    public void setLikedByCurentUser(boolean likedByCurentUser) {
        isLikedByCurentUser = likedByCurentUser;
    }

    public String getSecondTeam() {
        return secondTeam;
    }

    public void setSecondTeam(String secondTeam) {
        this.secondTeam = secondTeam;
    }

    public int getNumberOfLikes() {
        return numberOfLikes;
    }

    public void setNumberOfLikes(int numberOfLikes) {
        this.numberOfLikes = numberOfLikes;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getNumberOfParticipant() {
        return numberOfParticipant;
    }

    public void setNumberOfParticipant(int numberOfParticipant) {
        this.numberOfParticipant = numberOfParticipant;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootview = inflater.inflate(R.layout.fragment_dispute_detail, container, false);

        api = new Api(getActivity());

        initializeElements();
        initializeData();
        setDisputeStatus();
        setListeners();

        MainActivity.setCurentFragment(this);
        return rootview;
    }

    private void initializeData() {
        setSporDate(getDate());
        setSporParticipantCount(getNumberOfParticipant(), dispute);
        setSporLikeCount(getNumberOfLikes());
        setSporSubject(getSubject());
        setSporSubCategory();
        setSporStartTime(getTime());
        setViewsCount(getViewCount());
        setLikeImage(isLikedByCurentUser());
        setImage();
        setRate();

        dispute.viewCount += 1;

        api.IncrementViewsCount(dispute.id);

        /*DatabaseReference viewCount = myDatabase.getReference("spor/" + dispute.id + "/viewCount");
        viewCount.setValue(dispute.viewCount);*/

        fTeam.setText(getFerstTeam());
        sTeam.setText(getSecondTeam());
    }

    private void initializeElements() {
        submit = (Button) rootview.findViewById(R.id.submit);
        fTeam = (RadioButton) rootview.findViewById(R.id.ferstTeam);
        sTeam = (RadioButton) rootview.findViewById(R.id.secondTeam);
        rate = (EditText) rootview.findViewById(R.id.rate);
        progressBar = (ProgressBar) rootview.findViewById(R.id.progress_bar);

        getActivity().findViewById(R.id.search_right).setVisibility(View.GONE);
        getActivity().findViewById(R.id.search_left).setVisibility(View.GONE);
        getActivity().findViewById(R.id.search_field).setVisibility(View.GONE);
        getActivity().findViewById(R.id.close).setVisibility(View.GONE);
        getActivity().findViewById(R.id.spinner).setVisibility(View.GONE);

        title = (TextView) getActivity().findViewById(R.id.title);
        title.setText(dispute.category);
        title.setVisibility(View.VISIBLE);
    }

    private void setDisputeStatus() {
        final String s = dispute.date + " " + dispute.time;
        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        try {
            Date date = format.parse(s);
            long curUnixTime = System.currentTimeMillis();
            long progress = date.getTime() - curUnixTime;
            if (progress < 0) {
                TextView progressText = (TextView) rootview.findViewById(R.id.progress_text);
                TextView status = (TextView) rootview.findViewById(R.id.spor_status);
                progressBar.setProgress(100);
                progressBar.setVisibility(View.GONE);
                progressText.setVisibility(View.VISIBLE);
                rootview.findViewById(R.id.time_layout).setVisibility(View.GONE);
                rootview.findViewById(R.id.spor_date).setVisibility(View.GONE);
                if (dispute.result.equals("")) {
                    progressText.setText(R.string.Live);
                    status.setText(R.string.Live);
                } else {
                    progressText.setText(rootview.getResources().getString(R.string.end, dispute.result));
                    status.setText(R.string.done);
                }
            } else {
                progressBar.setVisibility(View.VISIBLE);
                rootview.findViewById(R.id.progress_text).setVisibility(View.GONE);
                progressBar.setMax(99999999);
                int progressInt = 99999999 - (int) progress;
                if (Integer.signum(progressInt) == 1) {
                    progressBar.setProgress(99999999 - (int) progress);
                } else {
                    progressBar.setProgress(0);
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void setListeners() {
        fTeam.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b)
                    selectedChoice = getFerstTeam();
            }
        });

        sTeam.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b)
                    selectedChoice = getSecondTeam();
            }
        });

        rootview.findViewById(R.id.imageLike).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onLikeClick(view);
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSubmitClick(view);
            }
        });
    }

    private void onSubmitClick(View view) {
        if (selectedChoice != null) {
            if (rate.getText() != null && !rate.getText().toString().equals("")) {
                int rateValue = Integer.parseInt(rate.getText().toString());
                if (rateValue <= (int) User.money) {
                    if (dispute.participants == null) {
                        dispute.participants = new HashMap<>();
                    }

                    Context context = rootview.getContext();
                    InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(rate.getWindowToken(), 0);

                    if (!dispute.participants.containsKey(User.id)) {
                        if (User.confirmed) {
                            Participant participant = new Participant();
                            dispute.money += rateValue;
                            User.money -= rateValue;
                            dispute.participantCount += 1;

                            participant.choice = selectedChoice;
                            participant.money = rateValue;
                            participant.spor_id = dispute.id;
                            participant.user_id = User.id;
                            participant.winnings = 0;

                            /*DatabaseReference participantCount = myDatabase.getReference("spor/" + dispute.id + "/participantCount");
                            participantCount.setValue(dispute.participantCount);

                            DatabaseReference sporMoney = myDatabase.getReference("spor/" + dispute.id + "/totalDisputeMoney");
                            sporMoney.setValue(dispute.money);

                            DatabaseReference clientMoney = myDatabase.getReference("users/" + client.id + "/money");
                            clientMoney.setValue(client.money);

                            DatabaseReference refParticipant = myDatabase.getReference("spor/" + dispute.id + "/participants");
                            refParticipant.child(client.id).setValue(participant);*/

                            Choice choice = new Choice();
                            choice.choice = selectedChoice;
                            choice.id = selectedChoice;
                            choice.spor_id = dispute.id;

                            ArrayList<Choice> choiceList = new ArrayList<>(dispute.choices.values());
                            ArrayList<String> keyList = new ArrayList<>(dispute.choices.keySet());
                            String selChoiceKey;

                            if (choiceList.get(0).choice.equals(selectedChoice)) {
                                choice.rate = choiceList.get(0).rate + rateValue;
                                selChoiceKey = keyList.get(0);
                            } else {
                                choice.rate = choiceList.get(1).rate + rateValue;
                                selChoiceKey = keyList.get(1);
                            }


                            /*DatabaseReference updateChoice = myDatabase.getReference("spor/" + dispute.id + "/choices/" + selChoiceKey);
                            updateChoice.setValue(choice);*/


                            fTeam.setSelected(false);
                            sTeam.setSelected(false);
                            rate.setText("");

                            api.addParticipantToDispute(dispute.id, rateValue, selectedChoice, selChoiceKey);
                            if (!api.error) {
                                api.updateUser();
                                if (!api.error) {
                                    dispute.choices.put(selChoiceKey, choice);
                                    dispute.participants.put(User.id, participant);
                                    setSporParticipantCount(dispute.participantCount, dispute);
                                    setRate();
                                    Log.w("SPOR ID", dispute.id);
                                    Toast.makeText(rootview.getContext(), R.string.stali_uchastnikom, Toast.LENGTH_LONG).show();
                                }
                            }
                        } else {
                            SweetAlertDialog mProgressDialog = new SweetAlertDialog(view.getContext(), SweetAlertDialog.ERROR_TYPE);
                            mProgressDialog.setTitleText("Ошибка");
                            mProgressDialog.setContentText(getResources().getString(R.string.not_verified));
                            mProgressDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    sweetAlertDialog.dismissWithAnimation();
                                }
                            });
                            mProgressDialog.show();
                        }
                    } else {
                        Toast.makeText(rootview.getContext(), R.string.already_in, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(rootview.getContext(), R.string.not_enough_money, Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(rootview.getContext(), R.string.money_first, Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(rootview.getContext(), R.string.choose_first, Toast.LENGTH_SHORT).show();
        }
    }

    private void onLikeClick(View view) {
        TextView sporLikeCount = (TextView) rootview.findViewById(R.id.like_count);
        int likeCount;
        if (!isLikedByCurentUser()) {
            likeCount = Integer.parseInt(sporLikeCount.getText().toString());
            likeCount++;

            if (dispute.likes != null) {
                if (!dispute.likes.containsKey(User.id)) {
                    dispute.likes.put(User.id, true);
                }
            } else {
                HashMap<String, Boolean> likes = new HashMap<>();
                likes.put(User.id, true);
                dispute.likes = likes;
            }
        } else {
            likeCount = Integer.parseInt(sporLikeCount.getText().toString());
            likeCount--;

            dispute.likes.remove(User.id);
        }

        dispute.likeCount = likeCount;
        api.like(User.id, dispute.id);

        /*DatabaseReference sporLikeCountInDB = myDatabase.getReference("spor/" + dispute.id + "/likeCount");
        sporLikeCountInDB.setValue(likeCount);

        DatabaseReference sporLikes = myDatabase.getReference("spor/" + dispute.id + "/likes");
        sporLikes.setValue(dispute.likes);*/

        ImageView like = (ImageView) view.findViewById(R.id.imageLike);
        if (!isLikedByCurentUser()) {
            like.setImageResource(R.drawable.like);
            setLikedByCurentUser(true);
        } else {
            like.setImageResource(R.drawable.like_dark);
            setLikedByCurentUser(false);
        }
        sporLikeCount.setText(Integer.toString(likeCount));
    }

    private void setRate() {
        ArrayList<Choice> arlist = new ArrayList<>(dispute.choices.values());

        TextView leftRateTextView = (TextView) rootview.findViewById(R.id.left_rate);
        TextView RightRateTextView = (TextView) rootview.findViewById(R.id.right_rate);
        TextView subjectTextView = (TextView) rootview.findViewById(R.id.spor_subject);

        String fTeam = Tools.regex("(.*?)([ ]*?)(-)", subjectTextView.getText().toString(), 1);
        if (!fTeam.equals(arlist.get(0).choice)) {
            RightRateTextView = (TextView) rootview.findViewById(R.id.left_rate);
            leftRateTextView = (TextView) rootview.findViewById(R.id.right_rate);
        }

        leftRateTextView.setText(Integer.toString(arlist.get(0).rate));
        RightRateTextView.setText(Integer.toString(arlist.get(1).rate));
    }

    private void setSporSubCategory() {
        TextView sporSubCategory = (TextView) rootview.findViewById(R.id.spor_sub_category);
        sporSubCategory.setText(dispute.subcategory);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        // write logic here b'z it is called when fragment is visible to user

    }

    private void setSporDate(String date) {
        TextView sporDate = (TextView) rootview.findViewById(R.id.spor_date);
        sporDate.setText(date);
    }

    private void setViewsCount(String viewsCount) {
        TextView viewCount = (TextView) rootview.findViewById(R.id.view_count);
        viewCount.setText(viewsCount);
    }

    private void setSporParticipantCount(int numberOfParticipant, Dispute dispute) {
        TextView sporParticipantCount = (TextView) rootview.findViewById(R.id.viewers_count);
        ImageView participant = (ImageView) rootview.findViewById(R.id.imageParticCount);
        sporParticipantCount.setText(Integer.toString(numberOfParticipant));
        if (dispute.participants != null) {
            if (dispute.participants.containsKey(User.id)) {
                participant.setImageResource(R.drawable.people_black);
                ((TextView) rootview.findViewById(R.id.viewers_count)).setTextColor(rootview.getResources().getColor(R.color.grey_700));
            } else {
                participant.setImageResource(R.drawable.people);
                ((TextView) rootview.findViewById(R.id.viewers_count)).setTextColor(rootview.getResources().getColor(R.color.grey_300));
            }
        } else {
            participant.setImageResource(R.drawable.people);
            ((TextView) rootview.findViewById(R.id.viewers_count)).setTextColor(rootview.getResources().getColor(R.color.grey_300));
        }
    }

    private void setSporLikeCount(int numberOfLikes) {
        TextView sporLikeCount = (TextView) rootview.findViewById(R.id.like_count);
        sporLikeCount.setText(Integer.toString(numberOfLikes));
        if (numberOfLikes > 0) {
            ImageView like = (ImageView) rootview.findViewById(R.id.imageLike);
            like.setImageResource(R.drawable.like);
        }
    }

    private void setSporSubject(String subject) {
        TextView sporSubject = (TextView) rootview.findViewById(R.id.spor_subject);
        sporSubject.setText(subject);
    }

    private void setSporStartTime(String time) {
        TextView startTime = (TextView) rootview.findViewById(R.id.spor_time);
        startTime.setText(time);
    }

    private void setImage() {
        final ImageView sporImage = (ImageView) rootview.findViewById(R.id.spor_Image);

        int drawable;
        boolean hasImage = true;

        if (dispute.photo == null) {
            sporImage.setBackground(null);
            switch (dispute.category) {
                case "Футбол":
//                    drawable = R.drawable.cat_foot;
                    Random randomFoot = new Random();
                    int numberFoot = 1;//randomFoot.nextInt(3) + 1;
                    drawable = rootview.getResources().getIdentifier("foot" + numberFoot, "drawable", MainActivity.getActivity().getPackageName());
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
                    drawable = rootview.getResources().getIdentifier("ten" + numberTen, "drawable", MainActivity.getActivity().getPackageName());
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
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    Bitmap bitmap = Tools.downloadDisputePhoto(getActivity(), dispute.photo);
                    if (bitmap != null) {
                        sporImage.setImageBitmap(bitmap);
                        rootview.findViewById(R.id.client_image_progress_bar).setVisibility(View.GONE);
                        sporImage.setBackground(null);
                    }
                }
            });
            thread.setDaemon(false);
            thread.start();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    public void setTotalDisputeMoney(int totalDisputeMoney) {
        this.totalDisputeMoney = totalDisputeMoney;
    }

    public void setDispute(Dispute dispute) {
        this.dispute = dispute;
    }

    public void setLikeImage(boolean likeImage) {
        ImageView like = (ImageView) rootview.findViewById(R.id.imageLike);
        if (likeImage) {
            like.setImageResource(R.drawable.like);
            ((TextView) rootview.findViewById(R.id.like_count)).setTextColor(rootview.getResources().getColor(R.color.grey_700));
        } else {
            like.setImageResource(R.drawable.like_dark);
            ((TextView) rootview.findViewById(R.id.like_count)).setTextColor(rootview.getResources().getColor(R.color.grey_300));
        }
    }
}
