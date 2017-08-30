package spor.automato.com.sporprojecttest.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;
import spor.automato.com.sporprojecttest.Activity.MainActivity;
import spor.automato.com.sporprojecttest.Adapter.NotificationAdapter;
import spor.automato.com.sporprojecttest.R;
import spor.automato.com.sporprojecttest.models.Dispute;
import spor.automato.com.sporprojecttest.models.User;


public class NotificationFragment extends Fragment {

    View rootview;

    FirebaseDatabase myDatabase;
    FirebaseAuth mAuth;
    User client;

    private RecyclerView notifList;
    private SweetAlertDialog mProgressDialog;

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

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                loadNotifications();
            }
        });
        thread.setDaemon(true);
        thread.start();

        MainActivity.setCurentFragment(this);
        return rootview;
    }

    private void loadNotifications() {
        myDatabase.getReference("users").child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                client = dataSnapshot.getValue(User.class);
                if (client.history != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mProgressDialog = new SweetAlertDialog(rootview.getContext(), SweetAlertDialog.PROGRESS_TYPE);
                            mProgressDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                            mProgressDialog.setTitleText("Загрузка");
                            mProgressDialog.setCancelable(false);
                            mProgressDialog.show();
                        }
                    });
                    myDatabase.getReference("spor").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            ArrayList<Dispute> disputes = new ArrayList<>();
                            for (DataSnapshot child : dataSnapshot.getChildren()) {
                                Dispute dispute = child.getValue(Dispute.class);
                                if (client.history.containsKey(dispute.id)){
                                    disputes.add(dispute);
                                }
                            }
                            NotificationAdapter adapter = new NotificationAdapter(rootview.getContext(), client.history, disputes);
                            notifList.setAdapter(adapter);
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mProgressDialog.dismiss();
                                }
                            });
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
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
