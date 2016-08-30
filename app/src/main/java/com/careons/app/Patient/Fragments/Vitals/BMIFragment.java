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
import android.text.InputFilter;
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
import com.careons.app.Patient.Database.Models.Vitals.BMI;
import com.careons.app.Shared.Interfaces.APIInterface;
import com.careons.app.R;
import com.careons.app.Patient.Services.APICallService;
import com.careons.app.Patient.Services.SharedPreferenceService;
import com.careons.app.Shared.Utils.DateTimeUtils;
import com.careons.app.Shared.Utils.ErrorHandlers;
import com.careons.app.Shared.Utils.InputFilterMinMax;
import com.careons.app.Shared.Utils.Validation;
import com.careons.app.Shared.Utils.VitalUtils;
import com.careons.app.databinding.ListItemVitalsBmiBinding;
import com.crashlytics.android.Crashlytics;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;

public class BMIFragment extends Fragment
        implements  DatePickerDialog.OnDateSetListener {

    private static Activity activity;
    private static BMIFragment bmiFragment;
    private static View rootView;
    private static RecyclerView vitalListRecyclerView;
    private static LinearLayout loader, contentLinearLayout;
    private static TextView emptyListTextView, dateTextView, saveEntryTextView;
    private static EditText heightFtEditText, heightIncEditText, weightEditText;
    private static ImageView cancelEntryImageView;
    private static TextView graph10TextView, graph25TextView, graph50TextView;

    private static Adapter<BMI, ListItemVitalsBmiBinding> bmiBindingAdapter;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_bmi, container, false);

        // Initialize instance
        activity = getActivity();
        bmiFragment = this;

        // Initialize layouts
        loader = (LinearLayout) rootView.findViewById(R.id.loader);
        contentLinearLayout = (LinearLayout) rootView.findViewById(R.id.vitals_bmi_content);
        emptyListTextView = (TextView) rootView.findViewById(R.id.vitals_bmi_empty);
        vitalListRecyclerView = (RecyclerView) rootView.findViewById(R.id.vitals_recycler_view);

        saveEntryTextView = (TextView) rootView.findViewById(R.id.save_entry);
        heightFtEditText = (EditText) rootView.findViewById(R.id.height_ft_edittext);
        heightIncEditText = (EditText) rootView.findViewById(R.id.height_inches_edittext);
        weightEditText = (EditText) rootView.findViewById(R.id.weight_edittext);


        /**
         * Fetch and Read Data
         */
        readVitalsList(getContext(), StaticConstants.SOURCE_READ);

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


    public static BMIFragment newInstance() {
        return new BMIFragment();
    }


    /**
     * Function to fetch instance of the current activity
     * @return
     */
    public static BMIFragment getInstance() {
        return bmiFragment;
    }




    /**
     * Function to display create entry dialog box
     */
    public void showCreateDialog(final Activity activity) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_vitals_bmi, null);
        builder.setView(dialogView);
        final AlertDialog dialog = builder.create();
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        // Set Vertical ScrollBar Enabled
        dialogView.findViewById(R.id.scrollBar).setVerticalScrollBarEnabled(false);

        // Initialize Data Fields
        dateTextView = (TextView) dialogView.findViewById(R.id.date_textview);
        dateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDatePicker();
            }
        });

        // Substitute current date and time
        dateTextView.setText(DateTimeUtils.getSimpleDate(getContext()));

        // Initialize fields
        heightFtEditText = (EditText) dialogView.findViewById(R.id.height_ft_edittext);
        heightFtEditText.setFilters(new InputFilter[]{new InputFilterMinMax("1", "7")});
        heightIncEditText = (EditText) dialogView.findViewById(R.id.height_inches_edittext);
        heightIncEditText.setFilters(new InputFilter[]{new InputFilterMinMax("0", "11")});
        weightEditText = (EditText) dialogView.findViewById(R.id.weight_edittext);
        weightEditText.setFilters(new InputFilter[]{new InputFilterMinMax("1", "300")});

        // Save Entry to Vitals
        saveEntryTextView = (TextView) dialogView.findViewById(R.id.save_entry);
        saveEntryTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                // Check for data
                if(heightFtEditText.getText().toString().trim().length() > 0) {

                    if(heightIncEditText.getText().toString().trim().length() > 0) {

                        if(weightEditText.getText().toString().trim().length() > 0) {

                            // Create Data object
                            HashMap<String, String> data = new HashMap<>();
                            data.put(StringConstants.KEY_BMI,  VitalUtils.calculateBMI(
                                    Integer.parseInt(heightFtEditText.getText().toString()),
                                    Integer.parseInt(heightIncEditText.getText().toString()),
                                    Integer.parseInt(weightEditText.getText().toString())));

                            data.put(StringConstants.KEY_TAG, VitalUtils.calculateBMIRangeTag(getContext(),
                                    Float.valueOf(data.get(StringConstants.KEY_BMI))));

                            data.put(StringConstants.KEY_BMI_HEIGHT_FT, heightFtEditText.getText().toString());
                            data.put(StringConstants.KEY_BMI_HEIGHT_INC, heightIncEditText.getText().toString());
                            data.put(StringConstants.KEY_BMI_WEIGHT, weightEditText.getText().toString());
                            data.put(StringConstants.KEY_DATE, dateTextView.getText().toString());
                            data.put(StringConstants.KEY_TIMESTAMP,
                                    String.valueOf(DateTimeUtils.convertDateToTimestamp(
                                                    data.get(StringConstants.KEY_DATE)
                                                            .concat(" ")
                                                            .concat(DateTimeUtils.getTime(getContext()))
                                    )));

                            // Create Entry
                            createVitalsEntry(activity.getApplicationContext(), data);

                            // Dialog Dismiss
                            dialog.dismiss();

                        } else {

                            // Weight not entered
                            weightEditText.setError(activity.getResources().getString(R.string.required));
                        }
                    } else {

                        // Height in inches not entered
                        heightIncEditText.setError(activity.getResources().getString(R.string.required));
                    }
                } else {

                    // Height in feet not entered
                    heightFtEditText.setError(activity.getResources().getString(R.string.required));
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
            String url = ServiceUrls.KEY_BMI.concat(StringConstants.KEY_CREATE);

            // Set data
            final HashMap<String, Object> postData = new HashMap<>();
            postData.put(StringConstants.KEY_PATIENT_ID, SharedPreferenceService.getValue(context, StringConstants.KEY_PATIENT_ID));
            postData.put(StringConstants.KEY_BMI, data.get(StringConstants.KEY_BMI));
            postData.put(StringConstants.KEY_TAG, data.get(StringConstants.KEY_TAG));
            postData.put(StringConstants.API_KEY_BMI_HEIGHT, ((Integer.parseInt(data.get(StringConstants.KEY_BMI_HEIGHT_FT)) * 12) +
                            Integer.parseInt(data.get(StringConstants.KEY_BMI_HEIGHT_INC))));
            postData.put(StringConstants.API_KEY_BMI_WEIGHT, data.get(StringConstants.KEY_BMI_WEIGHT));
            postData.put(StringConstants.KEY_DATE, DateTimeUtils.convertTimestampToUTC(Long.valueOf(data.get(StringConstants.KEY_TIMESTAMP))));
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
                        BMI bmiTuple = new BMI(
                                SharedPreferenceService.getValue(context, StringConstants.KEY_PATIENT_ID),
                                response.getString(StringConstants.KEY_BMI_ID),
                                data.get(StringConstants.KEY_BMI),
                                data.get(StringConstants.KEY_TAG),
                                data.get(StringConstants.KEY_BMI_HEIGHT_FT),
                                data.get(StringConstants.KEY_BMI_HEIGHT_INC),
                                data.get(StringConstants.KEY_BMI_WEIGHT),
                                data.get(StringConstants.KEY_DATE),
                                Long.valueOf(data.get(StringConstants.KEY_TIMESTAMP))
                        );

                        bmiTuple.save();

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

            // Log error
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
        ArrayList<BMI> bmiList = (ArrayList<BMI>) BMI.find(BMI.class,
                "patient_id = ?", SharedPreferenceService.getValue(context, StringConstants.KEY_PATIENT_ID));

        /**
         * Render Graph
         */
        if(source == StaticConstants.SOURCE_CREATE
                || source == StaticConstants.SOURCE_UPDATE
                || source == StaticConstants.SOURCE_DELETE) {

            VitalFragment.renderGraphBMI(bmiList);
        }


        // Check if database contains vital entries
        if(bmiList.size() > 0) {

            // Sort list before binding
            Collections.sort(bmiList);

            // Set adapter
            bmiBindingAdapter =
                    new Adapter<>(bmiList, R.layout.list_item_vitals_bmi, StaticConstants.VITALS_BMI_ADAPTER);

            vitalListRecyclerView.setLayoutManager(new LinearLayoutManager(context));
            vitalListRecyclerView.setAdapter(bmiBindingAdapter);


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
            String url = ServiceUrls.KEY_BMI.concat(StringConstants.KEY_UPDATE)
                    .concat(data.get(StringConstants.KEY_BMI_ID));

            // Set data
            final HashMap<String, Object> postData = new HashMap<>();
            postData.put(StringConstants.KEY_PATIENT_ID, SharedPreferenceService.getValue(context, StringConstants.KEY_PATIENT_ID));
            postData.put(StringConstants.API_KEY_BMI, data.get(StringConstants.KEY_BMI));
            postData.put(StringConstants.KEY_TAG, data.get(StringConstants.KEY_TAG));
            postData.put(StringConstants.API_KEY_BMI_HEIGHT,
                    ((Integer.parseInt(data.get(StringConstants.KEY_BMI_HEIGHT_FT))) * 12) +
                            Integer.parseInt(data.get(StringConstants.KEY_BMI_HEIGHT_INC)));
            postData.put(StringConstants.API_KEY_BMI_WEIGHT, data.get(StringConstants.KEY_BMI_WEIGHT));
            postData.put(StringConstants.KEY_DATE, DateTimeUtils.convertTimestampToUTC(Long.valueOf(data.get(StringConstants.KEY_TIMESTAMP))));
            postData.put(StringConstants.KEY_TIMESTAMP, Long.valueOf(data.get(StringConstants.KEY_TIMESTAMP)));


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
                        BMI bmi = BMI.find(BMI.class, "bmi_id = ?", data.get(StringConstants.KEY_BMI_ID)).get(0);

                        bmi.setBmi(data.get(StringConstants.KEY_BMI));
                        bmi.setTag(data.get(StringConstants.KEY_TAG));
                        bmi.setHeightFt(data.get(StringConstants.KEY_BMI_HEIGHT_FT));
                        bmi.setHeightInc(data.get(StringConstants.KEY_BMI_HEIGHT_INC));
                        bmi.setWeight(data.get(StringConstants.KEY_BMI_WEIGHT));
                        bmi.setDate(data.get(StringConstants.KEY_DATE));
                        bmi.setTimestamp(Long.valueOf(data.get(StringConstants.KEY_TIMESTAMP)));

                        bmi.save();

                        // Notify Adapter
                        readVitalsList(context, StaticConstants.SOURCE_UPDATE);

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
            String url = ServiceUrls.KEY_BMI
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
                        BMI bmi = BMI.find(BMI.class, "bmi_id = ?", id).get(0);
                        bmi.delete();

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
