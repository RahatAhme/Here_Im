package com.soft_sketch.hereim;


import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


/**
 * A simple {@link Fragment} subclass.
 */
public class Child_SOS extends Fragment {

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference rootRef = database.getReference();

    private Button sosBtn;

    private String parentID;
    private String childID;

    private String CHANNEL_ID ="c1";

    public Child_SOS() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_child__sos, container, false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            ((ChildActivity) Objects.requireNonNull(getContext())).setTitle("SOS");
        }



        parentID = getArguments().getString("parentid");
        childID = getArguments().getString("childid");

        sosBtn = view.findViewById(R.id.sosBtn_id);
        sosBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final DatabaseReference childSOSRef = rootRef.child(parentID).child("child").child(childID);
                final Map<String, Object> sosupdate = new HashMap<>();
                sosupdate.put("vibrationCode", true);
                ShowNoti();

                childSOSRef.updateChildren(sosupdate).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        final Thread thread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Thread.sleep(3000);
                                    ResetVibrator();
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


        return view;
    }

    private void ShowNoti() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getContext(), CHANNEL_ID)
                .setSmallIcon(R.drawable.location)
                .setContentTitle("GPS Disable")
                .setContentText("Please turn on GPS/Location service")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
    }

    public void ResetVibrator(){
        final DatabaseReference childSOSRef = rootRef.child(parentID).child("child").child(childID);
        final Map<String, Object> sosupdate = new HashMap<>();
        sosupdate.put("vibrationCode", false);


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
