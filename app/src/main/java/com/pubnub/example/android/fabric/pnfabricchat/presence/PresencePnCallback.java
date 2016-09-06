package com.pubnub.example.android.fabric.pnfabricchat.presence;

import android.util.Log;

import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableList;
import com.pubnub.api.Callback;
import com.pubnub.api.PubnubError;
import com.pubnub.example.android.fabric.pnfabricchat.mapbox.PresenceMapAdapter;
import com.pubnub.example.android.fabric.pnfabricchat.mapbox.PresenceMapPojo;
import com.pubnub.example.android.fabric.pnfabricchat.util.DateTimeUtil;
import com.pubnub.example.android.fabric.pnfabricchat.util.JsonUtil;

import org.json.JSONObject;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class PresencePnCallback extends Callback {
    private static final String TAG = PresencePnCallback.class.getName();
    private final PresenceListAdapter presenceListAdapter;
    private final PresenceMapAdapter presenceMapAdapter;

    public PresencePnCallback(PresenceListAdapter presenceListAdapter, PresenceMapAdapter presenceMapAdapter) {
        this.presenceListAdapter = presenceListAdapter;
        this.presenceMapAdapter = presenceMapAdapter;
    }

    @Override
    public void successCallback(String channel, Object message) {
        try {
            Log.v(TAG, "presenceP(" + JsonUtil.asJson(message) + ")");
        } catch (Exception e) {
            throw Throwables.propagate(e);
        }

        try {
            Map<String, Object> presence = JsonUtil.fromJSONObject((JSONObject) message, LinkedHashMap.class);

            List<Map<String, Object>> uuids;
            if (presence.containsKey("uuids")) {
                uuids = (List<Map<String, Object>>) presence.get("uuids");
            } else {
                uuids = ImmutableList.<Map<String, Object>>of(presence);
            }

            for (Map<String, Object> object : uuids) {
                String sender = (String) object.get("uuid");
                String presenceString = object.containsKey("action") ? (String) object.get("action") : "join";
                String timestamp = DateTimeUtil.getTimeStampUtc();
                PresencePojo pm = new PresencePojo(sender, presenceString, timestamp);

                if (object.containsKey("data") || object.containsKey("state")) {
                    // we have a state change
                    if (presenceMapAdapter != null) {
                        Log.v(TAG, "presenceStateChange(" + JsonUtil.asJson(presence) + ")");

                        if ("timeout".equals(presenceString) || "leave".equals(presenceString)) {
                            presenceMapAdapter.refresh(pm);
                        } else {
                            Map<String, Object> state = object.containsKey("data") ? (Map<String, Object>) object.get("data") : (Map<String, Object>) object.get("state");
                            ;

                            if (state.containsKey("lat") && state.containsKey("lon")) {
                                Double lat = Double.parseDouble((String) state.get("lat").toString());
                                Double lon = Double.parseDouble((String) state.get("lon").toString());

                                presenceMapAdapter.update(new PresenceMapPojo(sender, lat, lon, timestamp));
                            }
                        }
                    }
                }

                if (!"state-change".equals(presenceString)) {
                    // we have a group membership change
                    presenceListAdapter.add(pm);
                }
            }
        } catch (Exception e) {
            throw Throwables.propagate(e);
        }
    }

    @Override
    public void errorCallback(String channel, PubnubError error) {
        try {
            Log.v(TAG, "presenceErr(" + JsonUtil.asJson(error) + ")");
        } catch (Exception e) {
            throw Throwables.propagate(e);
        }
    }

    @Override
    public void connectCallback(String channel, Object message) {
        try {
            Log.v(TAG, "connP(" + JsonUtil.asJson(message) + ")");
        } catch (Exception e) {
            throw Throwables.propagate(e);
        }
    }

    @Override
    public void reconnectCallback(String channel, Object message) {
        try {
            Log.v(TAG, "reconnP(" + JsonUtil.asJson(message) + ")");
        } catch (Exception e) {
            throw Throwables.propagate(e);
        }
    }

    @Override
    public void disconnectCallback(String channel, Object message) {
        try {
            Log.v(TAG, "disconnP(" + JsonUtil.asJson(message) + ")");
        } catch (Exception e) {
            throw Throwables.propagate(e);
        }
    }
}
