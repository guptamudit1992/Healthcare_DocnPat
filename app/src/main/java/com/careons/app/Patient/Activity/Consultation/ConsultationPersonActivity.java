package com.careons.app.Patient.Activity.Consultation;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.careons.app.Patient.Commons.StringConstants;
import com.careons.app.Patient.Database.Models.Chat.ConsultationRecord;
import com.careons.app.Patient.Services.SharedPreferenceService;
import com.careons.app.R;
import com.careons.app.Shared.Utils.DateTimeUtils;

public class ConsultationPersonActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView selfDetailsTextView;
    private CheckBox selfCheckbox, someoneElseCheckbox;
    private CardView proceedCardView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consultation_person);

        // Initialize Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        // Initialize fields
        selfCheckbox = (CheckBox) findViewById(R.id.self);
        someoneElseCheckbox = (CheckBox) findViewById(R.id.someone_else);
        selfDetailsTextView = (TextView) findViewById(R.id.self_details);
        proceedCardView = (CardView) findViewById(R.id.proceed);
        proceedCardView.setOnClickListener(this);

        // Substitute values
        selfDetailsTextView.setText(
                        "("
                        .concat(SharedPreferenceService.getValue(getApplicationContext(), StringConstants.KEY_USERNAME))
                        .concat(", ")
                        .concat(DateTimeUtils.calculateAgeFromDOB(DateTimeUtils.convertDateLoginToTimestamp(
                                SharedPreferenceService.getValue(getApplicationContext(), StringConstants.KEY_DOB)))
                                .concat(" ")
                                .concat(getString(R.string.years)))
                        .concat(", ")
                        .concat(SharedPreferenceService.getValue(getApplicationContext(), StringConstants.KEY_GENDER))
                        .concat(")"));


        // Set on checked status change listener
        selfCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(isChecked) {

                    someoneElseCheckbox.setChecked(false);
                }
            }
        });

        someoneElseCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(isChecked) {

                    selfCheckbox.setChecked(false);
                }
            }
        });
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.proceed:
                if(selfCheckbox.isChecked()) {

                    /**
                     * Create consultation record object
                     */
                    ConsultationRecord consultationRecord = new ConsultationRecord(
                            SharedPreferenceService.getValue(getApplicationContext(), StringConstants.KEY_USERNAME),
                            DateTimeUtils.calculateAgeFromDOB(DateTimeUtils.convertDateLoginToTimestamp(
                                    SharedPreferenceService.getValue(getApplicationContext(), StringConstants.KEY_DOB))),
                            SharedPreferenceService.getValue(getApplicationContext(), StringConstants.KEY_GENDER),
                                    null, null);

                    // Navigate to Consultation Problems
                    Intent intent = new Intent(this, ConsultationProblemActivity.class);
                    intent.putExtra("consultationRecord", consultationRecord);
                    startActivity(intent);

                } else {

                    // Navigate to Consultation Problems
                    Intent intent = new Intent(this, ConsultationFormActivity.class);
                    startActivity(intent);
                }

                break;
        }
    }
}
