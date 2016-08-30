package com.careons.app.Patient.Activity.Main.HealthBook;

import android.content.Context;
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
import com.careons.app.Patient.Adapters.SearchListAdapter;
import com.careons.app.Patient.Commons.BuildProperties;
import com.careons.app.Patient.Commons.ServiceUrls;
import com.careons.app.Patient.Commons.StaticConstants;
import com.careons.app.Patient.Commons.StringConstants;
import com.careons.app.Patient.Fragments.HealthBook.MedicationFragment;
import com.careons.app.Shared.Interfaces.APIInterface;
import com.careons.app.R;
import com.careons.app.Patient.Services.APICallService;
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

public class AddMedicationHistoryActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, View.OnClickListener {

    private static LinearLayout loader;
    private static TextView emptySearchLayout, errorSearchLayout, internetFailureSearchLayout;

    private LinearLayout addLinearLayout, durationLinearLayout, start_date_layout, end_date_layout, searchBoxLayout;
    private EditText mSearchAddEditText, comments;
    private ListView mAddSearchlistView;
    private Timer timer = new Timer();
    private TextView nameOfMedication, problemHyperlinkTextView, start_date_textview, end_date_textview, mSave;
    private CheckBox contiMedication;
    private Toolbar toolbar;

    private boolean isStartDateClicked = true;
    private Spinner units_spinner, doses_spinner, frequency_spinner, durationSpinner;

    private String previousSearchString = "";
    private final long DELAY = 1000; // in ms
    private static String problemId;

    // Initial problem List
    private static List<HashMap<String, String>> initialMedication = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hb_add_medication);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        //toolbar.setNavigationIcon(R.drawable.ic_arrow);
        toolbar.setTitle(getString(R.string.add_medication));
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));

        // Back enabled
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Status Bar Color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.darker_mh));
        }

        // Initialize loader
        loader = (LinearLayout) findViewById(R.id.loader_search);
        loader.setVisibility(View.GONE);

        // Initialize Search list cases
        emptySearchLayout = (TextView) findViewById(R.id.empty_search);
        errorSearchLayout = (TextView) findViewById(R.id.error_search);
        internetFailureSearchLayout = (TextView) findViewById(R.id.internet_failure_search);


        // Initialize layouts
        addLinearLayout = (LinearLayout) findViewById(R.id.addLinearLayout);
        searchBoxLayout = (LinearLayout) findViewById(R.id.searchBoxLayout);
        durationLinearLayout = (LinearLayout) findViewById(R.id.durationLinearLayout);
        start_date_layout = (LinearLayout) findViewById(R.id.start_date_layout);
        end_date_layout = (LinearLayout) findViewById(R.id.end_date_layout);

        // Initialize Date Fields
        nameOfMedication = (TextView) findViewById(R.id.nameOfMedication);
        problemHyperlinkTextView = (TextView) findViewById(R.id.problem_hyperlink);
        start_date_textview = (TextView) findViewById(R.id.start_date_textview);
        end_date_textview = (TextView) findViewById(R.id.end_date_textview);
        mSave = (TextView) findViewById(R.id.save);
        mAddSearchlistView = (ListView) findViewById(R.id.listViewSerachMP);
        contiMedication = (CheckBox) findViewById(R.id.contiMedication);
        comments = (EditText) findViewById(R.id.comments);
        mSearchAddEditText = (EditText) findViewById(R.id.editTextSearch);

        units_spinner = (Spinner) findViewById(R.id.units_spinner);
        doses_spinner = (Spinner) findViewById(R.id.doses_spinner);
        frequency_spinner = (Spinner) findViewById(R.id.frequency_spinner);
        durationSpinner = (Spinner) findViewById(R.id.duration_spinner);

        durationLinearLayout.setVisibility(View.VISIBLE);
        start_date_layout.setVisibility(View.GONE);
        end_date_layout.setVisibility(View.GONE);

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

        // set Listener
        start_date_textview.setOnClickListener(this);
        end_date_textview.setOnClickListener(this);

        mSave.setOnClickListener(this);
        contiMedication.setOnClickListener(this);

        //bind duration spinner
        bindSpinner();

        /**
         * Handle Search List Text
         */

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
                finish();
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.start_date_textview:
                isStartDateClicked = true;
                openDatePicker();
                break;

            case R.id.end_date_textview:
                isStartDateClicked = false;
                openDatePicker();
                break;
            case R.id.contiMedication:
                if (contiMedication.isChecked()) {
                    durationLinearLayout.setVisibility(View.VISIBLE);
                    start_date_layout.setVisibility(View.GONE);
                    end_date_layout.setVisibility(View.GONE);
                } else {
                    start_date_layout.setVisibility(View.VISIBLE);
                    end_date_layout.setVisibility(View.VISIBLE);
                    durationLinearLayout.setVisibility(View.GONE);
                }
                break;

            case R.id.save:

                if (contiMedication.isChecked()) {
                    if (Validation.isSelectDose(getApplicationContext(), doses_spinner)
                            && Validation.isSelectUnit(getApplicationContext(), units_spinner)
                            && Validation.isSelectFrequency(getApplicationContext(), frequency_spinner)
                            && Validation.isSelectDuration(getApplicationContext(),durationSpinner)) {

                        saveData();
                    }
                } else if (Validation.isSelectDose(getApplicationContext(), doses_spinner)
                        && Validation.isSelectUnit(getApplicationContext(), units_spinner)
                        && Validation.isSelectFrequency(getApplicationContext(), frequency_spinner)
                        && !contiMedication.isChecked() && Validation.validateDate(start_date_textview,
                        getApplicationContext()) && Validation.validateDate(end_date_textview, getApplicationContext())) {

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
    private void saveData() {

        HashMap<String, String> selectedProblemData = new HashMap<>();
        selectedProblemData.put(StringConstants.KEY_MEDICATION_ID, problemId);
        selectedProblemData.put(StringConstants.KEY_MEDICATION_NAME, nameOfMedication.getText().toString());
        selectedProblemData.put(StringConstants.KEY_SEARCH_LIST_HYPERLINK, problemHyperlinkTextView.getText().toString());
        selectedProblemData.put(StringConstants.KEY_MEDICATION_UNITS, units_spinner.getSelectedItem().toString());
        selectedProblemData.put(StringConstants.KEY_MEDICATION_DOSES, doses_spinner.getSelectedItem().toString());
        selectedProblemData.put(StringConstants.KEY_MEDICATION_FREQUENCY, frequency_spinner.getSelectedItem().toString());
        selectedProblemData.put(StringConstants.KEY_MEDICATION_CONTINUING, String.valueOf(contiMedication.isChecked()));

        /**
         * Check for Duration to set Start Date
         */
        if (contiMedication.isChecked()) {

            selectedProblemData.put(StringConstants.KEY_MEDICATION_SINCE_WHEN,
                    String.valueOf(DateTimeUtils.calculateTimestampFromDuration(durationSpinner.getSelectedItemPosition())));
        } else {

            selectedProblemData.put(StringConstants.KEY_MEDICATION_SINCE_WHEN,
                    String.valueOf(DateTimeUtils.convertDateSimpleToTimestamp(start_date_textview.getText().toString())));
        }

        selectedProblemData.put(StringConstants.KEY_MEDICATION_END_DATE,
                String.valueOf(DateTimeUtils.convertDateSimpleToTimestamp(end_date_textview.getText().toString())));
        selectedProblemData.put(StringConstants.KEY_MEDICATION_COMMENTS, comments.getText().toString());


        /**
         * Create Record
         */
        MedicationFragment.createMedication(getApplicationContext(), selectedProblemData);
        finish();
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
        if (isStartDateClicked) {
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
            }else{
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

        dpd.setAccentColor(R.color.hb_medical_problem);
        dpd.showYearPickerFirst(true);

    }


    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        long timestamp = DateTimeUtils.convertTimeToTimestamp(year, monthOfYear, dayOfMonth, 0, 0);

        if (isStartDateClicked) {

            // Start Date
            start_date_textview.setError(null);
            start_date_textview.setText(DateTimeUtils.convertTimestampToDate(timestamp));

        } else {

            // End Date
            end_date_textview.setError(null);
            end_date_textview.setText(DateTimeUtils.convertTimestampToDate(timestamp));
        }
    }


    /**
     * Function to bind values to the spinner
     */
    public void bindSpinner() {
        // No of times
        // Create an ArrayAdapter using the string array and a spinner layout
        ArrayAdapter<CharSequence> adapter1;
        adapter1 = ArrayAdapter.createFromResource(getApplicationContext(), R.array.units, R.layout.item_spinner_healthbook);
        // Specify the layout to use when the list of choices appears
        adapter1.setDropDownViewResource(R.layout.item_spinner_healthbook_dropdown);
        // Apply the adapter to the spinner
        units_spinner.setAdapter(adapter1);

        // Doses
        // Create an ArrayAdapter using the string array and a spinner layout
        ArrayAdapter<CharSequence> adapter2;
        adapter2 = ArrayAdapter.createFromResource(getApplicationContext(), R.array.doses, R.layout.item_spinner_healthbook);
        // Specify the layout to use when the list of choices appears
        adapter2.setDropDownViewResource(R.layout.item_spinner_healthbook_dropdown);
        // Apply the adapter to the spinner
        doses_spinner.setAdapter(adapter2);

        // Frequency
        // Create an ArrayAdapter using the string array and a spinner layout
        ArrayAdapter<CharSequence> adapter3;
        adapter3 = ArrayAdapter.createFromResource(getApplicationContext(), R.array.frequency, R.layout.item_spinner_healthbook);
        // Specify the layout to use when the list of choices appears
        adapter3.setDropDownViewResource(R.layout.item_spinner_healthbook_dropdown);
        // Apply the adapter to the spinner
        frequency_spinner.setAdapter(adapter3);


        // Create an ArrayAdapter using the string array and a spinner layout
        ArrayAdapter<CharSequence> adapter4;
        adapter4 = ArrayAdapter.createFromResource(getApplicationContext(), R.array.duration, R.layout.item_spinner_healthbook);
        // Specify the layout to use when the list of choices appears
        adapter4.setDropDownViewResource(R.layout.item_spinner_healthbook_dropdown);
        // Apply the adapter to the spinner
        durationSpinner.setAdapter(adapter4);
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
                StringConstants.KEY_MEDICINES,
                searchQuery.replace(" ", "%20"),
                StaticConstants.KEY_SEARCH_LIST_LIMIT);

        // Set Headers
        final HashMap<String, String> headers = new HashMap<>();
        headers.put(StaticConstants.KEY_CONTENT_TYPE, StaticConstants.KEY_URLENCODED);
        headers.put(StaticConstants.KEY_HOST, StaticConstants.KEY_HOSTNAME);
        headers.put(StaticConstants.KEY_API_KEY, BuildProperties.azureSearchKey);

        // Clear Data
        initialMedication.clear();

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
                                initialMedication.add(searchResult);


                                /**
                                 * Bind adapter to the result
                                 */
                                mAddSearchlistView.setAdapter(
                                        new SearchListAdapter(AddMedicationHistoryActivity.this, initialMedication,
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
                problemId = addProblemIdTextView.getText().toString();

                // Set selected problem name and Id
                nameOfMedication.setText(initialMedication.get(position).get(StringConstants.KEY_NAME));
                problemHyperlinkTextView.setText(initialMedication.get(position).get(StringConstants.KEY_SEARCH_LIST_HYPERLINK));


                // Shut off keypad
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

                // Input details for the problem
                mAddSearchlistView.setVisibility(View.GONE);
                searchBoxLayout.setVisibility(View.GONE);
                addLinearLayout.setVisibility(View.VISIBLE);
            }
        });
    }
}