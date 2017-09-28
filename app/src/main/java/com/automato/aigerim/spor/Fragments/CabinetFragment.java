package com.automato.aigerim.spor.Fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.automato.aigerim.spor.Activity.EditingProfilePageActivity;
import com.automato.aigerim.spor.Activity.MainActivity;
import com.automato.aigerim.spor.Activity.SettingsActivity;
import com.automato.aigerim.spor.Adapter.UsersDisputeAdapter;
import com.automato.aigerim.spor.Models.Dispute;
import com.automato.aigerim.spor.Models.User;
import com.automato.aigerim.spor.Other.Tools.Tools;
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
import com.soundcloud.android.crop.Crop;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Pattern;

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
import ru.cloudpayments.sdk.business.domain.model.billing.CardsChargeResponse;
import ru.cloudpayments.sdk.view.PaymentTaskListener;

import static ru.cloudpayments.sdk.utils.Logger.log;


public class CabinetFragment extends Fragment implements View.OnClickListener {

    ImageView userProfileImage;
    TextView userName;
    TextView userEmail;
    TextView userAge;
    TextView userMoney;
    TextView sporLabel;
    ImageView changeProfile;
    FloatingActionButton activ;
    FloatingActionButton finished;
    FloatingActionButton wait;
    ProgressBar progressBar;
    RecyclerView userDisputesList;
    FirebaseStorage storage;
    EditText cardNumber;
    EditText expMonth;
    EditText expYear;
    EditText cvv;
    EditText cardHolder;
    EditText payment;
    StorageReference storageReference;
    FirebaseDatabase myDatabase;
    DatabaseReference reference;
    FirebaseAuth mAuth;
    CheckBox has3DSecure;
    User client;
    ArrayList<Dispute> userDisputes = new ArrayList<>();
    private View rootView;
    private String publickKey = "pk_d1f87a40424414e08730cadea80a1";
    private Pattern CODE_PATTERN = Pattern.compile("([0-9]{0,4})|([0-9]{4}-)+|([0-9]{4}-[0-9]{0,4})+");
    private Tools tools = new Tools();

    private PaymentTaskListener paymentTaskListener = new PaymentTaskListener() {
        @Override
        public void success(BaseResponse response) {
            // успешно
            if (response instanceof CardsAuthConfirmResponse)
                showSuccessResult(((CardsAuthConfirmResponse) response).transaction.cardHolderMessage);
            else if (response instanceof CardsAuthResponse)
                showSuccessResult(((CardsAuthResponse) response).auth.cardHolderMessage);
            else {
                if (!tools.isNullOrWhitespace(response.message)) {
                    showSuccessResult(response.message);
                } else {
                    if (response instanceof CardsChargeResponse) {
                        showSuccessResult(((CardsChargeResponse) response).transaction.cardHolderMessage);
                    } else {
                        Toast.makeText(rootView.getContext(), "Успешное пополнение счёта", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }

        @Override
        public void error(BaseResponse response) {
            // ошибка
            log("BuildInActivity got error " + response);
            if (response instanceof CardsAuthConfirmResponse)
                showErrorResult(((CardsAuthConfirmResponse) response).transaction.cardHolderMessage);
            else if (response instanceof CardsAuthResponse)
                showErrorResult(((CardsAuthResponse) response).auth.cardHolderMessage);
            else {
                if (!tools.isNullOrWhitespace(response.message)) {
                    if (!response.message.equals("Авторизация не пройдена")) {
                        showErrorResult(response.message);
                    } else { //Если такой вариант не подойдёт то надо стереть "} else {" и всё его содержимое а на лэйауте включить CheckBox
                        String cardNumberString = cardNumber.getText().toString().replace("-", "");
                        String expDate = getExpDate(expMonth, expYear);
                        String cvvCode = cvv.getText().toString();
                        String cardHolderName = cardHolder.getText().toString();

                        double money = Double.parseDouble(payment.getText().toString());
                        ICard card = CardFactory.create(cardNumberString, expDate, cvvCode);
                        if (card.isValidNumber()) {
                            try {
                                String criptogram = card.cardCryptogram(publickKey);
                                IPayment paymentAuth;
                                String message = "Take my money";
                                String currency = "KZT";
                                String description = "Поплнение счёта на Dispute";
                                paymentAuth = PaymentFactory.charge(getActivity(),
                                        publickKey, client.id, message,
                                        criptogram, cardHolderName, money, currency, description, "");
                                paymentAuth.run(paymentTaskListener);
                            } catch (UnsupportedEncodingException | NoSuchPaddingException | NoSuchProviderException | NoSuchAlgorithmException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                } else {
                    if (response instanceof CardsChargeResponse) {
                        showErrorResult(((CardsChargeResponse) response).transaction.cardHolderMessage);
                    } else {
                        showErrorResult("Произошла неизвестная ошибка");
                    }
                }
            }
        }

        @Override
        public void cancel() {
            // отменено пользователем
        }
    };

    private void showSuccessResult(String message) {
        SweetAlertDialog mProgressDialog = new SweetAlertDialog(rootView.getContext(), SweetAlertDialog.SUCCESS_TYPE);
        mProgressDialog.setTitleText("Успех!");
        mProgressDialog.setContentText(message);
        mProgressDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                sweetAlertDialog.dismissWithAnimation();
            }
        });
        mProgressDialog.show();
    }

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

        getActivity().findViewById(R.id.shadow).setVisibility(View.GONE);

        TextView title = (TextView) getActivity().findViewById(R.id.title);
        title.setText("Мой профиль");

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
        String s = dispute.date + " " + dispute.time;
        s = s.replace("/", ".");
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

                try {
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/mm/yyyy");
                    Date date = simpleDateFormat.parse(client.birthday);
                    int count = new Date().getYear() - date.getYear();

                    String endOfMessage = " лет";
                    int lastNumber = count % 10;
                    if (lastNumber == 2 || lastNumber == 3 || lastNumber == 4) {
                        endOfMessage = " года";
                    } else if (lastNumber == 1) {
                        endOfMessage = " год";
                    }
                    if (Integer.toString(count).length() >= 2) {
                        int lastTwoNumbers = count % 100;
                        if (lastTwoNumbers == 11 || lastTwoNumbers == 12 || lastTwoNumbers == 13 || lastTwoNumbers == 14) {
                            endOfMessage = " лет";
                        }
                    }

                    userAge.setText(count + endOfMessage);
                } catch (ParseException e) {
                    e.printStackTrace();
                    userAge.setText(client.birthday);
                }

                userMoney.setText(client.money + " тенге");
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
        this.changeProfile = (ImageView) rootView.findViewById(R.id.change_profile);

        TextView topUpTheBalance = (TextView) rootView.findViewById(R.id.top_up_balance);
        topUpTheBalance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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

        changeProfile.setOnClickListener(CabinetFragment.this);
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
        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_top_up_balance, null); // Получаем layout по его ID
        AlertDialog.Builder builder = new AlertDialog.Builder(rootView.getContext());

        builder.setView(view);
        builder.setCancelable(true);

        AlertDialog dialog = builder.create();
        initAndShowPaymentsDialog(dialog, view);
    }

    private void initAndShowPaymentsDialog(final AlertDialog dialog, View view) {
        Button ok = (Button) view.findViewById(R.id.done_up_balance);
        Button cancel = (Button) view.findViewById(R.id.cancel_up_balance);
        cardNumber = (EditText) view.findViewById(R.id.card_number_text);
        expMonth = (EditText) view.findViewById(R.id.expMonth);
        expYear = (EditText) view.findViewById(R.id.expYear);
        cvv = (EditText) view.findViewById(R.id.cvv_text);
        cardHolder = (EditText) view.findViewById(R.id.card_holder_text);
        payment = (EditText) view.findViewById(R.id.payment_text);
        has3DSecure = (CheckBox) view.findViewById(R.id.has_3d_secure);

        cardNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0 && !CODE_PATTERN.matcher(s).matches()) {
                    String input = s.toString();
                    String numbersOnly = keepNumbersOnly(input);
                    String code = formatNumbersAsCode(numbersOnly);

                    Log.w("", "numbersOnly" + numbersOnly);
                    Log.w("", "code" + code);

                    cardNumber.removeTextChangedListener(this);
                    cardNumber.setText(code);
                    // You could also remember the previous position of the cursor
                    cardNumber.setSelection(code.length());
                    cardNumber.addTextChangedListener(this);
                }
            }

            private String keepNumbersOnly(CharSequence s) {
                return s.toString().replaceAll("[^0-9]", ""); // Should of course be more robust
            }

            private String formatNumbersAsCode(CharSequence s) {
                int groupDigits = 0;
                String tmp = "";
                for (int i = 0; i < s.length(); ++i) {
                    tmp += s.charAt(i);
                    ++groupDigits;
                    if (groupDigits == 4) {
                        tmp += "-";
                        groupDigits = 0;
                    }
                }
                return tmp;
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String cardNumberString = cardNumber.getText().toString().replace("-", "");
                String expDate = getExpDate(expMonth, expYear);
                String cvvCode = cvv.getText().toString();
                String cardHolderName = cardHolder.getText().toString();

                boolean checked = check(cardNumberString, cvvCode, cardHolderName, cardNumber, expMonth, expYear, cvv, cardHolder, payment);

                if (checked) {
                    double money = Double.parseDouble(payment.getText().toString());
                    ICard card = CardFactory.create(cardNumberString, expDate, cvvCode);
                    if (card.isValidNumber()) {
                        try {
                            String criptogram = card.cardCryptogram(publickKey);
                            IPayment paymentAuth;
                            String message = "Take my money";
                            String currency = "KZT";
                            String description = "Поплнение счёта на Dispute";
                            if (has3DSecure.isChecked()) {
                                paymentAuth = PaymentFactory.charge(getActivity(),
                                        publickKey, client.id, message,
                                        criptogram, cardHolderName, money, currency, description, "");
                            } else {
                                paymentAuth = PaymentFactory.auth(getActivity(),
                                        publickKey, client.id, message,
                                        criptogram, cardHolderName, money, currency, description, "");
                            }
                            paymentAuth.run(paymentTaskListener);
                            dialog.dismiss();
                        } catch (UnsupportedEncodingException | NoSuchPaddingException | NoSuchProviderException | NoSuchAlgorithmException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
                            e.printStackTrace();
                        }
                    } else {
                        showErrorResult("Вы ввели не верный номер карты");
                    }
                }
            }
        });
        dialog.show();
    }

    private boolean check(String cardNumberString, String cvvCode, String cardHolderName, EditText cardNumber, EditText expMonth, EditText expYear, EditText cvv, EditText cardHolder, EditText payment) {
        boolean checked = true;

        if (tools.isNullOrWhitespace(cardNumberString)) {
            checked = false;
            cardNumber.setError("Обязательно");
        } else {
            cardNumber.setError(null);
        }

        if (tools.isNullOrWhitespace(expMonth.getText().toString())) {
            checked = false;
            expMonth.setError("Обязательно");
        } else {
            if (Integer.parseInt(expMonth.getText().toString()) > 12) {
                checked = false;
                expMonth.setError("Не корректный ввод");
            } else {
                expMonth.setError(null);
            }
        }

        if (tools.isNullOrWhitespace(expYear.getText().toString())) {
            checked = false;
            expYear.setError("Обязательно");
        } else {
            expYear.setError(null);
        }

        if (tools.isNullOrWhitespace(cvvCode)) {
            checked = false;
            cvv.setError("Обязательно");
        } else {
            cvv.setError(null);
        }

        if (tools.isNullOrWhitespace(cardHolderName)) {
            checked = false;
            cardHolder.setError("Обязательно");
        } else {
            cardHolder.setError(null);
        }

        if (tools.isNullOrWhitespace(payment.getText().toString())) {
            checked = false;
            payment.setError("Обязательно");
        } else {
            payment.setError(null);
        }
        return checked;
    }

    private String getExpDate(EditText expMonth, EditText expYear) {
        String month = expMonth.getText().length() < 2 ? "0" + expMonth.getText().toString() : expMonth.getText().toString();
        return month + expYear.getText().toString();
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

            case R.id.change_profile:
                changeProfileClick();
                break;

            default:
                break;
        }
    }

    private void changeProfileClick() {
        Intent intent = new Intent(getActivity(), EditingProfilePageActivity.class);
        startActivity(intent);
    }
}


