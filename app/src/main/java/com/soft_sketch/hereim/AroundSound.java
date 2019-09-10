package com.soft_sketch.hereim;


import android.app.ProgressDialog;
import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.soft_sketch.hereim.POJO.ChildInfo;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class AroundSound extends Fragment {

    private Button startRecord,playRecord;

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference rootRef = database.getReference();

    private String parentID,childID;
    private ProgressDialog progressDialog;


    public AroundSound() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view =  inflater.inflate(R.layout.fragment_around_sound, container, false);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Sound Around Child");


        startRecord = view.findViewById(R.id.recordStartBtn_id);
        playRecord = view.findViewById(R.id.playRecordBtn_id);

        parentID = getArguments().getString("parentIDpasser");
        childID = getArguments().getString("childIDpasser");

        progressDialog = new ProgressDialog(getContext());

        startRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final DatabaseReference childSOSRef = rootRef.child(parentID).child("child").child(childID);
                final Map<String, Object> recordUpdate = new HashMap<>();
                recordUpdate.put("soundURI","");
                recordUpdate.put("recodingCode", true);

                childSOSRef.updateChildren(recordUpdate).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        showProgressDialogue();
                        final Thread thread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Thread.sleep(5000);
                                    ResetRecord();
                                    progressDialog.dismiss();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                        thread.start();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

            }

        });

        playRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference childSOSRef = rootRef.child(parentID).child("child").child(childID);
                childSOSRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()){
                            ChildInfo childInfo = dataSnapshot.getValue(ChildInfo.class);
                            if (childInfo.getSoundURI()!=null){
                                MediaPlayer mediaPlayer = new MediaPlayer();
                                try {
                                    mediaPlayer.setDataSource(childInfo.getSoundURI());
                                    mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                        @Override
                                        public void onPrepared(MediaPlayer mediaPlayer) {
                                            mediaPlayer.start();
                                        }
                                    });
                                    mediaPlayer.prepare();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(getContext(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }

        });

        return view;
    }

    private void showProgressDialogue() {
        progressDialog.setIcon(R.drawable.record_ic);
        progressDialog.setTitle("Recording is on going!");
        progressDialog.setMessage("Sound around your child is recording.Please wait until it finish.");
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    public void ResetRecord(){
        final DatabaseReference childSOSRef = rootRef.child(parentID).child("child").child(childID);
        final Map<String, Object> sosupdate = new HashMap<>();
        sosupdate.put("recodingCode", false);


        childSOSRef.updateChildren(sosupdate).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
