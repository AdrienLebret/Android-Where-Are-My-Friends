package com.utt.wherearemyfriends.activity;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.utt.wherearemyfriends.MainController;
import com.utt.wherearemyfriends.network.Message;
import com.utt.wherearemyfriends.network.NetworkService;
import com.utt.wherearemyfriends.R;

public class DashboardActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String SERVER_IP = "195.178.227.53";
    private static final String SERVER_PORT = "7117";

    private Button option, manage;
    private TextView hello;
    private String name;
    private static final int REQUEST_ACCESS_FINE_LOCATION = 1;
    private static final int REQUEST_ACCESS_COARSE_LOCATION = 1;
    private NetConn conn;
    private NetworkService netServ;
    private MainController ctrl;
    private MapFragment map;

    private void getPermission(String name, int req) {
        if (PackageManager.PERMISSION_DENIED == ContextCompat.checkSelfPermission(this, name)) {
            // The permission is not already granted
            if (!ActivityCompat.shouldShowRequestPermissionRationale(this, name)) {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(this, new String[]{name}, req);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        FragmentManager fm = getSupportFragmentManager();
        ctrl = MainController.getInstance();
        ctrl.setActivity(this);

        // Find UI components
        hello = (TextView) findViewById(R.id.hello);
        manage = (Button) findViewById(R.id.groupBtn);
        manage.setOnClickListener(this);
        option = (Button) findViewById(R.id.optionOk);
        option.setOnClickListener(this);
        map = (MapFragment) fm.findFragmentById(R.id.map);

        //personalize hello message
        SharedPreferences sharedPref =
                getSharedPreferences(getString(R.string.file), Context.MODE_PRIVATE);
        name = sharedPref.getString("name", "default value");
        ctrl.setUsername(name);
        hello.setText("Hello " + name + " ! Choose or Create a group !");

        // Start the network service
        Intent intent = new Intent(DashboardActivity.this, NetworkService.class);
        intent.putExtra(NetworkService.SERVER_IP, SERVER_IP);
        intent.putExtra(NetworkService.SERVER_PORT, SERVER_PORT);
        if (savedInstanceState == null) {
            startService(intent);
        }
        conn = new NetConn();
        if (!bindService(intent, conn, 0)) {
            Log.d("TCPConnection: ", "No binding");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        netServ.disconnect();
    }


    public class NetConn implements ServiceConnection {
        public void onServiceConnected(ComponentName arg0, IBinder binder){
            // Connect to the server
            NetworkService.LocalService ls = (NetworkService.LocalService) binder;
            netServ = ls.getService();
            ctrl.connect(netServ);

            // Start locating the user
            getPermission(Manifest.permission.ACCESS_FINE_LOCATION, REQUEST_ACCESS_FINE_LOCATION);
            getPermission(Manifest.permission.ACCESS_COARSE_LOCATION, REQUEST_ACCESS_COARSE_LOCATION);
            map.startLocating();
        }

        public void onServiceDisconnected(ComponentName arg0){
        }
    }

    @Override
    public void onClick(View v) {
        if (R.id.groupBtn == v.getId()) {
            //change activity
            ctrl.send(Message.groups());
            Intent k = new Intent(DashboardActivity.this, GroupActivity.class);
            startActivity(k);
        }
        else if (R.id.optionOk == v.getId()) {
            //change activity
            Intent k = new Intent(DashboardActivity.this, OptionActivity.class);
            startActivity(k);
        }
    }
}
