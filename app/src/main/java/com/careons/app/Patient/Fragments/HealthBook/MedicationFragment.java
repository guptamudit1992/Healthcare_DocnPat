package com.careons.app.Patient.Fragments.HealthBook;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.careons.app.Patient.Activity.Main.HealthBook.AddMedicationHistoryActivity;
import com.careons.app.Patient.Activity.Main.HealthBook.HealthbookActivity;
import com.careons.app.Patient.Adapters.Adapter;
import com.careons.app.Patient.Adapters.SearchListAdapter;
import com.careons.app.Patient.Commons.BuildProperties;
import com.careons.app.Patient.Commons.ServiceUrls;
import com.careons.app.Patient.Commons.StaticConstants;
import com.careons.app.Patient.Commons.StringConstants;
import com.careons.app.Patient.Database.Models.Healthbook.Medication;
import com.careons.app.Shared.Interfaces.APIInterface;
import com.careons.app.R;
import com.careons.app.Patient.Services.APICallService;
import com.careons.app.Patient.Services.SharedPreferenceService;
import com.careons.app.Shared.Utils.DateTimeUtils;
import com.careons.app.Shared.Utils.ErrorHandlers;
import com.careons.app.Shared.Utils.Utils;
import com.careons.app.Shared.Utils.Validation;
import com.careons.app.databinding.CardMedicationBinding;
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

public class MedicationFragment extends Fragment implements DatePickerDialog.OnDateSetListener {

    private static Activity activity;
    private MedicationFragment context;

    private static LinearLayout loaderSearchLayout;
    private static TextView emptySearchLayout, errorSearchLayout, internetFailureSearchLayout;


    private View rootView;
    private static RecyclerView medicationHistoryList;
    private static LinearLayout loader;
    private static MedicationFragment mMedicationFragment;
    private static FloatingActionButton fab;

    private static Adapter<Medication, CardMedicationBinding> medicationBindingAdapter;
    private static ArrayList<Medication> healthbookMedication = new ArrayList<>();

    // Initial problem List Healthbook Medication
    private static ArrayList<String> initialMedicationList = new ArrayList<>();
    private static String problemId;

    //Add Layout
    private static RelativeLayout addSearchRelativeLayout;
    private static LinearLayout addLinearLayout, durationLinearLayout, start_date_layout, end_date_layout, searchBoxLayout;
    private EditText mSearchAddEditText, comments;
    private static ListView mAddSearchlistView;
    private Timer timer = new Timer();
    private TextView nameOfMedication, problemHyperlinkTextView, start_date_textview, end_date_textview, mSave;
    private CheckBox contiMedication;
    private Toolbar toolbar;
    private boolean isStartDateClicked = true;
    private Spinner units_spinner, doses_spinner, frequency_spinner, durationSpinner;

    private final long DELAY = 1000; // in ms
    private String previousSearchString = "";

    // Initial problem List
    private static List<HashMap<String, String>> initialMedication = new ArrayList<>();


    public MedicationFragment() {
        // Default Constructor
    }


    public static MedicationFragment getInstance() {
        return mMedicationFragment;
    }


    public static MedicationFragment newInstance() {
        mMedicationFragment = new MedicationFragment();
        return mMedicationFragment;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_medication_health_book_placeholder, container, false);
        return rootView;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize Activity
        activity = getActivity();
        context = this;

        // Loader
        loader = (LinearLayout) rootView.findViewById(R.id.loader);
        loader.setVisibility(View.GONE);

        // Loader Search
        loaderSearchLayout = (LinearLayout) rootView.findViewById(R.id.loader_search);
        loaderSearchLayout.setVisibility(View.GONE);

        // Initialize Search list cases
        emptySearchLayout = (TextView) rootView.findViewById(R.id.empty_search);
        errorSearchLayout = (TextView) rootView.findViewById(R.id.error_search);
        internetFailureSearchLayout = (TextView) rootView.findViewById(R.id.internet_failure_search);

        // Floating Action Button
        fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent mIntent = new Intent(getActivity(), AddMedicationHistoryActivity.class);
                startActivityForResult(mIntent, 1000);
            }
        });

        //Set fab Background Color
        fab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.darker_mh)));

        //Set Toolbar Color
        HealthbookActivity.setToolbarColor();

        // Recycler view
        medicationHistoryList = (RecyclerView) rootView.findViewById(R.id.medical_problem_recycler_view);
        addSearchRelativeLayout = (RelativeLayout) rootView.findViewById(R.id.addSearchRelativeLayout);

        List<Medication> healthbookMedicationList = Medication.find(Medication.class,
                "patient_id = ?", SharedPreferenceService.getValue(getContext(), StringConstants.KEY_PATIENT_ID));

        // Initialize medication problem list
        initialMedication = new ArrayList<>();

        // Initialize Add layouts
        addLinearLayout = (LinearLayout) rootView.findViewById(R.id.addLinearLayout);
        searchBoxLayout = (LinearLayout) rootView.findViewById(R.id.searchBoxLayout);
        mAddSearchlistView = (ListView) rootView.findViewById(R.id.listViewSerachMP);
        durationLinearLayout = (LinearLayout) rootView.findViewById(R.id.durationLinearLayout);
        start_date_layout = (LinearLayout) rootView.findViewById(R.id.start_date_layout);
        end_date_layout = (LinearLayout) rootView.findViewById(R.id.end_date_layout);

        // Initialize Date Fields
        nameOfMedication = (TextView) rootView.findViewById(R.id.nameOfMedication);
        problemHyperlinkTextView = (TextView) rootView.findViewById(R.id.problem_hyperlink);
        start_date_textview = (TextView) rootView.findViewById(R.id.start_date_textview);
        end_date_textview = (TextView) rootView.findViewById(R.id.end_date_textview);
        mSave = (TextView) rootView.findViewById(R.id.save);
        mSearchAddEditText = (EditText) rootView.findViewById(R.id.editTextSearch);
        comments =  (EditText) rootView.findViewById(R.id.comments);
        contiMedication = (CheckBox) rootView.findViewById(R.id.contiMedication);

        // Initialize spinners
        units_spinner = (Spinner) rootView.findViewById(R.id.units_spinner);
        doses_spinner = (Spinner) rootView.findViewById(R.id.doses_spinner);
        frequency_spinner = (Spinner) rootView.findViewById(R.id.frequency_spinner);
        durationSpinner = (Spinner) rootView.findViewById(R.id.duration_spinner);

        // Set visibility
        durationLinearLayout.setVisibility(View.VISIBLE);
        start_date_layout.setVisibility(View.GONE);
        end_date_layout.setVisibility(View.GONE);

        // On Focus change listener on start date
        start_date_textview.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    Validation.validateDate(start_date_textview, getContext());
                }
            }
        });

        // On Focus change listener on end date
        end_date_textview.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    Validation.validateDate(end_date_textview, getContext());
                }
            }
        });

        // set listener
        contiMedication.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                manageIsSufferingDisplay(contiMedication.isChecked());
            }
        });


        // Click listener on save
        mSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (contiMedication.isChecked()) {
                    if (Validation.isSelectDose(getContext(), doses_spinner)
                            && Validation.isSelectUnit(getContext(), units_spinner)
                            && Validation.isSelectFrequency(getContext(), frequency_spinner)
                            && Validation.isSelectDuration(getContext(),durationSpinner)) {

                        saveData();
                    }
                } else if (Validation.isSelectDose(getContext(), doses_spinner)
                        && Validation.isSelectUnit(getContext(), units_spinner)
                        && Validation.isSelectFrequency(getContext(), frequency_spinner)
                        && !contiMedication.isChecked() && Validation.validateDate(start_date_textview,
                        getContext()) && Validation.validateDate(end_date_textview, getContext())) {

                    saveData();
                }

            }
        });

        start_date_textview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isStartDateClicked = true;
                openDatePicker();
            }
        });

        end_date_textview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isStartDateClicked = false;
                openDatePicker();
            }
        });


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

        /**
         * Check if database has entries filed else display initial problem list
         */
        if (healthbookMedicationList.size() > 0) {

            addSearchRelativeLayout.setVisibility(View.GONE);
            readMedicationList(getContext());

        } else {

            addSearchRelativeLayout.setVisibility(View.VISIBLE);
            fab.setVisibility(View.GONE);
        }


        /**
         * Function to toggle CTA with keyboard
         */
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {

                List<Medication> medicationList = Medication.find(Medication.class,
                        "patient_id = ?", SharedPreferenceService.getValue(getContext(), StringConstants.KEY_PATIENT_ID));
                Utils.toggleFABKeyboard(rootView, fab, medicationList.size());
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        readMedicationList(getActivity());
    }


    /**
     * Function to save data
     */
    private void saveData(){

        HashMap<String, String> selectedProblemData = new HashMap<>();
        selectedProblemData.put(StringConstants.KEY_MEDICATION_ID, problemId);
        selectedProblemData.put(StringConstants.KEY_MEDICATION_NAME, nameOfMedication.getText().toString());
        selectedProblemData.put(StringConstants.KEY_SEARCH_LIST_HYPERLINK, problemHyperlinkTextView.getText().toString());
        selectedProblemData.put(StringConstants.KEY_MEDICATION_UNITS, units_spinner.getSelectedItem().toString());
        selectedProblemData.put(StringConstants.KEY_MEDICATION_DOSES, doses_spinner.getSelectedItem().toString());
        selectedProblemData.put(StringConstants.KEY_MEDICATION_FREQUENCY, frequency_spinner.getSelectedItem().toString());
        selectedProblemData.put(StringConstants.KEY_MEDICATION_CONTINUING, String.valueOf(contiMedication.isChecked()));
        selectedProblemData.put(StringConstants.KEY_MEDICATION_COMMENTS, comments.getText().toString());
        selectedProblemData.put(StringConstants.KEY_MEDICATION_END_DATE,
                String.valueOf(DateTimeUtils.convertDateSimpleToTimestamp(end_date_textview.getText().toString())));

        /**
         * Check if medication continued
         */
        if (contiMedication.isChecked()) {

            // Case: Continued
            selectedProblemData.put(StringConstants.KEY_MEDICATION_SINCE_WHEN,
                    String.valueOf(DateTimeUtils.calculateTimestampFromDuration(durationSpinner.getSelectedItemPosition())));
        } else {

            // Case: Not Continued
            selectedProblemData.put(StringConstants.KEY_MEDICATION_SINCE_WHEN,
                    String.valueOf(DateTimeUtils.convertDateSimpleToTimestamp(start_date_textview.getText().toString())));
        }

        addSearchRelativeLayout.setVisibility(View.GONE);
        fab.setVisibility(View.VISIBLE);

        createMedication(getActivity(), selectedProblemData);
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
            durationLinearLayout.setVisibility(View.VISIBLE);
            start_date_layout.setVisibility(View.GONE);
            end_date_layout.setVisibility(View.GONE);

        } else {

            // False -> Hide Duration, Show Start and End Date Layout
            durationLinearLayout.setVisibility(View.GONE);
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

        dpd.show(getActivity().getFragmentManager(), StringConstants.KEY_DATEPICKER_DIALOG);

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
     * Function to create medical problem
     *
     * @param context
     */
    public static void createMedication(final Context context, final HashMap<String, String> data) {

        // Set Initial State
        loader.setVisibility(View.VISIBLE);
        medicationHistoryList.setVisibility(View.GONE);

        // Url
        String url = ServiceUrls.KEY_HEALTHBOOK_MEDICATION
                .concat(StringConstants.KEY_CREATE);

        // Set data
        final HashMap<String, Object> postData = new HashMap<>();
        postData.put(StringConstants.KEY_PATIENT_ID, SharedPreferenceService.getValue(context, StringConstants.KEY_PATIENT_ID));
        postData.put(StringConstants.KEY_MEDICATION_ID, data.get(StringConstants.KEY_MEDICATION_ID));
        postData.put(StringConstants.KEY_MEDICATION_NAME, data.get(StringConstants.KEY_MEDICATION_NAME));
        postData.put(StringConstants.KEY_START_DATE,
                DateTimeUtils.convertTimestampToUTC(Long.valueOf(data.get(StringConstants.KEY_MEDICATION_SINCE_WHEN))));
        postData.put(StringConstants.KEY_MEDICATION_UNITS, data.get(StringConstants.KEY_MEDICATION_UNITS));
        postData.put(StringConstants.KEY_MEDICATION_DOSES, data.get(StringConstants.KEY_MEDICATION_DOSES));
        postData.put(StringConstants.KEY_MEDICATION_FREQUENCY, data.get(StringConstants.KEY_MEDICATION_FREQUENCY));
        postData.put(StringConstants.KEY_MEDICATION_CONTINUING,  Boolean.valueOf(data.get(StringConstants.KEY_MEDICATION_CONTINUING)));
        postData.put(StringConstants.KEY_END_DATE,
                DateTimeUtils.convertTimestampToUTC(Long.valueOf(data.get(StringConstants.KEY_MEDICATION_END_DATE))));
        postData.put(StringConstants.KEY_MEDICATION_COMMENTS, data.get(StringConstants.KEY_MEDICATION_COMMENTS));

        // Set Headers
        final HashMap<String, String> headers = new HashMap<>();
        headers.put(StaticConstants.KEY_AUTHORIZATION, StaticConstants.KEY_BEARER
                        .concat(" ")
                        .concat(SharedPreferenceService.getValue(context, StringConstants.KEY_TOKEN)));


        if (Validation.isConnected(context)) {

            /**
             * API Call
             */
            APICallService.PostAPICall(activity, context, url, postData, headers, new APIInterface() {
                @Override
                public void onSuccess(JSONObject response) {

                    try {

                        /**
                         * Check Start Date and End Date
                         */
                        String startDate = data.get(StringConstants.KEY_MEDICATION_SINCE_WHEN);
                        if(!startDate.isEmpty()) {
                            startDate = DateTimeUtils.convertTimestampToDate(Long.valueOf(startDate));
                        }

                        String endDate = data.get(StringConstants.KEY_MEDICATION_END_DATE);
                        if(!endDate.isEmpty()) {
                            endDate = DateTimeUtils.convertTimestampToDate(Long.valueOf(endDate));
                        }


                        // Create in Local Database
                        Medication medication = new Medication(
                                SharedPreferenceService.getValue(context, StringConstants.KEY_PATIENT_ID),
                                response.getString(StringConstants.KEY_P_ID),
                                data.get(StringConstants.KEY_MEDICATION_ID),
                                data.get(StringConstants.KEY_MEDICATION_NAME),
                                data.get(StringConstants.KEY_SEARCH_LIST_HYPERLINK),
                                startDate,
                                data.get(StringConstants.KEY_MEDICATION_UNITS),
                                data.get(StringConstants.KEY_MEDICATION_DOSES),
                                data.get(StringConstants.KEY_MEDICATION_FREQUENCY),
                                Boolean.valueOf(data.get(StringConstants.KEY_MEDICATION_CONTINUING)),
                                "",
                                endDate,
                                data.get(StringConstants.KEY_MEDICATION_COMMENTS)
                        );

                        // Save to Database Table
                        medication.save();

                        // Read medical problem list
                        readMedicationList(context);

                    } catch (Exception e) {

                        // Show Content
                        loader.setVisibility(View.GONE);
                        medicationHistoryList.setVisibility(View.VISIBLE);

                        // Log error
                        ErrorHandlers.handleError(activity);
                        Crashlytics.logException(e);
                    }
                }

                @Override
                public void onError(VolleyError error) {

                    // Show Content
                    loader.setVisibility(View.GONE);
                    medicationHistoryList.setVisibility(View.VISIBLE);
                }
            });

        } else {

            // Show Content
            loader.setVisibility(View.GONE);
            medicationHistoryList.setVisibility(View.VISIBLE);

            // Internet Not Available
            ErrorHandlers.handleInternetConnectionFailure(activity);
        }
    }


    /**
     * Function to fetch Medication problem data
     */
    public static void readMedicationList(final Context context) {

        // Empty object holder
        healthbookMedication.clear();


        // Read from database table
        List<Medication> healthbookMedicationList = Medication.find(Medication.class,
                "patient_id = ?", SharedPreferenceService.getValue(context, StringConstants.KEY_PATIENT_ID));

        /**
         * Check if database has entries filed
         */
        if (healthbookMedicationList.size() > 0) {

            for (int i = 0; i < healthbookMedicationList.size(); i++) {

                Medication medication = healthbookMedicationList.get(i);

                /**
                 * Check if Suffering -> Calculate Duration and Set End Date to null
                 */
                if(medication.isContinuingMedication()) {

                    String duration = DateTimeUtils.calculateDurationFromStartDate(
                            DateTimeUtils.convertDateSimpleToTimestamp(medication.getSinceWhen()));

                    medication.setDuration(duration);
                    medication.setEndDate("");
                }

                // Add to medication list
                healthbookMedication.add(medication);
            }

            /**
             * Save Count in Shared Preference
             */
            SharedPreferenceService.storeUserDetails(context, StringConstants.KEY_MEDICATION_COUNT,
                    String.valueOf(healthbookMedicationList.size()));


            // Set adapter
            medicationBindingAdapter =
                    new Adapter<>(healthbookMedication, R.layout.card_medication, StaticConstants.MEDICATION_ADAPTER);

            medicationHistoryList.setLayoutManager(new LinearLayoutManager(context));
            medicationHistoryList.setAdapter(medicationBindingAdapter);


            // Show content
            loader.setVisibility(View.GONE);
            medicationHistoryList.setVisibility(View.VISIBLE);

        } else {

            addSearchRelativeLayout.setVisibility(View.VISIBLE);
            fab.setVisibility(View.GONE);
            loader.setVisibility(View.GONE);

            // Input details for the problem VISIBLE
            mAddSearchlistView.setVisibility(View.VISIBLE);
            searchBoxLayout.setVisibility(View.VISIBLE);
            addLinearLayout.setVisibility(View.GONE);
        }
    }


    /**
     * Function to updateMedicalHistoryProblem medical problem
     *
     * @param context
     * @param data
     */
    public static void updateMedication(final Context context, final HashMap<String, String> data) {

        // Set Initial State
        loader.setVisibility(View.VISIBLE);
        medicationHistoryList.setVisibility(View.GONE);

        // Url
        String url = ServiceUrls.KEY_HEALTHBOOK_MEDICATION
                .concat(StringConstants.KEY_UPDATE)
                .concat(data.get(StringConstants.KEY_P_ID));

        // Set Data
        final HashMap<String, Object> postData = new HashMap<>();
        postData.put(StringConstants.KEY_MEDICATION_ID, Integer.valueOf(data.get(StringConstants.KEY_MEDICATION_ID)));
        postData.put(StringConstants.KEY_MEDICATION_NAME, data.get(StringConstants.KEY_MEDICATION_NAME));
        postData.put(StringConstants.KEY_START_DATE,
                DateTimeUtils.convertTimestampToUTC(Long.valueOf(data.get(StringConstants.KEY_MEDICATION_SINCE_WHEN))));
        postData.put(StringConstants.KEY_MEDICATION_UNITS, data.get(StringConstants.KEY_MEDICATION_UNITS));
        postData.put(StringConstants.KEY_MEDICATION_DOSES, data.get(StringConstants.KEY_MEDICATION_DOSES));
        postData.put(StringConstants.KEY_MEDICATION_FREQUENCY, data.get(StringConstants.KEY_MEDICATION_FREQUENCY));
        postData.put(StringConstants.KEY_MEDICATION_CONTINUING,  Boolean.valueOf(data.get(StringConstants.KEY_MEDICATION_CONTINUING)));
        postData.put(StringConstants.KEY_END_DATE,
                DateTimeUtils.convertTimestampToUTC(Long.valueOf(data.get(StringConstants.KEY_MEDICATION_END_DATE))));
        postData.put(StringConstants.KEY_MEDICATION_COMMENTS, data.get(StringConstants.KEY_MEDICATION_COMMENTS));

        // Set Headers
        final HashMap<String, String> headers = new HashMap<>();
        headers.put(StaticConstants.KEY_AUTHORIZATION,
                StaticConstants.KEY_BEARER.concat(" ")
                        .concat(SharedPreferenceService.getValue(context, StringConstants.KEY_TOKEN)));

        if (Validation.isConnected(context)) {

            // API Call
            APICallService.PutAPICall(activity, context, url, postData, headers, new APIInterface() {
                @Override
                public void onSuccess(JSONObject response) {

                    try {

                        /**
                         * Check Start Date and End Date
                         */
                        String startDate = data.get(StringConstants.KEY_MEDICATION_SINCE_WHEN);
                        if(!startDate.isEmpty()) {
                            startDate = DateTimeUtils.convertTimestampToDate(Long.valueOf(startDate));
                        }

                        String endDate = data.get(StringConstants.KEY_MEDICATION_END_DATE);
                        if(!endDate.isEmpty()) {
                            endDate = DateTimeUtils.convertTimestampToDate(Long.valueOf(endDate));
                        }


                        // Update in Local Database
                        Medication healthbookMedication =
                                Medication.find(Medication.class,
                                        "p_id = ?", data.get(StringConstants.KEY_P_ID)).get(0);

                        healthbookMedication.setSinceWhen(startDate);
                        healthbookMedication.setUnits(data.get(StringConstants.KEY_MEDICATION_UNITS));
                        healthbookMedication.setDoses(data.get(StringConstants.KEY_MEDICATION_DOSES));

                        healthbookMedication.setFrequency(data.get(StringConstants.KEY_MEDICATION_FREQUENCY));
                        healthbookMedication.setContinuingMedication(Boolean.valueOf(data.get(StringConstants.KEY_MEDICATION_CONTINUING)));
                        healthbookMedication.setDuration(data.get(StringConstants.KEY_MEDICATION_DUURATION));

                        healthbookMedication.setEndDate(endDate);
                        healthbookMedication.setComments(data.get(StringConstants.KEY_MEDICATION_COMMENTS));
                        healthbookMedication.save();

                        // Notify adapter
                        readMedicationList(context);

                    } catch (Exception e) {

                        // Show Content
                        loader.setVisibility(View.GONE);
                        medicationHistoryList.setVisibility(View.VISIBLE);

                        // Log error
                        ErrorHandlers.handleError(activity);
                        Crashlytics.logException(e);
                    }
                }

                @Override
                public void onError(VolleyError error) {

                    // Show Content
                    loader.setVisibility(View.GONE);
                    medicationHistoryList.setVisibility(View.VISIBLE);
                }
            });

        } else {

            // Show Content
            loader.setVisibility(View.GONE);
            medicationHistoryList.setVisibility(View.VISIBLE);

            // Internet Not Available
            ErrorHandlers.handleInternetConnectionFailure(activity);
        }
    }


    /**
     * Function to delete a medical problem
     *
     * @param context
     * @param pId
     */
    public static void deleteMedicationHistory(final Context context, final String pId) {

        // Set Initial State
        loader.setVisibility(View.VISIBLE);
        medicationHistoryList.setVisibility(View.GONE);

        // Url Creation
        String url = ServiceUrls.KEY_HEALTHBOOK_MEDICATION
                .concat(StringConstants.KEY_DELETE)
                .concat(StringConstants.KEY_SEPARATOR)
                .concat(pId);

        // Set Headers
        final HashMap<String, String> headers = new HashMap<>();
        headers.put(StaticConstants.KEY_AUTHORIZATION,
                StaticConstants.KEY_BEARER
                        .concat(" ")
                        .concat(SharedPreferenceService.getValue(context, StringConstants.KEY_TOKEN)));


        if (Validation.isConnected(context)) {

            // API Call
            APICallService.DeleteAPICall(activity, context, url, headers, new APIInterface() {
                @Override
                public void onSuccess(JSONObject response) {

                    try {

                        // Delete from local database
                        Medication healthbookMedication = Medication.find(Medication.class, "p_id = ?", pId).get(0);
                        healthbookMedication.delete();

                        // Notify adapter
                        readMedicationList(context);


                    } catch (Exception e) {

                        // Show Content
                        loader.setVisibility(View.GONE);
                        medicationHistoryList.setVisibility(View.VISIBLE);

                        // Log error
                        ErrorHandlers.handleError(activity);
                        Crashlytics.logException(e);
                    }
                }

                @Override
                public void onError(VolleyError error) {

                    // Show Content
                    loader.setVisibility(View.GONE);
                    medicationHistoryList.setVisibility(View.VISIBLE);
                }
            });

        } else {

            // Show Content
            loader.setVisibility(View.GONE);
            medicationHistoryList.setVisibility(View.VISIBLE);

            // Internet Not Available
            ErrorHandlers.handleInternetConnectionFailure(activity);
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

                            getActivity().runOnUiThread(new Runnable() {
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
        loaderSearchLayout.setVisibility(View.VISIBLE);
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

        if (Validation.isConnected(getActivity())) {

            // API Call
            APICallService.GetAPICall(activity, getActivity(), url, null, headers, new APIInterface() {

                @Override
                public void onSuccess(JSONObject response) {

                    try {

                        if (response.has(StringConstants.KEY_VALUES)) {

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
                                        new SearchListAdapter(getActivity(), initialMedication,
                                                StaticConstants.MEDICAL_PROBLEM_ADAPTER));
                            }

                            // Show Content
                            loaderSearchLayout.setVisibility(View.GONE);
                            mAddSearchlistView.setVisibility(View.VISIBLE);

                        } else {

                            // No result found
                            loaderSearchLayout.setVisibility(View.GONE);
                            emptySearchLayout.setVisibility(View.VISIBLE);
                        }
                    } catch (JSONException e) {

                        // Log error
                        Crashlytics.logException(e);

                        // Show Content
                        loaderSearchLayout.setVisibility(View.GONE);
                        errorSearchLayout.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onError(VolleyError error) {

                    // Show Content
                    loaderSearchLayout.setVisibility(View.GONE);
                    errorSearchLayout.setVisibility(View.VISIBLE);
                }
            });

        } else {

            // If internet connection is not available
            loaderSearchLayout.setVisibility(View.GONE);
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
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

                // Input details for the problem
                mAddSearchlistView.setVisibility(View.GONE);
                addLinearLayout.setVisibility(View.VISIBLE);
                searchBoxLayout.setVisibility(View.GONE);
            }
        });
    }


    /**
     * Function to bind values to the spinner
     */
    public void bindSpinner() {
        // No of times
        // Create an ArrayAdapter using the string array and a spinner layout
        ArrayAdapter<CharSequence> adapter1;
        adapter1 = ArrayAdapter.createFromResource(getActivity(), R.array.units, R.layout.item_spinner_healthbook);
        // Specify the layout to use when the list of choices appears
        adapter1.setDropDownViewResource(R.layout.item_spinner_healthbook_dropdown);
        // Apply the adapter to the spinner
        units_spinner.setAdapter(adapter1);

        // Doses
        // Create an ArrayAdapter using the string array and a spinner layout
        ArrayAdapter<CharSequence> adapter2;
        adapter2 = ArrayAdapter.createFromResource(getActivity(), R.array.doses, R.layout.item_spinner_healthbook);
        // Specify the layout to use when the list of choices appears
        adapter2.setDropDownViewResource(R.layout.item_spinner_healthbook_dropdown);
        // Apply the adapter to the spinner
        doses_spinner.setAdapter(adapter2);

        // Frequency
        // Create an ArrayAdapter using the string array and a spinner layout
        ArrayAdapter<CharSequence> adapter3;
        adapter3 = ArrayAdapter.createFromResource(getActivity(), R.array.frequency, R.layout.item_spinner_healthbook);
        // Specify the layout to use when the list of choices appears
        adapter3.setDropDownViewResource(R.layout.item_spinner_healthbook_dropdown);
        // Apply the adapter to the spinner
        frequency_spinner.setAdapter(adapter3);


        // Create an ArrayAdapter using the string array and a spinner layout
        ArrayAdapter<CharSequence> adapter4;
        adapter4 = ArrayAdapter.createFromResource(getActivity(), R.array.duration, R.layout.item_spinner_healthbook);
        // Specify the layout to use when the list of choices appears
        adapter4.setDropDownViewResource(R.layout.item_spinner_healthbook_dropdown);
        // Apply the adapter to the spinner
        durationSpinner.setAdapter(adapter4);
    }





    /**
     * Function to set FAB visibility
     * @param visibility
     */
    public static void setFABVisibility(boolean visibility){
        fab.setVisibility(visibility == true ? View.VISIBLE : View.GONE);
    }
}
