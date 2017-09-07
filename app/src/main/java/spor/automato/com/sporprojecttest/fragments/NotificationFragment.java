package spor.automato.com.sporprojecttest.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

import spor.automato.com.sporprojecttest.Activity.MainActivity;
import spor.automato.com.sporprojecttest.Activity.SettingsActivity;
import spor.automato.com.sporprojecttest.Adapter.NotificationAdapter;
import spor.automato.com.sporprojecttest.R;
import spor.automato.com.sporprojecttest.models.Dispute;
import spor.automato.com.sporprojecttest.models.Notification;
import spor.automato.com.sporprojecttest.models.User;


public class NotificationFragment extends Fragment {

    View rootview;

    FirebaseDatabase myDatabase;
    FirebaseAuth mAuth;
    User client;

    private RecyclerView notifList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootview = inflater.inflate(R.layout.fragment_notification, container, false);

        notifList = (RecyclerView) rootview.findViewById(R.id.notif_list);
        notifList.setHasFixedSize(false);

        LinearLayoutManager llm = new LinearLayoutManager(this.getActivity());
        notifList.setLayoutManager(llm);

        myDatabase = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();

        TextView title = (TextView) getActivity().findViewById(R.id.title);
        title.setText("Уведомления");

        loadNotifications();

        MainActivity.setCurentFragment(this);
        return rootview;
    }

    private void loadNotifications() {
        MainActivity.showLoader();

        myDatabase.getReference("users").child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                client = dataSnapshot.getValue(User.class);
                myDatabase.getReference("spor").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        ArrayList<Dispute> disputes = new ArrayList<>();
                        for (DataSnapshot child : dataSnapshot.getChildren()) {
                            Dispute dispute = child.getValue(Dispute.class);
                            if (!dispute.result.equals("") && dispute.participants != null) {
                                if (dispute.participants.containsKey(client.id)) {
                                    HashMap<String, Notification> notificationHashMap = client.history != null
                                            ? client.history : new HashMap<String, Notification>();
                                    Notification notification = new Notification();
                                    notification.checked = false;
                                    notification.money = dispute.participants.get(client.id).money;
                                    notification.sporID = dispute.id;
                                    notification.winnings = (int) dispute.participants.get(client.id).winnings;

                                    notificationHashMap.put(dispute.id, notification);

                                    DatabaseReference sporLikeCountInDB = myDatabase.getReference("users/" + client.id + "/history");
                                    sporLikeCountInDB.setValue(notificationHashMap);

                                    client.history = notificationHashMap;
                                    disputes.add(dispute);
                                }
                            }
                        }
                        if (client.history != null) {
                            NotificationAdapter adapter = new NotificationAdapter(rootview.getContext(), client.history, disputes, myDatabase, client.id);
                            notifList.setAdapter(adapter);
                        }
                        MainActivity.dismissWithAnimationLoader();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
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
