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
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.careons.app.Patient.Activity.Onboarding.OnboardingMedicalProblemsActivity;
import com.careons.app.Patient.Adapters.SearchListAdapter;
import com.careons.app.Patient.Commons.BuildProperties;
import com.careons.app.Patient.Commons.ServiceUrls;
import com.careons.app.Patient.Commons.StaticConstants;
import com.careons.app.Patient.Commons.StringConstants;
import com.careons.app.Patient.Database.Models.Healthbook.MedicalProblem;
import com.careons.app.Patient.Fragments.HealthBook.MedicalProblemFragment;
import com.careons.app.Shared.Interfaces.APIInterface;
import com.careons.app.R;
import com.careons.app.Patient.Services.APICallService;
import com.careons.app.Patient.Services.SharedPreferenceService;
import com.careons.app.Shared.Utils.DateTimeUtils;
import com.careons.app.Shared.Utils.Validation;
import com.crashlytics.android.Crashlytics;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class AddMedicalProblemActivity extends AppCompatActivity
        implements DatePickerDialog.OnDateSetListener, View.OnClickListener {

    private static MedicalProblem medicalProblem;

    private static LinearLayout loader;
    private static TextView emptySearchLayout, errorSearchLayout, internetFailureSearchLayout;

    private LinearLayout addLinearLayout, start_date_layout, end_date_layout, duration_layout, searchBarLayout;
    private EditText mSearchAddEditText, commentsEditText;
    private ListView mAddSearchlistView;
    private TextView problemIdTextView, problemNameTextView, problemHyperlinkTextView, start_date_textview, end_date_textview, saveContentTextView;
    private Toolbar toolbar;
    private CheckBox isSufferingCheckbox;

    private boolean isDateFirstclicked = true;
    private Spinner durationSpinner;

    private long startDateEpoch, endDateEpoch;
    private Timer timer = new Timer();
    private String previousSearchString = "";


    // Create an ArrayAdapter using the string array and a spinner layout
    ArrayAdapter<CharSequence> adapter;

    // Initial problem List
    private static List<HashMap<String, String>> initialMedicalProblems = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hb_add_medical_problem);


        // To handle back button
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getResources().getString(R.string.add_medical_problem));
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));

        // Status Bar Color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.darker_mp));
        }

        // Back enabled
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Initialize loader
        loader = (LinearLayout) findViewById(R.id.loader);
        loader.setVisibility(View.GONE);

        // Initialize Search list cases
        emptySearchLayout = (TextView) findViewById(R.id.empty_search);
        errorSearchLayout = (TextView) findViewById(R.id.error_search);
        internetFailureSearchLayout = (TextView) findViewById(R.id.internet_failure_search);

        // Initialize layouts
        addLinearLayout = (LinearLayout) findViewById(R.id.addLinearLayout);
        searchBarLayout = (LinearLayout) findViewById(R.id.searchBarLayout);
        start_date_layout = (LinearLayout) findViewById(R.id.start_date_layout);
        end_date_layout = (LinearLayout) findViewById(R.id.end_date_layout);
        duration_layout = (LinearLayout) findViewById(R.id.duration_layout);

        // Initialize list view
        mAddSearchlistView = (ListView) findViewById(R.id.listViewSerachMP);

        // Initialize fields
        problemIdTextView = (TextView) findViewById(R.id.problem_id);
        problemNameTextView = (TextView) findViewById(R.id.problem_name);
        problemHyperlinkTextView = (TextView) findViewById(R.id.problem_hyperlink);
        start_date_textview = (TextView) findViewById(R.id.start_date_textview);
        end_date_textview = (TextView) findViewById(R.id.end_date_textview);
        commentsEditText = (EditText) findViewById(R.id.comments);
        isSufferingCheckbox = (CheckBox) findViewById(R.id.isSufferingCheckbox);
        saveContentTextView = (TextView) findViewById(R.id.save_content);
        mSearchAddEditText = (EditText) findViewById(R.id.editTextSearch);
        durationSpinner = (Spinner) findViewById(R.id.duration_spinner);


        // Set on click listeners
        isSufferingCheckbox.setOnClickListener(this);
        saveContentTextView.setOnClickListener(this);
        start_date_textview.setOnClickListener(this);
        end_date_textview.setOnClickListener(this);


        // On Focus change listener on start date
        start_date_textview.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    Validation.validateDate(start_date_textview, getApplicationContext());
                }
            }
        });

        // On Focus change listener on end date
        end_date_textview.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    Validation.validateDate(end_date_textview, getApplicationContext());
                }
            }
        });


        /**
         * Handle Search List Text
         */
        handleSearchBar();


        //binf duration spinner
        bindSpinner();

        /**
         * Handle Search List Item Click
         */
        searchListItemClicked();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                finish();
        }

        return super.onOptionsItemSelected(item);
    }






    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.start_date_textview:
                isDateFirstclicked = true;
                openDatePicker();
                break;


            case R.id.end_date_textview:
                isDateFirstclicked = false;
                openDatePicker();
                break;


            case R.id.isSufferingCheckbox:
                manageIsSufferingDisplay(isSufferingCheckbox.isChecked());
                break;


            case R.id.save_content:

                if (isSufferingCheckbox.isChecked()) {

                    saveData();

                } else if (!isSufferingCheckbox.isChecked()
                        && Validation.validateDate(start_date_textview, getApplicationContext())
                        && Validation.validateDate(end_date_textview, getApplicationContext())) {

                    saveData();
                }
                break;


            default:
                break;
        }
    }


    /**
     * Function to save data
     */
    public void saveData() {

        HashMap<String, String> selectedProblemData = new HashMap<>();
        selectedProblemData.put(StringConstants.KEY_PROBLEMS_ID, problemIdTextView.getText().toString());
        selectedProblemData.put(StringConstants.KEY_PROBLEM_NAME, problemNameTextView.getText().toString());
        selectedProblemData.put(StringConstants.KEY_SEARCH_LIST_HYPERLINK, problemHyperlinkTextView.getText().toString());
        selectedProblemData.put(StringConstants.KEY_IS_SUFFERING, String.valueOf(isSufferingCheckbox.isChecked()));

        /**
         * Check for Duration to set Start Date
         */
        if (isSufferingCheckbox.isChecked()) {

            // Calculate Start Date from Duration
            selectedProblemData.put(StringConstants.KEY_START_DATE,
                    String.valueOf(DateTimeUtils.calculateTimestampFromDuration(durationSpinner.getSelectedItemPosition())));
        } else {

            selectedProblemData.put(StringConstants.KEY_START_DATE,
                    String.valueOf(DateTimeUtils.convertDateSimpleToTimestamp(start_date_textview.getText().toString())));
        }

        selectedProblemData.put(StringConstants.KEY_END_DATE,
                String.valueOf(DateTimeUtils.convertDateSimpleToTimestamp(end_date_textview.getText().toString())));
        selectedProblemData.put(StringConstants.KEY_COMMENT, String.valueOf(commentsEditText.getText().toString()));



        /**
         * Check if record already exists
         */
        if (MedicalProblem.find(MedicalProblem.class, "patient_id = ? and problem_Id = ?",
                SharedPreferenceService.getValue(getApplicationContext(), StringConstants.KEY_PATIENT_ID),
                problemIdTextView.getText().toString()).size() > 0) {

            // Add Pid for update
            selectedProblemData.put(StringConstants.KEY_P_ID, medicalProblem.getPId());

            // Found -> Check Calling Activity
            if (getIntent().getBooleanExtra(StringConstants.KEY_INONBOARDING, false)) {

                OnboardingMedicalProblemsActivity.updateMedicalProblem(getApplicationContext(), selectedProblemData);
                setResult(RESULT_OK, new Intent());

            } else {

                MedicalProblemFragment.updateMedicalProblem(getApplicationContext(), selectedProblemData);
                setResult(RESULT_OK, new Intent());

            }

        } else {

            // Not Found -> Check Calling Activity
            if (getIntent().getBooleanExtra(StringConstants.KEY_INONBOARDING, false)) {

                OnboardingMedicalProblemsActivity.createMedicalProblem(getApplicationContext(), selectedProblemData);
                setResult(RESULT_OK, new Intent());

            } else {

                MedicalProblemFragment.createMedicalProblem(getApplicationContext(), selectedProblemData);
                setResult(RESULT_OK, new Intent());
            }
        }

        // Navigate back to calling activity to see updated list
        finish();
    }






    /**
     * Function to manage is suffering flag
     */
    public void manageIsSufferingDisplay(boolean isSuffering) {

        /**
         * Check if still suffering
         */
        if(isSuffering) {

            // True -> Show Duration, Hide Start and End Date Layout
            duration_layout.setVisibility(View.VISIBLE);
            start_date_layout.setVisibility(View.GONE);
            end_date_layout.setVisibility(View.GONE);

        } else {

            // False -> Hide Duration, Show Start and End Date Layout
            duration_layout.setVisibility(View.GONE);
            start_date_layout.setVisibility(View.VISIBLE);
            end_date_layout.setVisibility(View.VISIBLE);
        }
    }


    /**
     * Function to implement date picker
     */
    public void openDatePicker() {
        Calendar now = Calendar.getInstance();
        DatePickerDialog dpd = DatePickerDialog.newInstance(this,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );
        if (isDateFirstclicked) {
            if (!Validation.isEmpty(end_date_textview.getText().toString())) {
                Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(DateTimeUtils.convertDateToLongForHealthBook(end_date_textview.getText().toString()));
                dpd = DatePickerDialog.newInstance(this,
                        cal.get(Calendar.YEAR),
                        cal.get(Calendar.MONTH),
                        cal.get(Calendar.DAY_OF_MONTH)
                );
                dpd.setMaxDate(cal);
                dpd.setYearRange(StaticConstants.MIN_DATE, cal.get(Calendar.YEAR));
            } else {
                dpd.setMaxDate(now);
                dpd.setYearRange(StaticConstants.MIN_DATE, now.get(Calendar.YEAR));
            }

        } else {
            if (!Validation.isEmpty(start_date_textview.getText().toString())) {
                Calendar cal2 = Calendar.getInstance();
                cal2.setTimeInMillis(DateTimeUtils.convertDateToLongForHealthBook(start_date_textview.getText().toString()));
                dpd = DatePickerDialog.newInstance(this,
                        cal2.get(Calendar.YEAR),
                        cal2.get(Calendar.MONTH),
                        cal2.get(Calendar.DAY_OF_MONTH)
                );
                dpd.setMinDate(cal2);
            }else{
                dpd.setYearRange(StaticConstants.MIN_DATE, now.get(Calendar.YEAR));
            }
            dpd.setMaxDate(now);
        }
        dpd.show(getFragmentManager(), StringConstants.KEY_DATEPICKER_DIALOG);
        //dpd.setAccentColor(R.color.light_grey);
        dpd.showYearPickerFirst(true);
    }


    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {

        /**
         * Check for Start Date or End Date
         */
        if(isDateFirstclicked) {

            // Start Date
            startDateEpoch = DateTimeUtils.convertTimeToTimestamp(year, monthOfYear, dayOfMonth, 0, 0);
            start_date_textview.setError(null);
            start_date_textview.setText(DateTimeUtils.convertTimestampToDate(startDateEpoch));

        } else {

            // End Date
            endDateEpoch = DateTimeUtils.convertTimeToTimestamp(year, monthOfYear, dayOfMonth, 0, 0);
            end_date_textview.setError(null);
            end_date_textview.setText(DateTimeUtils.convertTimestampToDate(endDateEpoch));
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

                                    if(!(previousSearchString.equalsIgnoreCase(searchString))) {
                                        previousSearchString = searchString;

                                        // Check if Search string length is greater than 2
                                        if (searchString.trim().length() > 2) {
                                            fetchSearchResults(searchString.trim());
                                            mAddSearchlistView.setVisibility(View.VISIBLE);
                                            addLinearLayout.setVisibility(View.GONE);
                                        }
                                    }
                                }
                            });
                        }
                        // TODO: do what you need here (refresh list)
                        // you will probably need to use runOnUiThread(Runnable action) for some specific actions


                    }

                }, 1);

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
        initialMedicalProblems.clear();

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
                                initialMedicalProblems.add(searchResult);

                                /**
                                 * Bind adapter to the result
                                 */
                                mAddSearchlistView.setAdapter(
                                        new SearchListAdapter(AddMedicalProblemActivity.this, initialMedicalProblems,
                                                StaticConstants.MEDICAL_PROBLEM_ADAPTER));
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
                problemNameTextView.setText(initialMedicalProblems.get(position).get(StringConstants.KEY_NAME));
                problemIdTextView.setText(initialMedicalProblems.get(position).get(StringConstants.KEY_ID));
                problemHyperlinkTextView.setText(initialMedicalProblems.get(position).get(StringConstants.KEY_SEARCH_LIST_HYPERLINK));


                // Shut off keypad
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

                // Input details for the problem
                mAddSearchlistView.setVisibility(View.GONE);
                searchBarLayout.setVisibility(View.GONE);
                addLinearLayout.setVisibility(View.VISIBLE);


                /**
                 * Check problem id in local database
                 */
                List<MedicalProblem> medicalProblemList = MedicalProblem.find(MedicalProblem.class, "patient_id = ? and problem_id = ?",
                        SharedPreferenceService.getValue(getApplicationContext(), StringConstants.KEY_PATIENT_ID),
                        problemId);


                /**
                 * Populate Data Fields
                 */
                if (medicalProblemList.size() > 0) {

                    // Record Found
                    medicalProblem = medicalProblemList.get(0);

                    start_date_textview.setText(medicalProblem.getStartDate());
                    end_date_textview.setText(medicalProblem.getEndDate());
                    isSufferingCheckbox.setChecked(medicalProblem.isStillSuffering());
                    commentsEditText.setText(medicalProblem.getComments());

                    // Set spinner position
                    String duration = DateTimeUtils.calculateDurationFromStartDate(
                            DateTimeUtils.convertDateSimpleToTimestamp(medicalProblem.getStartDate()));

                    int selectionPosition= adapter.getPosition(duration);
                    durationSpinner.setSelection(selectionPosition);

                    /**
                     * Check isSuffering Flag to display fields (Duration or Start, End Date)
                     */
                    manageIsSufferingDisplay(medicalProblem.isStillSuffering());
                }
            }
        });
    }


    /**
     * Function to bind values to the spinner
     */
    public void bindSpinner() {

        adapter = ArrayAdapter.createFromResource(this, R.array.duration, R.layout.item_spinner_healthbook);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(R.layout.item_spinner_dropdown);
        // Apply the adapter to the spinner
        durationSpinner.setAdapter(adapter);
    }
}