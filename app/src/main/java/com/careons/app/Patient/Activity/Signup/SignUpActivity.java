package com.careons.app.Patient.Activity.Signup;


import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.VolleyError;
import com.careons.app.Patient.Commons.ServiceUrls;
import com.careons.app.Patient.Commons.StringConstants;
import com.careons.app.Shared.Interfaces.APIInterface;
import com.careons.app.R;
import com.careons.app.Patient.Services.APICallService;
import com.careons.app.Shared.Utils.ErrorHandlers;
import com.careons.app.Shared.Utils.Validation;
import com.crashlytics.android.Crashlytics;

import org.json.JSONObject;

import java.util.HashMap;

public class SignUpActivity extends Activity {

    private static Activity activity;
    private EditText mNameEditText, mEmailEditText, mPwdEditText, mConfirmPwdEditText, mNumberEditText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // Initialize activity
        activity = this;

        //Set Vertical ScrollBar Enabled
        findViewById(R.id.scrollBar).setVerticalScrollBarEnabled(false);

        //Initialize EditText variables
        mNameEditText = (EditText) findViewById(R.id.signup_name);
        mEmailEditText = (EditText) findViewById(R.id.signup_email);
        mPwdEditText = (EditText) findViewById(R.id.signup_pwd);
        mConfirmPwdEditText = (EditText) findViewById(R.id.signup_confirm_pwd);
        mNumberEditText = (EditText) findViewById(R.id.phone_no);


        // On Focus change listener on username
        mNameEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {

                    validateUsername();
                }
            }
        });

        // On Focus change listener on email id
        mEmailEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {

                    validateEmailId();
                }
            }
        });

        // On Focus change listener on password
        mPwdEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {

                    validatePassword();
                }
            }
        });

        // On Focus change listener on confirm password
        mConfirmPwdEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {

                    validateConfirmPassword();
                }
            }
        });

        // On Focus change listener on phone no
        mNumberEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {

                    validatePhoneNo();
                }
            }
        });
    }


    /**
     * Function to validate username
     */
    public boolean validateUsername() {

        boolean isValidUsername = false;
        String username = mNameEditText.getText().toString();

        // Validate Username
        if(Validation.isEmpty(username)) {

            mNameEditText.setError(getString(R.string.err_msg_name));

        } else {

            isValidUsername = true;
            mNameEditText.setError(null);
        }

        return isValidUsername;
    }


    /**
     * Function to validate email
     */
    public boolean validateEmailId() {

        boolean isValidEmail = false;
        String email = mEmailEditText.getText().toString();

        // Validate Username
        if(Validation.isEmpty(email) || !(Validation.isValidEmail(email))) {

            mEmailEditText.setError(getString(R.string.err_msg_email));

        } else {

            isValidEmail = true;
            mEmailEditText.setError(null);
        }

        return isValidEmail;
    }


    /**
     * Function to validate password
     */
    public boolean validatePassword() {

        boolean isValidPassword = false;
        String password = mPwdEditText.getText().toString();

        // Validate Username
        if(Validation.isEmpty(password) || !(Validation.isValidPassword(password))) {

            mPwdEditText.setError(getString(R.string.err_msg_password));

        } else {

            isValidPassword = true;
            mPwdEditText.setError(null);
        }

        return isValidPassword;
    }


    /**
     * Function to validate confirm password
     */
    public boolean validateConfirmPassword() {

        boolean isValidConfirmPassword = false;
        String password = mPwdEditText.getText().toString();
        String confirmPassword = mConfirmPwdEditText.getText().toString();

        // Validate Username
        if(Validation.isEmpty(confirmPassword) || !(password.equalsIgnoreCase(confirmPassword))) {

            mConfirmPwdEditText.setError(getString(R.string.err_msg_confirm_password));

        } else {

            isValidConfirmPassword = true;
            mConfirmPwdEditText.setError(null);
        }

        return isValidConfirmPassword;
    }


    /**
     * Function to validate phone
     */
    public boolean validatePhoneNo() {

        boolean isValidPhone = false;
        String phone = mNumberEditText.getText().toString();

        // Validate Username
        if(!Validation.isEmpty(phone) && phone.length() < 10) {

            mNumberEditText.setError(getString(R.string.err_msg_phone_no));

        } else {

            isValidPhone = true;
            mNumberEditText.setError(null);
        }

        return isValidPhone;
    }


    /**
     * Function to signup user
     * @param view
     */
    public void signupRequest(View view) {

        final String name = mNameEditText.getText().toString();
        final String email = mEmailEditText.getText().toString();
        final String password = mPwdEditText.getText().toString();
        final String phone = mNumberEditText.getText().toString();

        // Check if all fields are valid
        if (validateUsername()
                && validateEmailId()
                && validatePassword()
                && validateConfirmPassword()
                && validatePhoneNo()) {

            if (Validation.isConnected(getApplicationContext())) {

                // Progress Dialog Popup
                final ProgressDialog popup = new ProgressDialog(this);
                popup.setMessage(getString(R.string.creating_account_message));
                popup.show();

                // Service URL
                final String url = ServiceUrls.KEY_SIGNUP;

                // Set Headers
                final HashMap<String, String> headers = new HashMap<>();
                //headers.put(StaticConstants.KEY_CONTENT_TYPE, StaticConstants.KEY_URLENCODED);

                // Constructing request body
                final HashMap<String, Object> data = new HashMap<>();
                data.put(StringConstants.API_KEY_NAME, name);
                data.put(StringConstants.API_KEY_EMAIL, email);
                data.put(StringConstants.API_KEY_PASSWORD, password);
                data.put(StringConstants.API_KEY_PHONE, phone);

                /**
                 * API Call
                 */
                APICallService.PostAPICall(this, this, url, data, headers, new APIInterface() {
                    @Override
                    public void onSuccess(JSONObject response) {
                        try {

                            // Dismiss item_loader
                            popup.dismiss();

                            HashMap<String, Object> loginData = new HashMap<>();
                            loginData.put(StringConstants.KEY_USERNAME, email);
                            loginData.put(StringConstants.KEY_PASSWORD, password);
                            loginData.put(StringConstants.KEY_GRANT_TYPE, StringConstants.KEY_PASSWORD);

                            LoginActivity.loginRequest(SignUpActivity.this, getApplicationContext(), loginData);

                        } catch (Exception e) {

                            // Dismiss item_loader
                            popup.dismiss();

                            // Log error
                            Crashlytics.logException(e);
                            ErrorHandlers.handleError(activity);
                        }
                    }

                    @Override
                    public void onError(VolleyError error) {

                        // Dismiss item_loader
                        popup.dismiss();

                        // Log error
                        Crashlytics.logException(error);
                        if(error != null){

                            // Check Network error
                            NetworkResponse response = error.networkResponse;
                            if (response != null && response.data != null) {

                                switch (response.statusCode) {

                                    case 409:
                                        ErrorHandlers.handleEmailExistsConflictFailure(activity);
                                        break;

                                    default:
                                        ErrorHandlers.handleAPIError(activity);
                                        break;
                                }
                            }
                        }
                    }
                });



            } else {

                // No Internet Connection
                ErrorHandlers.handleInternetConnectionFailure(activity);
            }

        } else {

            // Case : Fields data not valid
            Toast.makeText(getApplicationContext(), getString(R.string.err_msg_signup), Toast.LENGTH_LONG).show();
        }
    }
}
