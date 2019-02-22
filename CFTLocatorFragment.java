package com.success.successEntellus.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.success.successEntellus.R;

/**
 * Created by user on 7/24/2018.
 */

public class CFTLocatorFragment extends Fragment implements OnMapReadyCallback {
    private GoogleMap mMap;
    View layout;
    AutoCompleteTextView sv_search_location;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        layout=inflater.inflate(R.layout.activity_cftlocator,container,false);
       // init();
        SupportMapFragment mapFragment = (SupportMapFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(CFTLocatorFragment.this);

        return layout;
    }

    private void init() {
        sv_search_location=layout.findViewById(R.id.sv_search_location);
      //  sv_search_location.setIconified(false);
        sv_search_location.setFocusable(false);
        sv_search_location.clearFocus();
      //  sv_search_location.setQueryHint("Search Location");
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle("CFT Locator");
    }
}
