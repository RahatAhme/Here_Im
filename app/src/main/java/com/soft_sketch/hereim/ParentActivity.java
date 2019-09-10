package com.soft_sketch.hereim;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import android.os.Vibrator;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;

import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.soft_sketch.hereim.POJO.ChildInfo;
import com.soft_sketch.hereim.POJO.ParentInfo;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.Menu;
import android.view.Window;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class ParentActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private FragmentManager manager;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private FirebaseAuthOperation authOperation;
    private FirebaseDataBase dataBase;
    private SOS_Performer performer;

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference rootRef = database.getReference();

    private Bundle bundle;

    public String parentID = "";
    public String childID = "";
    private List<String> childIDlist;
    private List<ChildInfo> childList;

    private NotificationManagerCompat notificationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_core_acitivity);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        TextView navUsername = (TextView) headerView.findViewById(R.id.ParentUID);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        authOperation = new FirebaseAuthOperation(this);
        bundle = new Bundle();
        childIDlist = new ArrayList<>();
        childList = new ArrayList<>();
        dataBase = new FirebaseDataBase(this);
        performer = new SOS_Performer(this);

        notificationManager = NotificationManagerCompat.from(this);
        preferences = getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE);
        editor = preferences.edit();

        String temp1 = getIntent().getStringExtra("data");

        if (temp1 != null && temp1.equals("perform")) {
            String parentName = preferences.getString("ParentName", "Error");
            String parentPhone = preferences.getString("ParentPhone", "Error");
            parentID = authOperation.GetToken() + "";

            editor.putString("parentID", parentID);
            editor.apply();


            ParentInfo parentInfo = new ParentInfo(parentID, parentName, parentPhone);
            DatabaseReference parentRef = rootRef.child(parentID).child("parent");
            parentRef.setValue(parentInfo).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(ParentActivity.this, "SAVED", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        Toast.makeText(ParentActivity.this, "FAILED", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }

        navUsername.setText(parentID);

        rootRef.child(preferences.getString("parentID", "Error"))
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        childIDlist.clear();
                        if (dataSnapshot.hasChild("child")) {

                            rootRef.child(preferences.getString("parentID", "error")).child("child")
                                    .addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.exists()) {
                                                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                                    String id = ds.getKey();
                                                    childIDlist.add(id);
                                                }
                                                childID = childIDlist.get(0);
                                                editor.putString("childId", childID);
                                                editor.apply();
                                                rootRef.child(preferences.getString("parentID", "error"))
                                                        .child("child").child(childID)
                                                        .addValueEventListener(new ValueEventListener() {
                                                            final MediaPlayer catSoundMediaPlayer = MediaPlayer.create(ParentActivity.this, R.raw.beep);

                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                ChildInfo childInfo = dataSnapshot.getValue(ChildInfo.class);
                                                                if (childInfo.getVibrationCode() == true) {
                                                                   /* Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                                                                    v.vibrate(4000);*/
                                                                    catSoundMediaPlayer.start();
                                                                    SendNotification();
                                                                }
                                                            }

                                                            @Override
                                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                                            }
                                                        });

                                                manager = getSupportFragmentManager();
                                                FragmentTransaction ft = manager.beginTransaction();
                                                bundle.putString("parentIDpasser", preferences.getString("parentID", "Error"));
                                                bundle.putString("childIDpasser", childID);
                                                ParentMap parentMap = new ParentMap();
                                                parentMap.setArguments(bundle);
                                                ft.add(R.id.FragmentHolder_2_id, parentMap);
                                                ft.commit();
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {
                                            Toast.makeText(ParentActivity.this, "No child available", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        } else {
                            ShowNoChild();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        ShowNoChild();
                        Toast.makeText(ParentActivity.this, "Network problem!", Toast.LENGTH_SHORT).show();
                    }
                });

        //first fragment setup

        //bottomNav setup
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation_id);
        bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                manager = getSupportFragmentManager();
                Fragment selectedFragment = null;
                try {
                    switch (menuItem.getItemId()) {
                        case R.id.nav_loc:
                            selectedFragment = new ParentMap();
                            bundle.putString("parentIDpasser", preferences.getString("parentID", "Error"));
                            bundle.putString("childIDpasser", childID);
                            selectedFragment.setArguments(bundle);
                            menuItem.getIcon().setBounds(40, 40, 40, 40);
                            break;

                        case R.id.nav_pcall:
                     /*//   selectedFragment = new ParentTimeMonitoring();
                        Toast.makeText(ParentActivity.this, "This Part will be completed after apeche licese", Toast.LENGTH_SHORT).show();
                        menuItem.getIcon().setBounds(40, 40, 40, 40);
                        break;*/
                        case R.id.nav_sound:
                            bundle.putString("parentIDpasser", preferences.getString("parentID", "Error"));
                            bundle.putString("childIDpasser", childID);
                            selectedFragment = new AroundSound();
                            selectedFragment.setArguments(bundle);
                            menuItem.getIcon().setBounds(40, 40, 40, 40);
                            break;
                    }
                } catch (Exception ex) {

                    Toast.makeText(ParentActivity.this, ex.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
                FragmentTransaction ft = manager.beginTransaction();
                ft.replace(R.id.FragmentHolder_2_id, selectedFragment);
                ft.addToBackStack(null);
                ft.commit();

                return true;
            }

        });


    }

    private void SendNotification() {
        Intent activityIntent = new Intent(this, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this,
                0, activityIntent, 0);

        Intent broadcastIntent = new Intent(this, NotificationReceiver.class);
        broadcastIntent.putExtra("toastMessage", "Here is the Toast Message");
        PendingIntent actionIntent = PendingIntent.getBroadcast(this,
                0, broadcastIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new NotificationCompat.Builder(this, BaseApp.CHANNEL_1_ID)
                .setSmallIcon(R.drawable.logo)
                .setContentTitle("Channel_1_title")
                .setContentText("Channel_1_Content")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setColor(Color.RED)
                .setAutoCancel(true)
                .setOnlyAlertOnce(true)
                .addAction(R.drawable.logo, "Toast", actionIntent)
                .build();

        notificationManager.notify(1, notification);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.core_acitivity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.logOut_id) {
            authOperation.LogOut();
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("fragmentNumber", 1);
            startActivity(intent);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_switchChild_id) {
            Intent intent = new Intent(this,AppUseActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_tools) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @SuppressLint("SetTextI18n")
    private String showRadioButtonDialog() {

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.child_switch_dialogue);
        dialog.setCancelable(false);
        final RadioGroup rg = (RadioGroup) dialog.findViewById(R.id.childlist_radio_id);

        for (int i = 0; i < childIDlist.size(); i++) {
            int p = i + 1;
            RadioButton rb = new RadioButton(this); // dynamically creating RadioButton and adding to RadioGroup.
            rb.setText("Child: " + p);
            rg.addView(rb);
        }
        dialog.show();

        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                childID = childIDlist.get(0);

                dialog.dismiss();
            }
        });
        return childID;
    }

    public void ShowNoChild() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.no_child_dialogue);

        String title = "You have not added any child yet.Please connect your child with with user id "
                + preferences.getString("parentID", "Error");

        Button dialogButton = (Button) dialog.findViewById(R.id.btn_dialog);
        TextView diologTV = dialog.findViewById(R.id.text_dialog);
        diologTV.setText(title);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                finish();
            }
        });
        dialog.show();
    }


}
