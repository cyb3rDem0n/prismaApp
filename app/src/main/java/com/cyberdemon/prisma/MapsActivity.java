package com.cyberdemon.prisma;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.maps.android.data.kml.KmlLayer;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    //Bottom Menu
    private ConstraintLayout infoLayout;
    private ConstraintLayout projectLayout;
    private ConstraintLayout newsLayout;
    private ConstraintLayout mapLayout;

    // Maps
    private GoogleMap mMap;
    private static final String TAG = MapsActivity.class.getSimpleName();
    private UiSettings mUiSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Bottom Menu
        infoLayout = findViewById(R.id.contraintInfo);
        projectLayout = findViewById(R.id.contraintProject);
        newsLayout = findViewById(R.id.contraintNews);
        mapLayout = findViewById(R.id.contraintMap);

        infoLayout.setVisibility(View.VISIBLE);
        projectLayout.setVisibility(View.GONE);
        newsLayout.setVisibility(View.GONE);
        mapLayout.setVisibility(View.GONE);

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_info:
                    infoLayout.setVisibility(View.VISIBLE);
                    projectLayout.setVisibility(View.GONE);
                    newsLayout.setVisibility(View.GONE);
                    mapLayout.setVisibility(View.GONE);
                    return true;
                case R.id.navigation_project:
                    infoLayout.setVisibility(View.GONE);
                    projectLayout.setVisibility(View.VISIBLE);
                    newsLayout.setVisibility(View.GONE);
                    mapLayout.setVisibility(View.GONE);
                    return true;
                case R.id.navigation_news:
                    infoLayout.setVisibility(View.GONE);
                    projectLayout.setVisibility(View.GONE);
                    newsLayout.setVisibility(View.VISIBLE);
                    mapLayout.setVisibility(View.GONE);
                    return true;
                case R.id.navigation_maps:
                    infoLayout.setVisibility(View.GONE);
                    projectLayout.setVisibility(View.GONE);
                    newsLayout.setVisibility(View.GONE);
                    mapLayout.setVisibility(View.VISIBLE);
                    return true;
            }
            return false;
        }
    };

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        KmlLayer layer = null;

        mUiSettings = mMap.getUiSettings();
        mUiSettings.setZoomControlsEnabled(true);
        mUiSettings.setCompassEnabled(true);

        double centerLatitude = 42.22058974696394;
        double centerLongitude = 11.600794499999893;
        LatLng centeredPosition = new LatLng(centerLatitude, centerLongitude);


        try {
            boolean success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.style_json));

            if (!success) {
                Log.e(TAG, "Style parsing failed.");}

                layer = new KmlLayer(mMap, R.raw.camere, MapsActivity.this);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(centeredPosition,5.0f));
            layer.addLayerToMap();
        } catch (XmlPullParserException | IOException e) {
            e.printStackTrace();
        }


    }
}
