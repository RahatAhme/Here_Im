package com.soft_sketch.hereim;


import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.soft_sketch.hereim.POJO.ParentInfo;

import java.io.File;
import java.io.IOException;


public class Child_Message extends Fragment {

    private Button parentMgsBtn, policeMgsBtn,demoRecord;
    private Context context;

    private ProgressDialog progressDialog;

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference rootRef = database.getReference();

    private SharedPreferences preferences;


    public Child_Message() {
        // Required empty public constructor
    }

    public Child_Message(Context context) {
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_child__message, container, false);
        ((ChildActivity) getActivity()).setTitle("Message");

        progressDialog = new ProgressDialog(getContext());

        parentMgsBtn = view.findViewById(R.id.mgsToPrent_id);
        policeMgsBtn = view.findViewById(R.id.mgsToPolice_id);

        preferences = getContext().getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE);

        final SmsManager smsManager = SmsManager.getDefault();

        double latitude = Double.parseDouble(preferences.getString("lat","error"));
        double longitude = Double.parseDouble(preferences.getString("long","error"));

        final String message = "Please help me at: http://maps.google.com/?q=" + latitude+ "," +longitude;

        parentMgsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    rootRef.child(preferences.getString("parentID", "error")).child("parent").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            progressDialog.setMessage("বার্তা প্রেরণ করা হচ্ছে");
                            progressDialog.show();
                            String phone = dataSnapshot.getValue(ParentInfo.class).getParentNumber();
                            smsManager.sendTextMessage(phone, null, message, null, null);
                            progressDialog.setMessage("বার্তা প্রেরণ সম্পন্ন হয়েছে");
                            progressDialog.dismiss();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Toast.makeText(getContext(), "Network problem in sms sending", Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (Exception ex) {
                    Toast.makeText(getContext(), ex.getLocalizedMessage(),
                            Toast.LENGTH_LONG).show();
                }
            }
        });
        policeMgsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShowDialogue();
            }
        });


        return view;
    }



    private void ShowDialogue() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setIcon(R.drawable.call);
        builder.setMessage("Dhanmondi Police Station:" +
                "\n Phone No : +8801769690224")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        String phone = "+8801769690224";
                        SmsManager smsManager = SmsManager.getDefault();
                        double latitude = Double.parseDouble(preferences.getString("lat","error"));
                        double longitude = Double.parseDouble(preferences.getString("long","error"));

                        final String message = "Please help me at: http://maps.google.com/?q=" + latitude+ "," +longitude;
                        smsManager.sendTextMessage(phone, null, message, null, null);
                        Toast.makeText(getContext(), "Your Message Sent",Toast.LENGTH_LONG).show();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
        // Create the AlertDialog object and return it
        builder.create();
        builder.show();
    }

}
