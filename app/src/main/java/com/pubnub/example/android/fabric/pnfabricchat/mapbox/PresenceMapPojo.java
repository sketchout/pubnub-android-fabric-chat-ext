package com.pubnub.example.android.fabric.pnfabricchat.mapbox;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

public class PresenceMapPojo {
    private final String sender;
    private final Double lat;
    private final Double lon;
    private final String timestamp;

    public PresenceMapPojo(@JsonProperty("sender") String sender, @JsonProperty("lat") Double lat, @JsonProperty("lon") Double lon, @JsonProperty("timestamp") String timestamp) {
        this.sender = sender;
        this.lat = lat;
        this.lon = lon;
        this.timestamp = timestamp;
    }

    public String getSender() {
        return sender;
    }

    public Double getLat() {
        return lat;
    }

    public Double getLon() {
        return lon;
    }

    public String getTimestamp() {
        return timestamp;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        final PresenceMapPojo other = (PresenceMapPojo) obj;

        return Objects.equal(this.sender, other.sender)
                && Objects.equal(this.lat, other.lat)
                && Objects.equal(this.lon, other.lon)
                && Objects.equal(this.timestamp, other.timestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(sender, lat, lon, timestamp);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(PresenceMapPojo.class)
                .add("sender", sender)
                .add("lat", lat)
                .add("lon", lon)
                .add("timestamp", timestamp)
                .toString();
    }
}
