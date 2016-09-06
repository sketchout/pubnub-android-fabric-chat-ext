package com.pubnub.example.android.fabric.pnfabricchat.mapbox;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.google.common.base.Throwables;
import com.mapbox.mapboxsdk.annotations.MarkerView;
import com.mapbox.mapboxsdk.annotations.MarkerViewOptions;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.pubnub.example.android.fabric.pnfabricchat.presence.PresencePojo;
import com.pubnub.example.android.fabric.pnfabricchat.util.JsonUtil;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class PresenceMapAdapter {
    private final Context context;
    private final Map<String, MarkerView> latestMarker = new LinkedHashMap<>();
    private final Map<String, PresenceMapPojo> latestPresence = new LinkedHashMap<>();
    private AtomicReference<MapView> mapViewRef;
    private AtomicReference<MapboxMap> mapboxMapRef;

    public PresenceMapAdapter(Context context) {
        this.context = context;
    }

    public void setMapView(AtomicReference<MapView> mapViewRef, AtomicReference<MapboxMap> mapboxMapRef) {
        this.mapViewRef = mapViewRef;
        this.mapboxMapRef = mapboxMapRef;
    }

    public void update(final PresenceMapPojo message) {
        try {
            Log.d("mapadapterU", JsonUtil.asJson(message));
        } catch (Exception e) {
            throw Throwables.propagate(e);
        }

        latestPresence.put(message.getSender(), message);

        if (mapboxMapRef.get() == null) {
            return;
        }


        ((Activity) this.context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (latestMarker.containsKey(message.getSender())) {
                    mapboxMapRef.get().removeMarker(latestMarker.get(message.getSender()));
                    latestMarker.remove(message.getSender());
                }

                MarkerViewOptions markerOptions = new MarkerViewOptions()
                        .position(new LatLng(message.getLat(), message.getLon()))
                        .title(message.getSender())
                        .snippet(message.getTimestamp());

                MarkerView marker = mapboxMapRef.get().addMarker(markerOptions);

                latestMarker.put(message.getSender(), marker);
            }
        });
    }

    public void refresh(final PresencePojo message) {
        try {
            Log.d("mapadapterR", JsonUtil.asJson(message));
        } catch (Exception e) {
            throw Throwables.propagate(e);
        }

        String presence = message.getPresence();

        if ("timeout".equals(presence) || "leave".equals(presence)) {
            latestPresence.remove(message.getSender());

            if (mapboxMapRef.get() == null) {
                return;
            }

            if (latestMarker.containsKey(message.getSender())) {
                ((Activity) this.context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mapboxMapRef.get().removeMarker(latestMarker.get(message.getSender()));
                        latestMarker.remove(message.getSender());
                    }
                });
            }
        }
    }

    public void refreshAll() {
        for (PresenceMapPojo message : latestPresence.values()) {
            update(message);
        }
    }
}
