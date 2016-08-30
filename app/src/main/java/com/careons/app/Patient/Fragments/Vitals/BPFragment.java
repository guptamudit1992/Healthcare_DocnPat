package com.careons.app.Patient.Fragments.Vitals;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.careons.app.Patient.Adapters.Adapter;
import com.careons.app.Patient.Commons.ServiceUrls;
import com.careons.app.Patient.Commons.StaticConstants;
import com.careons.app.Patient.Commons.StringConstants;
import com.careons.app.Patient.Database.Models.Vitals.BloodPressure;
import com.careons.app.Shared.Interfaces.APIInterface;
import com.careons.app.R;
import com.careons.app.Patient.Services.APICallService;
import com.careons.app.Patient.Services.SharedPreferenceService;
import com.careons.app.Shared.Utils.DateTimeUtils;
import com.careons.app.Shared.Utils.ErrorHandlers;
import com.careons.app.Shared.Utils.Validation;
import com.careons.app.Shared.Utils.VitalUtils;
import com.careons.app.databinding.ListItemVitalsBpBinding;
import com.crashlytics.android.Crashlytics;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;

public class BPFragment extends Fragment
        implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    private static Activity activity;
    private static BPFragment bpFragment;
    private static View rootView;
    private static RecyclerView vitalListRecyclerView;
    private static LinearLayout loader, contentLinearLayout;
    private static TextView emptyListTextView, dateTextView, timeTextView, saveEntryTextView;
    private static EditText systolicEdittext, diastolicEditText;
    private static ImageView cancelEntryImageView;

    private static Adapter<BloodPressure, ListItemVitalsBpBinding> bloodPressureBindingAdapter;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_blood_pressure, container, false);

        // Initialize fragment
        activity = getActivity();
        bpFragment = this;

        loader = (LinearLayout) rootView.findViewById(R.id.loader);
        emptyListTextView = (TextView) rootView.findViewById(R.id.vitals_bp_empty);
        contentLinearLayout = (LinearLayout) rootView.findViewById(R.id.vitals_bp_content);
        vitalListRecyclerView = (RecyclerView) rootView.findViewById(R.id.vitals_recycler_view);

        /**
         * Fetch and Read Data
         */
        readVitalsList(getActivity(), StaticConstants.SOURCE_READ);
        VitalFragment.renderGraph();

        // Add Vitals
        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCreateDialog(getActivity());
            }
        });

        return rootView;
    }


    public static BPFragment newInstance() {
        return new BPFragment();
    }


    /**
     * Function to fetch instance of the current activity
     * @return
     */
    public static BPFragment getInstance() {
        return bpFragment;
    }


    /**
     * Function to display create entry dialog box
     */
    public void showCreateDialog(final Activity activity) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_vitals_bp, null);
        builder.setView(dialogView);
        final AlertDialog dialog = builder.create();
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        // Set Vertical ScrollBar Enabled
        dialogView.findViewById(R.id.scrollBar).setVerticalScrollBarEnabled(false);

        // Initialize Data Fields
        dateTextView = (TextView) dialogView.findViewById(R.id.date_textview);
        timeTextView = (TextView) dialogView.findViewById(R.id.time_textview);
        dateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDatePicker();
            }
        });
        timeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openTimePicker();
            }
        });

        // Substitute current date and time
        dateTextView.setText(DateTimeUtils.getSimpleDate(getActivity()));
        timeTextView.setText(DateTimeUtils.getTime(getActivity()));




        // Save Entry to Vitals
        saveEntryTextView = (TextView) dialogView.findViewById(R.id.save_entry);
        saveEntryTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Initialize data fields
                systolicEdittext = (EditText) dialogView.findViewById(R.id.systolic_edittext);
                diastolicEditText = (EditText) dialogView.findViewById(R.id.diastolic_edittext);


                if(systolicEdittext.getText().toString().isEmpty()) {

                    // Systolic BP not entered
                    systolicEdittext.setError(activity.getResources().getString(R.string.required));

                } else if (diastolicEditText.getText().toString().isEmpty()) {

                    // Diastolic BP not entered
                    diastolicEditText.setError(activity.getResources().getString(R.string.required));

                } else {

                    // Create vitals entry
                    HashMap<String, String> data = new HashMap();
                    data.put(StringConstants.KEY_SYSTOLIC_BP, systolicEdittext.getText().toString());
                    data.put(StringConstants.KEY_DIASTOLIC_BP, diastolicEditText.getText().toString());
                    data.put(StringConstants.KEY_TAG,
                            VitalUtils.calculateBPRangeTag(
                                    getActivity(),
                                    Integer.parseInt(systolicEdittext.getText().toString()),
                                    Integer.parseInt(diastolicEditText.getText().toString())));

                    data.put(StringConstants.KEY_DATE, dateTextView.getText().toString());
                    data.put(StringConstants.KEY_TIME, timeTextView.getText().toString());
                    data.put(StringConstants.KEY_TIMESTAMP,
                            String.valueOf(
                                DateTimeUtils.convertDateToTimestamp(
                                    data.get(StringConstants.KEY_DATE)
                                    .concat(" ")
                                    .concat(data.get(StringConstants.KEY_TIME))
                                )
                            )
                    );
                    createVitalsEntry(activity.getApplicationContext(), data);

                    // Dismiss dialog
                    dialog.dismiss();
                }
            }
        });

        // Cancel
        cancelEntryImageView = (ImageView) dialogView.findViewById(R.id.cancel);
        cancelEntryImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }


    /**
     * Function to implement date picker
     */
    public void openDatePicker() {

        Calendar now = Calendar.getInstance();
        DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(
                this,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );

        datePickerDialog.setMaxDate(now);
        datePickerDialog.setYearRange(StaticConstants.MIN_DATE, now.get(Calendar.YEAR));
        datePickerDialog.show(
                getActivity().getFragmentManager(), StringConstants.KEY_DATEPICKER_DIALOG);
        datePickerDialog.showYearPickerFirst(true);
    }


    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {

        // Convert to timestamp
        long timestamp = DateTimeUtils.convertTimeToTimestamp(year, monthOfYear, dayOfMonth, 0, 0);

        // Convert timestamp to Date
        dateTextView.setText(DateTimeUtils.convertTimestampToDate(timestamp));
    }


    /**
     * Function to implement time picker
     */
    public void openTimePicker() {

        Calendar now = Calendar.getInstance();
        TimePickerDialog timePickerDialog = TimePickerDialog.newInstance(
                this,
                now.get(Calendar.HOUR_OF_DAY),
                now.get(Calendar.MINUTE),
                false
        );

        timePickerDialog.show(getActivity().getFragmentManager(), StringConstants.KEY_TIMEPICKER_DIALOG);
    }


    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute, int second)  {

        // Convert to timestamp
        long timestamp = DateTimeUtils.convertTimeToTimestamp(0, 0, 0, hourOfDay, minute);
        String displayTime;
        String hour, min;


        // Check minute display
        if(minute < 10) {
            min = "0" + String.valueOf(minute);
        } else {
            min = String.valueOf(minute);
        }

        // Check for am/pm
        if(hourOfDay > 12) {

            if((hourOfDay - 12) < 10) {
                displayTime = "0" + (hourOfDay-12) + ":" + min + ":00 " + getString(R.string.pm);
            } else {
                displayTime = (hourOfDay-12) + ":" + min + ":00 " + getString(R.string.pm);
            }

        } else {

            if(hourOfDay < 10) {
                displayTime = "0" + hourOfDay + ":" + min + ":00 " + getString(R.string.am);
            } else {
                displayTime = hourOfDay + ":" + min + ":00 " + getString(R.string.am);
            }
        }

        // Convert timestamp to Time
        timeTextView.setText(displayTime);
    }





    /**
     * Function to create vitals entry
     * @param context
     */
    public static void createVitalsEntry(final Context context, final HashMap<String, String> data) {

        // Set initial states
        loader.setVisibility(View.VISIBLE);
        contentLinearLayout.setVisibility(View.GONE);
        emptyListTextView.setVisibility(View.GONE);


        /**
         * Call API to create
         */
        if (Validation.isConnected(context)) {

            // Url Formation
            String url = ServiceUrls.KEY_BLOOD_PRESSURE.concat(StringConstants.KEY_CREATE);

            // Set data
            final HashMap<String, Object> postData = new HashMap<>();
            postData.put(StringConstants.KEY_PATIENT_ID, SharedPreferenceService.getValue(context, StringConstants.KEY_PATIENT_ID));
            postData.put(StringConstants.KEY_SYSTOLIC_BP, data.get(StringConstants.KEY_SYSTOLIC_BP));
            postData.put(StringConstants.KEY_DIASTOLIC_BP, data.get(StringConstants.KEY_DIASTOLIC_BP));
            postData.put(StringConstants.KEY_TAG, data.get(StringConstants.KEY_TAG));
            postData.put(StringConstants.KEY_DATE, DateTimeUtils.convertTimestampToUTC(Long.valueOf(data.get(StringConstants.KEY_TIMESTAMP))));
            postData.put(StringConstants.KEY_TIME, DateTimeUtils.convertTimestampToUTCTime(Long.valueOf(data.get(StringConstants.KEY_TIMESTAMP))));
            postData.put(StringConstants.KEY_TIMESTAMP, Long.valueOf(data.get(StringConstants.KEY_TIMESTAMP)));

            // Set Headers
            final HashMap<String, String> headers = new HashMap<>();
            headers.put(StaticConstants.KEY_AUTHORIZATION,
                    StaticConstants.KEY_BEARER
                            .concat(" ")
                            .concat(SharedPreferenceService.getValue(context, StringConstants.KEY_TOKEN)));

            /**
             * API Call
             */
            APICallService.PostAPICall(activity, context, url, postData, headers, new APIInterface() {
                @Override
                public void onSuccess(JSONObject response) {

                    try {

                        // Create in Local Database
                        BloodPressure bloodPressure = new BloodPressure(
                                SharedPreferenceService.getValue(context, StringConstants.KEY_PATIENT_ID),
                                response.getString(StringConstants.KEY_BP_ID),
                                data.get(StringConstants.KEY_SYSTOLIC_BP),
                                data.get(StringConstants.KEY_DIASTOLIC_BP),
                                data.get(StringConstants.KEY_TAG),
                                data.get(StringConstants.KEY_DATE),
                                data.get(StringConstants.KEY_TIME),
                                Long.valueOf(data.get(StringConstants.KEY_TIMESTAMP))
                        );

                        bloodPressure.save();

                        // Read vitals
                        readVitalsList(context, StaticConstants.SOURCE_CREATE);

                    } catch (Exception e) {

                        // Show Content
                        loader.setVisibility(View.GONE);
                        contentLinearLayout.setVisibility(View.VISIBLE);

                        // Log error
                        Crashlytics.logException(e);
                        ErrorHandlers.handleError(activity);
                    }
                }

                @Override
                public void onError(VolleyError error) {

                    // Show Content
                    loader.setVisibility(View.GONE);
                    contentLinearLayout.setVisibility(View.VISIBLE);
                }
            });
        } else {

            // Show Content
            loader.setVisibility(View.GONE);
            contentLinearLayout.setVisibility(View.VISIBLE);

            // Show error
            ErrorHandlers.handleInternetConnectionFailure(activity);
        }
    }


    /**
     * Function to fetch vitals data
     * @param context
     */
    public static void readVitalsList(final Context context, final int source) {

        // Set initial states
        loader.setVisibility(View.VISIBLE);
        contentLinearLayout.setVisibility(View.GONE);
        emptyListTextView.setVisibility(View.GONE);

        /**
         * Read from database table
         */
        ArrayList<BloodPressure> bloodPressureList = (ArrayList<BloodPressure>) BloodPressure.find(BloodPressure.class,
                "patient_id = ?", SharedPreferenceService.getValue(context, StringConstants.KEY_PATIENT_ID));

        /**
         * Render Graph in case if (Create, Update, Delete)
         */
        if(source == StaticConstants.SOURCE_CREATE
                || source == StaticConstants.SOURCE_UPDATE
                || source == StaticConstants.SOURCE_DELETE) {

            VitalFragment.renderGraphBloodPressure(bloodPressureList);
        }


        /**
         * Show first data
         */
        if(bloodPressureList.size() > 0) {

            // Sort list before binding
            Collections.sort(bloodPressureList);

            // Set adapter
            bloodPressureBindingAdapter = new Adapter<>(bloodPressureList, R.layout.list_item_vitals_bp,
                            StaticConstants.VITALS_BLOOD_PRESSURE_ADAPTER);

            vitalListRecyclerView.setLayoutManager(new LinearLayoutManager(context));
            vitalListRecyclerView.setAdapter(bloodPressureBindingAdapter);

            // Show entries
            loader.setVisibility(View.GONE);
            contentLinearLayout.setVisibility(View.VISIBLE);

        } else {

            // In case of no entries
            loader.setVisibility(View.GONE);
            emptyListTextView.setVisibility(View.VISIBLE);
        }
    }


    /**
     * Function to update a vitals entry
     * @param context
     * @param data
     */
    public static void updateVitalsEntry(final Context context, final HashMap<String, String> data) {

        // Set initial states
        loader.setVisibility(View.VISIBLE);
        contentLinearLayout.setVisibility(View.GONE);
        emptyListTextView.setVisibility(View.GONE);


        if (Validation.isConnected(context)) {

            // Url
            String url = ServiceUrls.KEY_BLOOD_PRESSURE.concat(StringConstants.KEY_UPDATE)
                    .concat(data.get(StringConstants.KEY_BP_ID));

            // Set data
            final HashMap<String, Object> postData = new HashMap<>();
            postData.put(StringConstants.API_KEY_PATIENT_ID, SharedPreferenceService.getValue(context, StringConstants.KEY_PATIENT_ID));
            postData.put(StringConstants.KEY_SYSTOLIC_BP, data.get(StringConstants.KEY_SYSTOLIC_BP));
            postData.put(StringConstants.KEY_DIASTOLIC_BP, data.get(StringConstants.KEY_DIASTOLIC_BP));
            postData.put(StringConstants.KEY_TAG, data.get(StringConstants.KEY_TAG));
            postData.put(StringConstants.KEY_DATE,  DateTimeUtils.convertTimestampToUTC(Long.valueOf(data.get(StringConstants.KEY_TIMESTAMP))));
            postData.put(StringConstants.KEY_TIME, DateTimeUtils.convertTimestampToUTCTime(Long.valueOf(data.get(StringConstants.KEY_TIMESTAMP))));
            postData.put(StringConstants.KEY_TIMESTAMP,
                    Long.valueOf(data.get(StringConstants.KEY_TIMESTAMP)));

            // Set Headers
            final HashMap<String, String> headers = new HashMap<>();
            headers.put(StaticConstants.KEY_AUTHORIZATION,
                    StaticConstants.KEY_BEARER
                            .concat(" ")
                            .concat(SharedPreferenceService
                                    .getValue(context, StringConstants.KEY_TOKEN)));

            /**
             * API Call
             */
            APICallService.PutAPICall(activity, context, url, postData, headers, new APIInterface() {
                @Override
                public void onSuccess(JSONObject response) {

                    try {

                        // Update in Local Database
                        BloodPressure bloodPressure =
                                BloodPressure.find(BloodPressure.class, "bp_id = ?", data.get(StringConstants.KEY_BP_ID)).get(0);

                        bloodPressure.setSystolicBp(data.get(StringConstants.KEY_SYSTOLIC_BP));
                        bloodPressure.setDiastolicBp(data.get(StringConstants.KEY_DIASTOLIC_BP));
                        bloodPressure.setTag(data.get(StringConstants.KEY_TAG));
                        bloodPressure.setDate(data.get(StringConstants.KEY_DATE));
                        bloodPressure.setTime(data.get(StringConstants.KEY_TIME));
                        bloodPressure.setTimestamp(Long.valueOf(data.get(StringConstants.KEY_TIMESTAMP)));

                        bloodPressure.save();

                        // Notify Adapter
                        readVitalsList(context, StaticConstants.SOURCE_UPDATE);

                    } catch (Exception e) {

                        // Show Content
                        loader.setVisibility(View.GONE);
                        contentLinearLayout.setVisibility(View.VISIBLE);

                        // Log error
                        ErrorHandlers.handleError(activity);
                        Crashlytics.logException(e);
                    }
                }

                @Override
                public void onError(VolleyError error) {

                    // Show Content
                    loader.setVisibility(View.GONE);
                    contentLinearLayout.setVisibility(View.VISIBLE);
                }
            });
        } else {

            // Show Content
            loader.setVisibility(View.GONE);
            contentLinearLayout.setVisibility(View.VISIBLE);

            // Log error
            ErrorHandlers.handleInternetConnectionFailure(activity);
        }
    }


    /**
     * Function to delete a entry from Vitals
     * @param context
     * @param id
     */
    public static void deleteVitalsEntry(final Context context, final String id) {

        // Set initial states
        loader.setVisibility(View.VISIBLE);
        contentLinearLayout.setVisibility(View.GONE);
        emptyListTextView.setVisibility(View.GONE);


        if (Validation.isConnected(context)) {

            // Url Creation
            String url = ServiceUrls.KEY_BLOOD_PRESSURE
                    .concat(StringConstants.KEY_DELETE)
                    .concat(StringConstants.KEY_SEPARATOR).concat(id);

            // Set Headers
            final HashMap<String, String> headers = new HashMap<>();
            headers.put(StaticConstants.KEY_AUTHORIZATION,
                    StaticConstants.KEY_BEARER
                            .concat(" ")
                            .concat(SharedPreferenceService
                                    .getValue(context, StringConstants.KEY_TOKEN)));

            /**
             * API Call
             */
            APICallService.DeleteAPICall(activity, context, url, headers, new APIInterface() {
                @Override
                public void onSuccess(JSONObject response) {

                    try {

                        // Delete from local database
                        BloodPressure bloodPressure = BloodPressure.find(BloodPressure.class, "bp_id = ?", id).get(0);
                        bloodPressure.delete();

                        // Notify Adapter
                        readVitalsList(context, StaticConstants.SOURCE_DELETE);

                    } catch (Exception e) {

                        // Show Content
                        loader.setVisibility(View.GONE);
                        contentLinearLayout.setVisibility(View.VISIBLE);

                        // Log error
                        ErrorHandlers.handleAPIError(activity);
                        Crashlytics.logException(e);
                    }
                }

                @Override
                public void onError(VolleyError error) {

                    // Show Content
                    loader.setVisibility(View.GONE);
                    contentLinearLayout.setVisibility(View.VISIBLE);
                }
            });
        } else {

            // Show Content
            loader.setVisibility(View.GONE);
            contentLinearLayout.setVisibility(View.VISIBLE);

            // Log error
            ErrorHandlers.handleInternetConnectionFailure(activity);
        }
    }
}

