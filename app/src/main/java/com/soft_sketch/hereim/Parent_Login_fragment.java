package com.soft_sketch.hereim;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Parent_Login_fragment extends Fragment {
    private EditText parentNameET,parentPhoneET;
    private Button parentLoginBtn;

    private ProgressDialog progressDialog;

    private String parentName,parentPhone;

    private FirebaseAuthOperation firebaseAuthOperation;

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    public Parent_Login_fragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_parent__login_fragment, container, false);

        firebaseAuthOperation = new FirebaseAuthOperation(getContext());
        preferences = getContext().getSharedPreferences(getContext().getString(R.string.app_name), Context.MODE_PRIVATE);
        editor = preferences.edit();
        editor.putString(getContext().getString(R.string.PreferenceUserKey),"Parent");
        editor.apply();

        progressDialog = new ProgressDialog(getContext());

        parentNameET = view.findViewById(R.id.parent_name_ET_id);
        parentPhoneET = view.findViewById(R.id.parent_phone_ET_id);
        parentLoginBtn = view.findViewById(R.id.parent_log_btn_id);

        parentLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                parentName = parentNameET.getText().toString();
                parentPhone = parentPhoneET.getText().toString();
                ParentVarification(parentName,parentPhone);
            }
        });
        return view;
    }

    private void ParentVarification(String parentName, String parentPhone) {
        if (parentName.isEmpty()) {
            parentNameET.setError("Provide appropriate name!");
        } else if (parentPhone.length() < 11) {
            parentPhoneET.setError("Provide appropriate number!");
        } else {
            progressDialog.setIcon(R.drawable.verification_ic);
            progressDialog.setTitle("Waitting for verification!");
            progressDialog.setMessage("Your number is in verification. Please stay with us.");
            progressDialog.setCancelable(false);
            progressDialog.show();
            firebaseAuthOperation.HoldParent(parentName,parentPhone);
        }
    }
}
