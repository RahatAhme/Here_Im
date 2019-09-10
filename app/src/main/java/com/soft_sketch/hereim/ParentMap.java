package com.soft_sketch.hereim;


import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.soft_sketch.hereim.POJO.ChildInfo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class ParentMap extends Fragment implements OnMapReadyCallback {


    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference rootRef = database.getReference();

    private Geocoder geocoder;
    private List<Address> addresses;
    private ChildInfo childInfo;

    private String parentid;
    private String childID;

    private static final int LOCATION_REQUEST_CODE = 111;
    private GoogleMap map;


    public ParentMap() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_parent_map, container, false);
        ((ParentActivity) getActivity()).setTitle("Child Current Location");

        parentid = getArguments().getString("parentIDpasser");
        childID = getArguments().getString("childIDpasser");

        childInfo = new ChildInfo();
        addresses = new ArrayList<>();
        geocoder = new Geocoder(getContext());

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.parentMap_id);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;


        rootRef.child(parentid).child("child").child(childID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    childInfo = dataSnapshot.getValue(ChildInfo.class);

                    LatLng latLng = new LatLng(childInfo.getCurrentLocLati(), childInfo.getCurrentLocLong());
                    MarkerOptions options = new MarkerOptions();
                    UiSettings uiSettings = map.getUiSettings();
                    uiSettings.setZoomControlsEnabled(true);
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16f));

                    if (checkLocationPermission()) {
                        map.setMyLocationEnabled(true);
                        options.position(latLng);
                        map.addMarker(options);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


    }


    public boolean checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_REQUEST_CODE);
            return false;
        }
        return true;
    }

    public String getAddressName(double currentlet, double currentlog) {
        String location = "";
        try {
            addresses = geocoder.getFromLocation(currentlet, currentlog, 1);
            Address currentAddress = addresses.get(0);
            location = currentAddress.getAddressLine(0);
        } catch (IOException e) {
            Toast.makeText(getContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        }
        return location;
    }

}
