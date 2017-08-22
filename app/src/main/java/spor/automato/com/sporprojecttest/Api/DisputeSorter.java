package spor.automato.com.sporprojecttest.Api;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

import spor.automato.com.sporprojecttest.models.Dispute;
import spor.automato.com.sporprojecttest.models.Participant;

import static android.content.ContentValues.TAG;

/**
 * Created by dsklff on 8/18/17.
 */

public class DisputeSorter{

    FirebaseAuth mAuth;

    private ArrayList<Dispute> allDisputes;

    public DisputeSorter(){

    }

    public DisputeSorter(ArrayList<Dispute> allDisputes){
        this.allDisputes = allDisputes;
        mAuth = FirebaseAuth.getInstance();
    }

    public ArrayList<Dispute> getUserDisputes(){
        ArrayList<Dispute>userDisputes = new ArrayList<Dispute>();
        String userID = mAuth.getCurrentUser().getUid().toString();
        for (Dispute dispute: allDisputes){
            ArrayList<Participant>partcicpants = dispute.getParticp();
            for (Participant participant : partcicpants){
                if (participant.user_id.equalsIgnoreCase(userID)){
                    Log.i(TAG, "USER DISPUTE " + participant.money);
                    userDisputes.add(dispute);
                }
                Log.i(TAG, "USER DISPUTE " + participant.money);
            }
        }
        return userDisputes;
    }
}
