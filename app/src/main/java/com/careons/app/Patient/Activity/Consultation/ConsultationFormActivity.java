package com.careons.app.Patient.Activity.Consultation;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.careons.app.Patient.Database.Models.Chat.ConsultationRecord;
import com.careons.app.R;

public class ConsultationFormActivity extends AppCompatActivity implements View.OnClickListener {

    private CardView proceedCardView;
    private EditText nameEditText, ageEditText;
    private TextView maleTextView, femaleTextView;

    private String name, age;
    private String gender = "Male";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consultation_form);

        // Initialize Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        // Initialize fields
        nameEditText = (EditText) findViewById(R.id.name);
        ageEditText = (EditText) findViewById(R.id.age);
        maleTextView = (TextView) findViewById(R.id.male);
        femaleTextView = (TextView) findViewById(R.id.female);
        proceedCardView = (CardView) findViewById(R.id.proceed);

        // Attach click listeners
        proceedCardView.setOnClickListener(this);
        maleTextView.setOnClickListener(this);
        femaleTextView.setOnClickListener(this);



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
                if(nameEditText.getText().toString().isEmpty()
                        || ageEditText.getText().toString().isEmpty()) {

                    Toast.makeText(getApplicationContext(), "Form Incomplete", Toast.LENGTH_LONG).show();

                } else {


                    /**
                     * Create consultation record object
                     */
                    ConsultationRecord consultationRecord = new ConsultationRecord(
                            nameEditText.getText().toString(),
                            ageEditText.getText().toString(),
                            gender,
                            null, null);

                    Intent intent = new Intent(this, ConsultationProblemActivity.class);
                    intent.putExtra("consultationRecord", consultationRecord);
                    startActivity(intent);
                }
                break;

            case R.id.male:
                maleTextView.setBackgroundColor(getResources().getColor(R.color.light_grey));
                femaleTextView.setBackgroundColor(getResources().getColor(R.color.white));
                gender = "Male";
                break;

            case R.id.female:
                femaleTextView.setBackgroundColor(getResources().getColor(R.color.light_grey));
                maleTextView.setBackgroundColor(getResources().getColor(R.color.white));
                gender = "Female";
                break;
        }
    }
}
