package com.careons.app.Patient.Activity.Signup;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
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

public class ForgotPasswordActivity extends Activity {

    private EditText emailEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        // Initialize
        emailEditText = (EditText) findViewById(R.id.fp_input_email);


        // On Focus change listener on email id
        emailEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {

                    validateEmailId();
                }
            }
        });
    }


    /**
     * Function to validate email
     */
    public boolean validateEmailId() {

        boolean isValidEmail = false;
        String email = emailEditText.getText().toString();

        // Validate Username
        if(Validation.isEmpty(email) || !(Validation.isValidEmail(email))) {

            emailEditText.setError(getString(R.string.err_msg_email));

        } else {

            isValidEmail = true;
            emailEditText.setError(null);
        }

        return isValidEmail;
    }


    /**
     * Function to request for forgot password
     * @param view
     */
    public void forgotPasswordRequest(final View view) {

        final View _view = view;
        final String email = emailEditText.getText().toString();
        final String url = ServiceUrls.KEY_FORGOT_PASSWORD;

        // Check if valid email id has been entered
        if(validateEmailId()) {

            if (Validation.isConnected(getApplicationContext())) {

                // Progress Dialog Popup
                final ProgressDialog popup = new ProgressDialog(this);
                popup.setMessage(getString(R.string.retrieving_pwd));
                popup.show();


                // Set Headers
                final HashMap<String, String> headers = new HashMap<>();
                //headers.put(StaticConstants.KEY_CONTENT_TYPE, StaticConstants.KEY_URLENCODED);

                // Constructing request body
                final HashMap<String, Object> data = new HashMap<>();
                data.put(StringConstants.API_KEY_USERNAME, email);


                // API Call
                APICallService.PostAPICall(this, this, url, data, headers, new APIInterface() {
                    @Override
                    public void onSuccess(JSONObject response) {
                        try {

                            // Dismiss item_loader
                            popup.dismiss();

                            AlertDialog.Builder builder = new AlertDialog.Builder(ForgotPasswordActivity.this);
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
                            titleTextView.setVisibility(View.GONE);
                            descriptionTextView.setText(getString(R.string.forgot_password_success));
                            ctaPrimaryTextView.setText(getString(R.string.action_ok));


                            /**
                             * Set Actions on CTA - Primary
                             */
                            ctaPrimaryTextView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    dialog.dismiss();

                                    //Close Activity
                                    finish();
                                }
                            });


                            // Show dialog
                            dialog.show();

                        } catch (Exception e) {

                            Toast.makeText(getApplicationContext(), getString(R.string.try_again_in_sometime),
                                    Toast.LENGTH_LONG).show();

                            // Log error
                            Crashlytics.logException(e);
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

                                    case 400:
                                        ErrorHandlers.handleEmailNotRegisteredError(ForgotPasswordActivity.this);
                                        break;

                                    default:
                                        ErrorHandlers.handleAPIError(ForgotPasswordActivity.this);
                                        break;
                                }
                            }
                        }
                    }
                });
            } else {

                // If internet connection is not available
                ErrorHandlers.handleInternetConnectionFailure(ForgotPasswordActivity.this);
            }
        }
    }


    /**
     * Function to create a login request
     * @param view
     */
    public void loginRequest(View view) {
        finish();
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
}
