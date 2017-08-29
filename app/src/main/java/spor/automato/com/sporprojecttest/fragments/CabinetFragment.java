package spor.automato.com.sporprojecttest.fragments;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.annotations.Nullable;
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
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import spor.automato.com.sporprojecttest.Adapter.UsersDisputeAdapter;
import spor.automato.com.sporprojecttest.R;
import spor.automato.com.sporprojecttest.models.Dispute;
import spor.automato.com.sporprojecttest.models.User;

import static android.app.Activity.RESULT_OK;


public class CabinetFragment extends Fragment implements View.OnClickListener {

    static final int GALLERY_REQUEST = 1;

    View rootView;

    ImageView userProfileImage;
    TextView userName;
    TextView userEmail;
    TextView userAge;
    TextView userMoney;
    TextView sporLabel;
    ImageView selector;
    FloatingActionButton activ;
    FloatingActionButton finished;
    FloatingActionButton wait;
    ProgressBar progressBar;
    RecyclerView userDisputesList;
    FirebaseStorage storage;
    StorageReference storageReference;
    FirebaseDatabase myDatabase;
    DatabaseReference reference;
    FirebaseAuth mAuth;
    User client;
    ArrayList<Dispute> userDisputes = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_cabinet, container, false);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        myDatabase = FirebaseDatabase.getInstance();
        reference = myDatabase.getReference();
        mAuth = FirebaseAuth.getInstance();
        initViews();
        loadUserData();

        return rootView;
    }

    public void loadAllMyDisputes() {
        userDisputes.clear();
        sporLabel.setText(R.string.allDisputes);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("spor");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Dispute dispute = snapshot.getValue(Dispute.class);
                    if (dispute.participants != null) {
                        if (dispute.participants.containsKey(client.id)) {
                            userDisputes.add(dispute);
                        }
                    }
                }
                userDisputesList.setAdapter(new UsersDisputeAdapter(userDisputes));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void loadMyActivDisputes() {
        userDisputes.clear();
        sporLabel.setText(R.string.activDisputes);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("spor");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Dispute dispute = snapshot.getValue(Dispute.class);
                    if (dispute.participants != null) {
                        if (dispute.participants.containsKey(client.id) && isActivDispute(dispute)) {
                            userDisputes.add(dispute);
                        }
                    }
                }
                userDisputesList.setAdapter(new UsersDisputeAdapter(userDisputes));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void loadMyWaitDisputes() {
        userDisputes.clear();
        sporLabel.setText(R.string.waitDisputes);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("spor");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Dispute dispute = snapshot.getValue(Dispute.class);
                    if (dispute.participants != null) {
                        if (dispute.participants.containsKey(client.id) && isWaitDispute(dispute)) {
                            userDisputes.add(dispute);
                        }
                    }
                }
                userDisputesList.setAdapter(new UsersDisputeAdapter(userDisputes));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void loadMyFinishedDisputes() {
        userDisputes.clear();
        sporLabel.setText(R.string.finishedDisputes);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("spor");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Dispute dispute = snapshot.getValue(Dispute.class);
                    if (dispute.participants != null) {
                        if (dispute.participants.containsKey(client.id) && isFinishedDispute(dispute)) {
                            userDisputes.add(dispute);
                        }
                    }
                }
                userDisputesList.setAdapter(new UsersDisputeAdapter(userDisputes));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private boolean isActivDispute(Dispute dispute) {
        if (getProgress(dispute) < 0) {
            if (dispute.result.equals("")) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    private boolean isWaitDispute(Dispute dispute) {
        if (getProgress(dispute) < 0) {
            if (dispute.result.equals("")) {
                return false;
            } else {
                return false;
            }
        } else {
            return true;
        }
    }

    private boolean isFinishedDispute(Dispute dispute) {
        if (getProgress(dispute) < 0) {
            if (dispute.result.equals("")) {
                return false;
            } else {
                return true;
            }
        } else {
            return false;
        }
    }

    private long getProgress(Dispute dispute) {
        final String s = dispute.date + " " + dispute.time;
        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        long progres = 0;
        try {
            Date date = format.parse(s);
            long curUnixTime = System.currentTimeMillis();
            progres = date.getTime() - curUnixTime;
            return progres;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return progres;
    }

    public void loadUserData() {
        String userID = mAuth.getCurrentUser().getUid();
        reference.child("users").child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                client = dataSnapshot.getValue(User.class);
                userName.setText(client.name);
                userEmail.setText(client.email);
                userAge.setText(client.birthday);
                userMoney.setText(client.money + " тг");
                loadingClientProfile(client.id);
                loadAllMyDisputes();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void loadingClientProfile(String userID) {
        if (client.hasImage) {
            storageReference.child("Photos").child(userID).getBytes(1024 * 1024).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    Drawable image = new BitmapDrawable(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
                    userProfileImage.setImageDrawable(image);
                    progressBar.setVisibility(View.GONE);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(rootView.getContext(), "Не могу загрузить фотографию!", Toast.LENGTH_SHORT).show();
                    Log.e("ImageLoadFailure", e.getMessage());
                }
            });
        } else
            progressBar.setVisibility(View.GONE);
    }

    public void initViews() {
        this.userProfileImage = (ImageView) rootView.findViewById(R.id.client_profile);
        this.selector = (ImageView) rootView.findViewById(R.id.select);
        this.userName = (TextView) rootView.findViewById(R.id.client_name);
        this.userEmail = (TextView) rootView.findViewById(R.id.client_email);
        this.userAge = (TextView) rootView.findViewById(R.id.client_age);
        this.userMoney = (TextView) rootView.findViewById(R.id.client_money);
        this.sporLabel = (TextView) rootView.findViewById(R.id.client_spors_label);

        userProfileImage.setOnClickListener(CabinetFragment.this);
        selector.setOnClickListener(CabinetFragment.this);

        this.activ = (FloatingActionButton) rootView.findViewById(R.id.activ);
        activ.setOnClickListener(CabinetFragment.this);
        this.finished = (FloatingActionButton) rootView.findViewById(R.id.finished);
        finished.setOnClickListener(CabinetFragment.this);
        this.wait = (FloatingActionButton) rootView.findViewById(R.id.wait);
        wait.setOnClickListener(CabinetFragment.this);

        this.progressBar = (ProgressBar) rootView.findViewById(R.id.client_image_progress_bar);
        this.userDisputesList = (RecyclerView) rootView.findViewById(R.id.clientSpors);
        this.userDisputesList.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this.getActivity());
        llm.setOrientation(LinearLayoutManager.HORIZONTAL);
        userDisputesList.setLayoutManager(llm);
    }

    private void changeImageClick() {
        progressBar.setVisibility(View.VISIBLE);
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, GALLERY_REQUEST);
    }

    private void waitButtonClick() {
        loadMyWaitDisputes();
    }

    private void finishedButtonCLick() {
        loadMyFinishedDisputes();
    }

    private void activButtonClick() {
        loadMyActivDisputes();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.activ:
                activButtonClick();
                break;

            case R.id.finished:
                finishedButtonCLick();
                break;

            case R.id.wait:
                waitButtonClick();
                break;

            case R.id.client_profile:
            case R.id.select:
                changeImageClick();
                break;

            default:
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        switch (requestCode) {
            case GALLERY_REQUEST:
                if (resultCode == RESULT_OK) {
                    final Uri selectedImage = imageReturnedIntent.getData();
                    storageReference.child("Photos").child(client.id).putFile(selectedImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            try {
                                DatabaseReference participantCount = myDatabase.getReference("users/" + client.id + "/hasImage");
                                participantCount.setValue(true);

                                Toast.makeText(rootView.getContext(), "Изображение успешно обновлено", Toast.LENGTH_SHORT).show();
                                userProfileImage.setImageURI(selectedImage);
                            }catch (Exception e){
                                Toast.makeText(rootView.getContext(), "Произошла ошибка при отправке фотографии", Toast.LENGTH_LONG).show();
                                Log.e("ImageUploadFailure", e.getMessage());
                            }
                            progressBar.setVisibility(View.GONE);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(rootView.getContext(), "Произошла ошибка при отправке фотографии", Toast.LENGTH_LONG).show();
                            Log.e("ImageUploadFailure", e.getMessage());
                        }
                    });
                }else {
                    progressBar.setVisibility(View.GONE);
                }
                break;

            default:
                break;
        }
    }
}


