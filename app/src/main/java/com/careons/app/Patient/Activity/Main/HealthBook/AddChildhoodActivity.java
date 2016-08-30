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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.careons.app.Patient.Adapters.SearchListAdapter;
import com.careons.app.Patient.Commons.BuildProperties;
import com.careons.app.Patient.Commons.ServiceUrls;
import com.careons.app.Patient.Commons.StaticConstants;
import com.careons.app.Patient.Commons.StringConstants;
import com.careons.app.Patient.Database.Models.Healthbook.ChildhoodHistory;
import com.careons.app.Patient.Fragments.HealthBook.ChildhoodFragment;
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

public class AddChildhoodActivity extends AppCompatActivity
        implements DatePickerDialog.OnDateSetListener, View.OnClickListener {

    private static ChildhoodHistory childhoodHistory;

    private static LinearLayout loader;
    private static TextView emptySearchLayout, errorSearchLayout, internetFailureSearchLayout;

    private LinearLayout addLinearLayout, searchBarLayout;
    private Toolbar toolbar;
    private ListView mAddSearchlistView;
    private TextView problemIdTextView, problemNameTextView, problemHyperlinkTextView, saveContentTextView, editableTextViewDate;
    private EditText mSearchAddEditText, commentsEditText;
    private CheckBox isSufferingCheckbox;

    private Timer timer = new Timer();
    private final long DELAY = 1000; // in ms
    private String previousSearchString = "";


    // Initial problem List
    private static List<HashMap<String, String>> initialChildhoodHistoryList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hb_add_childhood_history);

        // To handle back button
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.add_childhood_history));
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));

        // Status Bar Color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.darker_ch));
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

        // Initialize fields
        mAddSearchlistView = (ListView) findViewById(R.id.listViewSerachMP);
        problemIdTextView = (TextView) findViewById(R.id.problem_id);
        problemNameTextView = (TextView) findViewById(R.id.problem_name);
        problemHyperlinkTextView = (TextView) findViewById(R.id.problem_hyperlink);
        editableTextViewDate = (TextView) findViewById(R.id.editableTextViewDate);
        saveContentTextView = (TextView) findViewById(R.id.save_content);
        commentsEditText = (EditText) findViewById(R.id.comments);
        isSufferingCheckbox = (CheckBox) findViewById(R.id.isSufferingCheckbox);

        // Set on click listeners
        saveContentTextView.setOnClickListener(this);
        editableTextViewDate.setOnClickListener(this);


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
                finish();
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.editableTextViewDate:
                openDatePicker();
                break;

            case R.id.save_content:
                if (Validation.validateStartDate(editableTextViewDate, getApplicationContext())) {
                    // Populate object with Data
                    HashMap<String, String> selectedProblemData = new HashMap<>();
                    selectedProblemData.put(StringConstants.KEY_PROBLEMS_ID, problemIdTextView.getText().toString());
                    selectedProblemData.put(StringConstants.KEY_PROBLEM_NAME, problemNameTextView.getText().toString());
                    selectedProblemData.put(StringConstants.KEY_SEARCH_LIST_HYPERLINK, problemHyperlinkTextView.getText().toString());
                    selectedProblemData.put(StringConstants.KEY_START_DATE,
                            String.valueOf(DateTimeUtils.convertDateSimpleToTimestamp(editableTextViewDate.getText().toString())));
                    selectedProblemData.put(StringConstants.KEY_IS_SUFFERING, String.valueOf(isSufferingCheckbox.isChecked()));
                    selectedProblemData.put(StringConstants.KEY_COMMENT, commentsEditText.getText().toString());

                    // Check if record already exists
                    if (ChildhoodHistory.find(ChildhoodHistory.class, "patient_id = ? and problem_Id = ?",
                            SharedPreferenceService.getValue(getApplicationContext(), StringConstants.KEY_PATIENT_ID),
                            problemIdTextView.getText().toString()).size() > 0) {

                        // Found
                        selectedProblemData.put(StringConstants.KEY_P_ID, childhoodHistory.getpId());
                        ChildhoodFragment.updateChildhoodHistory(getApplicationContext(), selectedProblemData);

                    } else {

                        // Not Found
                        ChildhoodFragment.createChildhoodHistory(getApplicationContext(), selectedProblemData);
                    }

                    // Navigate back to calling activity to see updated list
                    finish();
                }
                break;

            default:
                break;
        }
    }


    /**
     * Function to implement date picker
     */
    public void openDatePicker() {

        Calendar now = Calendar.getInstance();
        DatePickerDialog dpd = DatePickerDialog.newInstance(AddChildhoodActivity.this,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );
        dpd.show(getFragmentManager(), StringConstants.KEY_DATEPICKER_DIALOG);
        //dpd.setAccentColor(R.color.hb_childhood_history);
        dpd.showYearPickerFirst(true);
        dpd.setMaxDate(now);
        dpd.setYearRange(StaticConstants.MIN_DATE, now.get(Calendar.YEAR));
    }


    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {

        long startDateEpoch = DateTimeUtils.convertTimeToTimestamp(year, monthOfYear, dayOfMonth, 0, 0);
        editableTextViewDate.setError(null);
        editableTextViewDate.setText(DateTimeUtils.convertTimestampToDate(startDateEpoch));
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
                                            fetchSearchResults(searchString);
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
                StringConstants.KEY_PROBLEMS,
                searchQuery.replace(" ", "%20"),
                StaticConstants.KEY_SEARCH_LIST_LIMIT);

        // Set Headers
        final HashMap<String, String> headers = new HashMap<>();
        headers.put(StaticConstants.KEY_CONTENT_TYPE, StaticConstants.KEY_URLENCODED);
        headers.put(StaticConstants.KEY_HOST, StaticConstants.KEY_HOSTNAME);
        headers.put(StaticConstants.KEY_API_KEY, BuildProperties.azureSearchKey);

        // Clear Data
        initialChildhoodHistoryList.clear();

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
                                initialChildhoodHistoryList.add(searchResult);

                                /**
                                 * Bind adapter to the result
                                 */
                                mAddSearchlistView.setAdapter(
                                        new SearchListAdapter(AddChildhoodActivity.this, initialChildhoodHistoryList,
                                                StaticConstants.CHILDHOOD_ADAPTER));
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

                // Set selected problem name and Id
                problemNameTextView.setText(initialChildhoodHistoryList.get(position).get(StringConstants.KEY_NAME));
                problemIdTextView.setText(initialChildhoodHistoryList.get(position).get(StringConstants.KEY_ID));
                problemHyperlinkTextView.setText(initialChildhoodHistoryList.get(position).get(StringConstants.KEY_SEARCH_LIST_HYPERLINK));


                // Shut off keypad
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

                // Input details for the problem
                searchBarLayout.setVisibility(View.GONE);
                mAddSearchlistView.setVisibility(View.GONE);
                addLinearLayout.setVisibility(View.VISIBLE);



                /**
                 * Check problem id in local database
                 */
                List<ChildhoodHistory> childhoodHistoryList = ChildhoodHistory.find(ChildhoodHistory.class, "patient_id = ? and problem_id = ?",
                        SharedPreferenceService.getValue(getApplicationContext(), StringConstants.KEY_PATIENT_ID),
                        problemId);


                // See if record already exists to prepopulate data
                if (childhoodHistoryList.size() > 0) {

                    // Record Found
                    childhoodHistory = childhoodHistoryList.get(0);

                    editableTextViewDate.setText(childhoodHistory.getStartDate());
                    isSufferingCheckbox.setChecked(childhoodHistory.isStillSuffering());
                    commentsEditText.setText(childhoodHistory.getComments());
                }
            }
        });
    }
}