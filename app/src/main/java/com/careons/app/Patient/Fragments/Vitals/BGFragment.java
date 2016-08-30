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
import com.careons.app.Patient.Database.Models.Vitals.BloodGlucose;
import com.careons.app.Shared.Interfaces.APIInterface;
import com.careons.app.R;
import com.careons.app.Patient.Services.APICallService;
import com.careons.app.Patient.Services.SharedPreferenceService;
import com.careons.app.Shared.Utils.DateTimeUtils;
import com.careons.app.Shared.Utils.ErrorHandlers;
import com.careons.app.Shared.Utils.Validation;
import com.careons.app.Shared.Utils.VitalUtils;
import com.careons.app.databinding.ListItemVitalsBgBinding;
import com.crashlytics.android.Crashlytics;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;

public class BGFragment extends Fragment
        implements View.OnClickListener, DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    private static Activity activity;
    private static BGFragment bgFragment;
    private static View rootView;
    private static RecyclerView vitalListRecyclerView;
    private static LinearLayout loader, contentLinearLayout;
    private static ImageView cancelEntryImageView;
    private static TextView emptyListTextView, dateTextView, timeTextView, saveEntryTextView, checkupRandom, checkupFasting, checkupPostMeal;
    private static TextView graphPostMealTextView, graphPreMealTextView, graphRandomTextView;
    private static EditText bloodGlucoseEditText;

    private static Adapter<BloodGlucose, ListItemVitalsBgBinding> bloodGlucoseBindingAdapter;
    private static String checkupSelected;
    private static int checkupSelectedScale = StaticConstants.VITALS_RANDOM;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_blood_glucose, container, false);

        // Initialize fragment
        activity = getActivity();
        bgFragment = this;

        loader = (LinearLayout) rootView.findViewById(R.id.loader);
        emptyListTextView = (TextView) rootView.findViewById(R.id.vitals_bg_empty);
        contentLinearLayout = (LinearLayout) rootView.findViewById(R.id.vitals_bg_content);
        vitalListRecyclerView = (RecyclerView) rootView.findViewById(R.id.vitals_recycler_view);

        // Initialize Graph Formats
        graphPostMealTextView = (TextView) rootView.findViewById(R.id.graph_post_meal);
        graphPreMealTextView = (TextView) rootView.findViewById(R.id.graph_pre_meal);
        graphRandomTextView = (TextView) rootView.findViewById(R.id.graph_random);

        graphRandomTextView.setOnClickListener(this);
        graphPostMealTextView.setOnClickListener(this);
        graphPreMealTextView.setOnClickListener(this);

        /**
         * Initialize check up selected scale
         */
        selectCurrentTab();


        /**
         * Fetch and Read Data
         */
        readVitalsList(getActivity(), StaticConstants.SOURCE_READ);


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


    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.graph_random:
                unselectCheckupScale();
                checkupSelectedScale = StaticConstants.VITALS_RANDOM;
                selectCurrentTab();
                readVitalsList(getContext(), StaticConstants.SOURCE_UPDATE);
                break;

            case R.id.graph_pre_meal:
                unselectCheckupScale();
                checkupSelectedScale = StaticConstants.VITALS_PRE_MEAL;
                selectCurrentTab();
                readVitalsList(getContext(), StaticConstants.SOURCE_UPDATE);
                break;

            case R.id.graph_post_meal:
                unselectCheckupScale();
                checkupSelectedScale = StaticConstants.VITALS_POST_MEAL;
                selectCurrentTab();
                readVitalsList(getContext(), StaticConstants.SOURCE_UPDATE);
                break;

            default:
                break;
        }
    }


    public static BGFragment newInstance() {
        return new BGFragment();
    }

    /**
     * Function to fetch instance of the current activity
     * @return
     */
    public static BGFragment getInstance() {
        return bgFragment;
    }







    /**
     * Function to display create entry dialog box
     */
    public void showCreateDialog(final Activity activity) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_vitals_bg, null);
        builder.setView(dialogView);
        final AlertDialog dialog = builder.create();
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        // Set Vertical ScrollBar Enabled
        dialogView.findViewById(R.id.scrollBar).setVerticalScrollBarEnabled(false);

        // Initialize Data Fields
        checkupRandom = (TextView) dialogView.findViewById(R.id.checkup_random);
        checkupFasting = (TextView) dialogView.findViewById(R.id.checkup_fasting);
        checkupPostMeal = (TextView) dialogView.findViewById(R.id.checkup_post_meal);
        dateTextView = (TextView) dialogView.findViewById(R.id.date_textview);
        timeTextView = (TextView) dialogView.findViewById(R.id.time_textview);

        // Attaching Click listeners
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

        /**
         * Select Graph Tab (By Default)
         */
        switch (checkupSelectedScale){

            case StaticConstants.VITALS_RANDOM:
                unSelectDialogCheckup();
                selectDialogCheckup(checkupRandom);
                checkupSelected = getString(R.string.random);
                ((TextView) dialogView.findViewById(R.id.selector_desc)).setText(getString(R.string.random_desc));
                break;

            case StaticConstants.VITALS_PRE_MEAL:
                unSelectDialogCheckup();
                selectDialogCheckup(checkupFasting);
                checkupSelected = getString(R.string.pre_meal);
                ((TextView) dialogView.findViewById(R.id.selector_desc)).setText(getString(R.string.pre_meal_desc));
                break;

            case StaticConstants.VITALS_POST_MEAL:
                unSelectDialogCheckup();
                selectDialogCheckup(checkupPostMeal);
                checkupSelected = getString(R.string.post_meal);
                ((TextView) dialogView.findViewById(R.id.selector_desc)).setText(getString(R.string.post_meal_desc));
                break;

            default:
                break;
        }


        checkupRandom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                unSelectDialogCheckup();
                selectDialogCheckup(checkupRandom);
                checkupSelected = getString(R.string.random);
                ((TextView) dialogView.findViewById(R.id.selector_desc)).setText(getString(R.string.random_desc));
            }
        });

        checkupFasting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                unSelectDialogCheckup();
                selectDialogCheckup(checkupFasting);
                checkupSelected = getString(R.string.pre_meal);
                ((TextView) dialogView.findViewById(R.id.selector_desc)).setText(getString(R.string.pre_meal_desc));
            }
        });

        checkupPostMeal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                unSelectDialogCheckup();
                selectDialogCheckup(checkupPostMeal);
                checkupSelected = getString(R.string.post_meal);
                ((TextView) dialogView.findViewById(R.id.selector_desc)).setText(getString(R.string.post_meal_desc));
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
                bloodGlucoseEditText = (EditText) dialogView.findViewById(R.id.bg_edittext);


                if(bloodGlucoseEditText.getText().toString().isEmpty()) {

                    // Blood Glucose not entered
                    bloodGlucoseEditText.setError(activity.getResources().getString(R.string.required));

                } else {

                    // Create vitals entry
                    HashMap<String, String> data = new HashMap();
                    data.put(StringConstants.KEY_BLOOD_GLUCOSE, bloodGlucoseEditText.getText().toString());
                    data.put(StringConstants.KEY_CHECKUP, checkupSelected);
                    data.put(StringConstants.KEY_TAG, VitalUtils.calculateBGRangeTag(getActivity(),
                            Integer.parseInt(data.get(StringConstants.KEY_BLOOD_GLUCOSE)),
                            data.get(StringConstants.KEY_CHECKUP)));

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
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute, int second) {

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
     * Function to unselect checkup options in dialog box
     */
    public void unSelectDialogCheckup() {

        checkupRandom.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        checkupRandom.setTextColor(getResources().getColor(R.color.white));
        checkupFasting.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        checkupFasting.setTextColor(getResources().getColor(R.color.white));
        checkupPostMeal.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        checkupPostMeal.setTextColor(getResources().getColor(R.color.white));
    }

    /**
     * Function to un select checkup scale
     */
    public void unselectCheckupScale() {

        graphRandomTextView.setBackgroundColor(getResources().getColor(R.color.white));
        graphRandomTextView.setTextColor(getResources().getColor(R.color.text_color));
        graphPreMealTextView.setBackgroundColor(getResources().getColor(R.color.white));
        graphPreMealTextView.setTextColor(getResources().getColor(R.color.text_color));
        graphPostMealTextView.setBackgroundColor(getResources().getColor(R.color.white));
        graphPostMealTextView.setTextColor(getResources().getColor(R.color.text_color));
    }


    /**
     * Function to select current tab
     */
    public void selectCurrentTab() {

        switch (checkupSelectedScale) {

            case StaticConstants.VITALS_RANDOM:
                graphRandomTextView.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                graphRandomTextView.setTextColor(getResources().getColor(R.color.white));
                break;

            case StaticConstants.VITALS_PRE_MEAL:
                graphPreMealTextView.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                graphPreMealTextView.setTextColor(getResources().getColor(R.color.white));
                break;

            case StaticConstants.VITALS_POST_MEAL:
                graphPostMealTextView.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                graphPostMealTextView.setTextColor(getResources().getColor(R.color.white));
                break;

            default:
                break;
        }
    }


    /**
     * Function to unselect checkup options in dialog box
     */
    public void selectDialogCheckup(TextView textView) {

        textView.setBackgroundColor(getResources().getColor(R.color.white));
        textView.setTextColor(getResources().getColor(R.color.colorPrimary));
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
            String url = ServiceUrls.KEY_BLOOD_GLUCOSE.concat(StringConstants.KEY_CREATE);

            // Set data
            final HashMap<String, Object> postData = new HashMap<>();
            postData.put(StringConstants.KEY_PATIENT_ID, SharedPreferenceService.getValue(context, StringConstants.KEY_PATIENT_ID));
            postData.put(StringConstants.API_KEY_BLOOD_GLUCOSE, data.get(StringConstants.KEY_BLOOD_GLUCOSE));
            postData.put(StringConstants.API_KEY_CHECKUP, data.get(StringConstants.KEY_CHECKUP));
            postData.put(StringConstants.KEY_TAG, data.get(StringConstants.KEY_TAG));
            postData.put(StringConstants.KEY_DATE, DateTimeUtils.convertTimestampToUTC(Long.valueOf(data.get(StringConstants.KEY_TIMESTAMP))));
            postData.put(StringConstants.KEY_TIME, DateTimeUtils.convertTimestampToUTCTime(Long.valueOf(data.get(StringConstants.KEY_TIMESTAMP))));
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
            APICallService.PostAPICall(activity, context, url, postData, headers, new APIInterface() {
                @Override
                public void onSuccess(JSONObject response) {

                    try {

                        // Create in Local Database
                        BloodGlucose bloodGlucose = new BloodGlucose(
                                SharedPreferenceService.getValue(context, StringConstants.KEY_PATIENT_ID),
                                response.getString(StringConstants.KEY_BG_ID),
                                data.get(StringConstants.KEY_BLOOD_GLUCOSE),
                                data.get(StringConstants.KEY_CHECKUP),
                                data.get(StringConstants.KEY_TAG),
                                data.get(StringConstants.KEY_DATE),
                                data.get(StringConstants.KEY_TIME),
                                Long.valueOf(data.get(StringConstants.KEY_TIMESTAMP))
                        );

                        bloodGlucose.save();

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
        ArrayList<BloodGlucose> bloodGlucoseList = new ArrayList<>();
        switch (checkupSelectedScale) {

            case StaticConstants.VITALS_POST_MEAL:

                /**
                 * Read from database table
                 */
                bloodGlucoseList = (ArrayList<BloodGlucose>) BloodGlucose.find(BloodGlucose.class,
                        "patient_id = ? and checkup = ?", SharedPreferenceService.getValue(context, StringConstants.KEY_PATIENT_ID),
                        context.getString(R.string.post_meal));
                break;

            case StaticConstants.VITALS_PRE_MEAL:

                /**
                 * Read from database table
                 */
                bloodGlucoseList = (ArrayList<BloodGlucose>) BloodGlucose.find(BloodGlucose.class,
                        "patient_id = ? and checkup = ?", SharedPreferenceService.getValue(context, StringConstants.KEY_PATIENT_ID),
                        context.getString(R.string.pre_meal));
                break;

            case StaticConstants.VITALS_RANDOM:
                /**
                 * Read from database table
                 */
                bloodGlucoseList = (ArrayList<BloodGlucose>) BloodGlucose.find(BloodGlucose.class,
                        "patient_id = ? and checkup = ?", SharedPreferenceService.getValue(context, StringConstants.KEY_PATIENT_ID),
                        context.getString(R.string.random));
                break;

            default:
                break;
        }


        /**
         * Render Graph
         */
        if(source == StaticConstants.SOURCE_CREATE
                || source == StaticConstants.SOURCE_UPDATE
                || source == StaticConstants.SOURCE_DELETE) {

            System.out.println(":Check graph scale snet - " + checkupSelectedScale);

            VitalFragment.renderGraphBloodGlucose(context, checkupSelectedScale);
        }


        // Check if database contains vital entries
        if(bloodGlucoseList.size() > 0) {

            // Sort list before binding
            Collections.sort(bloodGlucoseList);

            // Set adapter
            bloodGlucoseBindingAdapter =
                    new Adapter<>(bloodGlucoseList, R.layout.list_item_vitals_bg, StaticConstants.VITALS_BLOOD_GLUCOSE_ADAPTER);

            vitalListRecyclerView.setLayoutManager(new LinearLayoutManager(context));
            vitalListRecyclerView.setAdapter(bloodGlucoseBindingAdapter);


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
            String url = ServiceUrls.KEY_BLOOD_GLUCOSE.concat(StringConstants.KEY_UPDATE)
                    .concat(data.get(StringConstants.KEY_BP_ID));

            // Set data
            final HashMap<String, Object> postData = new HashMap<>();
            postData.put(StringConstants.KEY_PATIENT_ID, SharedPreferenceService.getValue(context, StringConstants.KEY_PATIENT_ID));
            postData.put(StringConstants.API_KEY_BLOOD_GLUCOSE, data.get(StringConstants.KEY_BLOOD_GLUCOSE));
            postData.put(StringConstants.API_KEY_CHECKUP, data.get(StringConstants.KEY_CHECKUP));
            postData.put(StringConstants.KEY_TAG, data.get(StringConstants.KEY_TAG));
            postData.put(StringConstants.KEY_DATE, DateTimeUtils.convertTimestampToUTC(Long.valueOf(data.get(StringConstants.KEY_TIMESTAMP))));
            postData.put(StringConstants.KEY_TIME, DateTimeUtils.convertTimestampToUTCTime(Long.valueOf(data.get(StringConstants.KEY_TIMESTAMP))));
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
                        BloodGlucose bloodGlucose =
                                BloodGlucose.find(BloodGlucose.class, "bg_id = ?", data.get(StringConstants.KEY_BP_ID)).get(0);

                        bloodGlucose.setBloodGlucose(data.get(StringConstants.KEY_BLOOD_GLUCOSE));
                        bloodGlucose.setCheckup(data.get(StringConstants.KEY_CHECKUP));
                        bloodGlucose.setTag(data.get(StringConstants.KEY_TAG));
                        bloodGlucose.setDate(data.get(StringConstants.KEY_DATE));
                        bloodGlucose.setTime(data.get(StringConstants.KEY_TIME));
                        bloodGlucose.setTimestamp(Long.valueOf(data.get(StringConstants.KEY_TIMESTAMP)));

                        bloodGlucose.save();

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
            String url = ServiceUrls.KEY_BLOOD_GLUCOSE
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
                        BloodGlucose bloodGlucose = BloodGlucose.find(BloodGlucose.class, "bg_id = ?", id).get(0);
                        bloodGlucose.delete();

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
