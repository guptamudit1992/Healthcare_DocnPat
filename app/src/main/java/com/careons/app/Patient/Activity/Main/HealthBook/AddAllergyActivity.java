package com.careons.app.Patient.Activity.Main.HealthBook;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.careons.app.Patient.Adapters.AddMedicalProblemSearchAdapter;
import com.careons.app.Patient.Commons.StaticConstants;
import com.careons.app.Patient.Commons.StringConstants;
import com.careons.app.Patient.Database.Models.Healthbook.Allergy;
import com.careons.app.Patient.Fragments.HealthBook.AllergyFragment;
import com.careons.app.R;
import com.careons.app.Patient.Services.SharedPreferenceService;
import com.careons.app.Shared.Utils.DateTimeUtils;
import com.careons.app.Shared.Utils.Validation;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class AddAllergyActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    private static Allergy allergy;
    private static List<Allergy> allergyList;
    private RadioGroup mRadioGroup;
    private AddMedicalProblemSearchAdapter mFoodAdapter, mEnvironmentAdapter, mMedicationAdapter;
    private ListView mAllergyListView;
    private String mAllergyTypeName = "Food";
    private String mAllergyId = "";
    // Initialize food List

    private ArrayList<HashMap<String, String>> mFoodArrayList;
    private ArrayList<HashMap<String, String>> mEnvironmentArrayList;
    private ArrayList<HashMap<String, String>> mMedicationArrayList;

    private LinearLayout displayViewLinearLayout, addLinearLayout;
    private TextView problemNameTextView, problemHyperlinkTextView, allergyTypeTextView, dateTextView, commentsTextView;
    private Spinner kindOfReactionSpinner;

    // Create an ArrayAdapter using the string array and a spinner layout
    ArrayAdapter<CharSequence> adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_allergy);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        toolbar.setTitle(getString(R.string.add_allergy_history));
        setSupportActionBar(toolbar);

        // Status Bar Color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.darker_al));
        }

        // Back enabled
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        addLinearLayout = (LinearLayout) findViewById(R.id.addLinearLayout);
        displayViewLinearLayout = (LinearLayout) findViewById(R.id.dispViewLinearLayout);
        problemNameTextView = (TextView) findViewById(R.id.allergyNameTextView);
        problemHyperlinkTextView = (TextView) findViewById(R.id.problem_hyperlink);
        allergyTypeTextView = (TextView) findViewById(R.id.allergyTypeTextView);

        kindOfReactionSpinner = (Spinner) findViewById(R.id.kindOfReaction_spinner);

        dateTextView = (TextView) findViewById(R.id.date_textview);
        commentsTextView = (TextView) findViewById(R.id.commentsTextView);
        dateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDatePicker();
            }
        });


        mRadioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {

                    case R.id.radioButton1:
                        mAllergyListView.setAdapter(mFoodAdapter);
                        mFoodAdapter.notifyDataSetChanged();
                        mAllergyTypeName = getString(R.string.allergy_food);
                        break;

                    case R.id.radioButton2:
                        mAllergyListView.setAdapter(mEnvironmentAdapter);
                        mEnvironmentAdapter.notifyDataSetChanged();
                        mAllergyTypeName = getString(R.string.allergy_environment);
                        break;

                    case R.id.radioButton3:
                        mAllergyListView.setAdapter(mMedicationAdapter);
                        mMedicationAdapter.notifyDataSetChanged();
                        mAllergyTypeName = getString(R.string.allergy_medication);
                        break;
                }
            }
        });


        initializeData();


        /**
         * Bind adapter to the result
         */
        mFoodAdapter = new AddMedicalProblemSearchAdapter(this, mFoodArrayList, 0);

        mEnvironmentAdapter = new AddMedicalProblemSearchAdapter(this, mEnvironmentArrayList, 0);

        mMedicationAdapter = new AddMedicalProblemSearchAdapter(this, mMedicationArrayList, 0);

        mAllergyListView = (ListView) findViewById(R.id.listViewAddAllergy);
        mAllergyListView.setAdapter(mFoodAdapter);

        // Handle Search list click listener
        mAllergyListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                // Shut off keypad
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

                displayViewLinearLayout.setVisibility(View.GONE);
                addLinearLayout.setVisibility(View.VISIBLE);

                if (mAllergyTypeName.equals(getString(R.string.allergy_food))) {

                    allergyTypeTextView.setText(getString(R.string.allergy_food));
                    problemNameTextView.setText(mFoodArrayList.get(position).get("value"));
                    problemHyperlinkTextView.setText(mFoodArrayList.get(position).get(StringConstants.KEY_SEARCH_LIST_HYPERLINK));
                    mAllergyId = mFoodArrayList.get(position).get("id").toString();

                } else if (mAllergyTypeName.equals(getString(R.string.allergy_environment))) {

                    allergyTypeTextView.setText(getString(R.string.allergy_environment));
                    problemNameTextView.setText(mEnvironmentArrayList.get(position).get("value"));
                    problemHyperlinkTextView.setText(mEnvironmentArrayList.get(position).get(StringConstants.KEY_SEARCH_LIST_HYPERLINK));
                    mAllergyId = mEnvironmentArrayList.get(position).get("id").toString();

                } else if (mAllergyTypeName.equals(getString(R.string.allergy_medication))) {

                    allergyTypeTextView.setText(getString(R.string.allergy_medication));
                    problemNameTextView.setText(mMedicationArrayList.get(position).get("value"));
                    problemHyperlinkTextView.setText(mMedicationArrayList.get(position).get(StringConstants.KEY_SEARCH_LIST_HYPERLINK));
                    mAllergyId = mMedicationArrayList.get(position).get("id").toString();
                }

                /**
                 * Check if already exists in local database
                 */
                allergyList = Allergy.find(Allergy.class, "patient_id = ? and allergy_id = ?",
                        SharedPreferenceService.getValue(getApplicationContext(), StringConstants.KEY_PATIENT_ID),
                        mAllergyId);

                if(allergyList.size() > 0) {

                    // Populate data fields
                    allergy = allergyList.get(0);

                    dateTextView.setText(allergy.getStartDate());
                    commentsTextView.setText(allergy.getComment());

                    int selectionPosition= adapter.getPosition(allergy.getReaction());
                    kindOfReactionSpinner.setSelection(selectionPosition);
                }

            }
        });

        // On Focus change listener on start date
        dateTextView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    Validation.validateDate(dateTextView, getApplicationContext());
                }
            }
        });


        //Bind Spinner
        bindSpinner();
    }









    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                finish();
        }

        return super.onOptionsItemSelected(item);
    }


    /**
     * Initialize Data
     */
    private void initializeData() {

        /**
         * Food
         */
        mFoodArrayList = new ArrayList<>();
        HashMap<String, String> hashMapac = new HashMap<String, String>();
        hashMapac.put("id", "1");
        hashMapac.put("value", getResources().getString(R.string.allergy_food_1));
        hashMapac.put(StringConstants.KEY_SEARCH_LIST_HYPERLINK, getResources().getString(R.string.allergy_food_url));
        mFoodArrayList.add(hashMapac);

        hashMapac = new HashMap<String, String>();
        hashMapac.put("id", "2");
        hashMapac.put("value", getResources().getString(R.string.allergy_food_2));
        hashMapac.put(StringConstants.KEY_SEARCH_LIST_HYPERLINK, getResources().getString(R.string.allergy_food_url));
        mFoodArrayList.add(hashMapac);

        hashMapac = new HashMap<String, String>();
        hashMapac.put("id", "3");
        hashMapac.put("value", getResources().getString(R.string.allergy_food_3));
        hashMapac.put(StringConstants.KEY_SEARCH_LIST_HYPERLINK, getResources().getString(R.string.allergy_food_url));
        mFoodArrayList.add(hashMapac);

        hashMapac = new HashMap<String, String>();
        hashMapac.put("id", "4");
        hashMapac.put("value", getResources().getString(R.string.allergy_food_4));
        hashMapac.put(StringConstants.KEY_SEARCH_LIST_HYPERLINK, getResources().getString(R.string.allergy_food_url));
        mFoodArrayList.add(hashMapac);

        hashMapac = new HashMap<String, String>();
        hashMapac.put("id", "5");
        hashMapac.put("value", getResources().getString(R.string.allergy_food_5));
        hashMapac.put(StringConstants.KEY_SEARCH_LIST_HYPERLINK, getResources().getString(R.string.allergy_food_url));
        mFoodArrayList.add(hashMapac);

        hashMapac = new HashMap<String, String>();
        hashMapac.put("id", "6");
        hashMapac.put("value", getResources().getString(R.string.allergy_food_6));
        hashMapac.put(StringConstants.KEY_SEARCH_LIST_HYPERLINK, getResources().getString(R.string.allergy_food_url));
        mFoodArrayList.add(hashMapac);

        hashMapac = new HashMap<String, String>();
        hashMapac.put("id", "7");
        hashMapac.put("value", getResources().getString(R.string.allergy_food_7));
        hashMapac.put(StringConstants.KEY_SEARCH_LIST_HYPERLINK, getResources().getString(R.string.allergy_food_url));
        mFoodArrayList.add(hashMapac);

        hashMapac = new HashMap<String, String>();
        hashMapac.put("id", "8");
        hashMapac.put("value", getResources().getString(R.string.allergy_food_8));
        hashMapac.put(StringConstants.KEY_SEARCH_LIST_HYPERLINK, getResources().getString(R.string.allergy_food_url));
        mFoodArrayList.add(hashMapac);

        hashMapac = new HashMap<String, String>();
        hashMapac.put("id", "9");
        hashMapac.put("value", getResources().getString(R.string.allergy_food_9));
        hashMapac.put(StringConstants.KEY_SEARCH_LIST_HYPERLINK, getResources().getString(R.string.allergy_food_url));
        mFoodArrayList.add(hashMapac);

        hashMapac = new HashMap<String, String>();
        hashMapac.put("id", "10");
        hashMapac.put("value", getResources().getString(R.string.allergy_food_10));
        hashMapac.put(StringConstants.KEY_SEARCH_LIST_HYPERLINK, getResources().getString(R.string.allergy_food_url));
        mFoodArrayList.add(hashMapac);

        hashMapac = new HashMap<String, String>();
        hashMapac.put("id", "11");
        hashMapac.put("value", getResources().getString(R.string.allergy_food_11));
        hashMapac.put(StringConstants.KEY_SEARCH_LIST_HYPERLINK, getResources().getString(R.string.allergy_food_url));
        mFoodArrayList.add(hashMapac);

        hashMapac = new HashMap<String, String>();
        hashMapac.put("id", "12");
        hashMapac.put("value", getResources().getString(R.string.allergy_food_12));
        hashMapac.put(StringConstants.KEY_SEARCH_LIST_HYPERLINK, getResources().getString(R.string.allergy_food_url));
        mFoodArrayList.add(hashMapac);

        hashMapac = new HashMap<String, String>();
        hashMapac.put("id", "13");
        hashMapac.put("value", getResources().getString(R.string.allergy_food_13));
        hashMapac.put(StringConstants.KEY_SEARCH_LIST_HYPERLINK, getResources().getString(R.string.allergy_food_url));
        mFoodArrayList.add(hashMapac);

        hashMapac = new HashMap<String, String>();
        hashMapac.put("id", "14");
        hashMapac.put("value", getResources().getString(R.string.allergy_food_14));
        hashMapac.put(StringConstants.KEY_SEARCH_LIST_HYPERLINK, getResources().getString(R.string.allergy_food_url));
        mFoodArrayList.add(hashMapac);

        hashMapac = new HashMap<String, String>();
        hashMapac.put("id", "15");
        hashMapac.put("value", getResources().getString(R.string.allergy_food_15));
        hashMapac.put(StringConstants.KEY_SEARCH_LIST_HYPERLINK, getResources().getString(R.string.allergy_food_url));
        mFoodArrayList.add(hashMapac);

        hashMapac = new HashMap<String, String>();
        hashMapac.put("id", "16");
        hashMapac.put("value", getResources().getString(R.string.allergy_food_16));
        hashMapac.put(StringConstants.KEY_SEARCH_LIST_HYPERLINK, getResources().getString(R.string.allergy_food_url));
        mFoodArrayList.add(hashMapac);

        hashMapac = new HashMap<String, String>();
        hashMapac.put("id", "17");
        hashMapac.put("value", getResources().getString(R.string.allergy_food_17));
        hashMapac.put(StringConstants.KEY_SEARCH_LIST_HYPERLINK, getResources().getString(R.string.allergy_food_url));
        mFoodArrayList.add(hashMapac);

        hashMapac = new HashMap<String, String>();
        hashMapac.put("id", "18");
        hashMapac.put("value", getResources().getString(R.string.allergy_food_18));
        hashMapac.put(StringConstants.KEY_SEARCH_LIST_HYPERLINK, getResources().getString(R.string.allergy_food_url));
        mFoodArrayList.add(hashMapac);

        hashMapac = new HashMap<String, String>();
        hashMapac.put("id", "19");
        hashMapac.put("value", getResources().getString(R.string.allergy_food_19));
        hashMapac.put(StringConstants.KEY_SEARCH_LIST_HYPERLINK, getResources().getString(R.string.allergy_food_url));
        mFoodArrayList.add(hashMapac);

        hashMapac = new HashMap<String, String>();
        hashMapac.put("id", "20");
        hashMapac.put("value", getResources().getString(R.string.allergy_food_20));
        hashMapac.put(StringConstants.KEY_SEARCH_LIST_HYPERLINK, getResources().getString(R.string.allergy_food_url));
        mFoodArrayList.add(hashMapac);

        hashMapac = new HashMap<String, String>();
        hashMapac.put("id", "21");
        hashMapac.put("value", getResources().getString(R.string.allergy_food_21));
        hashMapac.put(StringConstants.KEY_SEARCH_LIST_HYPERLINK, getResources().getString(R.string.allergy_food_url));
        mFoodArrayList.add(hashMapac);

        hashMapac = new HashMap<String, String>();
        hashMapac.put("id", "22");
        hashMapac.put("value", getResources().getString(R.string.allergy_food_22));
        hashMapac.put(StringConstants.KEY_SEARCH_LIST_HYPERLINK, getResources().getString(R.string.allergy_food_url));
        mFoodArrayList.add(hashMapac);

        hashMapac = new HashMap<String, String>();
        hashMapac.put("id", "23");
        hashMapac.put("value", getResources().getString(R.string.allergy_food_23));
        hashMapac.put(StringConstants.KEY_SEARCH_LIST_HYPERLINK, getResources().getString(R.string.allergy_food_url));
        mFoodArrayList.add(hashMapac);

        hashMapac = new HashMap<String, String>();
        hashMapac.put("id", "24");
        hashMapac.put("value", getResources().getString(R.string.allergy_food_24));
        hashMapac.put(StringConstants.KEY_SEARCH_LIST_HYPERLINK, getResources().getString(R.string.allergy_food_url));
        mFoodArrayList.add(hashMapac);

        hashMapac = new HashMap<String, String>();
        hashMapac.put("id", "25");
        hashMapac.put("value", getResources().getString(R.string.allergy_food_25));
        hashMapac.put(StringConstants.KEY_SEARCH_LIST_HYPERLINK, getResources().getString(R.string.allergy_food_url));
        mFoodArrayList.add(hashMapac);

        hashMapac = new HashMap<String, String>();
        hashMapac.put("id", "26");
        hashMapac.put("value", getResources().getString(R.string.allergy_food_26));
        hashMapac.put(StringConstants.KEY_SEARCH_LIST_HYPERLINK, getResources().getString(R.string.allergy_food_url));
        mFoodArrayList.add(hashMapac);

        hashMapac = new HashMap<String, String>();
        hashMapac.put("id", "27");
        hashMapac.put("value", getResources().getString(R.string.allergy_food_27));
        hashMapac.put(StringConstants.KEY_SEARCH_LIST_HYPERLINK, getResources().getString(R.string.allergy_food_url));
        mFoodArrayList.add(hashMapac);

        hashMapac = new HashMap<String, String>();
        hashMapac.put("id", "28");
        hashMapac.put("value", getResources().getString(R.string.allergy_food_28));
        hashMapac.put(StringConstants.KEY_SEARCH_LIST_HYPERLINK, getResources().getString(R.string.allergy_food_url));
        mFoodArrayList.add(hashMapac);

        hashMapac = new HashMap<String, String>();
        hashMapac.put("id", "29");
        hashMapac.put("value", getResources().getString(R.string.allergy_food_29));
        hashMapac.put(StringConstants.KEY_SEARCH_LIST_HYPERLINK, getResources().getString(R.string.allergy_food_url));
        mFoodArrayList.add(hashMapac);

        hashMapac = new HashMap<String, String>();
        hashMapac.put("id", "30");
        hashMapac.put("value", getResources().getString(R.string.allergy_food_30));
        hashMapac.put(StringConstants.KEY_SEARCH_LIST_HYPERLINK, getResources().getString(R.string.allergy_food_url));
        mFoodArrayList.add(hashMapac);

        hashMapac = new HashMap<String, String>();
        hashMapac.put("id", "31");
        hashMapac.put("value", getResources().getString(R.string.allergy_food_31));
        hashMapac.put(StringConstants.KEY_SEARCH_LIST_HYPERLINK, getResources().getString(R.string.allergy_food_url));
        mFoodArrayList.add(hashMapac);

        hashMapac = new HashMap<String, String>();
        hashMapac.put("id", "32");
        hashMapac.put("value", getResources().getString(R.string.allergy_food_32));
        hashMapac.put(StringConstants.KEY_SEARCH_LIST_HYPERLINK, getResources().getString(R.string.allergy_food_url));
        mFoodArrayList.add(hashMapac);

        hashMapac = new HashMap<String, String>();
        hashMapac.put("id", "33");
        hashMapac.put("value", getResources().getString(R.string.allergy_food_33));
        hashMapac.put(StringConstants.KEY_SEARCH_LIST_HYPERLINK, getResources().getString(R.string.allergy_food_url));
        mFoodArrayList.add(hashMapac);

        hashMapac = new HashMap<String, String>();
        hashMapac.put("id", "34");
        hashMapac.put("value", getResources().getString(R.string.allergy_food_34));
        hashMapac.put(StringConstants.KEY_SEARCH_LIST_HYPERLINK, getResources().getString(R.string.allergy_food_url));
        mFoodArrayList.add(hashMapac);

        hashMapac = new HashMap<String, String>();
        hashMapac.put("id", "35");
        hashMapac.put("value", getResources().getString(R.string.allergy_food_35));
        hashMapac.put(StringConstants.KEY_SEARCH_LIST_HYPERLINK, getResources().getString(R.string.allergy_food_url));
        mFoodArrayList.add(hashMapac);

        hashMapac = new HashMap<String, String>();
        hashMapac.put("id", "36");
        hashMapac.put("value", getResources().getString(R.string.allergy_food_36));
        hashMapac.put(StringConstants.KEY_SEARCH_LIST_HYPERLINK, getResources().getString(R.string.allergy_food_url));
        mFoodArrayList.add(hashMapac);

        hashMapac = new HashMap<String, String>();
        hashMapac.put("id", "37");
        hashMapac.put("value", getResources().getString(R.string.allergy_food_37));
        hashMapac.put(StringConstants.KEY_SEARCH_LIST_HYPERLINK, getResources().getString(R.string.allergy_food_url));
        mFoodArrayList.add(hashMapac);

        hashMapac = new HashMap<String, String>();
        hashMapac.put("id", "38");
        hashMapac.put("value", getResources().getString(R.string.allergy_food_38));
        hashMapac.put(StringConstants.KEY_SEARCH_LIST_HYPERLINK, getResources().getString(R.string.allergy_food_url));
        mFoodArrayList.add(hashMapac);

        hashMapac = new HashMap<String, String>();
        hashMapac.put("id", "39");
        hashMapac.put("value", getResources().getString(R.string.allergy_food_39));
        hashMapac.put(StringConstants.KEY_SEARCH_LIST_HYPERLINK, getResources().getString(R.string.allergy_food_url));
        mFoodArrayList.add(hashMapac);

        hashMapac = new HashMap<String, String>();
        hashMapac.put("id", "40");
        hashMapac.put("value", getResources().getString(R.string.allergy_food_40));
        hashMapac.put(StringConstants.KEY_SEARCH_LIST_HYPERLINK, getResources().getString(R.string.allergy_food_url));
        mFoodArrayList.add(hashMapac);

        hashMapac = new HashMap<String, String>();
        hashMapac.put("id", "41");
        hashMapac.put("value", getResources().getString(R.string.allergy_food_41));
        hashMapac.put(StringConstants.KEY_SEARCH_LIST_HYPERLINK, getResources().getString(R.string.allergy_food_url));
        mFoodArrayList.add(hashMapac);

        hashMapac = new HashMap<String, String>();
        hashMapac.put("id", "42");
        hashMapac.put("value", getResources().getString(R.string.allergy_food_42));
        hashMapac.put(StringConstants.KEY_SEARCH_LIST_HYPERLINK, getResources().getString(R.string.allergy_food_url));
        mFoodArrayList.add(hashMapac);

        hashMapac = new HashMap<String, String>();
        hashMapac.put("id", "43");
        hashMapac.put("value", getResources().getString(R.string.allergy_food_43));
        hashMapac.put(StringConstants.KEY_SEARCH_LIST_HYPERLINK, getResources().getString(R.string.allergy_food_url));
        mFoodArrayList.add(hashMapac);

        hashMapac = new HashMap<String, String>();
        hashMapac.put("id", "44");
        hashMapac.put("value", getResources().getString(R.string.allergy_food_44));
        hashMapac.put(StringConstants.KEY_SEARCH_LIST_HYPERLINK, getResources().getString(R.string.allergy_food_url));
        mFoodArrayList.add(hashMapac);

        hashMapac = new HashMap<String, String>();
        hashMapac.put("id", "45");
        hashMapac.put("value", getResources().getString(R.string.allergy_food_45));
        hashMapac.put(StringConstants.KEY_SEARCH_LIST_HYPERLINK, getResources().getString(R.string.allergy_food_url));
        mFoodArrayList.add(hashMapac);

        hashMapac = new HashMap<String, String>();
        hashMapac.put("id", "46");
        hashMapac.put("value", getResources().getString(R.string.allergy_food_46));
        hashMapac.put(StringConstants.KEY_SEARCH_LIST_HYPERLINK, getResources().getString(R.string.allergy_food_url));
        mFoodArrayList.add(hashMapac);

        hashMapac = new HashMap<String, String>();
        hashMapac.put("id", "47");
        hashMapac.put("value", getResources().getString(R.string.allergy_food_47));
        hashMapac.put(StringConstants.KEY_SEARCH_LIST_HYPERLINK, getResources().getString(R.string.allergy_food_url));
        mFoodArrayList.add(hashMapac);

        hashMapac = new HashMap<String, String>();
        hashMapac.put("id", "48");
        hashMapac.put("value", getResources().getString(R.string.allergy_food_48));
        hashMapac.put(StringConstants.KEY_SEARCH_LIST_HYPERLINK, getResources().getString(R.string.allergy_food_url));
        mFoodArrayList.add(hashMapac);

        hashMapac = new HashMap<String, String>();
        hashMapac.put("id", "49");
        hashMapac.put("value", getResources().getString(R.string.allergy_food_49));
        hashMapac.put(StringConstants.KEY_SEARCH_LIST_HYPERLINK, getResources().getString(R.string.allergy_food_url));
        mFoodArrayList.add(hashMapac);

        hashMapac = new HashMap<String, String>();
        hashMapac.put("id", "50");
        hashMapac.put("value", getResources().getString(R.string.allergy_food_50));
        hashMapac.put(StringConstants.KEY_SEARCH_LIST_HYPERLINK, getResources().getString(R.string.allergy_food_url));
        mFoodArrayList.add(hashMapac);


        hashMapac = new HashMap<String, String>();
        hashMapac.put("id", "51");
        hashMapac.put("value", getResources().getString(R.string.allergy_food_51));
        hashMapac.put(StringConstants.KEY_SEARCH_LIST_HYPERLINK, getResources().getString(R.string.allergy_food_url));
        mFoodArrayList.add(hashMapac);

        hashMapac = new HashMap<String, String>();
        hashMapac.put("id", "52");
        hashMapac.put("value", getResources().getString(R.string.allergy_food_52));
        hashMapac.put(StringConstants.KEY_SEARCH_LIST_HYPERLINK, getResources().getString(R.string.allergy_food_url));
        mFoodArrayList.add(hashMapac);

        hashMapac = new HashMap<String, String>();
        hashMapac.put("id", "53");
        hashMapac.put("value", getResources().getString(R.string.allergy_food_53));
        hashMapac.put(StringConstants.KEY_SEARCH_LIST_HYPERLINK, getResources().getString(R.string.allergy_food_url));
        mFoodArrayList.add(hashMapac);

        hashMapac = new HashMap<String, String>();
        hashMapac.put("id", "54");
        hashMapac.put("value", getResources().getString(R.string.allergy_food_54));
        hashMapac.put(StringConstants.KEY_SEARCH_LIST_HYPERLINK, getResources().getString(R.string.allergy_food_url));
        mFoodArrayList.add(hashMapac);
        hashMapac = new HashMap<String, String>();

        hashMapac.put("id", "55");
        hashMapac.put("value", getResources().getString(R.string.allergy_food_55));
        hashMapac.put(StringConstants.KEY_SEARCH_LIST_HYPERLINK, getResources().getString(R.string.allergy_food_url));
        mFoodArrayList.add(hashMapac);

        hashMapac = new HashMap<String, String>();
        hashMapac.put("id", "56");
        hashMapac.put("value", getResources().getString(R.string.allergy_food_56));
        hashMapac.put(StringConstants.KEY_SEARCH_LIST_HYPERLINK, getResources().getString(R.string.allergy_food_url));
        mFoodArrayList.add(hashMapac);

        hashMapac = new HashMap<String, String>();
        hashMapac.put("id", "57");
        hashMapac.put("value", getResources().getString(R.string.allergy_food_57));
        hashMapac.put(StringConstants.KEY_SEARCH_LIST_HYPERLINK, getResources().getString(R.string.allergy_food_url));
        mFoodArrayList.add(hashMapac);

        hashMapac = new HashMap<String, String>();
        hashMapac.put("id", "58");
        hashMapac.put("value", getResources().getString(R.string.allergy_food_58));
        hashMapac.put(StringConstants.KEY_SEARCH_LIST_HYPERLINK, getResources().getString(R.string.allergy_food_url));
        mFoodArrayList.add(hashMapac);

        hashMapac = new HashMap<String, String>();
        hashMapac.put("id", "59");
        hashMapac.put("value", getResources().getString(R.string.allergy_food_59));
        hashMapac.put(StringConstants.KEY_SEARCH_LIST_HYPERLINK, getResources().getString(R.string.allergy_food_url));
        mFoodArrayList.add(hashMapac);

        hashMapac = new HashMap<String, String>();
        hashMapac.put("id", "60");
        hashMapac.put("value", getResources().getString(R.string.allergy_food_60));
        hashMapac.put(StringConstants.KEY_SEARCH_LIST_HYPERLINK, getResources().getString(R.string.allergy_food_url));
        mFoodArrayList.add(hashMapac);

        hashMapac = new HashMap<String, String>();
        hashMapac.put("id", "61");
        hashMapac.put("value", getResources().getString(R.string.allergy_food_61));
        hashMapac.put(StringConstants.KEY_SEARCH_LIST_HYPERLINK, getResources().getString(R.string.allergy_food_url));
        mFoodArrayList.add(hashMapac);

        hashMapac = new HashMap<String, String>();
        hashMapac.put("id", "62");
        hashMapac.put("value", getResources().getString(R.string.allergy_food_62));
        hashMapac.put(StringConstants.KEY_SEARCH_LIST_HYPERLINK, getResources().getString(R.string.allergy_food_url));
        mFoodArrayList.add(hashMapac);

        hashMapac = new HashMap<String, String>();
        hashMapac.put("id", "63");
        hashMapac.put("value", getResources().getString(R.string.allergy_food_63));
        hashMapac.put(StringConstants.KEY_SEARCH_LIST_HYPERLINK, getResources().getString(R.string.allergy_food_url));
        mFoodArrayList.add(hashMapac);

        hashMapac = new HashMap<String, String>();
        hashMapac.put("id", "64");
        hashMapac.put("value", getResources().getString(R.string.allergy_food_64));
        hashMapac.put(StringConstants.KEY_SEARCH_LIST_HYPERLINK, getResources().getString(R.string.allergy_food_url));
        mFoodArrayList.add(hashMapac);

        hashMapac = new HashMap<String, String>();
        hashMapac.put("id", "65");
        hashMapac.put("value", getResources().getString(R.string.allergy_food_65));
        hashMapac.put(StringConstants.KEY_SEARCH_LIST_HYPERLINK, getResources().getString(R.string.allergy_food_url));
        mFoodArrayList.add(hashMapac);

        hashMapac = new HashMap<String, String>();
        hashMapac.put("id", "66");
        hashMapac.put("value", getResources().getString(R.string.allergy_food_66));
        hashMapac.put(StringConstants.KEY_SEARCH_LIST_HYPERLINK, getResources().getString(R.string.allergy_food_url));
        mFoodArrayList.add(hashMapac);

        hashMapac = new HashMap<String, String>();
        hashMapac.put("id", "67");
        hashMapac.put("value", getResources().getString(R.string.allergy_food_67));
        hashMapac.put(StringConstants.KEY_SEARCH_LIST_HYPERLINK, getResources().getString(R.string.allergy_food_url));
        mFoodArrayList.add(hashMapac);

        hashMapac = new HashMap<String, String>();
        hashMapac.put("id", "68");
        hashMapac.put("value", getResources().getString(R.string.allergy_food_68));
        hashMapac.put(StringConstants.KEY_SEARCH_LIST_HYPERLINK, getResources().getString(R.string.allergy_food_url));
        mFoodArrayList.add(hashMapac);

        hashMapac = new HashMap<String, String>();
        hashMapac.put("id", "69");
        hashMapac.put("value", getResources().getString(R.string.allergy_food_69));
        hashMapac.put(StringConstants.KEY_SEARCH_LIST_HYPERLINK, getResources().getString(R.string.allergy_food_url));
        mFoodArrayList.add(hashMapac);

        hashMapac = new HashMap<String, String>();
        hashMapac.put("id", "70");
        hashMapac.put("value", getResources().getString(R.string.allergy_food_70));
        hashMapac.put(StringConstants.KEY_SEARCH_LIST_HYPERLINK, getResources().getString(R.string.allergy_food_url));
        mFoodArrayList.add(hashMapac);

        hashMapac = new HashMap<String, String>();
        hashMapac.put("id", "71");
        hashMapac.put("value", getResources().getString(R.string.allergy_food_71));
        hashMapac.put(StringConstants.KEY_SEARCH_LIST_HYPERLINK, getResources().getString(R.string.allergy_food_url));
        mFoodArrayList.add(hashMapac);

        hashMapac = new HashMap<String, String>();
        hashMapac.put("id", "72");
        hashMapac.put("value", getResources().getString(R.string.allergy_food_72));
        hashMapac.put(StringConstants.KEY_SEARCH_LIST_HYPERLINK, getResources().getString(R.string.allergy_food_url));
        mFoodArrayList.add(hashMapac);

        hashMapac = new HashMap<String, String>();
        hashMapac.put("id", "73");
        hashMapac.put("value", getResources().getString(R.string.allergy_food_73));
        hashMapac.put(StringConstants.KEY_SEARCH_LIST_HYPERLINK, getResources().getString(R.string.allergy_food_url));
        mFoodArrayList.add(hashMapac);

        hashMapac = new HashMap<String, String>();
        hashMapac.put("id", "74");
        hashMapac.put("value", getResources().getString(R.string.allergy_food_74));
        hashMapac.put(StringConstants.KEY_SEARCH_LIST_HYPERLINK, getResources().getString(R.string.allergy_food_url));
        mFoodArrayList.add(hashMapac);

        hashMapac = new HashMap<String, String>();
        hashMapac.put("id", "75");
        hashMapac.put("value", getResources().getString(R.string.allergy_food_75));
        hashMapac.put(StringConstants.KEY_SEARCH_LIST_HYPERLINK, getResources().getString(R.string.allergy_food_url));
        mFoodArrayList.add(hashMapac);

        hashMapac = new HashMap<String, String>();
        hashMapac.put("id", "76");
        hashMapac.put("value", getResources().getString(R.string.allergy_food_76));
        hashMapac.put(StringConstants.KEY_SEARCH_LIST_HYPERLINK, getResources().getString(R.string.allergy_food_url));
        mFoodArrayList.add(hashMapac);

        hashMapac = new HashMap<String, String>();
        hashMapac.put("id", "77");
        hashMapac.put("value", getResources().getString(R.string.allergy_food_77));
        hashMapac.put(StringConstants.KEY_SEARCH_LIST_HYPERLINK, getResources().getString(R.string.allergy_food_url));
        mFoodArrayList.add(hashMapac);

        hashMapac = new HashMap<String, String>();
        hashMapac.put("id", "78");
        hashMapac.put("value", getResources().getString(R.string.allergy_food_78));
        hashMapac.put(StringConstants.KEY_SEARCH_LIST_HYPERLINK, getResources().getString(R.string.allergy_food_url));
        mFoodArrayList.add(hashMapac);

        hashMapac = new HashMap<String, String>();
        hashMapac.put("id", "79");
        hashMapac.put("value", getResources().getString(R.string.allergy_food_79));
        hashMapac.put(StringConstants.KEY_SEARCH_LIST_HYPERLINK, getResources().getString(R.string.allergy_food_url));
        mFoodArrayList.add(hashMapac);

        hashMapac = new HashMap<String, String>();
        hashMapac.put("id", "80");
        hashMapac.put("value", getResources().getString(R.string.allergy_food_80));
        hashMapac.put(StringConstants.KEY_SEARCH_LIST_HYPERLINK, getResources().getString(R.string.allergy_food_url));
        mFoodArrayList.add(hashMapac);

        hashMapac = new HashMap<String, String>();
        hashMapac.put("id", "81");
        hashMapac.put("value", getResources().getString(R.string.allergy_food_81));
        hashMapac.put(StringConstants.KEY_SEARCH_LIST_HYPERLINK, getResources().getString(R.string.allergy_food_url));
        mFoodArrayList.add(hashMapac);

        hashMapac = new HashMap<String, String>();
        hashMapac.put("id", "82");
        hashMapac.put("value", getResources().getString(R.string.allergy_food_82));
        hashMapac.put(StringConstants.KEY_SEARCH_LIST_HYPERLINK, getResources().getString(R.string.allergy_food_url));
        mFoodArrayList.add(hashMapac);

        hashMapac = new HashMap<String, String>();
        hashMapac.put("id", "83");
        hashMapac.put("value", getResources().getString(R.string.allergy_food_83));
        hashMapac.put(StringConstants.KEY_SEARCH_LIST_HYPERLINK, getResources().getString(R.string.allergy_food_url));
        mFoodArrayList.add(hashMapac);

        hashMapac = new HashMap<String, String>();
        hashMapac.put("id", "84");
        hashMapac.put("value", getResources().getString(R.string.allergy_food_84));
        hashMapac.put(StringConstants.KEY_SEARCH_LIST_HYPERLINK, getResources().getString(R.string.allergy_food_url));
        mFoodArrayList.add(hashMapac);

        hashMapac = new HashMap<String, String>();
        hashMapac.put("id", "85");
        hashMapac.put("value", getResources().getString(R.string.allergy_food_85));
        hashMapac.put(StringConstants.KEY_SEARCH_LIST_HYPERLINK, getResources().getString(R.string.allergy_food_url));
        mFoodArrayList.add(hashMapac);

        hashMapac = new HashMap<String, String>();
        hashMapac.put("id", "86");
        hashMapac.put("value", getResources().getString(R.string.allergy_food_86));
        hashMapac.put(StringConstants.KEY_SEARCH_LIST_HYPERLINK, getResources().getString(R.string.allergy_food_url));
        mFoodArrayList.add(hashMapac);

        hashMapac = new HashMap<String, String>();
        hashMapac.put("id", "87");
        hashMapac.put("value", getResources().getString(R.string.allergy_food_87));
        hashMapac.put(StringConstants.KEY_SEARCH_LIST_HYPERLINK, getResources().getString(R.string.allergy_food_url));
        mFoodArrayList.add(hashMapac);

        hashMapac = new HashMap<String, String>();
        hashMapac.put("id", "88");
        hashMapac.put("value", getResources().getString(R.string.allergy_food_88));
        hashMapac.put(StringConstants.KEY_SEARCH_LIST_HYPERLINK, getResources().getString(R.string.allergy_food_url));
        mFoodArrayList.add(hashMapac);

        hashMapac = new HashMap<String, String>();
        hashMapac.put("id", "89");
        hashMapac.put("value", getResources().getString(R.string.allergy_food_89));
        hashMapac.put(StringConstants.KEY_SEARCH_LIST_HYPERLINK, getResources().getString(R.string.allergy_food_url));
        mFoodArrayList.add(hashMapac);

        hashMapac = new HashMap<String, String>();
        hashMapac.put("id", "90");
        hashMapac.put("value", getResources().getString(R.string.allergy_food_90));
        hashMapac.put(StringConstants.KEY_SEARCH_LIST_HYPERLINK, getResources().getString(R.string.allergy_food_url));
        mFoodArrayList.add(hashMapac);

        hashMapac = new HashMap<String, String>();
        hashMapac.put("id", "91");
        hashMapac.put("value", getResources().getString(R.string.allergy_food_91));
        hashMapac.put(StringConstants.KEY_SEARCH_LIST_HYPERLINK, getResources().getString(R.string.allergy_food_url));
        mFoodArrayList.add(hashMapac);

        hashMapac = new HashMap<String, String>();
        hashMapac.put("id", "92");
        hashMapac.put("value", getResources().getString(R.string.allergy_food_92));
        hashMapac.put(StringConstants.KEY_SEARCH_LIST_HYPERLINK, getResources().getString(R.string.allergy_food_url));
        mFoodArrayList.add(hashMapac);

        hashMapac = new HashMap<String, String>();
        hashMapac.put("id", "93");
        hashMapac.put("value", getResources().getString(R.string.allergy_food_93));
        hashMapac.put(StringConstants.KEY_SEARCH_LIST_HYPERLINK, getResources().getString(R.string.allergy_food_url));
        mFoodArrayList.add(hashMapac);

        hashMapac = new HashMap<String, String>();
        hashMapac.put("id", "94");
        hashMapac.put("value", getResources().getString(R.string.allergy_food_94));
        hashMapac.put(StringConstants.KEY_SEARCH_LIST_HYPERLINK, getResources().getString(R.string.allergy_food_url));
        mFoodArrayList.add(hashMapac);

        hashMapac = new HashMap<String, String>();
        hashMapac.put("id", "95");
        hashMapac.put("value", getResources().getString(R.string.allergy_food_95));
        hashMapac.put(StringConstants.KEY_SEARCH_LIST_HYPERLINK, getResources().getString(R.string.allergy_food_url));
        mFoodArrayList.add(hashMapac);

        hashMapac = new HashMap<String, String>();
        hashMapac.put("id", "96");
        hashMapac.put("value", getResources().getString(R.string.allergy_food_96));
        hashMapac.put(StringConstants.KEY_SEARCH_LIST_HYPERLINK, getResources().getString(R.string.allergy_food_url));
        mFoodArrayList.add(hashMapac);

        hashMapac = new HashMap<String, String>();
        hashMapac.put("id", "97");
        hashMapac.put("value", getResources().getString(R.string.allergy_food_97));
        hashMapac.put(StringConstants.KEY_SEARCH_LIST_HYPERLINK, getResources().getString(R.string.allergy_food_url));
        mFoodArrayList.add(hashMapac);

        hashMapac = new HashMap<String, String>();
        hashMapac.put("id", "98");
        hashMapac.put("value", getResources().getString(R.string.allergy_food_98));
        hashMapac.put(StringConstants.KEY_SEARCH_LIST_HYPERLINK, getResources().getString(R.string.allergy_food_url));
        mFoodArrayList.add(hashMapac);

        hashMapac = new HashMap<String, String>();
        hashMapac.put("id", "99");
        hashMapac.put("value", getResources().getString(R.string.allergy_food_99));
        hashMapac.put(StringConstants.KEY_SEARCH_LIST_HYPERLINK, getResources().getString(R.string.allergy_food_url));
        mFoodArrayList.add(hashMapac);

        hashMapac = new HashMap<String, String>();
        hashMapac.put("id", "127");
        hashMapac.put("value", getResources().getString(R.string.allergy_food_127));
        hashMapac.put(StringConstants.KEY_SEARCH_LIST_HYPERLINK, getResources().getString(R.string.allergy_food_url));
        mFoodArrayList.add(hashMapac);


        /**
         * Environment
         */
        mEnvironmentArrayList = new ArrayList<HashMap<String, String>>();
        hashMapac = new HashMap<String, String>();
        hashMapac.put("id", "109");
        hashMapac.put("value", getResources().getString(R.string.allergy_environment_109));
        hashMapac.put(StringConstants.KEY_SEARCH_LIST_HYPERLINK, getResources().getString(R.string.allergy_environment_url));
        mEnvironmentArrayList.add(hashMapac);

        hashMapac = new HashMap<String, String>();
        hashMapac.put("id", "110");
        hashMapac.put("value", getResources().getString(R.string.allergy_environment_110));
        hashMapac.put(StringConstants.KEY_SEARCH_LIST_HYPERLINK, getResources().getString(R.string.allergy_environment_url));
        mEnvironmentArrayList.add(hashMapac);

        hashMapac = new HashMap<String, String>();
        hashMapac.put("id", "111");
        hashMapac.put("value", getResources().getString(R.string.allergy_environment_111));
        hashMapac.put(StringConstants.KEY_SEARCH_LIST_HYPERLINK, getResources().getString(R.string.allergy_environment_url));
        mEnvironmentArrayList.add(hashMapac);

        hashMapac = new HashMap<String, String>();
        hashMapac.put("id", "112");
        hashMapac.put("value", getResources().getString(R.string.allergy_environment_112));
        hashMapac.put(StringConstants.KEY_SEARCH_LIST_HYPERLINK, getResources().getString(R.string.allergy_environment_url));
        mEnvironmentArrayList.add(hashMapac);

        hashMapac = new HashMap<String, String>();
        hashMapac.put("id", "113");
        hashMapac.put("value", getResources().getString(R.string.allergy_environment_113));
        hashMapac.put(StringConstants.KEY_SEARCH_LIST_HYPERLINK, getResources().getString(R.string.allergy_environment_url));
        mEnvironmentArrayList.add(hashMapac);

        hashMapac = new HashMap<String, String>();
        hashMapac.put("id", "114");
        hashMapac.put("value", getResources().getString(R.string.allergy_environment_114));
        hashMapac.put(StringConstants.KEY_SEARCH_LIST_HYPERLINK, getResources().getString(R.string.allergy_environment_url));
        mEnvironmentArrayList.add(hashMapac);

        hashMapac = new HashMap<String, String>();
        hashMapac.put("id", "115");
        hashMapac.put("value", getResources().getString(R.string.allergy_environment_115));
        hashMapac.put(StringConstants.KEY_SEARCH_LIST_HYPERLINK, getResources().getString(R.string.allergy_environment_url));
        mEnvironmentArrayList.add(hashMapac);

        hashMapac = new HashMap<String, String>();
        hashMapac.put("id", "116");
        hashMapac.put("value", getResources().getString(R.string.allergy_environment_116));
        hashMapac.put(StringConstants.KEY_SEARCH_LIST_HYPERLINK, getResources().getString(R.string.allergy_environment_url));
        mEnvironmentArrayList.add(hashMapac);

        hashMapac = new HashMap<String, String>();
        hashMapac.put("id", "117");
        hashMapac.put("value", getResources().getString(R.string.allergy_environment_117));
        hashMapac.put(StringConstants.KEY_SEARCH_LIST_HYPERLINK, getResources().getString(R.string.allergy_environment_url));
        mEnvironmentArrayList.add(hashMapac);

        hashMapac = new HashMap<String, String>();
        hashMapac.put("id", "118");
        hashMapac.put("value", getResources().getString(R.string.allergy_environment_118));
        hashMapac.put(StringConstants.KEY_SEARCH_LIST_HYPERLINK, getResources().getString(R.string.allergy_environment_url));
        mEnvironmentArrayList.add(hashMapac);

        hashMapac = new HashMap<String, String>();
        hashMapac.put("id", "119");
        hashMapac.put("value", getResources().getString(R.string.allergy_environment_119));
        hashMapac.put(StringConstants.KEY_SEARCH_LIST_HYPERLINK, getResources().getString(R.string.allergy_environment_url));
        mEnvironmentArrayList.add(hashMapac);

        hashMapac = new HashMap<String, String>();
        hashMapac.put("id", "120");
        hashMapac.put("value", getResources().getString(R.string.allergy_environment_120));
        hashMapac.put(StringConstants.KEY_SEARCH_LIST_HYPERLINK, getResources().getString(R.string.allergy_environment_url));
        mEnvironmentArrayList.add(hashMapac);

        hashMapac = new HashMap<String, String>();
        hashMapac.put("id", "121");
        hashMapac.put("value", getResources().getString(R.string.allergy_environment_121));
        hashMapac.put(StringConstants.KEY_SEARCH_LIST_HYPERLINK, getResources().getString(R.string.allergy_environment_url));
        mEnvironmentArrayList.add(hashMapac);

        hashMapac = new HashMap<String, String>();
        hashMapac.put("id", "122");
        hashMapac.put("value", getResources().getString(R.string.allergy_environment_122));
        hashMapac.put(StringConstants.KEY_SEARCH_LIST_HYPERLINK, getResources().getString(R.string.allergy_environment_url));
        mEnvironmentArrayList.add(hashMapac);

        hashMapac = new HashMap<String, String>();
        hashMapac.put("id", "123");
        hashMapac.put("value", getResources().getString(R.string.allergy_environment_123));
        hashMapac.put(StringConstants.KEY_SEARCH_LIST_HYPERLINK, getResources().getString(R.string.allergy_environment_url));
        mEnvironmentArrayList.add(hashMapac);

        hashMapac = new HashMap<String, String>();
        hashMapac.put("id", "124");
        hashMapac.put("value", getResources().getString(R.string.allergy_environment_124));
        hashMapac.put(StringConstants.KEY_SEARCH_LIST_HYPERLINK, getResources().getString(R.string.allergy_environment_url));
        mEnvironmentArrayList.add(hashMapac);

        hashMapac = new HashMap<String, String>();
        hashMapac.put("id", "125");
        hashMapac.put("value", getResources().getString(R.string.allergy_environment_125));
        hashMapac.put(StringConstants.KEY_SEARCH_LIST_HYPERLINK, getResources().getString(R.string.allergy_environment_url));
        mEnvironmentArrayList.add(hashMapac);

        hashMapac = new HashMap<String, String>();
        hashMapac.put("id", "126");
        hashMapac.put("value", getResources().getString(R.string.allergy_environment_126));
        hashMapac.put(StringConstants.KEY_SEARCH_LIST_HYPERLINK, getResources().getString(R.string.allergy_environment_url));
        mEnvironmentArrayList.add(hashMapac);

        hashMapac = new HashMap<String, String>();
        hashMapac.put("id", "128");
        hashMapac.put("value", getResources().getString(R.string.allergy_environment_128));
        hashMapac.put(StringConstants.KEY_SEARCH_LIST_HYPERLINK, getResources().getString(R.string.allergy_environment_url));
        mEnvironmentArrayList.add(hashMapac);


        /**
         * Medication
         */
        mMedicationArrayList = new ArrayList<HashMap<String, String>>();
        hashMapac = new HashMap<String, String>();
        hashMapac.put("id", "100");
        hashMapac.put("value", getResources().getString(R.string.allergy_medication_100));
        hashMapac.put(StringConstants.KEY_SEARCH_LIST_HYPERLINK, getResources().getString(R.string.allergy_medication_url));
        mMedicationArrayList.add(hashMapac);

        hashMapac = new HashMap<String, String>();
        hashMapac.put("id", "101");
        hashMapac.put("value", getResources().getString(R.string.allergy_medication_101));
        hashMapac.put(StringConstants.KEY_SEARCH_LIST_HYPERLINK, getResources().getString(R.string.allergy_medication_url));
        mMedicationArrayList.add(hashMapac);

        hashMapac = new HashMap<String, String>();
        hashMapac.put("id", "102");
        hashMapac.put("value", getResources().getString(R.string.allergy_medication_102));
        hashMapac.put(StringConstants.KEY_SEARCH_LIST_HYPERLINK, getResources().getString(R.string.allergy_medication_url));
        mMedicationArrayList.add(hashMapac);

        hashMapac = new HashMap<String, String>();
        hashMapac.put("id", "103");
        hashMapac.put("value", getResources().getString(R.string.allergy_medication_103));
        hashMapac.put(StringConstants.KEY_SEARCH_LIST_HYPERLINK, getResources().getString(R.string.allergy_medication_url));
        mMedicationArrayList.add(hashMapac);

        hashMapac = new HashMap<String, String>();
        hashMapac.put("id", "104");
        hashMapac.put("value", getResources().getString(R.string.allergy_medication_104));
        hashMapac.put(StringConstants.KEY_SEARCH_LIST_HYPERLINK, getResources().getString(R.string.allergy_medication_url));
        mMedicationArrayList.add(hashMapac);

        hashMapac = new HashMap<String, String>();
        hashMapac.put("id", "105");
        hashMapac.put("value", getResources().getString(R.string.allergy_medication_105));
        hashMapac.put(StringConstants.KEY_SEARCH_LIST_HYPERLINK, getResources().getString(R.string.allergy_medication_url));
        mMedicationArrayList.add(hashMapac);

        hashMapac = new HashMap<String, String>();
        hashMapac.put("id", "106");
        hashMapac.put("value", getResources().getString(R.string.allergy_medication_106));
        hashMapac.put(StringConstants.KEY_SEARCH_LIST_HYPERLINK, getResources().getString(R.string.allergy_medication_url));
        mMedicationArrayList.add(hashMapac);

        hashMapac = new HashMap<String, String>();
        hashMapac.put("id", "107");
        hashMapac.put("value", getResources().getString(R.string.allergy_medication_107));
        hashMapac.put(StringConstants.KEY_SEARCH_LIST_HYPERLINK, getResources().getString(R.string.allergy_medication_url));
        mMedicationArrayList.add(hashMapac);

        hashMapac = new HashMap<String, String>();
        hashMapac.put("id", "108");
        hashMapac.put("value", getResources().getString(R.string.allergy_medication_108));
        hashMapac.put(StringConstants.KEY_SEARCH_LIST_HYPERLINK, getResources().getString(R.string.allergy_medication_url));
        mMedicationArrayList.add(hashMapac);

        hashMapac = new HashMap<String, String>();
        hashMapac.put("id", "129");
        hashMapac.put("value", getResources().getString(R.string.allergy_medication_129));
        hashMapac.put(StringConstants.KEY_SEARCH_LIST_HYPERLINK, getResources().getString(R.string.allergy_medication_url));
        mMedicationArrayList.add(hashMapac);
    }


    /**
     * Save Date
     * @param view
     */
    public void goToSave(View view) {

        if (Validation.validateStartDate(dateTextView, getApplicationContext())) {

            // Shut off keypad
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

            // Populate object with Data
            HashMap<String, String> selectedAllergyData = new HashMap<>();
            selectedAllergyData.put(StringConstants.KEY_ALLERGY_ID, mAllergyId.toString());
            selectedAllergyData.put(StringConstants.KEY_ALLERGY_TYPE, mAllergyTypeName);
            selectedAllergyData.put(StringConstants.KEY_ALLERGY_NAME, problemNameTextView.getText().toString());
            selectedAllergyData.put(StringConstants.KEY_SEARCH_LIST_HYPERLINK, problemHyperlinkTextView.getText().toString());
            selectedAllergyData.put(StringConstants.KEY_ALLERGY_KIND_OF_REACTION, kindOfReactionSpinner.getSelectedItem().toString());
            selectedAllergyData.put(StringConstants.KEY_START_DATE,
                    String.valueOf(DateTimeUtils.convertDateSimpleToTimestamp(dateTextView.getText().toString())));
            selectedAllergyData.put(StringConstants.KEY_COMMENT, commentsTextView.getText().toString());

            /**
             * Check if record already exists
             */
            if(allergyList.size() > 0) {

                // Record Exists -> Update
                selectedAllergyData.put(StringConstants.KEY_ALLERGY_P_ID, allergy.getpId());
                AllergyFragment.updateAllergy(getApplicationContext(), selectedAllergyData);

            } else {

                // Record Does Not Exists -> Create
                AllergyFragment.createAllergyRecords(getApplicationContext(), selectedAllergyData);
            }

            setResult(RESULT_OK, new Intent());
            finish();
        }
    }


    public void close(View view) {
        finish();
    }










    /**
     * Function to implement date picker
     */
    public void openDatePicker() {

        Calendar now = Calendar.getInstance();
        DatePickerDialog dpd = DatePickerDialog.newInstance(
                AddAllergyActivity.this,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );
        dpd.show(getFragmentManager(), StringConstants.KEY_DATEPICKER_DIALOG);
//        dpd.setAccentColor(R.color.al);
        //dpd.setStyle(0,R.style.allergy_date_picker_theme);
        dpd.showYearPickerFirst(true);
        dpd.setMaxDate(now);
        dpd.setYearRange(StaticConstants.MIN_DATE, now.get(Calendar.YEAR));
    }


    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {

        long timestamp = DateTimeUtils.convertTimeToTimestamp(year, monthOfYear, dayOfMonth, 0, 0);
        dateTextView.setError(null);
        dateTextView.setText(DateTimeUtils.convertTimestampToDate(timestamp));
    }


    /**
     * Function to bind values to the spinner
     */
    public void bindSpinner() {

        adapter = ArrayAdapter.createFromResource(this, R.array.kind_of_reaction, R.layout.item_spinner_healthbook);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(R.layout.item_spinner_healthbook_dropdown);
        // Apply the adapter to the spinner
        kindOfReactionSpinner.setAdapter(adapter);
    }
}
