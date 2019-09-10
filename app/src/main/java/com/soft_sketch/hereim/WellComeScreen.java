package com.soft_sketch.hereim;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class WellComeScreen extends Fragment {

    private SplashScreenIntf screenIntf;

    private FirebaseAuthOperation authOperation;
    private SharedPreferences preferences;

    public WellComeScreen() {
        // Required empty public constructor
    }

    public void onAttach(Context context) {
        super.onAttach(context);
        screenIntf = (SplashScreenIntf) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_well_come_screen, container, false);

        authOperation = new FirebaseAuthOperation(getContext());
        preferences = getContext().getSharedPreferences(getContext().getString(R.string.app_name),Context.MODE_PRIVATE);
        final String user = preferences.getString(getContext().getString(R.string.PreferenceUserKey),"Error");

        final boolean logStatus = authOperation.LogStatus();


      Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                    if (logStatus && user.equals("Parent")){
                        screenIntf.ParentLogedIn();
                    }else if (logStatus && user.equals("Child")){
                        screenIntf.ChildLogedIn();
                    }else {
                        screenIntf.OnTimeOut();
                    }

                } catch (InterruptedException e) {
                    Log.e("Wellcome",e.getLocalizedMessage());
                }
            }
        });
        thread.start();
        return  view;
    }

    interface SplashScreenIntf{
        void OnTimeOut();
        void ParentLogedIn();
        void ChildLogedIn();
    }

}
