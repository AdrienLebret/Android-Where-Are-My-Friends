package com.utt.wherearemyfriends.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.utt.wherearemyfriends.Group;
import com.utt.wherearemyfriends.MainController;
import com.utt.wherearemyfriends.R;
import com.utt.wherearemyfriends.network.Message;

public class GroupActivity extends AppCompatActivity implements View.OnClickListener {
    private Button create, join, leave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);

        // Find UI components
        create = (Button) findViewById(R.id.createGroup);
        create.setOnClickListener(this);
        join = (Button) findViewById(R.id.joinGroup);
        join.setOnClickListener(this);
        leave = (Button) findViewById(R.id.leaveGroup);
        leave.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (R.id.createGroup == v.getId()) {
            //change activity
            Intent k = new Intent(GroupActivity.this, CreateGroupActivity.class);
            startActivity(k);
        }
        else if (R.id.joinGroup == v.getId()) {
            //change activity
            Intent k = new Intent(GroupActivity.this, JoinGroupActivity.class);
            startActivity(k);
        }
        else if (R.id.leaveGroup == v.getId()) {
            //Code for leaving a group
            MainController ctrl = MainController.getInstance();
            Group group = ctrl.getCurrentGroup().getValue();
            if (group != null && group.getId() != null) {
                ctrl.send(Message.unregister(group.getId()));
            }

            //change activity back to dashboard
            Intent k = new Intent(GroupActivity.this, DashboardActivity.class);
            startActivity(k);
        }
    }

}
