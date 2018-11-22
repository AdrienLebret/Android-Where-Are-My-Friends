package com.utt.wherearemyfriends.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.utt.wherearemyfriends.MainController;
import com.utt.wherearemyfriends.network.Message;
import com.utt.wherearemyfriends.R;

public class CreateGroupActivity extends AppCompatActivity implements View.OnClickListener {
    private Button create;
    private EditText gname;
    private MainController ctrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);
        ctrl = MainController.getInstance();

        //find ui component
        create = (Button) findViewById(R.id.createOk);
        create.setOnClickListener(this);
        gname = (EditText) findViewById(R.id.gname);
    }

    @Override
    public void onClick(View v) {
        if (R.id.createOk == v.getId()) {
            String group = gname.getText().toString();
            String user = ctrl.getUser().getValue().getName();
            ctrl.send(Message.register(group, user));

            //change activity
            Intent k = new Intent(CreateGroupActivity.this, DashboardActivity.class);
            startActivity(k);
        }
    }
}
