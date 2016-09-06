package com.pubnub.example.android.fabric.pnfabricchat.mapbox;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mapbox.mapboxsdk.constants.Style;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.pubnub.example.android.fabric.pnfabricchat.R;

import java.util.concurrent.atomic.AtomicReference;

public class PresenceMapTabFragment extends Fragment {
    private PresenceMapAdapter presenceMapAdapter;
    private AtomicReference<MapView> mapViewRef = new AtomicReference<MapView>();
    private AtomicReference<MapboxMap> mapboxMapRef = new AtomicReference<MapboxMap>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_presence_map, container, false);

        MapView mapView = (MapView) view.findViewById(R.id.mapboxMarkerMapView);
        mapViewRef.set(mapView);

        mapView.setAccessToken(getString(R.string.com_mapbox_mapboxsdk_accessToken));
        mapView.onCreate(savedInstanceState);

        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(MapboxMap mapboxMap) {
                mapboxMap.setStyle(Style.MAPBOX_STREETS);
                mapboxMapRef.set(mapboxMap);

                if (presenceMapAdapter != null) {
                    presenceMapAdapter.refreshAll();
                }
            }
        });

        return view;
    }

    public void setAdapter(PresenceMapAdapter presenceMapAdapter) {
        this.presenceMapAdapter = presenceMapAdapter;
        presenceMapAdapter.setMapView(mapViewRef, mapboxMapRef);
    }

    @Override
    public void onPause() {
        super.onPause();
        mapViewRef.get().onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapViewRef.get().onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapViewRef.get().onDestroy();
    }
}
