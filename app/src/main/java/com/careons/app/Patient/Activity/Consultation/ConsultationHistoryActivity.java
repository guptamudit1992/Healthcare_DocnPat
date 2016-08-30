package com.careons.app.Patient.Activity.Consultation;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.careons.app.Patient.Activity.Chat.ChatActivity;
import com.careons.app.R;

public class ConsultationHistoryActivity extends AppCompatActivity implements View.OnClickListener {

    private LinearLayout chatHistoryLinearLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consultation_history);

        // Initialize Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        // Initialize fields
        chatHistoryLinearLayout = (LinearLayout) findViewById(R.id.chat_history_1);
        chatHistoryLinearLayout.setOnClickListener(this);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // I do not want this...
                // Home as up button is to navigate to Home-Activity not previous activity
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.chat_history_1:
                Intent intent = new Intent(this, ChatActivity.class);
                intent.putExtra("source", "history");
                startActivity(intent);
                break;
        }
    }
}
