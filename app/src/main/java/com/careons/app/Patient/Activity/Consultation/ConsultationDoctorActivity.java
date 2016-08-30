package com.careons.app.Patient.Activity.Consultation;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.careons.app.Patient.Activity.Chat.ChatActivity;
import com.careons.app.Patient.Database.Models.Chat.ConsultationRecord;
import com.careons.app.R;

public class ConsultationDoctorActivity extends AppCompatActivity implements View.OnClickListener {

    private CardView proceedCardView;
    private ConsultationRecord consultationRecord;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consultation_doctor);

        // Initialize Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        // Initialize fields
        proceedCardView = (CardView) findViewById(R.id.proceed);
        proceedCardView.setOnClickListener(this);

        // Fetch parcelable record from intent
        Bundle data = getIntent().getExtras();
        consultationRecord = data.getParcelable("consultationRecord");
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

            case R.id.proceed:
                Intent intent = new Intent(this, ChatActivity.class);
                intent.putExtra("source", "live");
                intent.putExtra("consultationRecord", consultationRecord);
                startActivity(intent);
                break;
        }
    }

}
