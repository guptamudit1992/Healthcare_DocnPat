package com.careons.app.Patient.Activity.Consultation;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.careons.app.Patient.Commons.StaticConstants;
import com.careons.app.Patient.Database.Models.Chat.ConsultationRecord;
import com.careons.app.R;

public class ConsultationLoaderActivity extends AppCompatActivity {

    private final Handler handler = new Handler();
    private ConsultationRecord consultationRecord;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consultation_loader);

        // Initialize Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        // Fetch parcelable record from intent
        Bundle data = getIntent().getExtras();
        consultationRecord = data.getParcelable("consultationRecord");

        // Splash activity handler
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                // This method will be executed once the timer is over
                Intent intent = new Intent(ConsultationLoaderActivity.this, ConsultationDoctorActivity.class);
                intent.putExtra("consultationRecord", consultationRecord);
                startActivity(intent);

                // close this activity
                finish();

            }
        }, StaticConstants.SPLASH_SCREEN_TIMEOUT);
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
}
