package com.careons.app.Patient.Activity.Main.HealthBook;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.careons.app.Patient.Activity.Onboarding.OnboardingFamilyHistoryActivity;
import com.careons.app.Patient.Adapters.SearchListAdapter;
import com.careons.app.Patient.Commons.BuildProperties;
import com.careons.app.Patient.Commons.ServiceUrls;
import com.careons.app.Patient.Commons.StaticConstants;
import com.careons.app.Patient.Commons.StringConstants;
import com.careons.app.Patient.Database.Models.Healthbook.FamilyHistory;
import com.careons.app.Patient.Fragments.HealthBook.FamilyFragment;
import com.careons.app.Shared.Interfaces.APIInterface;
import com.careons.app.R;
import com.careons.app.Patient.Services.APICallService;
import com.careons.app.Patient.Services.SharedPreferenceService;
import com.careons.app.Shared.Utils.Validation;
import com.crashlytics.android.Crashlytics;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class AddFamilyActivity extends AppCompatActivity implements View.OnClickListener{

    private static FamilyHistory familyHistory;

    private static LinearLayout loader;
    private static TextView emptySearchLayout, errorSearchLayout, internetFailureSearchLayout;

    private LinearLayout addLinearLayout, searchBarLayout;
    private ListView mAddSearchlistView;
    private Toolbar toolbar;
    private TextView problemIdTextView, problemNameTextView, problemHyperlinkTextView, saveContentTextView;
    private EditText mSearchAddEditText, commentsEditText;

    private CheckBox checkbox1, checkbox2, checkbox3, checkbox4, checkbox5, checkbox6;
    private EditText age1, age2, age3, age4, age5, age6;
    private TextView ageTitle1, ageTitle2, ageTitle3, ageTitle4, ageTitle5, ageTitle6;

    private Timer timer = new Timer();
    private final long DELAY = 1000; // in ms
    private String previousSearchString = "";

    // Initial problem List
    private static List<HashMap<String, String>> initialFamilyProblems = new ArrayList<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hb_add_family);

        // To handle back button
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.setTitle(getResources().getString(R.string.family_history));
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));

        // Status Bar Color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.darker_fh));
        }

        // Back enabled
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Initialize layouts
        addLinearLayout = (LinearLayout) findViewById(R.id.addLinearLayout);
        searchBarLayout = (LinearLayout) findViewById(R.id.searchBarLayout);

        // Initialize loader
        loader = (LinearLayout) findViewById(R.id.loader);
        loader.setVisibility(View.GONE);

        // Initialize Search list cases
        emptySearchLayout = (TextView) findViewById(R.id.empty_search);
        errorSearchLayout = (TextView) findViewById(R.id.error_search);
        internetFailureSearchLayout = (TextView) findViewById(R.id.internet_failure_search);

        // Initialize fields
        mAddSearchlistView = (ListView) findViewById(R.id.listViewSerachMP);
        problemIdTextView = (TextView) findViewById(R.id.problem_id);
        problemNameTextView = (TextView) findViewById(R.id.problem_name);
        problemHyperlinkTextView = (TextView) findViewById(R.id.problem_hyperlink);
        commentsEditText = (EditText) findViewById(R.id.comments);

        checkbox1 = (CheckBox) findViewById(R.id.checkbox1);
        checkbox2 = (CheckBox) findViewById(R.id.checkbox2);
        checkbox3 = (CheckBox) findViewById(R.id.checkbox3);
        checkbox4 = (CheckBox) findViewById(R.id.checkbox4);
        checkbox5 = (CheckBox) findViewById(R.id.checkbox5);
        checkbox6 = (CheckBox) findViewById(R.id.checkbox6);

        // Set on click listeners
        checkbox1.setOnClickListener(this);
        checkbox2.setOnClickListener(this);
        checkbox3.setOnClickListener(this);
        checkbox4.setOnClickListener(this);
        checkbox5.setOnClickListener(this);
        checkbox6.setOnClickListener(this);

        ageTitle1 = (TextView) findViewById(R.id.ageTitle1);
        ageTitle2= (TextView) findViewById(R.id.ageTitle2);
        ageTitle3 = (TextView) findViewById(R.id.ageTitle3);
        ageTitle4 = (TextView) findViewById(R.id.ageTitle4);
        ageTitle5 = (TextView) findViewById(R.id.ageTitle5);
        ageTitle6 = (TextView) findViewById(R.id.ageTitle6);

        age1 = (EditText) findViewById(R.id.age1);
        age2 = (EditText) findViewById(R.id.age2);
        age3 = (EditText) findViewById(R.id.age3);
        age4 = (EditText) findViewById(R.id.age4);
        age5 = (EditText) findViewById(R.id.age5);
        age6 = (EditText) findViewById(R.id.age6);

        // Save Content
        saveContentTextView = (TextView) findViewById(R.id.save_content);
        saveContentTextView.setOnClickListener(this);


        /**
         * Handle Search List Text
         */
        mSearchAddEditText = (EditText) findViewById(R.id.editTextSearch);
        handleSearchBar();


        /**
         * Handle Search List Item Click
         */
        searchListItemClicked();
    }














    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                //finish();
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onClick(View v) {

        switch(v.getId()){

            case R.id.checkbox1:
                ageTitle1.setVisibility(checkbox1.isChecked() ? View.VISIBLE : View.GONE);
                age1.setVisibility(checkbox1.isChecked() ? View.VISIBLE : View.GONE);
                if(checkbox1.isChecked() ){
                    age1.requestFocus();
                }
                break;

            case R.id.checkbox2:
                ageTitle2.setVisibility(checkbox2.isChecked() ? View.VISIBLE : View.GONE);
                age2.setVisibility(checkbox2.isChecked() ? View.VISIBLE : View.GONE);
                if(checkbox2.isChecked() ){
                    age2.requestFocus();
                }
                break;

            case R.id.checkbox3:
                ageTitle3.setVisibility(checkbox3.isChecked() ? View.VISIBLE : View.GONE);
                age3.setVisibility(checkbox3.isChecked() ? View.VISIBLE : View.GONE);
                if(checkbox3.isChecked() ){
                    age3.requestFocus();
                }
                break;

            case R.id.checkbox4:
                ageTitle4.setVisibility(checkbox4.isChecked() ? View.VISIBLE : View.GONE);
                age4.setVisibility(checkbox4.isChecked() ? View.VISIBLE : View.GONE);
                if(checkbox4.isChecked() ){
                    age4.requestFocus();
                }
                break;

            case R.id.checkbox5:
                ageTitle5.setVisibility(checkbox5.isChecked() ? View.VISIBLE : View.GONE);
                age5.setVisibility(checkbox5.isChecked() ? View.VISIBLE : View.GONE);
                if(checkbox5.isChecked() ){
                    age5.requestFocus();
                }
                break;

            case R.id.checkbox6:
                ageTitle6.setVisibility(checkbox6.isChecked() ? View.VISIBLE : View.GONE);
                age6.setVisibility(checkbox6.isChecked() ? View.VISIBLE : View.GONE);
                if(checkbox6.isChecked() ){
                    age6.requestFocus();
                }
                break;


            case R.id.save_content:
                if (checkbox1.isChecked() || checkbox2.isChecked() || checkbox3.isChecked() || checkbox4.isChecked() || checkbox5.isChecked() || checkbox6.isChecked()) {
                    boolean executed = true;
                    if (checkbox1.isChecked() && !Validation.validateDateOfDetection(age1, getApplicationContext())) {
                        executed = false;
                        return;
                    }
                    if (checkbox2.isChecked() && !Validation.validateDateOfDetection(age2, getApplicationContext())) {
                        executed = false;
                        return;
                    }
                    if (checkbox3.isChecked() && !Validation.validateDateOfDetection(age3, getApplicationContext())) {
                        executed = false;
                        return;
                    }
                    if (checkbox4.isChecked() && !Validation.validateDateOfDetection(age4, getApplicationContext())) {
                        executed = false;
                        return;
                    }
                    if (checkbox5.isChecked() && !Validation.validateDateOfDetection(age5, getApplicationContext())) {
                        executed = false;
                        return;
                    }
                    if (checkbox6.isChecked() && !Validation.validateDateOfDetection(age6, getApplicationContext())) {
                        executed = false;
                        return;
                    }

                    if (executed) {
                        // Populate object with Data
                        HashMap<String, String> selectedProblemData = new HashMap<>();
                        selectedProblemData.put(StringConstants.KEY_PROBLEMS_ID, problemIdTextView.getText().toString());
                        selectedProblemData.put(StringConstants.KEY_PROBLEM_NAME, problemNameTextView.getText().toString());
                        selectedProblemData.put(StringConstants.KEY_SEARCH_LIST_HYPERLINK, problemHyperlinkTextView.getText().toString());

                        selectedProblemData.put(StringConstants.KEY_FAMILY_FATHER_SUFFERING, String.valueOf(checkbox1.isChecked()));
                        selectedProblemData.put(StringConstants.KEY_FAMILY_FATHER_AGE, age1.getText().toString());

                        selectedProblemData.put(StringConstants.KEY_FAMILY_MOTHER_SUFFERING, String.valueOf(checkbox2.isChecked()));
                        selectedProblemData.put(StringConstants.KEY_FAMILY_MOTHER_AGE, age2.getText().toString());

                        selectedProblemData.put(StringConstants.KEY_FAMILY_BROTHER_SUFFERING, String.valueOf(checkbox3.isChecked()));
                        selectedProblemData.put(StringConstants.KEY_FAMILY_BROTHER_AGE, age3.getText().toString());

                        selectedProblemData.put(StringConstants.KEY_FAMILY_SISTER_SUFFERING, String.valueOf(checkbox4.isChecked()));
                        selectedProblemData.put(StringConstants.KEY_FAMILY_SISTER_AGE, age4.getText().toString());

                        selectedProblemData.put(StringConstants.KEY_FAMILY_GF_SUFFERING, String.valueOf(checkbox5.isChecked()));
                        selectedProblemData.put(StringConstants.KEY_FAMILY_GF_AGE, age5.getText().toString());

                        selectedProblemData.put(StringConstants.KEY_FAMILY_GM_SUFFERING, String.valueOf(checkbox6.isChecked()));
                        selectedProblemData.put(StringConstants.KEY_FAMILY_GM_AGE, age6.getText().toString());

                        selectedProblemData.put(StringConstants.KEY_COMMENT, commentsEditText.getText().toString());

                        // Check if record already exists
                        if (FamilyHistory.find(FamilyHistory.class, "patient_id = ? and problem_id = ?",
                                SharedPreferenceService.getValue(getApplicationContext(), StringConstants.KEY_PATIENT_ID),
                                problemIdTextView.getText().toString()).size() > 0) {

                            selectedProblemData.put(StringConstants.KEY_P_ID, familyHistory.getpId());

                            // Found -> Check Calling Activity
                            if (getIntent().getBooleanExtra(StringConstants.KEY_INONBOARDING, false)) {

                                OnboardingFamilyHistoryActivity.updateFamilyHistoryProblem(getApplicationContext(), selectedProblemData);
                                setResult(RESULT_OK, new Intent());

                            } else {

                                FamilyFragment.updateFamilyHistoryProblem(getApplicationContext(), selectedProblemData);
                                setResult(RESULT_OK, new Intent());
                            }

                        } else {

                            // Not Found -> Check Calling Activity
                            if (getIntent().getBooleanExtra(StringConstants.KEY_INONBOARDING, false)) {

                                OnboardingFamilyHistoryActivity.createFamilyHistoryProblem(getApplicationContext(), selectedProblemData);
                                setResult(RESULT_OK, new Intent());

                            } else {

                                FamilyFragment.createFamilyHistoryProblem(getApplicationContext(), selectedProblemData);
                                setResult(RESULT_OK, new Intent());
                            }
                        }

                        // Navigate back to calling activity to see updated list
                        finish();
                        break;
                    }
                }else{
                    Toast.makeText(getApplication().getApplicationContext(),"Please select minimum one value",Toast.LENGTH_SHORT).show();
                }

            default:
                break;
        }
    }

















    /**
     * Function to handle search bar action
     */
    public void handleSearchBar() {

        mSearchAddEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                // TODO Auto-generated method stub
                timer.cancel();
                timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {

                        synchronized (this) {

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    String searchString = mSearchAddEditText.getText().toString();

                                    // Check if Search string length is greater than 2
                                    if (searchString.trim().length() > 2) {
                                        if (!Validation.isEmpty(searchString.toString())) {

                                            if(!(previousSearchString.equalsIgnoreCase(searchString))) {
                                                previousSearchString = searchString;

                                                fetchSearchResults(searchString);
                                                mAddSearchlistView.setVisibility(View.VISIBLE);
                                                addLinearLayout.setVisibility(View.GONE);
                                            }
                                        }
                                    }
                                }
                            });

                        }
                        // TODO: do what you need here (refresh list)
                        // you will probably need to use runOnUiThread(Runnable action) for some specific actions


                    }

                }, DELAY);

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub

            }
        });
    }


    /**
     * Function to show search results
     */
    public void fetchSearchResults(String searchQuery) {

        // Set Initial State
        loader.setVisibility(View.VISIBLE);
        emptySearchLayout.setVisibility(View.GONE);
        errorSearchLayout.setVisibility(View.GONE);
        internetFailureSearchLayout.setVisibility(View.GONE);
        mAddSearchlistView.setVisibility(View.GONE);


        // Set URL
        final String url = String.format(ServiceUrls.KEY_HEALTHBOOK_SEARCH,
                StringConstants.KEY_PROBLEMS,
                searchQuery.replace(" ", "%20"),
                StaticConstants.KEY_SEARCH_LIST_LIMIT);

        // Set Headers
        final HashMap<String, String> headers = new HashMap<>();
        headers.put(StaticConstants.KEY_CONTENT_TYPE, StaticConstants.KEY_URLENCODED);
        headers.put(StaticConstants.KEY_HOST, StaticConstants.KEY_HOSTNAME);
        headers.put(StaticConstants.KEY_API_KEY, BuildProperties.azureSearchKey);

        // Clear Data
        initialFamilyProblems.clear();

        if (Validation.isConnected(getApplicationContext())) {

            // API Call
            APICallService.GetAPICall(this, this, url, null, headers, new APIInterface() {

                @Override
                public void onSuccess(JSONObject response) {

                    try {

                        if (response.has(StringConstants.KEY_VALUES)
                                && response.getJSONArray(StringConstants.KEY_VALUES).length() > 0) {

                            JSONArray data = response.getJSONArray(StringConstants.KEY_VALUES);
                            for (int i = 0; i < data.length(); i++) {

                                JSONObject jsonObject = data.getJSONObject(i);
                                HashMap<String, String> searchResult = new HashMap<>();
                                searchResult.put(StringConstants.KEY_ID, jsonObject.getString(StringConstants.KEY_SEARCH_LIST_ID));
                                searchResult.put(StringConstants.KEY_NAME, jsonObject.getString(StringConstants.KEY_SEARCH_LIST_NAME));
                                searchResult.put(StringConstants.KEY_CATEGORY, getString(R.string.problems));
                                searchResult.put(StringConstants.KEY_SEARCH_LIST_HYPERLINK,
                                        jsonObject.getString(StringConstants.KEY_SEARCH_LIST_HYPERLINK));
                                initialFamilyProblems.add(searchResult);

                                /**
                                 * Bind adapter to the result
                                 */
                                mAddSearchlistView.setAdapter(
                                        new SearchListAdapter(AddFamilyActivity.this,
                                                initialFamilyProblems, StaticConstants.FAMILY_ADAPTER));
                            }

                            // Show Content
                            loader.setVisibility(View.GONE);
                            mAddSearchlistView.setVisibility(View.VISIBLE);

                        } else {

                            // No result found
                            loader.setVisibility(View.GONE);
                            emptySearchLayout.setVisibility(View.VISIBLE);
                        }
                    } catch (JSONException e) {

                        // Log error
                        Crashlytics.logException(e);

                        // Show Content
                        loader.setVisibility(View.GONE);
                        errorSearchLayout.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onError(VolleyError error) {

                    // Show Content
                    loader.setVisibility(View.GONE);
                    errorSearchLayout.setVisibility(View.VISIBLE);
                }
            });

        } else {

            // If internet connection is not available
            loader.setVisibility(View.GONE);
            internetFailureSearchLayout.setVisibility(View.VISIBLE);
        }
    }


    /**
     * Function to handle search options list item click
     */
    public void searchListItemClicked() {
        mAddSearchlistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                // Fetch problem Id
                TextView addProblemIdTextView = (TextView) view.findViewById(R.id.problem_id);
                String problemId = addProblemIdTextView.getText().toString();

                // Set selected problem name, Id and hyperlink
                problemNameTextView.setText(initialFamilyProblems.get(position).get(StringConstants.KEY_NAME));
                problemIdTextView.setText(initialFamilyProblems.get(position).get(StringConstants.KEY_ID));
                problemHyperlinkTextView.setText(initialFamilyProblems.get(position).get(StringConstants.KEY_SEARCH_LIST_HYPERLINK));


                // Shut off keypad
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

                // Input details for the problem
                searchBarLayout.setVisibility(View.GONE);
                mAddSearchlistView.setVisibility(View.GONE);
                addLinearLayout.setVisibility(View.VISIBLE);


                /**
                 * Check if problem id exists in local database
                 */
                List<FamilyHistory> familyHistoryList = FamilyHistory.find(FamilyHistory.class, "patient_id = ? and problem_id = ?",
                        SharedPreferenceService.getValue(getApplicationContext(), StringConstants.KEY_PATIENT_ID),
                        problemId);


                // See if record already exists to prepopulate data
                if (familyHistoryList.size() > 0) {

                    // Record Found
                    familyHistory = familyHistoryList.get(0);

                    checkbox1.setChecked(familyHistory.isFatherStillSuffering());
                    age1.setText(familyHistory.getFatherAge());
                    if(checkbox1.isChecked()) {
                        age1.setVisibility(View.VISIBLE);
                    }

                    checkbox2.setChecked(familyHistory.isMotherStillSuffering());
                    age2.setText(familyHistory.getMotherAge());
                    if(checkbox2.isChecked()) {
                        age2.setVisibility(View.VISIBLE);
                    }

                    checkbox3.setChecked(familyHistory.isBrotherStillSuffering());
                    age3.setText(familyHistory.getBrotherAge());
                    if(checkbox3.isChecked()) {
                        age3.setVisibility(View.VISIBLE);
                    }

                    checkbox4.setChecked(familyHistory.isSisterStillSuffering());
                    age4.setText(familyHistory.getSisterAge());
                    if(checkbox4.isChecked()) {
                        age4.setVisibility(View.VISIBLE);
                    }

                    checkbox5.setChecked(familyHistory.isGrandFatherStillSuffering());
                    age5.setText(familyHistory.getGrandFatherAge());
                    if(checkbox5.isChecked()) {
                        age5.setVisibility(View.VISIBLE);
                    }

                    checkbox6.setChecked(familyHistory.isGrandMotherStillSuffering());
                    age6.setText(familyHistory.getGrandMotherAge());
                    if(checkbox6.isChecked()) {
                        age6.setVisibility(View.VISIBLE);
                    }

                    commentsEditText.setText(familyHistory.getComments());
                }
            }
        });
    }
}