package com.automato.aigerim.spor.Fragments;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;
import android.widget.TextView;

import com.automato.aigerim.spor.Activity.MainActivity;
import com.automato.aigerim.spor.Adapter.NotificationAdapter;
import com.automato.aigerim.spor.Models.Dispute;
import com.automato.aigerim.spor.Models.Notification;
import com.automato.aigerim.spor.Models.User;
import com.automato.aigerim.spor.Other.Api;
import com.automato.aigerim.spor.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;


public class NotificationFragment extends Fragment {

    View rootview;
    Api api;

    private RecyclerView notifList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootview = inflater.inflate(R.layout.fragment_notification, container, false);

        api = new Api(getActivity());

        notifList = (RecyclerView) rootview.findViewById(R.id.notif_list);
        notifList.setHasFixedSize(false);

        LinearLayoutManager llm = new LinearLayoutManager(this.getActivity());
        notifList.setLayoutManager(llm);

        TextView title = (TextView) getActivity().findViewById(R.id.title);
        title.setText("Уведомления");

        loadNotifications();

        MainActivity.setCurentFragment(this);
        return rootview;
    }

    private void loadNotifications() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                ArrayList<Dispute> disputes = api.getAllDisputes();

                for (Dispute dispute : disputes) {
                    if (!dispute.result.equals("") && dispute.participants != null && dispute.calculated) {
                        if (dispute.participants.containsKey(User.id)) {
                            HashMap<String, Notification> notificationHashMap = User.history != null
                                    ? User.history : new HashMap<String, Notification>();
                            Notification notification = new Notification();
                            notification.checked = false;
                            notification.money = dispute.participants.get(User.id).money;
                            notification.sporID = dispute.id;
                            notification.winnings = (int) dispute.participants.get(User.id).winnings;

                            if (!notificationHashMap.containsKey(dispute.id)) {
                                notificationHashMap.put(dispute.id, notification);
                                User.money += notification.winnings;

                                LinkedHashMap<String, Notification> newMap = new LinkedHashMap<>();
                                ArrayList<String> arrayList = new ArrayList<>(notificationHashMap.keySet());
                                for (int i = arrayList.size() - 1; i >= 0; i--) {
                                    String key = arrayList.get(i);
                                    Notification value = notificationHashMap.get(key);
                                    newMap.put(key, value);
                                }
                                notificationHashMap = newMap;
                            }
                            User.history = notificationHashMap;
                            disputes.add(dispute);
                        }
                    }
                }
                if (User.history != null) {
                    rootview.findViewById(R.id.no_notifications).setVisibility(View.GONE);
                    api.updateUser();

                    NotificationAdapter adapter = new NotificationAdapter(rootview.getContext(), User.history, disputes);
                    notifList.setAdapter(adapter);
                }else {
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            rootview.findViewById(R.id.no_notifications).setVisibility(View.VISIBLE);
                        }
                    });
                }
            }
        });
        thread.setDaemon(false);
        thread.start();
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
