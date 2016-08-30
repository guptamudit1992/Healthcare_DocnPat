package com.careons.app.Patient.Activity.Consultation;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.careons.app.Patient.Adapters.Adapter;
import com.careons.app.Patient.Commons.StaticConstants;
import com.careons.app.Patient.Database.Models.Chat.ConsultationRecord;
import com.careons.app.Patient.Database.Models.Consultation.Consultation;
import com.careons.app.R;
import com.careons.app.Shared.Utils.Utils;
import com.careons.app.databinding.ListItemSymptomsSelectedBinding;

import java.util.ArrayList;

public class ConsultationDescriptionActivity extends AppCompatActivity implements View.OnClickListener {

    private View root;
    private RecyclerView symtomsRecyclerView;
    private CardView proceedCardView;
    private EditText descriptionEditText;
    private TextView symptomsTextView;
    private LinearLayout ctaLayout;

    private Adapter<Consultation, ListItemSymptomsSelectedBinding> listItemSymptomsBindingAdapter;
    private ArrayList<Consultation> consultationArrayList = new ArrayList<>();
    private ArrayList<String> symptomsList = new ArrayList<>();
    private ConsultationRecord record;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consultation_description);

        // Initialize Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        // Initialize fields
        symtomsRecyclerView = (RecyclerView) findViewById(R.id.symptoms_recyclerview);
        symptomsTextView = (TextView) findViewById(R.id.symptoms_header);
        descriptionEditText = (EditText) findViewById(R.id.description);
        ctaLayout = (LinearLayout) findViewById(R.id.cta_layout);
        proceedCardView = (CardView) findViewById(R.id.proceed);
        proceedCardView.setOnClickListener(this);


        // Fetch symptoms list from intent
        Intent intent = getIntent();
        symptomsList = intent.getStringArrayListExtra("symptoms");
        if (!(symptomsList.size() > 0)) {
            symptomsTextView.setVisibility(View.GONE);
        }


        // Fetch parcelable record from intent
        Bundle data = getIntent().getExtras();
        record = (ConsultationRecord) data.getParcelable("consultationRecord");


        /**
         * Handle Keyboard
         */
        root = findViewById(R.id.root);
        root.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {

                Utils.toggleCTAKeyboard(root, ctaLayout);
            }
        });


        /**
         * Populate symptoms selected
         */
        populateSymptomsSelected();
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

                /**
                 * Create consultation record object
                 */
                ConsultationRecord consultationRecord = new ConsultationRecord(
                        record.getName(), record.getAge(), record.getGender(),
                        symptomsList,
                        descriptionEditText.getText().toString());


                Intent intent = new Intent(this, ConsultationLoaderActivity.class);
                intent.putExtra("consultationRecord", consultationRecord);
                startActivity(intent);
                break;
        }
    }


    /**
     * Function to populate symptoms selected
     */
    public void populateSymptomsSelected() {

        // Clear array list before population
        consultationArrayList.clear();

        // Populate Array
        for(int i = 0; i < symptomsList.size(); i++) {

            consultationArrayList.add(new Consultation(symptomsList.get(i)));
        }


        // Set adapter
        listItemSymptomsBindingAdapter =
                new Adapter<>(consultationArrayList, R.layout.list_item_symptoms_selected,
                        StaticConstants.SELECTED_SYMPTOMS_LIST_ADAPTER);

        symtomsRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL));
        symtomsRecyclerView.setAdapter(listItemSymptomsBindingAdapter);
    }

}
