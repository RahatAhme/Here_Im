package com.soft_sketch.hereim;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;


import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;

import java.security.SecureRandom;
import java.util.concurrent.TimeUnit;

public class FirebaseAuthOperation {

    private FirebaseAuth auth = FirebaseAuth.getInstance();

    private String varificationCode;
    private Context context;
    private String username, userphone;
    private String parentToken;

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    private boolean signIn = false;

    public FirebaseAuthOperation(Context context) {
        this.context = context;
    }

    public void GetOTP(String number) {
        userphone = number;
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                number,
                60,
                TimeUnit.SECONDS,
                TaskExecutors.MAIN_THREAD,
                mCallBack
        );
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks
            mCallBack = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            varificationCode = s;
        }

        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            String code = phoneAuthCredential.getSmsCode();
            if (code != null) {
                verifyCode(code);
            }
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Log.e("LoginException", e.getLocalizedMessage());
        }
    };

    private void verifyCode(String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(varificationCode, code);
        signInWithCredential(credential);

    }

    private void signInWithCredential(final PhoneAuthCredential credential) {

        auth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            preferences = context.getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE);
                            editor = preferences.edit();

                            String USER = context.getString(R.string.PreferenceUserKey);
                            String user = preferences.getString(USER, "Error");

                            if (user.equals("Parent")) {
                                editor.putString("ParentName",username);
                                editor.putString("ParentPhone",userphone);
                                editor.apply();
                                Intent intent = new Intent(context, ParentActivity.class);
                                intent.putExtra("data","perform");
                                context.startActivity(intent);
                                ((MainActivity)context).finish();
                            }else if (user.equals("Child")){
                                editor.putString("ChildName",username);
                                editor.putString("ChildPhone",userphone);
                                editor.putString("ParentID",parentToken);
                                editor.apply();
                                Intent intent = new Intent(context, ChildActivity.class);
                                intent.putExtra("data","perform");
                                context.startActivity(intent);
                                ((MainActivity)context).finish();
                            }
                        }
                    }

                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("Exception", e.getLocalizedMessage());
                Toast.makeText(context, "Please Check Your Number", Toast.LENGTH_SHORT).show();
            }
        });

    }

    public String GetToken() {

        String token = "";
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            String easy = RandomString.digits + auth.getUid();
            RandomString tickets = new RandomString(8, new SecureRandom(), easy);
            token = new StringBuilder(String.valueOf(tickets)).reverse().toString().substring(0, 7);
            Log.e("TOKEN", token);
        }
        return token;
    }

    public boolean LogStatus() {
        boolean logStatus = false;
        if (auth.getUid() != null) {
            logStatus = true;
        }
        return logStatus;
    }

    public void HoldParent(String parentName, String parentPhone) {
        GetOTP("+88" + parentPhone);
        username = parentName;
        userphone = parentPhone;
    }

    public void HoldChild(String childName, String childPhone, String parentID) {
        GetOTP("+88"+childPhone);
        username = childName;
        userphone = childPhone;
        parentToken = parentID;
    }

    public void LogOut(){
        auth.signOut();

        //fragment transation
    }
}
