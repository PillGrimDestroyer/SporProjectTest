package com.automato.aigerim.spor.Other;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.widget.RecyclerView;

import com.automato.aigerim.spor.Activity.MainActivity;
import com.automato.aigerim.spor.Adapter.UsersDisputeAdapter;
import com.automato.aigerim.spor.Models.Choice;
import com.automato.aigerim.spor.Models.Dispute;
import com.automato.aigerim.spor.Models.DisputeFromOlimp;
import com.automato.aigerim.spor.Models.Participant;
import com.automato.aigerim.spor.Models.User;
import com.automato.aigerim.spor.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by HAOR on 11.10.2017.
 */

public class Api {
    private final String url = "http://95.85.60.144:3000/api/";
    public boolean error = false;
    private Context context;
    private SweetAlertDialog mProgressDialog;

    public Api(Context context) {
        this.context = context;
    }

    private void succesDialog(final String message) {
        if (!((Activity) context).isFinishing()) {
            dismissWithAnimationLoader();
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    SweetAlertDialog dialog = new SweetAlertDialog(context, SweetAlertDialog.SUCCESS_TYPE);
                    dialog.setTitleText("Успех!");
                    dialog.setContentText(message);
                    dialog.setConfirmText("Ок");
                    dialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            sweetAlertDialog.dismissWithAnimation();
                        }
                    });
                    dialog.show();
                }
            });
        }
    }

    private void errorDialog(final String message) {
        if (!((Activity) context).isFinishing()) {
            dismissWithAnimationLoader();
            error = true;
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    SweetAlertDialog dialog = new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE);
                    dialog.setTitleText("Ошибка");
                    dialog.setContentText(message);
                    dialog.setConfirmText("Ок");
                    dialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            sweetAlertDialog.dismissWithAnimation();
                        }
                    });
                    dialog.show();
                }
            });
        }
    }

    private void showLoader() {
        if (!((Activity) context).isFinishing()) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    mProgressDialog = new SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE);
                    mProgressDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                    mProgressDialog.setTitleText("Загрузка");
                    mProgressDialog.setCancelable(false);
                    mProgressDialog.show();
                }
            });
        }
    }

    private void dismissWithAnimationLoader() {
        if (!((Activity) context).isFinishing()) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    if (mProgressDialog != null) {
                        mProgressDialog.dismissWithAnimation();
                        mProgressDialog = null;
                    }
                }
            });
        }
    }

    public void updateUser() {
        showLoader();

        final Requests requests = new Requests();
        final String dopurl = "user";

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                JSONObject reqjson = new JSONObject();
                String params;
                try {
                    reqjson.put("user_id", User.id);
                    reqjson.put("email", User.email);
                    reqjson.put("birthday", User.birthday);
                    reqjson.put("gender", User.gender);
                    reqjson.put("hasImage", User.hasImage);
                    reqjson.put("money", User.money);
                    reqjson.put("name", User.name);
                    reqjson.put("receiveNotifications", User.receiveNotifications);

                    params = reqjson.toString();

                    AsyncTask asyncTask = requests.execute(url + dopurl, params, "PUT");
                    String resp = "";
                    resp = asyncTask.get().toString();

                    String ss = resp.substring(resp.indexOf("{"), resp.lastIndexOf("}") + 1);
                    JSONObject respjson = new JSONObject(ss);
                    String status = respjson.getString("status");
                    if (status.equals("ok")) {
                        //Ничего не делать
                        dismissWithAnimationLoader();
                    } else {
                        resp = respjson.getString("msg");
                        errorDialog(resp);
                    }
                } catch (InterruptedException | ExecutionException | JSONException | StringIndexOutOfBoundsException e) {
                    errorDialog(e.getMessage());
                }
            }
        });
        thread.setDaemon(false);
        thread.start();
    }

    public void forgotPass(final String email) {
        showLoader();

        final Requests requests = new Requests();
        final String dopurl = "forgotpassword";

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                JSONObject reqjson = new JSONObject();
                String params;
                try {
                    reqjson.put("email", email);

                    params = reqjson.toString();

                    AsyncTask asyncTask = requests.execute(url + dopurl, params);
                    String resp = "";
                    resp = asyncTask.get().toString();

                    String ss = resp.substring(resp.indexOf("{"), resp.lastIndexOf("}") + 1);
                    JSONObject respjson = new JSONObject(ss);
                    String status = respjson.getString("status");
                    if (status.equals("ok")) {
                        succesDialog("На вашу почту было выслано сообщение");
                    } else {
                        resp = respjson.getString("msg");
                        errorDialog(resp);
                    }
                } catch (InterruptedException | ExecutionException | JSONException | StringIndexOutOfBoundsException e) {
                    errorDialog(e.getMessage());
                }
            }
        });
        thread.setDaemon(false);
        thread.start();
    }

    public boolean logIn(final String email, final String pass) {
        showLoader();

        boolean allright = false;
        final Requests requests = new Requests();
        final String dopurl = "login";

        JSONObject reqjson = new JSONObject();
        String params;
        try {
            reqjson.put("email", email);
            reqjson.put("password", pass);

            params = reqjson.toString();

            AsyncTask asyncTask = requests.execute(url + dopurl, params);
            String resp = "";
            resp = asyncTask.get().toString();

            String ss = resp.substring(resp.indexOf("{"), resp.lastIndexOf("}") + 1);
            JSONObject respjson = new JSONObject(ss);
            String status = respjson.getString("status");
            if (status.equals("ok")) {
                JSONObject userObj = respjson.getJSONObject("user");
                User.email = userObj.getString("email");
                User.name = userObj.getString("name");
                User.gender = userObj.getString("gender");
                User.birthday = userObj.getString("birthday");
                User.money = userObj.getInt("money");
                User.confirmed = userObj.getBoolean("confirmed");
                User.id = userObj.getString("_id");
                User.hasImage = Boolean.parseBoolean(userObj.getString("hasImage"));

                boolean isAdmin = User.email.equals("automato.android@yandex.ru");
                MainActivity.setAdmin(isAdmin);
                allright = true;

                dismissWithAnimationLoader();
            } else {
                resp = respjson.getString("msg");
                errorDialog(resp);
            }
        } catch (InterruptedException | ExecutionException | JSONException | StringIndexOutOfBoundsException e) {
            allright = false;
            errorDialog(e.getMessage());
        }

        return allright;
    }

    public void IncrementViewsCount(final String dispute_id) {
        final Requests requests = new Requests();
        final String dopurl = "viewsCount";

        final JSONObject reqjson = new JSONObject();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    reqjson.put("_id", dispute_id);

                    String params = reqjson.toString();

                    AsyncTask asyncTask = requests.execute(url + dopurl, params, "PUT");
                    String resp = "";
                    resp = asyncTask.get().toString();

                    String ss = resp.substring(resp.indexOf("{"), resp.lastIndexOf("}") + 1);
                    JSONObject respjson = new JSONObject(ss);
                    String status = respjson.getString("status");
                    if (status.equals("ok")) {

                    } else {
                        resp = respjson.getString("msg");
                        errorDialog(resp);
                    }
                } catch (InterruptedException | ExecutionException | JSONException | StringIndexOutOfBoundsException e) {
                    errorDialog(e.getMessage());
                }
            }
        });
        thread.setDaemon(false);
        thread.start();
    }

    public void signUp(final String email, final String password, final String name, final String birthday, final String gender, final Uri imageUri) {
        showLoader();

        final Requests requests = new Requests();
        final String dopurl = "user";

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                JSONObject reqjson = new JSONObject();
                String params;
                try {
                    reqjson.put("email", email);
                    reqjson.put("password", password);
                    reqjson.put("name", name);
                    reqjson.put("birthday", birthday);
                    reqjson.put("gender", gender);
                    if (imageUri != null)
                        reqjson.put("hasImage", true);
                    else
                        reqjson.put("hasImage", false);

                    params = reqjson.toString();

                    AsyncTask asyncTask = requests.execute(url + dopurl, params);
                    String resp = "";
                    resp = asyncTask.get().toString();

                    String ss = resp.substring(resp.indexOf("{"), resp.lastIndexOf("}") + 1);
                    JSONObject respjson = new JSONObject(ss);
                    String status = respjson.getString("status");
                    if (status.equals("ok")) {
                        User.email = email;
                        User.name = name;
                        User.gender = gender;
                        User.birthday = birthday;
                        User.money = 1000;
                        User.confirmed = false;
                        User.id = respjson.getString("user_id");

                        if (imageUri != null) {
                            Tools.uploadUserPhoto(imageUri, context, true);
                        }
                        dismissWithAnimationLoader();

                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                SweetAlertDialog dialog = new SweetAlertDialog(context, SweetAlertDialog.SUCCESS_TYPE);
                                dialog.setTitleText("Поздравляю!");
                                dialog.setContentText("Успешная регистрация!");
                                dialog.setConfirmText("Ок");
                                dialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                                        sweetAlertDialog.dismissWithAnimation();

                                        Intent intent = new Intent(context, MainActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        context.startActivity(intent);
                                    }
                                });
                                dialog.show();
                            }
                        });
                    } else {
                        resp = respjson.getString("msg");
                        errorDialog(resp);
                    }
                } catch (InterruptedException | ExecutionException | JSONException | StringIndexOutOfBoundsException e) {
                    errorDialog(e.getMessage());
                }
            }
        });
        thread.setDaemon(false);
        thread.start();



        /*mAuth.createUserWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                Log.d("info", "createUserWithEmail:success");
                final FirebaseUser user = mAuth.getCurrentUser();

                DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("users");
                final String userId = user.getUid();
                User p1 = new User(userId, name, user.getEmail(), birthday, gender);
                mDatabase.child(userId).setValue(p1);

                if (imageUriCrop != null) {
                    final StorageReference filepath = mStorage.child("Photos").child(userId);
                    filepath.putFile(imageUriCrop).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            final long ONE_MEGABYTE = 1024 * 1024;
                            filepath.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                @Override
                                public void onSuccess(byte[] bytes) {
                                    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("users");
                                    mDatabase.child(userId + "/hasImage").setValue(true);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                }
                            });

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(RegistrationActivity.this, "Не удалось загрузить изображение", Toast.LENGTH_LONG).show();
                        }
                    });

                }
                if (imageUriFull != null) {
                    final StorageReference filepath = mStorage.child("Photos").child(userId + "-full");
                    filepath.putFile(imageUriFull);
                }
                hideProgressDialog();
                Toast.makeText(RegistrationActivity.this, "Письмо с подтверждением аккаунта отправлено на " + user.getEmail(), Toast.LENGTH_LONG).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                String message = e.getMessage();
                String title = "Ошибка";
                SweetAlertDialog dialog = new SweetAlertDialog(RegistrationActivity.this, SweetAlertDialog.ERROR_TYPE);
                dialog.setTitleText(title);
                dialog.setConfirmText("Ок");
                if (e instanceof FirebaseAuthUserCollisionException) {
                    message = "Пользователь с данным email уже существует";
                }
                dialog.setContentText(message);
                dialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.dismissWithAnimation();
                    }
                });
                hideProgressDialog();
                dialog.show();
            }
        });*/
    }

    public void addNewDispute(final DisputeFromOlimp disputeFromOlimp, final String photo, final BottomNavigationView navigation) {
//        showLoader();

        final Requests requests = new Requests();
        final String dopurl = "newdispute";

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                JSONObject reqjson = new JSONObject();
                String params;
                try {
                    reqjson.put("subject", disputeFromOlimp.rivors);
                    reqjson.put("date", disputeFromOlimp.date);
                    reqjson.put("time", disputeFromOlimp.time);
                    reqjson.put("category", disputeFromOlimp.category);
                    reqjson.put("result", "");
//                    reqjson.put("likeCount", "0");
//                    reqjson.put("viewCount", "0");
//                    reqjson.put("participantCount", "0");
                    reqjson.put("subcategory", disputeFromOlimp.subcategory);
                    reqjson.put("photo", photo);

                    JSONObject choice1 = new JSONObject();
                    JSONObject choice2 = new JSONObject();
                    JSONObject choices = new JSONObject();

                    choice1.put("choice", disputeFromOlimp.rivor1.trim());
//                    choice1.put("id", disputeFromOlimp.rivor1.trim());
                    choice1.put("rate", 0);

                    choice2.put("choice", disputeFromOlimp.rivor2.trim());
//                    choice2.put("id", disputeFromOlimp.rivor2.trim());
                    choice2.put("rate", 0);

                    choices.put("rivor1", choice1);
                    choices.put("rivor2", choice2);

                    reqjson.put("choices", choices);

                    params = reqjson.toString();

                    AsyncTask asyncTask = requests.execute(url + dopurl, params);
                    String resp = "";
                    resp = asyncTask.get().toString();

                    String ss = resp.substring(resp.indexOf("{"), resp.lastIndexOf("}") + 1);
                    JSONObject respjson = new JSONObject(ss);
                    String status = respjson.getString("status");
                    if (status.equals("ok")) {
                        dismissWithAnimationLoader();
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                SweetAlertDialog dialog = new SweetAlertDialog(context, SweetAlertDialog.SUCCESS_TYPE);
                                dialog.setTitleText("Успех!");
                                dialog.setContentText("Спор успешно создан");
                                dialog.setConfirmText("Ок");
                                dialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                                        sweetAlertDialog.dismissWithAnimation();
                                        navigation.setSelectedItemId(R.id.main);
                                    }
                                });
                                dialog.show();
                            }
                        });
                    } else {
                        resp = respjson.getString("msg");
                        errorDialog(resp);
                    }
                } catch (InterruptedException | ExecutionException | JSONException | StringIndexOutOfBoundsException e) {
                    errorDialog(e.getMessage());
                }
            }
        });
        thread.setDaemon(false);
        thread.start();
    }

    public ArrayList<Dispute> getUserDisputes(final RecyclerView userDisputesList) {
        showLoader();

        ArrayList<Dispute> disputes = new ArrayList<>();

        final Requests requests = new Requests();
        final String dopurl = "userDispute";

        JSONObject reqjson = new JSONObject();
        String params;
        try {
            params = "user_id=" + User.id;

            AsyncTask asyncTask = requests.execute(url + dopurl + "?" + params, null);
            String resp = "";
            resp = asyncTask.get().toString();

            String ss = resp.substring(resp.indexOf("{"), resp.lastIndexOf("}") + 1);
            JSONObject respjson = new JSONObject(ss);
            String status = respjson.getString("status");
            if (status.equals("ok")) {
                JSONArray jsonArray = respjson.getJSONArray("disputes");

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonDisputeObject = jsonArray.getJSONObject(i);
                    JSONArray jsonArray1 = jsonDisputeObject.getJSONArray("spors");
                    jsonDisputeObject = jsonArray1.getJSONObject(0);
                    Dispute dispute = new Dispute();

                    dispute.id = jsonDisputeObject.getString("_id");
//                    dispute.likeCount = jsonDisputeObject.getInt("likeCount");
                    dispute.category = jsonDisputeObject.getString("category");
                    dispute.result = jsonDisputeObject.getString("result");
                    dispute.calculated = jsonDisputeObject.getBoolean("calculated");
                    dispute.date = jsonDisputeObject.getString("date");
                    try {
                        dispute.photo = jsonDisputeObject.getString("photo");
                    } catch (Exception ex) {

                    }
                    dispute.subcategory = jsonDisputeObject.getString("subcategory");
                    dispute.subject = jsonDisputeObject.getString("subject");
                    dispute.time = jsonDisputeObject.getString("time");
//                    dispute.viewCount = jsonDisputeObject.getInt("viewCount");

                    JSONObject jsonChoiceObject = jsonDisputeObject.getJSONObject("choices");
                    JSONObject jsonRivor1Object = jsonChoiceObject.getJSONObject("rivor1");
                    JSONObject jsonRivor2Object = jsonChoiceObject.getJSONObject("rivor2");

                    Choice rivor1 = new Choice();
                    rivor1.rate = jsonRivor1Object.getInt("rate");
                    rivor1.choice = jsonRivor1Object.getString("choice");
//                    rivor1.spor_id = jsonRivor1Object.getString("spor_id");
//                    rivor1.id = jsonRivor1Object.getString("_id");

                    Choice rivor2 = new Choice();
                    rivor2.rate = jsonRivor2Object.getInt("rate");
                    rivor2.choice = jsonRivor2Object.getString("choice");
//                    rivor2.spor_id = jsonRivor2Object.getString("spor_id");
//                    rivor2.id = jsonRivor2Object.getString("_id");

                    dispute.choices.put("rivor1", rivor1);
                    dispute.choices.put("rivor2", rivor2);

                    /*JSONArray jsonArrayLikes = jsonDisputeObject.getJSONArray("likes");
                    for (int j = 0; j < jsonArrayLikes.length(); j++) {
                        dispute.likes.put(jsonArrayLikes.getString(i), true);
                    }

                    JSONArray jsonArrayParticipants = jsonDisputeObject.getJSONArray("participants");
                    for (int j = 0; j < jsonArrayParticipants.length(); j++) {
                        JSONObject jsonParticipantObject = jsonArrayParticipants.getJSONObject(j);
                        Participant participant = new Participant();
                        participant.choice = jsonParticipantObject.getString("choice");
                        participant.money = jsonParticipantObject.getInt("money");
                        participant.spor_id = jsonParticipantObject.getString("spor_id");
                        participant.user_id = jsonParticipantObject.getString("user_id");
                        participant.winnings = jsonParticipantObject.getDouble("winnings");

                        dispute.participants.put(participant.user_id, participant);
                    }*/
                    disputes.add(dispute);
                }
                dismissWithAnimationLoader();
            } else {
                resp = respjson.getString("msg");
                userDisputesList.setAdapter(new UsersDisputeAdapter(new ArrayList<Dispute>()));
                errorDialog(resp);
            }
        } catch (InterruptedException | ExecutionException | JSONException | StringIndexOutOfBoundsException e) {
            errorDialog(e.getMessage());
        }
        return disputes;
    }

    public ArrayList<Dispute> getAllDisputes() {
        showLoader();

        ArrayList<Dispute> disputes = new ArrayList<>();

        final Requests requests = new Requests();
        final String dopurl = "dispute";

        JSONObject reqjson = new JSONObject();
        String params;
        try {
            params = "";
//            params = reqjson.toString();

            AsyncTask asyncTask = requests.execute(url + dopurl + "?" + params, null);
            String resp = "";
            resp = asyncTask.get().toString();

            String ss = resp.substring(resp.indexOf("{"), resp.lastIndexOf("}") + 1);
            JSONObject respjson = new JSONObject(ss);
            String status = respjson.getString("status");
            if (status.equals("ok")) {
                JSONArray jsonArray = respjson.getJSONArray("disputes");

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonDisputeObject = jsonArray.getJSONObject(i);
                    Dispute dispute = new Dispute();

                    dispute.id = jsonDisputeObject.getString("_id");
                    dispute.category = jsonDisputeObject.getString("category");
                    dispute.result = jsonDisputeObject.getString("result");
                    dispute.calculated = jsonDisputeObject.getBoolean("calculated");
                    dispute.date = jsonDisputeObject.getString("date");
                    try {
                        dispute.photo = jsonDisputeObject.getString("photo");
                    } catch (Exception e) {

                    }
                    dispute.subcategory = jsonDisputeObject.getString("subcategory");
                    dispute.subject = jsonDisputeObject.getString("subject");
                    dispute.time = jsonDisputeObject.getString("time");
                    dispute.viewCount = jsonDisputeObject.getInt("viewCount");

                    JSONObject jsonChoiceObject = jsonDisputeObject.getJSONObject("choices");
                    JSONObject jsonRivor1Object = jsonChoiceObject.getJSONObject("rivor1");
                    JSONObject jsonRivor2Object = jsonChoiceObject.getJSONObject("rivor2");

                    Choice rivor1 = new Choice();
                    rivor1.rate = jsonRivor1Object.getInt("rate");
                    rivor1.choice = jsonRivor1Object.getString("choice");
                    /*rivor1.spor_id = jsonRivor1Object.getString("spor_id");
                    rivor1.id = jsonRivor1Object.getString("_id");*/

                    Choice rivor2 = new Choice();
                    rivor2.rate = jsonRivor2Object.getInt("rate");
                    rivor2.choice = jsonRivor2Object.getString("choice");
                    /*rivor2.spor_id = jsonRivor2Object.getString("spor_id");
                    rivor2.id = jsonRivor2Object.getString("_id");*/

                    dispute.choices = new HashMap<>();
                    dispute.choices.put("rivor1", rivor1);
                    dispute.choices.put("rivor2", rivor2);

                    dispute.likes = new HashMap<>();
                    JSONArray jsonArrayLikes = jsonDisputeObject.getJSONArray("likes");
                    for (int j = 0; j < jsonArrayLikes.length(); j++) {
                        JSONObject jsonObject = jsonArrayLikes.getJSONObject(j);
                        dispute.likes.put(jsonObject.getString("user_id"), true);
                    }
                    dispute.likeCount = dispute.likes.values().size();

                    dispute.participants = new HashMap<>();
                    JSONArray jsonArrayParticipants = jsonDisputeObject.getJSONArray("participants");
                    for (int j = 0; j < jsonArrayParticipants.length(); j++) {
                        JSONObject jsonParticipantObject = jsonArrayParticipants.getJSONObject(j);
                        Participant participant = new Participant();
                        participant.choice = jsonParticipantObject.getString("choice");
                        participant.money = jsonParticipantObject.getInt("money");
                        participant.spor_id = jsonParticipantObject.getString("spor_id");
                        participant.user_id = jsonParticipantObject.getString("user_id");
                        participant.winnings = jsonParticipantObject.getDouble("winnings");

                        dispute.participants.put(participant.user_id, participant);
                    }
                    dispute.participantCount = dispute.participants.values().size();
                    disputes.add(dispute);
                }
                dismissWithAnimationLoader();
            } else {
                resp = respjson.getString("msg");
                errorDialog(resp);
            }
        } catch (InterruptedException | ExecutionException | JSONException | StringIndexOutOfBoundsException e) {
            errorDialog(e.getMessage());
        }
        return disputes;
    }

    public ArrayList<Dispute> getAllDisputes(int count) {
        showLoader();

        ArrayList<Dispute> disputes = new ArrayList<>();

        final Requests requests = new Requests();
        final String dopurl = "dispute";

        JSONObject reqjson = new JSONObject();
        String params;
        try {
            reqjson.put("count", count);

            params = "count=" + count;

            AsyncTask asyncTask = requests.execute(url + dopurl + "?" + params, null);
            String resp = "";
            resp = asyncTask.get().toString();

            String ss = resp.substring(resp.indexOf("{"), resp.lastIndexOf("}") + 1);
            JSONObject respjson = new JSONObject(ss);
            String status = respjson.getString("status");
            if (status.equals("ok")) {
                JSONArray jsonArray = respjson.getJSONArray("disputes");

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonDisputeObject = jsonArray.getJSONObject(i);
                    Dispute dispute = new Dispute();

                    dispute.id = jsonDisputeObject.getString("_id");
                    dispute.category = jsonDisputeObject.getString("category");
                    dispute.result = jsonDisputeObject.getString("result");
                    dispute.calculated = jsonDisputeObject.getBoolean("calculated");
                    dispute.date = jsonDisputeObject.getString("date");
                    try {
                        dispute.photo = jsonDisputeObject.getString("photo");
                    } catch (Exception ex) {

                    }
                    dispute.subcategory = jsonDisputeObject.getString("subcategory");
                    dispute.subject = jsonDisputeObject.getString("subject");
                    dispute.time = jsonDisputeObject.getString("time");
                    dispute.viewCount = jsonDisputeObject.getInt("viewCount");

                    JSONObject jsonChoiceObject = jsonDisputeObject.getJSONObject("choices");
                    JSONObject jsonRivor1Object = jsonChoiceObject.getJSONObject("rivor1");
                    JSONObject jsonRivor2Object = jsonChoiceObject.getJSONObject("rivor2");

                    Choice rivor1 = new Choice();
                    rivor1.rate = jsonRivor1Object.getInt("rate");
                    rivor1.choice = jsonRivor1Object.getString("choice");
//                    rivor1.spor_id = jsonRivor1Object.getString("spor_id");
//                    rivor1.id = jsonRivor1Object.getString("_id");

                    Choice rivor2 = new Choice();
                    rivor2.rate = jsonRivor2Object.getInt("rate");
                    rivor2.choice = jsonRivor2Object.getString("choice");
//                    rivor2.spor_id = jsonRivor2Object.getString("spor_id");
//                    rivor2.id = jsonRivor2Object.getString("_id");

                    dispute.choices.put("rivor1", rivor1);
                    dispute.choices.put("rivor2", rivor2);

                    JSONArray jsonArrayLikes = jsonDisputeObject.getJSONArray("likes");
                    for (int j = 0; j < jsonArrayLikes.length(); j++) {
                        JSONObject jsonObject = jsonArrayLikes.getJSONObject(j);
                        dispute.likes.put(jsonObject.getString("user_id"), true);
                    }
                    dispute.likeCount = dispute.likes.values().size();

                    JSONArray jsonArrayParticipants = jsonDisputeObject.getJSONArray("participants");
                    for (int j = 0; j < jsonArrayParticipants.length(); j++) {
                        JSONObject jsonParticipantObject = jsonArrayParticipants.getJSONObject(j);
                        Participant participant = new Participant();
                        participant.choice = jsonParticipantObject.getString("choice");
                        participant.money = jsonParticipantObject.getInt("money");
                        participant.spor_id = jsonParticipantObject.getString("spor_id");
                        participant.user_id = jsonParticipantObject.getString("user_id");
                        participant.winnings = jsonParticipantObject.getDouble("winnings");

                        dispute.participants.put(participant.user_id, participant);
                    }
                    dispute.participantCount = dispute.participants.values().size();
                    disputes.add(dispute);
                }
                dismissWithAnimationLoader();
            } else {
                resp = respjson.getString("msg");
                errorDialog(resp);
            }
        } catch (InterruptedException | ExecutionException | JSONException | StringIndexOutOfBoundsException e) {
            errorDialog(e.getMessage());
        }
        return disputes;
    }

    public void addParticipantToDispute(final String dispute_id, final int money, final String choice, final String choiceKey) {
//        showLoader();

        final Requests requests = new Requests();
        final String dopurl = "participant";

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                JSONObject reqjson = new JSONObject();
                String params;
                try {
                    reqjson.put("user_id", User.id);
                    reqjson.put("money", money);
                    reqjson.put("choice", Uri.encode(choice));
                    reqjson.put("dispute_id", dispute_id);
                    reqjson.put("choice_key", choiceKey);

                    params = reqjson.toString();

                    AsyncTask asyncTask = requests.execute(url + dopurl, params);
                    String resp = "";
                    resp = asyncTask.get().toString();

                    String ss = resp.substring(resp.indexOf("{"), resp.lastIndexOf("}") + 1);
                    JSONObject respjson = new JSONObject(ss);
                    String status = respjson.getString("status");
                    if (status.equals("ok")) {
                        //Ничиго не делать
                        dismissWithAnimationLoader();
                    } else {
                        resp = respjson.getString("msg");
                        errorDialog(resp);
                    }
                } catch (InterruptedException | ExecutionException | JSONException | StringIndexOutOfBoundsException e) {
                    errorDialog(e.getMessage());
                }
            }
        });
        thread.setDaemon(false);
        thread.start();
    }

    public ArrayList<Dispute> getSortedBySubCategoryDisputes(String category, String subCategory) throws UnsupportedEncodingException {
//        showLoader();

        ArrayList<Dispute> disputes = new ArrayList<>();

        final Requests requests = new Requests();
        final String dopurl = "disputecat";

        JSONObject reqjson = new JSONObject();
        String params;
        try {
            category = URLEncoder.encode(category, "UTF-8");
            subCategory = URLEncoder.encode(subCategory, "UTF-8");
            params = "category=" + category + "&subcategory=" + subCategory;

            AsyncTask asyncTask = requests.execute(url + dopurl + "?" + params, null);
            String resp = "";
            resp = asyncTask.get().toString();

            String ss = resp.substring(resp.indexOf("{"), resp.lastIndexOf("}") + 1);
            JSONObject respjson = new JSONObject(ss);
            String status = respjson.getString("status");
            if (status.equals("ok")) {
                JSONArray jsonArray = respjson.getJSONArray("disputes");

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonDisputeObject = jsonArray.getJSONObject(i);
                    Dispute dispute = new Dispute();

                    dispute.id = jsonDisputeObject.getString("_id");
                    dispute.category = jsonDisputeObject.getString("category");
                    dispute.result = jsonDisputeObject.getString("result");
                    dispute.calculated = jsonDisputeObject.getBoolean("calculated");
                    dispute.date = jsonDisputeObject.getString("date");
                    try {
                        dispute.photo = jsonDisputeObject.getString("photo");
                    } catch (Exception ex) {

                    }
                    dispute.subcategory = jsonDisputeObject.getString("subcategory");
                    dispute.subject = jsonDisputeObject.getString("subject");
                    dispute.time = jsonDisputeObject.getString("time");
                    dispute.viewCount = jsonDisputeObject.getInt("viewCount");

                    JSONObject jsonChoiceObject = jsonDisputeObject.getJSONObject("choices");
                    JSONObject jsonRivor1Object = jsonChoiceObject.getJSONObject("rivor1");
                    JSONObject jsonRivor2Object = jsonChoiceObject.getJSONObject("rivor2");

                    Choice rivor1 = new Choice();
                    rivor1.rate = jsonRivor1Object.getInt("rate");
                    rivor1.choice = jsonRivor1Object.getString("choice");
//                    rivor1.spor_id = jsonRivor1Object.getString("spor_id");
//                    rivor1.id = jsonRivor1Object.getString("_id");

                    Choice rivor2 = new Choice();
                    rivor2.rate = jsonRivor2Object.getInt("rate");
                    rivor2.choice = jsonRivor2Object.getString("choice");
//                    rivor2.spor_id = jsonRivor2Object.getString("spor_id");
//                    rivor2.id = jsonRivor2Object.getString("_id");

                    dispute.choices.put("rivor1", rivor1);
                    dispute.choices.put("rivor2", rivor2);

                    JSONArray jsonArrayLikes = jsonDisputeObject.getJSONArray("likes");
                    for (int j = 0; j < jsonArrayLikes.length(); j++) {
                        JSONObject jsonObject = jsonArrayLikes.getJSONObject(j);
                        dispute.likes.put(jsonObject.getString("user_id"), true);
                    }
                    dispute.likeCount = dispute.likes.values().size();

                    JSONArray jsonArrayParticipants = jsonDisputeObject.getJSONArray("participants");
                    for (int j = 0; j < jsonArrayParticipants.length(); j++) {
                        JSONObject jsonParticipantObject = jsonArrayParticipants.getJSONObject(j);
                        Participant participant = new Participant();
                        participant.choice = jsonParticipantObject.getString("choice");
                        participant.money = jsonParticipantObject.getInt("money");
                        participant.spor_id = jsonParticipantObject.getString("spor_id");
                        participant.user_id = jsonParticipantObject.getString("user_id");
                        participant.winnings = jsonParticipantObject.getDouble("winnings");

                        dispute.participants.put(participant.user_id, participant);
                    }
                    dispute.participantCount = dispute.participants.values().size();
                    disputes.add(dispute);
                }
                dismissWithAnimationLoader();
            } else {
                resp = respjson.getString("msg");
                errorDialog(resp);
            }
        } catch (InterruptedException | ExecutionException | JSONException | StringIndexOutOfBoundsException e) {
            errorDialog(e.getMessage());
        }
        return disputes;
    }

    public void changePass(final String newPass, final String oldPass) {
        showLoader();

        final Requests requests = new Requests();
        final String dopurl = "changepassword";

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                JSONObject reqjson = new JSONObject();
                String params;
                try {
                    reqjson.put("user_id", User.id);
                    reqjson.put("new_password", newPass);
                    reqjson.put("old_password", oldPass);

                    params = reqjson.toString();

                    AsyncTask asyncTask = requests.execute(url + dopurl, params);
                    String resp = "";
                    resp = asyncTask.get().toString();

                    String ss = resp.substring(resp.indexOf("{"), resp.lastIndexOf("}") + 1);
                    JSONObject respjson = new JSONObject(ss);
                    String status = respjson.getString("status");
                    if (status.equals("ok")) {
                        succesDialog("Пароль успешно изменён");
                    } else {
                        resp = respjson.getString("msg");
                        errorDialog(resp);
                    }
                } catch (InterruptedException | ExecutionException | JSONException | StringIndexOutOfBoundsException e) {
                    errorDialog(e.getMessage());
                }
            }
        });
        thread.setDaemon(false);
        thread.start();
    }

    public ArrayList<String> getSortedByCategoryDisputes(String category) throws UnsupportedEncodingException {
        showLoader();

        final Requests requests = new Requests();
        final String dopurl = "disputecat";
        ArrayList<String> subCategoryList = new ArrayList<>();

        String params;
        try {
            category = URLEncoder.encode(category, "UTF-8");
            params = "category=" + category;

            AsyncTask asyncTask = requests.execute(url + dopurl + "?" + params, null);
            String resp = "";
            resp = asyncTask.get().toString();

            String ss = resp.substring(resp.indexOf("{"), resp.lastIndexOf("}") + 1);
            JSONObject respjson = new JSONObject(ss);
            String status = respjson.getString("status");
            if (status.equals("ok")) {
                JSONArray jsonArray = respjson.getJSONArray("subcategory");
                for (int i = 0; i < jsonArray.length(); i++) {
                    subCategoryList.add(jsonArray.getString(i));
                }
                dismissWithAnimationLoader();
            } else {
                resp = respjson.getString("msg");
                errorDialog(resp);
            }
        } catch (InterruptedException | ExecutionException | JSONException | StringIndexOutOfBoundsException e) {
            errorDialog(e.getMessage());
        }
        return subCategoryList;


        /*reference.orderByKey().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int i = 0;
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    boolean hasSubCat = ds.hasChild("subcategory");
                    boolean hasCat = ds.hasChild("category");
                    Dispute d = ds.getValue(Dispute.class);
                    if (hasSubCat && hasCat) {
                        boolean alreadyHave = false;
                        for (int j = 0; j < mData.size(); j++) {
                            if (mData.get(j).subcategory.equals(d.subcategory))
                                alreadyHave = true;
                        }
                        if (!alreadyHave && category.equals(d.category)) {
                            mData.put(i, d);
                            i++;
                        }
                    }
                }
                adapter = new CategoryDetailAdapter(rootView.getContext(), category, mData);
                adapter.setClickListener(instance);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });*/
    }

    public void like(final String user_id, final String dispute_id) {
        final Requests requests = new Requests();
        final String dopurl = "like";

        final JSONObject reqjson = new JSONObject();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    reqjson.put("user_id", user_id);
                    reqjson.put("dispute_id", dispute_id);

                    String params = reqjson.toString();

                    AsyncTask asyncTask = requests.execute(url + dopurl, params);
                    String resp = "";
                    resp = asyncTask.get().toString();

                    String ss = resp.substring(resp.indexOf("{"), resp.lastIndexOf("}") + 1);
                    JSONObject respjson = new JSONObject(ss);
                    String status = respjson.getString("status");
                    if (status.equals("ok")) {
                        //Ничего не делаю
                        dismissWithAnimationLoader();
                    } else {
                        resp = respjson.getString("msg");
                        errorDialog(resp);
                    }
                } catch (InterruptedException | ExecutionException | JSONException | StringIndexOutOfBoundsException e) {
                    errorDialog(e.getMessage());
                }
            }
        });
        thread.setDaemon(false);
        thread.start();
    }
}
