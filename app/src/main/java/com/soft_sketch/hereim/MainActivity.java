package com.soft_sketch.hereim;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity implements WellComeScreen.SplashScreenIntf, User_Selection_fragment.UserSelectionIntf, ConnectivityReceiver.ConnectivityReceiverListener {

    private FragmentManager manager;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    private FirebaseAuthOperation authOperation;

    private AlertDialog alertbox;

    private String user = null;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        authOperation = new FirebaseAuthOperation(this);

        checkConnection();
    }

    private void checkConnection() {
        boolean isConnected = ConnectivityReceiver.isConnected();
        showSnack(isConnected);
    }

    private void showSnack(boolean isConnected) {

        if (isConnected) {
            if(getIntent().getIntExtra("fragmentNumber",0)==1){
                manager = getSupportFragmentManager();
                FragmentTransaction ft = manager.beginTransaction();
                User_Selection_fragment user_selection_fragment = new User_Selection_fragment();
                ft.add(R.id.FragmentHolder_1_id, user_selection_fragment);
                ft.commit();
            }else  {
                manager = getSupportFragmentManager();
                FragmentTransaction ft = manager.beginTransaction();
                WellComeScreen wellComeScreen = new WellComeScreen();
                ft.add(R.id.FragmentHolder_1_id, wellComeScreen);
                ft.commit();
            }


        } else {
            ShowConnectionDialogue();
        }
    }

    private void ShowConnectionDialogue() {

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("No Internet ");
        builder.setCancelable(false);
        builder.setMessage("Please Connect Wifi or Mobile Data");
        builder.setIcon(R.drawable.connectionless_icon);
        builder.setPositiveButton("Exit", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        builder.setNegativeButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.setCancelable(false);
        builder.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MyApplication.getInstance().setConnectivityListener(this);
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        showSnack(isConnected);
    }

    @Override
    public void OnTimeOut() {
        User_Selection_fragment userSelectionFragment = new User_Selection_fragment();
        FragmentTransaction ft = manager.beginTransaction();
        ft.replace(R.id.FragmentHolder_1_id, userSelectionFragment);
        ft.commit();
    }

    @Override
    public void ParentLogedIn() {
        Intent intent = new Intent(this,ParentActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void ChildLogedIn() {
        Intent intent = new Intent(this,ChildActivity.class);
        startActivity(intent);
        finish();
    }


    @Override
    public void OnCompleteUserSelection(int position) {
        FragmentTransaction ft = manager.beginTransaction();
        if (position == 1) {
            Parent_Login_fragment parentLogINFragment = new Parent_Login_fragment();
            ft.replace(R.id.FragmentHolder_1_id, parentLogINFragment);
            ft.addToBackStack(null);
            ft.commit();
        } else if (position == 2) {
            Child_Login_fragment childLogINFragment = new Child_Login_fragment();
            ft.replace(R.id.FragmentHolder_1_id, childLogINFragment);
            ft.addToBackStack(null);
            ft.commit();
        }
    }


}
