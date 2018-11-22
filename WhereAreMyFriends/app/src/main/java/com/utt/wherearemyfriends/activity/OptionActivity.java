package com.utt.wherearemyfriends.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.utt.wherearemyfriends.R;

public class OptionActivity extends AppCompatActivity implements View.OnClickListener {

    private Button optionOk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_option);

        //find ui component
        optionOk = (Button) findViewById(R.id.optionOk);
        optionOk.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (R.id.optionOk == v.getId()) {
            //change activity
            Intent k = new Intent(OptionActivity.this, DashboardActivity.class);
            startActivity(k);
        }
    }
}
