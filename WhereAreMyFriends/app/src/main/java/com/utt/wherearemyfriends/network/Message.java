package com.utt.wherearemyfriends.network;

import android.util.JsonWriter;

import com.google.android.gms.maps.model.LatLng;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringWriter;

public class Message {
    private static String write(String type, String[] keys, String[] values) {
        StringWriter sw = new StringWriter();
        try (JsonWriter jw = new JsonWriter(new BufferedWriter(sw))) {
            jw.beginObject().name("type").value(type);
            for (int i = 0; i < keys.length; ++i) {
                jw.name(keys[i]).value(values[i]);
            }
            jw.endObject();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sw.toString();
    }

    public static String register(String group, String member) {
        return write("register",
            new String[]{"group", "member"},
            new String[]{ group,   member});
    }

    public static String unregister(String id) {
        return write("unregister",
                new String[]{"id"},
                new String[]{ id});
    }

    public static String members(String group) {
        return write("members",
                new String[]{"group"},
                new String[]{ group});
    }

    public static String groups() {
        // return write("groups", new String[]{}, new String[]{});
        return "{\"type\": \"groups\"}";
    }

    public static String location(String id, LatLng latLng) {
        String latitude  = Double.toString(latLng.latitude);
        String longitude = Double.toString(latLng.longitude);
        return write("location",
                new String[]{"id", "longitude", "latitude"},
                new String[]{ id,   longitude,   latitude});
    }
}
