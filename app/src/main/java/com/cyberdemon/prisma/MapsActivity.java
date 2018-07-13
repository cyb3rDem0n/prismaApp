package com.cyberdemon.prisma;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

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
    }

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
