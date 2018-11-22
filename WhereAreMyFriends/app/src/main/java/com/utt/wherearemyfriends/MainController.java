package com.utt.wherearemyfriends;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.utt.wherearemyfriends.network.Message;
import com.utt.wherearemyfriends.network.NetworkService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainController {
    private static final String TAG = "MainController";
    private static final MainController ourInstance = new MainController();
    //
    private NetworkService netServ;
    //
    private MutableLiveData<Map<String, Group>> groups;
    private MutableLiveData<Group> currentGroup;
    private MutableLiveData<Member> user;
    //
    private AppCompatActivity activity;

    private MainController() {
        groups = new MutableLiveData<>();
        groups.setValue(new HashMap<>());
        currentGroup = new MutableLiveData<>();
        currentGroup.setValue(null);
        user = new MutableLiveData<>();
        user.setValue(new Member(""));
    }

    public static MainController getInstance() {
        return ourInstance;
    }

    public void send(String message) {
        netServ.sendMessage(message);
    }

    public void connect(NetworkService netServ) {
        netServ.connect();
        this.netServ = netServ;
    }

    public void setActivity(AppCompatActivity activity) {
        this.activity = activity;
    }

    /*
     * Listen for data changes
     */

    public LiveData<Map<String, Group>> getGroups() {
        return groups;
    }

    public LiveData<Group> getCurrentGroup() {
        return currentGroup;
    }

    public LiveData<Member> getUser() {
        return user;
    }

    public void setUsername(String name) {
        Member u = user.getValue();
        if (u != null) {
            u.setName(name);
            user.postValue(u);
        }
    }

    /*
     * Process messages from the server
     */
    private void register(String group, String id) {
        Map<String, Group> groups = this.groups.getValue();
        Group newGroup = new Group(group, id, new ArrayList<>());
        if (groups != null) {
            groups.put(group, newGroup);
            this.groups.postValue(groups);
        }
        currentGroup.postValue(newGroup);
        send(Message.members(group));
    }

    private void unregister(String id) {
        Group curGroup = this.currentGroup.getValue();
        if (curGroup != null && id.equals(curGroup.getId())) {
            this.currentGroup.postValue(null);
        }
    }

    private void members(String group, List<Member> members) {
        Group newGroup  = new Group(group, members);
        Group curGroup = this.currentGroup.getValue();
        if (curGroup != null && group.equals(curGroup.getName())) {
            this.currentGroup.postValue(newGroup);
        }
        Map<String, Group> groups = this.groups.getValue();
        if (groups != null) {
            groups.put(group, newGroup);
            this.groups.postValue(groups);
        }
    }

    private void groups(Map<String, Group> groups) {
        this.groups.postValue(groups);
    }

    private void location(String id, LatLng coords) {
        Member u = user.getValue();
        if (u != null) {
            user.postValue(new Member(u.getName(), coords));
        }
    }

    private void exception(String message) {
        if (message.startsWith("Bad argument")) {
            if (message.contains("longitude")) {
                // Attempt to register again into the group
                Group group = currentGroup.getValue();
                Member u = user.getValue();
                if (group != null && u != null) {
                    send(Message.register(group.getName(), u.getName()));
                    return;
                }
            }
        }
        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
    }

    public void process(String result) {
        Thread t = new ProcessMessage(result);
        t.start();
    }

    /*
     * Message reception thread
     */

    public class ProcessMessage extends Thread {
        private String message;

        private Member readMember(JSONObject obj) throws JSONException {
            String name = obj.getString("member");
            LatLng coords = null;
            try {
                String lat = obj.getString("latitude");
                String lng = obj.getString("longitude");
                if (!"NaN".equals(lat) && !"NaN".equals(lng)) {
                    coords = new LatLng(Double.parseDouble(lat), Double.parseDouble(lng));
                }
            } catch (JSONException e) {
                // No error for missing coordinates
            }
            return new Member(name, coords);
        }

        private List<Member> readMembers(JSONArray list) throws JSONException {
            List<Member> members = new ArrayList<>();
            for (int i = 0; i < list.length(); ++i) {
                members.add(readMember(list.getJSONObject(i)));
            }
            return members;
        }

        private Map<String, Group> readGroups(JSONArray list) throws JSONException {
            Map<String, Group> groups = new HashMap<>();
            for (int i = 0; i < list.length(); ++i) {
                String name = list.getJSONObject(i).getString("group");
                groups.put(name, new Group(name));
            }
            return groups;
        }

        private void process(JSONObject message) throws JSONException {
            String type = message.getString("type");
            Log.d(TAG, "Received message of type: " + type);
            String group, id;
            switch (type) {
                case "register":
                    group = message.getString("group");
                    id = message.getString("id");
                    register(group, id);
                    break;
                case "unregister":
                    id = message.getString("id");
                    unregister(id);
                    break;
                case "members":
                    group = message.getString("group");
                    List<Member> members = readMembers(message.getJSONArray("members"));
                    members(group, members);
                    break;
                case "groups":
                    Map<String, Group> groups = readGroups(message.getJSONArray("groups"));
                    groups(groups);
                    break;
                case "location":
                    id = message.getString("id");
                    String lat = message.getString("latitude");
                    String lng = message.getString("longitude");
                    LatLng coords = new LatLng(Double.parseDouble(lat), Double.parseDouble(lng));
                    location(id, coords);
                    break;
                case "locations":
                    group = message.getString("group");
                    List<Member> locations = readMembers(message.getJSONArray("location"));
                    members(group, locations);
                    break;
                case "exception":
                    String info = message.getString("message");
                    exception(info);
                    break;
                // TODO for grade VG
                case "textchat":
                case "upload":
                case "imagechat":
                default:
                    Log.e(TAG, "Unknown message type: " + type);
            }
        }

        ProcessMessage(String message) {
            this.message = message;
        }

        public void run() {
            try {
                JSONObject msg = new JSONObject(message);
                process(msg);
            } catch (JSONException e) {
                Log.e(TAG, "Error processing message", e);
            }
        }
    }
}
