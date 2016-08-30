package com.careons.app.Patient.Activity.Signup;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.careons.app.Patient.Activity.Main.MainActivity;
import com.careons.app.Patient.Activity.Onboarding.OnboardingMedicalProblemsActivity;
import com.careons.app.Patient.Commons.ServiceUrls;
import com.careons.app.Patient.Commons.StaticConstants;
import com.careons.app.Patient.Commons.StringConstants;
import com.careons.app.Patient.Database.Models.Profile.Profile;
import com.careons.app.Shared.Interfaces.APIInterface;
import com.careons.app.R;
import com.careons.app.Patient.Services.APICallService;
import com.careons.app.Patient.Services.SharedPreferenceService;
import com.careons.app.Shared.Utils.Validation;

import org.json.JSONObject;

import java.util.HashMap;

public class WelcomeActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
    }


    @Override
    public void onBackPressed() {

        finish();
    }


    /**
     * Function to navigate to onboarding
     * @param view
     */
    public void gotoOnboarding(View view) {

        Intent intent = new Intent(this, OnboardingMedicalProblemsActivity.class);
        startActivity(intent);

        // close this activity
        finish();
    }



    /**
     * Function to update profile - onboarding complete
     */
    public static void updateOnboardingComplete(final Activity activity) {

        // Service URL
        final String url = ServiceUrls.KEY_PROFILE;

        if (Validation.isConnected(activity)) {

            // Progress Dialog Popup
            final ProgressDialog popup = new ProgressDialog(activity);
            popup.setMessage(activity.getString(R.string.welcome_message));
            popup.show();

            // Set Headers
            final HashMap<String, String> headers = new HashMap<>();
            headers.put(StaticConstants.KEY_AUTHORIZATION,
                    StaticConstants.KEY_BEARER
                            .concat(" ")
                            .concat(SharedPreferenceService
                                    .getValue(activity.getApplicationContext(), StringConstants.KEY_TOKEN)));


            // Constructing request body
            final HashMap<String, Object> data = new HashMap<>();
            data.put(StringConstants.KEY_ID, SharedPreferenceService.getValue(activity, StringConstants.KEY_PATIENT_ID));
            data.put(StringConstants.API_KEY_DOB, SharedPreferenceService.getValue(activity, StringConstants.KEY_DOB));
            data.put(StringConstants.API_KEY_SEX, SharedPreferenceService.getValue(activity, StringConstants.KEY_GENDER));
            data.put(StringConstants.API_KEY_BLOOD_GROUP, SharedPreferenceService.getValue(
                    activity, StringConstants.KEY_BLOOD_GROUP).replace(" ", ""));
            data.put(StringConstants.API_KEY_IS_ACCOUNT_CREATED, 1);
            data.put(StringConstants.API_KEY_IS_ONBOARDING_COMPLETE, 1);


            // API Call
            APICallService.PutAPICall(activity, activity, url, data, headers, new APIInterface() {
                @Override
                public void onSuccess(JSONObject response) {

                    // Dismiss item_loader
                    popup.dismiss();

                    /**
                     * Update Local Database (is Onboarding Complete Flag)
                     */
                    Profile profile = Profile.find(Profile.class, "patient_id = ?",
                            SharedPreferenceService.getValue(activity, StringConstants.KEY_PATIENT_ID)).get(0);
                    profile.setOnboardingComplete(true);

                    profile.save();


                    /**
                     * Store values in the Shared Preference (is Onboarding Complete Flag)
                     */
                    SharedPreferenceService.storeUserDetails(activity,
                            StringConstants.KEY_IS_ONBOARDING_COMPLETE, "true");


                    // Goto Main Activity
                    Intent intent = new Intent(activity, MainActivity.class);
                    activity.startActivity(intent);

                    // close this activity
                    ActivityCompat.finishAffinity(activity);
                }

                @Override
                public void onError(VolleyError error) {

                    // Dismiss item_loader
                    popup.dismiss();

                    Toast.makeText(activity, activity.getString(R.string.try_again_in_sometime),
                            Toast.LENGTH_LONG).show();
                }
            });
        } else {

            // If internet connection is not available
            Toast.makeText(activity, activity.getString(R.string.err_internet_not_available),
                    Toast.LENGTH_LONG).show();
        }
    }

}
