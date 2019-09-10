package com.soft_sketch.hereim;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} subclass.
 */
public class ParentTimeMonitoring extends Fragment {


    public ParentTimeMonitoring() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_parent_time_monitoring, container, false);
        ((ParentActivity) getActivity())
                .setTitle("Social Media Time");
        return view;
    }

    interface ParentTimeMonitorIntf{

    }

}
