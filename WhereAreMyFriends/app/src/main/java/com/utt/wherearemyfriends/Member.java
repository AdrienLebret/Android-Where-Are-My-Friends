package com.utt.wherearemyfriends;

import com.google.android.gms.maps.model.LatLng;

public class Member {
    private String name;
    private LatLng coords; // Can be null

    public Member(String name) {
        this(name, null);
    }

    public Member(String name, LatLng coords) {
        this.name = name;
        this.coords = coords;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LatLng getCoords() {
        return coords;
    }

    public void setCoords(LatLng coords) {
        this.coords = coords;
    }
}
