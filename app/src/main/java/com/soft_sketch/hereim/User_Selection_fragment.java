package com.soft_sketch.hereim;


import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class User_Selection_fragment extends Fragment {

    private Spinner userlistSpinner;
    private UserSelectionIntf userSelectionIntf;


    public User_Selection_fragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        userSelectionIntf = (UserSelectionIntf) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user__selection_fragment, container, false);

        userlistSpinner = view.findViewById(R.id.userlist_spinner_id);

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(view.getContext(),
                android.R.layout.simple_spinner_dropdown_item,
                userlist());
        userlistSpinner.setAdapter(spinnerAdapter);
        userlistSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                switch (position){
                    case 0:{
                        Toast.makeText(getContext(), "Please select who are you.", Toast.LENGTH_SHORT).show();
                        break;
                    }
                    case 1:{
                        userSelectionIntf.OnCompleteUserSelection(position);
                        userlistSpinner.setSelection(0);
                        break;
                    }
                    case 2:{
                        userSelectionIntf.OnCompleteUserSelection(position);
                        userlistSpinner.setSelection(0);
                        break;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        return view;
    }


    private List<String> userlist() {
        List<String> user = new ArrayList<>();
        user.add("Log In As");
        user.add("Parent");
        user.add("Child");
        return user;
    }


    interface UserSelectionIntf{
        void OnCompleteUserSelection(int position);
    }

}
