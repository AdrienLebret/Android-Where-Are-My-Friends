package com.utt.wherearemyfriends.activity;

import android.arch.lifecycle.Observer;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.utt.wherearemyfriends.Group;
import com.utt.wherearemyfriends.MainController;
import com.utt.wherearemyfriends.Member;
import com.utt.wherearemyfriends.R;
import com.utt.wherearemyfriends.network.Message;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class JoinGroupActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener {
    private Button join;
    private ArrayAdapter<String> listAdapter;
    private Map<String, Group> groups;
    private ListView mainListView;
    private MainController ctrl;
    private String clickGroup;

    Observer<Map<String, Group>> updateList = (@Nullable Map<String, Group> groups) -> {
        if (groups != null) {
            this.groups = groups;
            List<String> names = new ArrayList<>(groups.keySet());
            listAdapter = new ArrayAdapter<>(this, R.layout.simplerow, R.id.label, names);
            mainListView.setAdapter(listAdapter);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_group);

        //find ui component
        join = (Button) findViewById(R.id.joinOk);
        join.setOnClickListener(this);
        mainListView = (ListView) findViewById(R.id.list);
        mainListView.setOnItemClickListener(this);

        // listview
        ctrl = MainController.getInstance();
        ctrl.getGroups().observeForever(updateList);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.d("HelloListView", "You clicked Item: " + id + " at position:" + position);
        clickGroup = mainListView.getItemAtPosition(position).toString();
        Log.i("HelloListView", "group : " + clickGroup);
        //ajouter un effet visuel pour montrer la ligne selectionnée?
    }

    @Override
    public void onClick(View v) {
        if (R.id.joinOk == v.getId()) {
            //join group
            Member user = ctrl.getUser().getValue();
            if (user != null) {
                ctrl.send(Message.register(clickGroup, user.getName()));
            }

            //change activity
            Intent k = new Intent(JoinGroupActivity.this, DashboardActivity.class);
            startActivity(k);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ctrl.getGroups().removeObserver(updateList);
    }
}
