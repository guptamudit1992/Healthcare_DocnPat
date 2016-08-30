package com.careons.app.Patient.Activity.Consultation;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.careons.app.Patient.Adapters.Adapter;
import com.careons.app.Patient.Commons.StaticConstants;
import com.careons.app.Patient.Database.Models.Chat.ConsultationRecord;
import com.careons.app.Patient.Database.Models.Consultation.Consultation;
import com.careons.app.R;
import com.careons.app.databinding.ListItemSymptomsBinding;

import java.util.ArrayList;

public class ConsultationProblemActivity extends AppCompatActivity implements View.OnClickListener {

    private RecyclerView problemRecyclerView;
    private CardView proceedCardView;

    private Adapter<Consultation, ListItemSymptomsBinding> listItemSymptomsBindingAdapter;
    private ArrayList<Consultation> consultationArrayList = new ArrayList<>();
    private static ArrayList<String> selectedConsultationArrayList = new ArrayList<>();
    private ConsultationRecord consultationRecord;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consultation_problem);

        // Initialize Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        // Initialize fields
        problemRecyclerView = (RecyclerView) findViewById(R.id.problem_recyclerview);
        proceedCardView = (CardView) findViewById(R.id.proceed);
        proceedCardView.setOnClickListener(this);


        // Fetch parcelable record from intent
        Bundle data = getIntent().getExtras();
        consultationRecord = (ConsultationRecord) data.getParcelable("consultationRecord");

        /**
         * Initialize and Populate Recyclerview
         */
        populateSymptomsList();
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
                Intent intent = new Intent(this, ConsultationDescriptionActivity.class);
                intent.putExtra("symptoms", selectedConsultationArrayList);
                intent.putExtra("consultationRecord", consultationRecord);
                startActivity(intent);
                break;
        }
    }


    /**
     * Function to populate symptoms list
     */
    public void populateSymptomsList() {

        // Clear array list before population
        consultationArrayList.clear();
        selectedConsultationArrayList.clear();


        // Populate List
        consultationArrayList.add(new Consultation("Fever"));
        consultationArrayList.add(new Consultation("Weight Loss / Gain"));
        consultationArrayList.add(new Consultation("Difficulty Sleeping"));
        consultationArrayList.add(new Consultation("Loss of Appetite"));
        consultationArrayList.add(new Consultation("Mood Changes"));
        consultationArrayList.add(new Consultation("Fatigue / Weakness"));
        consultationArrayList.add(new Consultation("Hospitalized (past six months)"));

        consultationArrayList.add(new Consultation("Headache"));
        consultationArrayList.add(new Consultation("Dizzy / Light head"));
        consultationArrayList.add(new Consultation("Vision Changes"));
        consultationArrayList.add(new Consultation("Hearing Loss / Ringing"));
        consultationArrayList.add(new Consultation("Ear Drainage"));
        consultationArrayList.add(new Consultation("Nasal Discharge"));
        consultationArrayList.add(new Consultation("Congestion / Sinus Problem"));
        consultationArrayList.add(new Consultation("Sore Throat"));
        consultationArrayList.add(new Consultation("Allergies"));
        consultationArrayList.add(new Consultation("Numbness / Tingling"));
        consultationArrayList.add(new Consultation("History of fainting / Siezzure"));
        consultationArrayList.add(new Consultation("Memory Loss"));
        consultationArrayList.add(new Consultation("History of Stroke"));
        consultationArrayList.add(new Consultation("History of Falls"));



        // Set adapter
        listItemSymptomsBindingAdapter =
                new Adapter<>(consultationArrayList, R.layout.list_item_symptoms,
                        StaticConstants.SYMPTOMS_LIST_ADAPTER);

        problemRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        problemRecyclerView.setAdapter(listItemSymptomsBindingAdapter);
    }


    /**
     * Function to select symptoms
     */
    public static void selectSymptoms(ArrayList<String> symptomsList) {

        selectedConsultationArrayList = symptomsList;
    }
}
