package com.careons.app.Patient.Activity.Signup;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.careons.app.Patient.Commons.ServiceUrls;
import com.careons.app.Patient.Commons.StaticConstants;
import com.careons.app.Patient.Commons.StringConstants;
import com.careons.app.Patient.Database.Models.Profile.Profile;
import com.careons.app.Patient.Database.Models.Vitals.BMI;
import com.careons.app.Patient.Enums.BloodGroup;
import com.careons.app.Shared.Interfaces.APIInterface;
import com.careons.app.R;
import com.careons.app.Patient.Services.APICallService;
import com.careons.app.Patient.Services.SharedPreferenceService;
import com.careons.app.Shared.Utils.DateTimeUtils;
import com.careons.app.Shared.Utils.ErrorHandlers;
import com.careons.app.Shared.Utils.InputFilterMinMax;
import com.careons.app.Shared.Utils.Validation;
import com.careons.app.Shared.Utils.VitalUtils;
import com.crashlytics.android.Crashlytics;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import org.json.JSONObject;

import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.HashMap;

public class CreateAccountActivity extends Activity
        implements View.OnClickListener, DatePickerDialog.OnDateSetListener, AdapterView.OnItemSelectedListener {

    private static Activity activity;

    private TextView dobTextView;
    private EditText heightFtEditText, heightIncEditText, weightEditText;
    private Spinner genderSpinner, bloodGroupSpinner;

    private long dobTimestamp;
    private String dobString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        // Initialize activity
        activity = this;

        //Set Vertical ScrollBar Enabled
        findViewById(R.id.scrollBar).setVerticalScrollBarEnabled(false);

        // Initialize
        dobTextView = (TextView) findViewById(R.id.dob);
        heightFtEditText = (EditText) findViewById(R.id.height_ft);
        heightFtEditText.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                v.setFocusable(true);
                v.setFocusableInTouchMode(true);
                return false;
            }
        });
        heightFtEditText.setFilters(new InputFilter[]{new InputFilterMinMax("1", "7")});

        heightIncEditText = (EditText) findViewById(R.id.height_inc);
        heightIncEditText.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                v.setFocusable(true);
                v.setFocusableInTouchMode(true);
                return false;
            }
        });
        heightIncEditText.setFilters(new InputFilter[]{new InputFilterMinMax("0", "11")});

        weightEditText = (EditText) findViewById(R.id.weight);
        weightEditText.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                v.setFocusable(true);
                v.setFocusableInTouchMode(true);
                return false;
            }
        });

        weightEditText.setFilters(new InputFilter[]{new InputFilterMinMax("1", "300")});

        genderSpinner = (Spinner) findViewById(R.id.gender_spinner);
        bloodGroupSpinner = (Spinner) findViewById(R.id.blood_group_spinner);


        // Attach click listener
        dobTextView.setOnClickListener(this);
        genderSpinner.setOnItemSelectedListener(this);
        bloodGroupSpinner.setOnItemSelectedListener(this);
        bindSpinner(1);
        bindSpinner(2);

        // On Focus change listener on date of birth
        dobTextView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {

                    validateDOB();
                }
            }
        });

        // On Focus change listener on height (in feet)
        heightFtEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {

                    validateHeightFt();
                }
            }
        });

        // On Focus change listener on height (in inches)
        heightIncEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {

                    validateHeightInches();
                }
            }
        });

        // On Focus change listener on date of birth
        weightEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {

                    validateWeight();
                }
            }
        });
    }


    @Override
    public void onResume() {
        super.onResume();

        DatePickerDialog dpd = (DatePickerDialog) getFragmentManager().findFragmentByTag(StringConstants.KEY_DATEPICKER_DIALOG);
        if(dpd != null) dpd.setOnDateSetListener(this);
    }

    @Override
    public void onBackPressed() {

        finish();
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }


    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.dob:
                openDatePicker();
                break;

            default:
                break;
        }
    }


    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        String dateString = dayOfMonth + " " + (new DateFormatSymbols().getMonths()[monthOfYear]) + " " + year;
        dobTextView.setText(dateString);

        String month = String.valueOf(monthOfYear+1);
        if(monthOfYear+1 < 10) {

            month = "0"+month;
        }

        String day = String.valueOf(dayOfMonth);
        if(dayOfMonth < 10) {
            day = "0"+day;
        }

        dobString = year + "-" + month + "-" + day;

        // Calculate timestamp for date of birth
        dobTimestamp = DateTimeUtils.convertTimeToTimestamp(year, monthOfYear, dayOfMonth, 0, 0);
    }

    /**
     * Function to implement date picker
     */
    public void openDatePicker() {

        Calendar now = Calendar.getInstance();
        DatePickerDialog dpd = DatePickerDialog.newInstance(
                this,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );

        dpd.show(getFragmentManager(), StringConstants.KEY_DATEPICKER_DIALOG);
        dpd.setAccentColor(getResources().getColor(R.color.colorPrimary));
        dpd.showYearPickerFirst(true);
        dpd.setMaxDate(now);
    }

    /**
     * Function to bind values to the spinner
     */
    public void bindSpinner(int i) {

        // Create an ArrayAdapter using the string array and a spinner layout
        ArrayAdapter<CharSequence> adapter;

        switch (i) {

            case 1:
                adapter = ArrayAdapter.createFromResource(this,
                        R.array.gender, R.layout.item_spinner_auth);
                // Specify the layout to use when the list of choices appears
                adapter.setDropDownViewResource(R.layout.item_spinner_dropdown);
                // Apply the adapter to the spinner
                genderSpinner.setAdapter(adapter);
                break;

            case 2:
                adapter = ArrayAdapter.createFromResource(this,
                        R.array.blood_group, R.layout.item_spinner_auth);
                // Specify the layout to use when the list of choices appears
                adapter.setDropDownViewResource(R.layout.item_spinner_dropdown);
                // Apply the adapter to the spinner
                bloodGroupSpinner.setAdapter(adapter);
                break;

            default:
                break;
        }
    }


    /**
     * Function to validate date of birth
     * @return
     */
    public boolean validateDOB() {

        boolean isValidDOB = false;
        String dateOfBirth = dobTextView.getText().toString();

        // Validate Date of Birth
        if(Validation.isEmpty(dateOfBirth)) {

            // Date of Birth not selected -> Show Error
            dobTextView.setError(getString(R.string.err_msg_dob));
        } else {

            // DOB Valid
            isValidDOB = true;
            dobTextView.setError(null);
        }

        return isValidDOB;
    }

    /**
     * Function to validate Gender
     * @return
     */
    public boolean isSelectGender() {

        boolean isValidGender = true;

        if(genderSpinner.getSelectedItemPosition() < 1) {
            isValidGender = false;
            Toast.makeText(this, getString(R.string.select_gender), Toast.LENGTH_SHORT).show();
        }
        return isValidGender;
    }
    /**
     * Function to validate Blood Group
     * @return
     */
    public boolean isSelectBloodGroup() {

        boolean isValidBloodGroup= true;

        if(bloodGroupSpinner.getSelectedItem().toString().equals(getString(R.string.s_blood_group))) {
            isValidBloodGroup = false;
            Toast.makeText(this, getString(R.string.select_blood_group),Toast.LENGTH_SHORT).show();
        }
        return isValidBloodGroup;
    }





    /**
     * Function to validate height in feet
     * @return
     */
    public boolean validateHeightFt() {

        boolean isValidHFT = false;
        String heightFt = heightFtEditText.getText().toString();

        // Validate height in feet
        if(Validation.isEmpty(heightFt)) {

            // Height not entered -> Show Error
            heightFtEditText.setError(getString(R.string.err_msg_height));

        } else {

            // Height entered
            isValidHFT = true;
            heightFtEditText.setError(null);
        }

        return isValidHFT;
    }


    /**
     * Function to validate height in inches
     * @return
     */
    public boolean validateHeightInches() {

        boolean isValidHINC = false;
        String heightInc = heightIncEditText.getText().toString();

        // Validate height in inches
        if(Validation.isEmpty(heightInc)) {

            // Height not entered -> Show Error
            heightIncEditText.setError(getString(R.string.err_msg_height));

        } else {

            if(Integer.parseInt(heightInc) > 11) {

                // Height not entered -> Show Error
                heightIncEditText.setError(getString(R.string.err_msg_height_inches));
            } else {

                // Height (in inches) entered
                isValidHINC = true;
                heightIncEditText.setError(null);
            }
        }

        return isValidHINC;
    }


    /**
     * Function to validate weight
     * @return
     */
    public boolean validateWeight() {

        boolean isValidWT = false;
        String weight = weightEditText.getText().toString();

        // Validate weight
        if(Validation.isEmpty(weight)) {

            // Height not entered -> Show Error
            weightEditText.setError(getString(R.string.err_msg_weight));
        } else {

            // Height entered
            isValidWT = true;
            weightEditText.setError(null);
        }


        return isValidWT;
    }


    /**
     * Function to create account details
     * @param view
     */
    public void createAccountRequest(View view) {

        // Extract data
        final String dob = dobTextView.getText().toString();
        final String gender = genderSpinner.getSelectedItem().toString();
        final String heightFt = heightFtEditText.getText().toString();
        final String heightInc = heightIncEditText.getText().toString();
        final String weight = weightEditText.getText().toString();
        final String bloodGroup = bloodGroupSpinner.getSelectedItem().toString();

        // Service URL
        final String url = ServiceUrls.KEY_PROFILE;

        if(validateDOB()
                && isSelectGender()
                && validateHeightFt()
                && validateHeightInches()
                && validateWeight()
                && isSelectBloodGroup()) {

            if (Validation.isConnected(getApplicationContext())) {

                // Progress Dialog Popup
                final ProgressDialog popup = new ProgressDialog(this);
                popup.setMessage(getString(R.string.completing_profile_message));
                popup.show();

                // Set Headers
                final HashMap<String, String> headers = new HashMap<>();
                headers.put(StaticConstants.KEY_AUTHORIZATION,
                        StaticConstants.KEY_BEARER
                                .concat(" ")
                                .concat(SharedPreferenceService
                                        .getValue(getApplicationContext(), StringConstants.KEY_TOKEN)));


                // Constructing request body
                final HashMap<String, Object> data = new HashMap<>();
                data.put(StringConstants.KEY_ID, SharedPreferenceService.getValue(getApplicationContext(), StringConstants.KEY_PATIENT_ID));
                data.put(StringConstants.API_KEY_DOB, DateTimeUtils.convertTimestampToUTC(dobTimestamp));
                data.put(StringConstants.API_KEY_SEX, gender);
                data.put(StringConstants.API_KEY_BLOOD_GROUP, bloodGroup.replace(" ", ""));
                data.put(StringConstants.API_KEY_IS_ACCOUNT_CREATED, 1);
                data.put(StringConstants.API_KEY_IS_ONBOARDING_COMPLETE, 0);


                // API Call
                APICallService.PutAPICall(this, this, url, data, headers, new APIInterface() {
                    @Override
                    public void onSuccess(JSONObject response) {

                        try {

                            // Dismiss item_loader
                            popup.dismiss();

                            /**
                             * Update Local Database (Name, Phone, Date of Birth, Gender, Blood Group, is Account Created Flag)
                             */
                            Profile profile = Profile.find(Profile.class, "patient_id = ?",
                                    SharedPreferenceService.getValue(getApplicationContext(), StringConstants.KEY_PATIENT_ID)).get(0);
                            profile.setUsername(response.getString(StringConstants.API_KEY_NAME));
                            profile.setPhone(response.getString(StringConstants.API_KEY_PHONE));
                            profile.setDob(dobString);
                            profile.setAge(DateTimeUtils.calculateAgeFromDOB(
                                    Long.valueOf(dobTimestamp)));
                            profile.setGender(response.getString(StringConstants.API_KEY_SEX));
                            profile.setBloodGroup(BloodGroup.valueOf(response.getString(StringConstants.API_KEY_BLOOD_GROUP)).getBloodGroup());
                            profile.setAccountCreated(true);

                            profile.save();


                            /**
                             * Store values in the Shared Preference (Date of Birth, Gender, Blood Group, is Account Created Flag)
                             */
                            SharedPreferenceService.storeUserDetails(getApplicationContext(), StringConstants.KEY_DOB,
                                    dobString);
                            SharedPreferenceService.storeUserDetails(getApplicationContext(),
                                    StringConstants.KEY_DOB_TIMESTAMP, String.valueOf(dobTimestamp));
                            SharedPreferenceService.storeUserDetails(getApplicationContext(),
                                    StringConstants.KEY_AGE, DateTimeUtils.calculateAgeFromDOB(Long.valueOf(dobTimestamp)));
                            SharedPreferenceService.storeUserDetails(getApplicationContext(), StringConstants.KEY_GENDER, gender);
                            SharedPreferenceService.storeUserDetails(getApplicationContext(), StringConstants.KEY_BLOOD_GROUP,
                                    BloodGroup.valueOf(response.getString(StringConstants.API_KEY_BLOOD_GROUP)).getBloodGroup());
                            SharedPreferenceService.storeUserDetails(getApplicationContext(), StringConstants.KEY_IS_ACCOUNT_CREATED, "true");


                            // Calculate BMI
                            calculateBMI(Integer.parseInt(heightFt), Integer.parseInt(heightInc), Integer.parseInt(weight));

                            // Goto Onboarding
                            Intent intent = new Intent(getApplicationContext(), WelcomeActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            finish();

                        } catch (Exception e) {

                            Toast.makeText(getApplicationContext(), getString(R.string.try_again_in_sometime), Toast.LENGTH_LONG).show();

                            // Log error
                            Crashlytics.logException(e);
                        }
                    }

                    @Override
                    public void onError(VolleyError error) {

                        // Dismiss item_loader
                        popup.dismiss();

                        Toast.makeText(getApplicationContext(), getString(R.string.try_again_in_sometime),
                                Toast.LENGTH_LONG).show();
                    }
                });
            } else {

                // If internet connection is not available
                Toast.makeText(getApplicationContext(), getString(R.string.err_internet_not_available),
                        Toast.LENGTH_LONG).show();
            }
        } else {

            // Case : Fields data not valid
            Toast.makeText(getApplicationContext(), getString(R.string.err_msg_signup), Toast.LENGTH_LONG).show();
        }
    }


    /**
     * Function to calculate BMI from height and weight at the time of create account
     * @param heightFt
     * @param heightInc
     * @param weight
     */
    public void calculateBMI(final int heightFt, final int heightInc, final int weight) {

        /**
         * Call API to create
         */
        if (Validation.isConnected(getApplicationContext())) {

            // Url Formation
            String url = ServiceUrls.KEY_BMI.concat(StringConstants.KEY_CREATE);

            String bmi =  VitalUtils.calculateBMI(heightFt, heightInc, weight);

            // Set data
            final HashMap<String, Object> postData = new HashMap<>();
            postData.put(StringConstants.KEY_PATIENT_ID, SharedPreferenceService.getValue(getApplicationContext(), StringConstants.KEY_PATIENT_ID));
            postData.put(StringConstants.KEY_BMI, bmi);
            postData.put(StringConstants.KEY_TAG, VitalUtils.calculateBMIRangeTag(getApplicationContext(), Float.valueOf(bmi)));
            postData.put(StringConstants.API_KEY_BMI_HEIGHT, (heightFt * 12 + heightInc));
            postData.put(StringConstants.API_KEY_BMI_WEIGHT, weight);
            postData.put(StringConstants.KEY_DATE,
                    DateTimeUtils.convertTimestampToDate(DateTimeUtils.getCurrentTimestamp()));
            postData.put(StringConstants.KEY_TIMESTAMP, DateTimeUtils.getCurrentTimestamp());

            // Set Headers
            final HashMap<String, String> headers = new HashMap<>();
            headers.put(StaticConstants.KEY_AUTHORIZATION,
                    StaticConstants.KEY_BEARER
                            .concat(" ")
                            .concat(SharedPreferenceService
                                    .getValue(getApplicationContext(), StringConstants.KEY_TOKEN)));

            /**
             * API Call
             */
            APICallService.PostAPICall(activity, getApplicationContext(), url, postData, headers, new APIInterface() {
                @Override
                public void onSuccess(JSONObject response) {

                    try {

                        // Create in Local Database
                        BMI bmiTuple = new BMI(
                                SharedPreferenceService.getValue(getApplicationContext(), StringConstants.KEY_PATIENT_ID),
                                response.getString(StringConstants.KEY_BMI_ID),
                                postData.get(StringConstants.KEY_BMI).toString(),
                                postData.get(StringConstants.KEY_TAG).toString(),
                                String.valueOf(heightFt),
                                String.valueOf(heightInc),
                                String.valueOf(weight),
                                postData.get(StringConstants.KEY_DATE).toString(),
                                Long.valueOf(postData.get(StringConstants.KEY_TIMESTAMP).toString())
                        );

                        bmiTuple.save();

                    } catch (Exception e) {


                        // Log error
                        Crashlytics.logException(e);
                        ErrorHandlers.handleError(activity);
                    }
                }

                @Override
                public void onError(VolleyError error) {

                }
            });
        } else {

            // Internet Not Available
            ErrorHandlers.handleInternetConnectionFailure(activity);

        }
    }
}
