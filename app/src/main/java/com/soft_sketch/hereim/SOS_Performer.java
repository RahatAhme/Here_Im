package com.soft_sketch.hereim;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Vibrator;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.soft_sketch.hereim.POJO.ChildInfo;

import java.util.HashMap;
import java.util.Map;

public class SOS_Performer {
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference rootRef = database.getReference();

    private Context context;

    public SOS_Performer(Context context) {
        this.context = context;
    }

    public void UpdateVibrator(final String parentID,final String childID){
        rootRef.child(parentID).child("child").child(childID).addValueEventListener(new ValueEventListener() {
            final MediaPlayer catSoundMediaPlayer = MediaPlayer.create(context, R.raw.beep);
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               for (DataSnapshot ds : dataSnapshot.getChildren()){
                   ChildInfo childInfo = ds.getValue(ChildInfo.class);
                    boolean status = childInfo.getVibrationCode();
                    if (status==true){
                        Vibrator v = (Vibrator)context.getSystemService(Context.VIBRATOR_SERVICE);
                        v.vibrate(22500);
                        catSoundMediaPlayer.start();
                        Toast.makeText(context, "Vibrate", Toast.LENGTH_SHORT).show();
                    }
               }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void resetStatus(String parentID,String childID) {
        DatabaseReference childSOSRef = rootRef.child(parentID).child("child").child(childID);
        Map<String, Object> sosupdate = new HashMap<>();
        sosupdate.put("vibrationCode", false);

        childSOSRef.updateChildren(sosupdate).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(context, "Done", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
