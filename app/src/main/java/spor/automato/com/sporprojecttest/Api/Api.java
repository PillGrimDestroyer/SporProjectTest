package spor.automato.com.sporprojecttest.Api;

import android.nfc.Tag;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import spor.automato.com.sporprojecttest.models.Dispute;
import spor.automato.com.sporprojecttest.models.Participant;

import static android.content.ContentValues.TAG;

public class Api {

    public static Api sharedInstance = new Api();
    public DatabaseReference databaseReference;
    ArrayList<Dispute> allDisputes;

    public Api(){

    }

    public ArrayList<Dispute> getAllDisputes(){
            allDisputes = new ArrayList<Dispute>();
            databaseReference = FirebaseDatabase.getInstance().getReference().child("spor");
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Dispute dispute = snapshot.getValue(Dispute.class);
                        for (DataSnapshot child : snapshot.getChildren()) {
                            if (child.getKey().equalsIgnoreCase("participants")) {
                                for (DataSnapshot part : child.getChildren()) {
                                    Participant participant = part.getValue(Participant.class);
                                    dispute.addParticipant(participant);
                                    allDisputes.add(dispute);
                                    Log.i(TAG, "count "+ allDisputes.size());
                                }
                            }
                        }
                    }
                    Log.i(TAG, "Size wwc"+ allDisputes.size());
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
       return allDisputes;
    }
}