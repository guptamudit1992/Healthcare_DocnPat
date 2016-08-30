package com.careons.app.Patient.Activity.Others;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.VolleyError;
import com.careons.app.Patient.Commons.ServiceUrls;
import com.careons.app.Patient.Commons.StaticConstants;
import com.careons.app.Patient.Commons.StringConstants;
import com.careons.app.Shared.Interfaces.APIInterface;
import com.careons.app.R;
import com.careons.app.Patient.Services.APICallService;
import com.careons.app.Patient.Services.SharedPreferenceService;
import com.careons.app.Shared.Utils.ErrorHandlers;
import com.careons.app.Shared.Utils.Validation;
import com.crashlytics.android.Crashlytics;

import org.json.JSONObject;

import java.util.HashMap;

public class ChangePasswordActivity extends AppCompatActivity {

    private static EditText currentPasswordEditText, newPasswordEditText, confirmPasswordEditText;
    private static ProgressDialog popup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        // Initialize Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.change_password));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        /**
         * Current Password
         */
        currentPasswordEditText = (EditText) findViewById(R.id.current_password);
        currentPasswordEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if(!hasFocus) {

                    validatePassword(currentPasswordEditText);
                }
            }
        });

        /**
         * New Password
         */
        newPasswordEditText = (EditText) findViewById(R.id.new_password);
        newPasswordEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if(!hasFocus) {

                    validatePassword(newPasswordEditText);
                }
            }
        });

        /**
         * Confirm Password
         */
        confirmPasswordEditText = (EditText) findViewById(R.id.confirm_password);
        confirmPasswordEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if(!hasFocus) {

                    validateConfirmPassword();
                }
            }
        });


        /**
         * Change Password FAB Listener
         */
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(validatePassword(currentPasswordEditText)
                        && validatePassword(newPasswordEditText)
                        && validateConfirmPassword()) {

                    changePassword();

                } else {

                    // Case : Fields data not valid
                    Toast.makeText(getApplicationContext(), getString(R.string.err_msg_signup), Toast.LENGTH_LONG).show();
                }
            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
        }

        return super.onOptionsItemSelected(item);
    }


    /**
     * Function to validate password
     * @param passwordEditText
     * @return
     */
    public boolean validatePassword(EditText passwordEditText) {

        boolean isValidPassword = false;
        String password = passwordEditText.getText().toString();

        // Validate Username
        if(Validation.isEmpty(password) || !(Validation.isValidPassword(password))) {

            passwordEditText.setError(getString(R.string.err_msg_password));

        } else {

            isValidPassword = true;
            passwordEditText.setError(null);
        }

        return isValidPassword;
    }


    /**
     * Function to validate confirm password
     */
    public boolean validateConfirmPassword() {

        boolean isValidConfirmPassword = false;
        String password = newPasswordEditText.getText().toString();
        String confirmPassword = confirmPasswordEditText.getText().toString();

        // Validate Username
        if(Validation.isEmpty(confirmPassword) || !(password.equalsIgnoreCase(confirmPassword))) {

            confirmPasswordEditText.setError(getString(R.string.err_msg_confirm_password));

        } else {

            isValidConfirmPassword = true;
            confirmPasswordEditText.setError(null);
        }

        return isValidConfirmPassword;
    }


    /**
     * Function to change password
     */
    public void changePassword() {

        // Url construction
        final String url = ServiceUrls.KEY_CHANGE_PASSWORD;

        // Initialize Data
        HashMap<String, Object> data = new HashMap<>();
        data.put(StringConstants.API_KEY_NEW_PASSWORD, newPasswordEditText.getText().toString());

        // Set Headers
        final HashMap<String, String> headers = new HashMap<>();
        headers.put(StaticConstants.KEY_AUTHORIZATION,
                StaticConstants.KEY_BEARER
                        .concat(" ")
                        .concat(SharedPreferenceService.getValue(getApplicationContext(), StringConstants.KEY_TOKEN)));


        if (Validation.isConnected(this)) {

            // Progress Dialog Popup
            popup = new ProgressDialog(this);
            popup.setMessage(getString(R.string.changing_password_message));
            popup.show();


            // API Call
            APICallService.PostAPICall(this, this, url, data, headers, new APIInterface() {
                @Override
                public void onSuccess(JSONObject response) {

                    try {

                        /**
                         * Update Token in Shared Preference Memory
                         */
                        updateToken();

                    }  catch (Exception e) {

                        // Dismiss item_loader
                        popup.dismiss();

                        // Log error
                        Crashlytics.logException(e);
                        ErrorHandlers.handleError(ChangePasswordActivity.this);
                    }
                }

                @Override
                public void onError(VolleyError error) {

                    // Dismiss item_loader
                    popup.dismiss();
                }
            });
        } else {

            // If internet connection is not available
            ErrorHandlers.handleInternetConnectionFailure(this);
        }
    }


    /**
     * Function to update token
     */
    public void updateToken() {

        // Url construction
        final String url = ServiceUrls.KEY_LOGIN;

        // Initialize Data
        HashMap<String, Object> data = new HashMap<>();
        data.put(StringConstants.KEY_USERNAME,
                SharedPreferenceService.getValue(this, StringConstants.KEY_EMAIL));
        data.put(StringConstants.KEY_PASSWORD, newPasswordEditText.getText().toString());
        data.put(StringConstants.KEY_GRANT_TYPE, StringConstants.KEY_PASSWORD);

        // Set Headers
        final HashMap<String, String> headers = new HashMap<>();
        headers.put(StaticConstants.KEY_CONTENT_TYPE, StaticConstants.KEY_URLENCODED);


        if (Validation.isConnected(this)) {

            // API Call
            APICallService.TokenAPICall(this, url, data, headers, new APIInterface() {
                @Override
                public void onSuccess(JSONObject response) {

                    try {

                        // Dismiss item_loader
                        popup.dismiss();

                        /**
                         * Store Generated Token in Database
                         */
                        SharedPreferenceService.storeUserDetails(getApplicationContext(), StringConstants.KEY_TOKEN,
                                response.getString(StringConstants.KEY_TOKEN));

                        // Password Change Success
                        passwordChangeSuccess();

                    }  catch (Exception e) {

                        // Dismiss item_loader
                        popup.dismiss();

                        // Log error
                        Crashlytics.logException(e);
                        ErrorHandlers.handleError(ChangePasswordActivity.this);
                    }
                }


                @Override
                public void onError(VolleyError error) {

                    System.out.println("Check Error For Updating Token - " + error);

                    // Dismiss item_loader
                    popup.dismiss();

                    // Log error
                    Crashlytics.logException(error);
                    if(error != null){

                        // Check Network error
                        NetworkResponse response = error.networkResponse;
                        if (response != null && response.data != null) {

                            switch (response.statusCode) {

                                case 400:
                                    ErrorHandlers.handleEmailPasswordNotMatchError(ChangePasswordActivity.this);
                                    break;

                                default:
                                    ErrorHandlers.handleAPIError(ChangePasswordActivity.this);
                                    break;
                            }
                        }
                    }
                }
            });
        } else {

            // If internet connection is not available
            ErrorHandlers.handleInternetConnectionFailure(this);
        }
    }


    /**
     * Function to indicate password change has been successful
     */
    public void passwordChangeSuccess() {


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.dialog_error, null);
        builder.setView(dialogView);
        final AlertDialog dialog = builder.create();

        /**
         * Initialize Data Fields
         */
        ImageView errorImageView = (ImageView) dialogView.findViewById(R.id.error_image);
        TextView titleTextView = (TextView) dialogView.findViewById(R.id.error_title);
        TextView descriptionTextView = (TextView) dialogView.findViewById(R.id.error_description);
        TextView ctaPrimaryTextView = (TextView) dialogView.findViewById(R.id.error_primary_cta);


        /**
         * Substitute values
         */
        errorImageView.setImageDrawable(getResources().getDrawable(R.drawable.dp6));
        titleTextView.setText(getString(R.string.dialog_password_change_title));
        descriptionTextView.setText(getString(R.string.dialog_password_change));
        ctaPrimaryTextView.setText(getString(R.string.action_ok));


        /**
         * Set Actions on CTA - Primary
         */
        ctaPrimaryTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();

                // Finish Activity
                finish();
            }
        });


        // Show dialog
        dialog.show();
    }
}
