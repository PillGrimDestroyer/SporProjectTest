package com.automato.aigerim.spor.Fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.automato.aigerim.spor.Activity.MainActivity;
import com.automato.aigerim.spor.Models.DisputeFromOlimp;
import com.automato.aigerim.spor.Other.Api;
import com.automato.aigerim.spor.Other.Tools;
import com.automato.aigerim.spor.R;

import java.util.Date;
import java.util.Random;

import static android.app.Activity.RESULT_OK;

/**
 * Created by HAOR on 05.09.2017.
 */

public class AddDisputeFragment extends Fragment {

    static final int GALLERY_REQUEST = 1;
    Api api;
    private Tools tools = new Tools();
    private View rootView;
    private String patternForDate = "\\d{2}\\/\\d{2}\\/\\d{4}";
    private String patternForTime = "\\d{2}:\\d{2}";
    private String photo;
    private DisputeFromOlimp disputeFromOlimp;

    private EditText date;
    private EditText time;
    private ImageView done;
    private EditText subject;
    private EditText category;
    private EditText subcategory;
    private EditText rivor1;
    private EditText rivor2;
    private FloatingActionButton photoButton;
    private ImageView disputImage;
    private Uri selectedImage;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_dispute, container, false);
        this.rootView = view;

        api = new Api(getActivity());

        initializeElements();

        done = (ImageView) getActivity().findViewById(R.id.done);

        photoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, GALLERY_REQUEST);
            }
        });

        done.setVisibility(View.VISIBLE);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validate(true)) {
                    MainActivity.showLoader();
                    try {
                        if (disputeFromOlimp == null) {
                            prepareData();
                        }
                        if (selectedImage != null) {
                            Tools.uploadDisputePhoto(getActivity(), selectedImage, photo);
                            writeDisputeInDB();
                        } else {
                            writeDisputeInDB();
                        }
                    } catch (Exception e) {
                        MainActivity.dismissWithAnimationLoader();
                        Toast.makeText(rootView.getContext(), "Ошибка при добавлении спора", Toast.LENGTH_SHORT).show();
                        Log.e("AddDisputeException", e.getMessage());
                    }
                }
            }
        });

        TextView title = (TextView) getActivity().findViewById(R.id.title);
        title.setText("Добавить спор");

        MainActivity.setCurentFragment(this);
        return view;
    }

    private void prepareData() {
        disputeFromOlimp = new DisputeFromOlimp();
        disputeFromOlimp.date = date.getText().toString();
        disputeFromOlimp.category = category.getText().toString();
        disputeFromOlimp.rivor1 = rivor1.getText().toString();
        disputeFromOlimp.rivor2 = rivor2.getText().toString();
        disputeFromOlimp.rivors = subject.getText().toString();
        disputeFromOlimp.subcategory = subcategory.getText().toString();
        disputeFromOlimp.time = time.getText().toString();
    }

    private void writeDisputeInDB() {
        api.addNewDispute(disputeFromOlimp, photo, (BottomNavigationView) getActivity().findViewById(R.id.navigation));
    }

    private boolean validate(boolean alright) {
        if (!date.getText().toString().equals("")) {
            if (!Tools.regex(patternForDate, date.getText().toString()) && date.getText().toString().length() != 10) {
                date.setError("Не верный формат");
                alright = false;
            } else {
                date.setError(null);
            }
        } else {
            date.setError("Обязательно для заполнения");
        }
        if (!time.getText().toString().equals("")) {
            if (!Tools.regex(patternForTime, time.getText().toString()) && time.getText().toString().length() != 5) {
                time.setError("Не верный формат");
                alright = false;
            } else {
                time.setError(null);
            }
        } else {
            time.setError("Обязательно для заполнения");
            alright = false;
        }

        if (subject.getText().toString().equals("")) {
            subject.setError("Обязательно для заполнения");
            alright = false;
        } else {
            subject.setError(null);
        }

        if (category.getText().toString().equals("")) {
            category.setError("Обязательно для заполнения");
            alright = false;
        } else {
            category.setError(null);
        }

        if (subcategory.getText().toString().equals("")) {
            subcategory.setError("Обязательно для заполнения");
            alright = false;
        } else {
            subcategory.setError(null);
        }

        if (rivor1.getText().toString().equals("")) {
            rivor1.setError("Обязательно для заполнения");
            alright = false;
        } else {
            rivor1.setError(null);
        }

        if (rivor2.getText().toString().equals("")) {
            rivor2.setError("Обязательно для заполнения");
            alright = false;
        } else {
            rivor2.setError(null);
        }
        return alright;
    }

    private void initializeElements() {
        date = (EditText) rootView.findViewById(R.id.date);
        time = (EditText) rootView.findViewById(R.id.time);
        subject = (EditText) rootView.findViewById(R.id.spor_name_edit_text);
        category = (EditText) rootView.findViewById(R.id.spor_category_edit_text);
        subcategory = (EditText) rootView.findViewById(R.id.spor_sub_category_edit_text);
        rivor1 = (EditText) rootView.findViewById(R.id.spor_rivor1_edit_text);
        rivor2 = (EditText) rootView.findViewById(R.id.spor_rivor2_edit_text);
        photoButton = (FloatingActionButton) rootView.findViewById(R.id.add_photo);
        disputImage = (ImageView) rootView.findViewById(R.id.dispute_image);

        if (disputeFromOlimp != null) {
            setDate(disputeFromOlimp.date);
            setTime(disputeFromOlimp.time);
            setSubject(disputeFromOlimp.rivors);
            setCategory(disputeFromOlimp.category);
            setSubcategory(disputeFromOlimp.subcategory);
            setRivor1(disputeFromOlimp.rivor1);
            setRivor2(disputeFromOlimp.rivor2);
        }
    }

    private void setDate(String date) {
        this.date.setText(date);
    }

    private void setTime(String time) {
        this.time.setText(time);
    }

    private void setSubject(String subject) {
        this.subject.setText(subject);
    }

    private void setCategory(String category) {
        this.category.setText(category);
    }

    private void setSubcategory(String subcategory) {
        this.subcategory.setText(subcategory);
    }

    private void setRivor1(String rivor1) {
        this.rivor1.setText(rivor1);
    }

    private void setRivor2(String rivor2) {
        this.rivor2.setText(rivor2);
    }

    public void setDisputeFromOlimp(DisputeFromOlimp disputeFromOlimp) {
        this.disputeFromOlimp = disputeFromOlimp;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case GALLERY_REQUEST:
                if (resultCode == RESULT_OK) {
                    final Uri selectedImage = data.getData();
                    galleryResponse(selectedImage);
                } else {
                    Toast.makeText(rootView.getContext(), "Не удалось загрузить картинку", Toast.LENGTH_SHORT).show();
                }
                break;

            default:
                break;
        }
    }

    private void galleryResponse(final Uri selectedImage) {
        Random random = new Random();

        photo = new Date().getTime() + "_" + random.nextInt(9999);
        String selfile = Tools.getRealPathFromUri(getActivity(), selectedImage);
        String filetype = selfile.substring(selfile.lastIndexOf("."));
        photo = photo + filetype;

        disputImage.setImageURI(selectedImage);
        this.selectedImage = selectedImage;

        rootView.findViewById(R.id.imageViewCenter).setVisibility(View.GONE);
        rootView.findViewById(R.id.text_on_image).setVisibility(View.GONE);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        // write logic here b'z it is called when fragment is visible to user
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }
}
