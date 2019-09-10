package com.soft_sketch.hereim;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class Child_Current_Loc extends Fragment implements OnMapReadyCallback {

    private static final int LOCATION_REQUEST_CODE = 111;
    private GoogleMap map;

    private FusedLocationProviderClient client;
    private Geocoder geocoder;
    private LocationRequest locationRequest;

    private String parentID = "";
    private String childID = "";

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference rootRef = database.getReference();

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    private LocationManager locationManager;
    private boolean isGPSEnabled;

    public Child_Current_Loc() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_child__current__loc, container, false);
        ((ChildActivity) getActivity()).setTitle("Child Current Location");

        parentID = getArguments().getString("pidforloc");
        childID = getArguments().getString("cidforloc");

        client = new FusedLocationProviderClient(getContext());
        geocoder = new Geocoder(getContext());

        getUpdateLocation();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.childMap_id);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        MarkerOptions options = new MarkerOptions();
        UiSettings uiSettings = map.getUiSettings();
        uiSettings.setZoomControlsEnabled(true);

        if (CheckPermission()) {

            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
            } else {

                map.setMyLocationEnabled(true);
                uiSettings.setZoomControlsEnabled(true);
            }
        }
    }

    private boolean CheckPermission() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
            return false;
        }
        return true;
    }

    public void gotoLocSetting() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        final String action = Settings.ACTION_LOCATION_SOURCE_SETTINGS;
        String message = "Do you want open GPS setting?";

        builder.setMessage(message)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface d, int id) {
                                startActivity(new Intent(action));
                                d.dismiss();
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface d, int id) {
                                d.cancel();
                            }
                        });
        builder.create().show();
    }

    public void getUpdateLocation() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            CheckPermission();
        } else {
            if (chkGPSorNetworkEnabled()){
                client.requestLocationUpdates(getLocationRequest(), callback, null);
            }else {
                ShowDialogue();
            }
        }
    }

    private LocationRequest getLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(60000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setFastestInterval(300000);
        return locationRequest;
    }

    public LocationCallback callback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            for (Location location : locationResult.getLocations()) {
                if (location == null) {
                    ShowDialogue();
                    return;
                }
                preferences = getContext().getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE);
                editor = preferences.edit();

                double updateLet = location.getLatitude();
                double updateLong = location.getLongitude();

                editor.putString("lat", String.valueOf(updateLet));
                editor.putString("long", String.valueOf(updateLong));
                editor.apply();

                DatabaseReference updateloc = rootRef.child(parentID).child("child").child(childID);
                Map<String, Object> locUpdate = new HashMap<>();

                locUpdate.put("currentLocLati", updateLet);
                locUpdate.put("currentLocLong", updateLong);

                updateloc.updateChildren(locUpdate).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getContext(), "Location update", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    };

    public boolean chkGPSorNetworkEnabled() {

        locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);

        return isGPSEnabled = locationManager.isProviderEnabled(locationManager.GPS_PROVIDER);
    }

    private void ShowDialogue() {
        String gpsMsg = "Please Enable your GPS/Location Service";

        android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(getContext());
        dialog.setMessage(gpsMsg);
        dialog.setCancelable(false);

        dialog.setPositiveButton("GPS Settings", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent gpsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(gpsIntent);
            }
        });
        dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                getActivity().finish();
            }
        });
        dialog.show();

    }
}
