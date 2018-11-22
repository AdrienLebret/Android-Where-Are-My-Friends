package com.utt.wherearemyfriends.activity;

import android.Manifest;
import android.arch.lifecycle.Observer;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.utt.wherearemyfriends.Group;
import com.utt.wherearemyfriends.MainController;
import com.utt.wherearemyfriends.Member;
import com.utt.wherearemyfriends.R;
import com.utt.wherearemyfriends.network.Message;

import java.util.HashMap;
import java.util.Map;

public class MapFragment extends Fragment implements OnMapReadyCallback {
    public static final String TAG = "MapFragment";
    MapView mMapView;
    private GoogleMap googleMap;
    private MainController ctrl;
    private Marker userMark;
    private String id;
    private Member user;
    private Map<String, Marker> markers;

    // Updating the view
    Observer<Member> markUser = (Member user) -> {
        if (user != null) {
            this.user = user;
            if (user.getCoords() != null) {
                setUserMark();
            }
        }
    };

    Observer<Group> markMembers = (Group group) -> {
        if (group != null) {
            id = group.getId();
            // Mark the members
            for (Marker m : markers.values()) {
                m.remove();
            }
            markers.clear();
            for (Member m : group.getMembers()) {
                if (m.getCoords() != null && !m.getName().equals(user.getName())) {
                    markers.put(m.getName(), mark(m));
                }
            }
        }
    };

    private void setUserMark() {
        LatLng coords = user.getCoords();
        if (userMark == null) {
            userMark = googleMap.addMarker(new MarkerOptions()
                    .position(coords)
                    .title(user.getName())
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
            // Move the camera to the user coords the first time they're set
            googleMap.moveCamera((CameraUpdateFactory.newLatLng(coords)));
            /*
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(coords).zoom(12).build();
            googleMap.animateCamera(CameraUpdateFactory
                .newCameraPosition(cameraPosition));
            */
        } else {
            userMark.setPosition(coords);
        }
    }

    private Marker mark(Member member) {
        return googleMap.addMarker(new MarkerOptions()
                .position(member.getCoords())
                .title(member.getName())
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
    }

    private final class MapLocationListener implements LocationListener {
        @Override
        public void onLocationChanged(Location loc) {
            LatLng coords = new LatLng(loc.getLatitude(), loc.getLongitude());
            if (id != null) {
                ctrl.send(Message.location(id, coords));
            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) { }

        @Override
        public void onProviderEnabled(String provider) { }

        @Override
        public void onProviderDisabled(String provider) { }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ctrl = MainController.getInstance();
        markers = new HashMap<>();
        ctrl.getUser().observeForever(markUser);
        ctrl.getCurrentGroup().observeForever(markMembers);
    }

    public void startLocating() {
        FragmentActivity act = getActivity();
        if (act == null) {
            return;
        }
        LocationManager locationManager;
        try {
            locationManager = (LocationManager) act
                    .getSystemService(Context.LOCATION_SERVICE);
        } catch (Exception e) {
            Log.e(TAG, "Unable to get LocationManager: ", e);
            return;
        }
        LocationListener locationListener = new MapLocationListener();

        if (PackageManager.PERMISSION_GRANTED == ActivityCompat
                .checkSelfPermission(act, Manifest.permission.ACCESS_FINE_LOCATION) ||
            PackageManager.PERMISSION_GRANTED == ActivityCompat
                .checkSelfPermission(act, Manifest.permission.ACCESS_COARSE_LOCATION)) {
            locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, 30000, 0, locationListener);
            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, 30000, 0, locationListener);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // inflate and return the layout
        View v = inflater.inflate(R.layout.location_fragment, container,
                false);
        mMapView = (MapView) v.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);
        mMapView.getMapAsync(this);

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
        ctrl.getUser().removeObserver(markUser);
        ctrl.getCurrentGroup().removeObserver(markMembers);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    @Override
    public void onMapReady(GoogleMap gMap) {
        googleMap = gMap;
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }
}