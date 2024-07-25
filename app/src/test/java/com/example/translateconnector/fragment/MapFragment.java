package com.example.translateconnector.fragment;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.imoktranslator.R;

public class MapFragment extends BaseFragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Marker mMarker;

    private String address;
    private String lat;
    private String lng;
    private LatLng mLatlng;

    public static MapFragment newInstance(String lat, String lng, String address) {

        Bundle args = new Bundle();

        MapFragment fragment = new MapFragment();
        fragment.setArguments(args);
        fragment.address = address;
        fragment.lat = lat;
        fragment.lng = lng;
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_map;
    }

    @Override
    protected void initViews() {
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mLatlng = new LatLng(Double.parseDouble(lat), Double.parseDouble(lng));
    }

    @Override
    public boolean isHandleBackPressedOnFragment() {
        return false;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setRotateGesturesEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        addMarkerToMap();
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);
    }

    private void addMarkerToMap() {
        if (mLatlng != null && mMap != null) {
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(mLatlng);
            markerOptions.anchor(0.5f, 0.5f); // center of the image
            if (mMarker != null)
                mMarker.remove();
            markerOptions.title(address);

            mMarker = mMap.addMarker(markerOptions);
            moveCameraWithAnimation(mLatlng);

        }
    }

    private void moveCameraWithAnimation(LatLng ll) {
//        mMarker.hideInfoWindow();
        float currentZoom = mMap.getCameraPosition().zoom;
        CameraUpdate location = CameraUpdateFactory.newLatLngZoom(ll, currentZoom < 13 ? 16 : currentZoom);
        mMap.animateCamera(location, 1000, new GoogleMap.CancelableCallback() {
            @Override
            public void onFinish() {
                mMarker.showInfoWindow();
            }

            @Override
            public void onCancel() {

            }
        });
    }
}
