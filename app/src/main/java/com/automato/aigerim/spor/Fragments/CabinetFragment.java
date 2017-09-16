package com.automato.aigerim.spor.Fragments;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

import com.automato.aigerim.spor.Activity.MainActivity;
import com.automato.aigerim.spor.Adapter.UsersDisputeAdapter;
import com.automato.aigerim.spor.Models.Dispute;
import com.automato.aigerim.spor.Models.User;
import com.automato.aigerim.spor.R;
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

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import cn.pedant.SweetAlert.SweetAlertDialog;
import ru.cloudpayments.sdk.CardFactory;
import ru.cloudpayments.sdk.ICard;
import ru.cloudpayments.sdk.IPayment;
import ru.cloudpayments.sdk.PaymentFactory;
import ru.cloudpayments.sdk.business.domain.model.BaseResponse;
import ru.cloudpayments.sdk.business.domain.model.billing.CardsAuthConfirmResponse;
import ru.cloudpayments.sdk.business.domain.model.billing.CardsAuthResponse;
import ru.cloudpayments.sdk.view.PaymentTaskListener;

import static ru.cloudpayments.sdk.utils.Logger.log;


public class CabinetFragment extends Fragment implements View.OnClickListener {

    View rootView;

    ImageView userProfileImage;
    TextView userName;
    TextView userEmail;
    TextView userAge;
    TextView userMoney;
    TextView sporLabel;
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
    private PaymentTaskListener paymentTaskListener = new PaymentTaskListener() {
        @Override
        public void success(BaseResponse baseResponse) {
            // успешно
            Toast.makeText(rootView.getContext(), "Ok", Toast.LENGTH_SHORT).show();
            // baseResponse instanceof CardsAuthConfirmResponse - оплата 3ds
            // baseResponse instanceof CardsAuthResponse
        }

        @Override
        public void error(BaseResponse response) {
            // ошибка
            log("BuildInActivity got error " + response);
            if (response instanceof CardsAuthConfirmResponse)
                showErrorResult(((CardsAuthConfirmResponse) response).transaction.cardHolderMessage);
            if (response instanceof CardsAuthResponse)
                showErrorResult(((CardsAuthResponse) response).auth.cardHolderMessage);
            else
                showErrorResult(response.message);
        }

        @Override
        public void cancel() {
            // отменено пользователем
            Toast.makeText(rootView.getContext(), "Cancel", Toast.LENGTH_SHORT).show();
        }
    };

    private void showErrorResult(String message) {
        SweetAlertDialog mProgressDialog = new SweetAlertDialog(rootView.getContext(), SweetAlertDialog.ERROR_TYPE);
        mProgressDialog.setTitleText("Ошибка");
        mProgressDialog.setContentText(message);
        mProgressDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                sweetAlertDialog.dismissWithAnimation();
            }
        });
        mProgressDialog.show();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_cabinet, container, false);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        myDatabase = FirebaseDatabase.getInstance();
        reference = myDatabase.getReference();
        mAuth = FirebaseAuth.getInstance();

        TextView title = (TextView) getActivity().findViewById(R.id.title);
        title.setText("Личный кабинет");

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
                    Bitmap image = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    userProfileImage.setImageBitmap(image);
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
        this.userName = (TextView) rootView.findViewById(R.id.client_name);
        this.userEmail = (TextView) rootView.findViewById(R.id.client_email);
        this.userAge = (TextView) rootView.findViewById(R.id.client_age);
        this.userMoney = (TextView) rootView.findViewById(R.id.client_money);
        this.sporLabel = (TextView) rootView.findViewById(R.id.client_spors_label);

        TextView topUpTheBalance = (TextView) rootView.findViewById(R.id.top_up_balance);
        topUpTheBalance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(rootView.getContext(), "Click", Toast.LENGTH_SHORT).show();
                topUpTheBalance();
            }
        });

        if (MainActivity.isAdmin()) {
            rootView.findViewById(R.id.age_layout).setVisibility(View.GONE);
            rootView.findViewById(R.id.client_money).setVisibility(View.GONE);
            rootView.findViewById(R.id.sort_buttons).setVisibility(View.GONE);
            rootView.findViewById(R.id.clientSpors).setVisibility(View.GONE);
            rootView.findViewById(R.id.client_spors_label).setVisibility(View.GONE);
            topUpTheBalance.setVisibility(View.GONE);
        }

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

    public void topUpTheBalance() {
        ICard card = CardFactory.create("5200828282828210", "0718", "123");
        if (card.isValidNumber()) {
            try {
                String criptogram = card.cardCryptogram("pk_d1f87a40424414e08730cadea80a1");
                IPayment paymentAuth = PaymentFactory.auth(getActivity(),
                        "pk_d1f87a40424414e08730cadea80a1", "testInvoiceId", "test_acc@mail.ru",
                        criptogram, "Piter", 15.0, "KZT", "Поплнение счёта на Dispute", "");
                paymentAuth.run(paymentTaskListener);
            } catch (UnsupportedEncodingException | NoSuchPaddingException | NoSuchProviderException | NoSuchAlgorithmException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
                e.printStackTrace();
            }
        } else {
            //CardNumber is not valid
        }
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

            default:
                break;
        }
    }
}


