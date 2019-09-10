package com.soft_sketch.hereim;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.os.Vibrator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.soft_sketch.hereim.POJO.ParentInfo;

import java.util.List;
import java.util.Objects;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChildEmergency_Callig extends Fragment {
    private Button parentCallBtn, policeCallBtn, helplineCallBtn;

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference rootRef = database.getReference();

    private SharedPreferences preferences;

    private  String id = "";

    private String token ="";


    public ChildEmergency_Callig() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_child_emergency__callig, container, false);

        id = getArguments().getString("idForCall");

        Toast.makeText(getContext(), id, Toast.LENGTH_SHORT).show();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            ((ChildActivity) Objects.requireNonNull(getActivity())).setTitle("Emergency Calling");
        }

        parentCallBtn = view.findViewById(R.id.callToParent_id);
        policeCallBtn = view.findViewById(R.id.callToPolice_id);
        helplineCallBtn = view.findViewById(R.id.callToHelpLine_id);

        preferences = getContext().getSharedPreferences(getString(R.string.app_name),Context.MODE_PRIVATE);

        parentCallBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                rootRef.child(preferences.getString("parentID","error")).child("parent").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                       String phone = dataSnapshot.getValue(ParentInfo.class).getParentNumber();
                        Toast.makeText(getContext(), "Calling", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phone));
                        startActivity(intent);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(getContext(), "Network creates problem in calling", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });

        policeCallBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               ShowDialogue();
            }
        });

        helplineCallBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + 888));
                startActivity(intent);
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
                        Toast.makeText(getContext(), "Calling", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phone));
                        startActivity(intent);
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
