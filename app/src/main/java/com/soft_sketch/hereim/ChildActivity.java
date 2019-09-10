package com.soft_sketch.hereim;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaRecorder;
import android.net.Network;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Vibrator;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.internal.Constants;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.soft_sketch.hereim.POJO.ChildInfo;
import com.soft_sketch.hereim.POJO.ParentInfo;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class ChildActivity extends AppCompatActivity {

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference rootRef = database.getReference();

    private FragmentManager manager;
    private FirebaseAuthOperation authOperation;
    private FirebaseDataBase db;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private Bundle bundle;
    private StorageReference storageReference;

    public String childID;
    public String phone;
    private String temp;

    private MediaRecorder recorder;
    private String fileName = "";
    private Uri downloadUri;
    private String time;
    private StorageReference mStorageRef;

    @SuppressLint("CommitPrefEdits")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child);

        String temp1 = getIntent().getStringExtra("data");

        mStorageRef = FirebaseStorage.getInstance().getReference();
        time = String.valueOf(System.currentTimeMillis());

        fileName = Environment.getExternalStorageDirectory().getAbsolutePath();
        fileName +="/record_"+time+".3gp";

        authOperation = new FirebaseAuthOperation(this);
        db = new FirebaseDataBase(this);
        bundle = new Bundle();
        storageReference = FirebaseStorage.getInstance().getReference();

        preferences = getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE);
        editor = preferences.edit();

        if (temp1 != null && temp1.equals("perform")) {

            boolean vibrationCode = false;
            boolean recodingCode = false;
            double currentLocLate = 0.0;
            double currentLocLong = 0.0;
            String soundURI = "";

            String childName = preferences.getString("ChildName", "Error");
            String childPhone = preferences.getString("ChildPhone", "Error");
            temp = preferences.getString("ParentID", "Error");
            childID = authOperation.GetToken();



            DatabaseReference parentID = FirebaseDatabase.getInstance().getReference(temp);

            ChildInfo childInfo = new ChildInfo(childID, childName, childPhone,soundURI,vibrationCode, recodingCode, currentLocLate, currentLocLong);

            DatabaseReference childRef = parentID.child("child").child(childID);
            childRef.setValue(childInfo).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(ChildActivity.this, "Data added", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(ChildActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
            });

            editor.putString("parentID", temp);
            editor.putString("childID", childID);
            editor.apply();
        }

        manager = getSupportFragmentManager();
        FragmentTransaction ft = manager.beginTransaction();
        Child_Current_Loc child_current_loc = new Child_Current_Loc();
        bundle.putString("pidforloc", preferences.getString("parentID", "Error"));
        bundle.putString("cidforloc", preferences.getString("childID", "Error"));
        child_current_loc.setArguments(bundle);
        ft.add(R.id.FragmentHolder_3_id, child_current_loc);
        ft.commit();


        BottomNavigationView bottomNav = findViewById(R.id.child_bottom_navigation_id);
        bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                Fragment selectedFragment = null;

                try {

                    switch (menuItem.getItemId()) {

                        case R.id.nav_loc:
                            selectedFragment = new Child_Current_Loc();
                            bundle.putString("idForCall", preferences.getString("parentID", "Error"));
                            selectedFragment.setArguments(bundle);
                            menuItem.getIcon().setBounds(40, 40, 40, 40);
                            break;
                        case R.id.nav_calling:
                            selectedFragment = new ChildEmergency_Callig();
                            bundle.putString("idForCall", preferences.getString("parentID", "Error"));
                            selectedFragment.setArguments(bundle);
                            menuItem.getIcon().setBounds(40, 40, 40, 40);
                            break;
                        case R.id.nav_message:
                            selectedFragment = new Child_Message();
                            bundle.putString("idForMgs", preferences.getString("parentID", "Error"));
                            selectedFragment.setArguments(bundle);
                            menuItem.getIcon().setBounds(40, 40, 40, 40);
                            break;
                        case R.id.nav_sos:
                            selectedFragment = new Child_SOS();
                            bundle.putString("parentid", preferences.getString("parentID", "Error"));
                            bundle.putString("childid", preferences.getString("childID", "Error"));
                            selectedFragment.setArguments(bundle);
                            menuItem.getIcon().setBounds(40, 40, 40, 40);
                            break;

                    }
                } catch (Exception ex) {
                    Toast.makeText(ChildActivity.this, ex.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }

                FragmentTransaction ft = manager.beginTransaction();
                ft.replace(R.id.FragmentHolder_3_id, selectedFragment);
                ft.addToBackStack(null);
                ft.commit();

                return true;
            }
        });

        rootRef.child(preferences.getString("parentID", "Error")).child("child")
                .child(preferences.getString("childID", "Error"))
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        ChildInfo childInfo = dataSnapshot.getValue(ChildInfo.class);
                        if (childInfo.getRecodingCode()== true){
                            StartRecording();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });



    }


    private void StartRecording() {

        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setOutputFile(fileName);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            recorder.prepare();
        } catch (IOException e) {
            Log.e("Recording", "prepare() failed");
        }

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(6000);
                    stopRecording();

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
        recorder.start();
    }

    private void stopRecording() {
        recorder.stop();
        recorder.release();
        recorder = null;
        UploadToStore();

    }

    private void UploadToStore() {
        final StorageReference filepath = mStorageRef.child("Audio").child(time);
        Uri uri = Uri.fromFile(new File(fileName));
        filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        downloadUri = uri;
                       UploadToDB(downloadUri.toString());
                    }

                });
            }

        });
    }

    private void UploadToDB(String uri) {

        final DatabaseReference childRecodeRef = rootRef.child(preferences.getString("parentID", "Error"))
                .child("child").child(preferences.getString("childID", "Error"));
        final Map<String, Object> sosupdate = new HashMap<>();
        sosupdate.put("soundURI", uri);

        childRecodeRef.updateChildren(sosupdate).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(ChildActivity.this, "Upload Successful", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ChildActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.core_acitivity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.logOut_id) {
            authOperation.LogOut();
            Intent intent  =   new Intent(this,MainActivity.class);
            intent.putExtra("fragmentNumber",1);
            startActivity(intent);
            finish();
        }
        return true;
    }
}
