package com.careons.app.Patient.Activity.Signup;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.VolleyError;
import com.careons.app.Patient.Activity.Main.MainActivity;
import com.careons.app.Patient.Commons.BuildProperties;
import com.careons.app.Patient.Commons.ServiceUrls;
import com.careons.app.Patient.Commons.StaticConstants;
import com.careons.app.Patient.Commons.StringConstants;
import com.careons.app.Patient.Database.Models.Healthbook.Allergy;
import com.careons.app.Patient.Database.Models.Healthbook.ChildhoodHistory;
import com.careons.app.Patient.Database.Models.Healthbook.FamilyHistory;
import com.careons.app.Patient.Database.Models.Healthbook.Gynaecology;
import com.careons.app.Patient.Database.Models.Healthbook.Lifestyle;
import com.careons.app.Patient.Database.Models.Healthbook.MedicalProblem;
import com.careons.app.Patient.Database.Models.Healthbook.Medication;
import com.careons.app.Patient.Database.Models.Healthbook.SurgicalHistory;
import com.careons.app.Patient.Database.Models.Profile.Profile;
import com.careons.app.Patient.Enums.BloodGroup;
import com.careons.app.Patient.Services.APICallService;
import com.careons.app.Patient.Services.SharedPreferenceService;
import com.careons.app.Patient.Services.SyncDatabaseService;
import com.careons.app.R;
import com.careons.app.Shared.Components.Crashlytics.LogUser;
import com.careons.app.Shared.Components.GoogleOAuth.GetUsernameTask;
import com.careons.app.Shared.Interfaces.APIInterface;
import com.careons.app.Shared.Utils.DateTimeUtils;
import com.careons.app.Shared.Utils.ErrorHandlers;
import com.careons.app.Shared.Utils.Validation;
import com.crashlytics.android.Crashlytics;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class LoginActivity extends FragmentActivity implements
        GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {

    private EditText usernameText, passwordText;
    private GoogleApiClient mGoogleApiClient;

    private static final String TAG = "IdTokenActivity";
    private static final int RC_GET_TOKEN = 9002;

    // Facebook CallbackManager
    private CallbackManager callbackManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize Facebook sdk
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_login);

        // Gmail Button click listeners
        findViewById(R.id.google_sign_in_button).setOnClickListener(this);

        // Initialize Edittext variables
        usernameText = (EditText) findViewById(R.id.login_input_email);
        passwordText = (EditText) findViewById(R.id.login_input_pwd);

        /**
         * Disable scroll bar
         */
        findViewById(R.id.scrollBar).setVerticalScrollBarEnabled(false);

        // On Focus change listener on username
        usernameText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {

                    validateUsername();
                }
            }
        });

        // On Focus change listener on password
        passwordText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {

                    validatePassword();
                }
            }
        });


        /**
         * Facebook Registration
         */
        callbackManager = CallbackManager.Factory.create();
        LoginButton mFacebookLoginButton = (LoginButton) findViewById(R.id.fb_login_button);
        mFacebookLoginButton.setReadPermissions(Arrays.asList("user_about_me", "email"));


        // Callback registration
        mFacebookLoginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                // Call Sign in with Facebook API
                signinWithFB(LoginActivity.this, getApplicationContext(), loginResult.getAccessToken().getToken());

                // Logout from Facebook
                LoginManager.getInstance().logOut();
            }

            @Override
            public void onCancel() {

                Toast.makeText(getApplicationContext(), getString(R.string.err_cancel_auth), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(FacebookException exception) {

                Toast.makeText(getApplicationContext(), getString(R.string.err_facebook_auth), Toast.LENGTH_LONG).show();
            }
        });


        /**
         * Gmail Registration
         */
        // For sample only: make sure there is a valid server client ID.
        validateServerClientID();

        // [START configure_signin]
        // Request only the user's ID token, which can be used to identify the
        // user securely to your backend. This will contain the user's basic
        // profile (name, profile picture URL, etc) so you should not need to
        // make an additional call to personalize your application.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(BuildProperties.googleDeveloperId)
                .requestEmail()
                .requestProfile()
                .build();
        // [END configure_signin]

        // Build GoogleAPIClient with the Google Sign-In API and the above options.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.google_sign_in_button:
                if (Validation.isConnected(getApplicationContext())) {
                    getIdToken();
                } else {

                    // If internet connection is not available
                    Toast.makeText(getApplicationContext(), getString(R.string.err_internet_not_available), Toast.LENGTH_LONG).show();
                }
                break;
        }
    }


    /**
     * Function to validate username
     */
    public boolean validateUsername(){

        boolean isValidUsername = false;
        String username = usernameText.getText().toString();

        // Validate Username
        if(Validation.isEmpty(username) || !(Validation.isValidEmail(username))) {

            usernameText.setError(getString(R.string.err_msg_name));

        } else {

            isValidUsername = true;
            usernameText.setError(null);
        }

        return isValidUsername;
    }


    /**
     * Function to validate password
     */
    public boolean validatePassword() {

        boolean isValidPassword = false;
        String password = passwordText.getText().toString();

        // Validate Username
        if(Validation.isEmpty(password) || !(Validation.isValidPassword(password))) {

            passwordText.setError(getString(R.string.err_msg_password));

        } else {

            isValidPassword = true;
            passwordText.setError(null);
        }

        return isValidPassword;
    }


    /**
     * Funtion to validate username and password
     * @param view
     */
    public void loginRequest(View view) {

        // Fetch data
        final String username = ((EditText) findViewById(R.id.login_input_email)).getText().toString();
        final String password = ((EditText) findViewById(R.id.login_input_pwd)).getText().toString();

        if(validateUsername() && validatePassword()) {

            // Construct body
            final HashMap<String, Object> data = new HashMap<>();
            data.put(StringConstants.KEY_USERNAME, username);
            data.put(StringConstants.KEY_PASSWORD, password);
            data.put(StringConstants.KEY_GRANT_TYPE, StringConstants.KEY_PASSWORD);

            /**
             * Call Login API
             */
            loginRequest(this, getApplicationContext(), data);
        }
    }


    /**
     * Function to navigate to create account
     * @param view
     */
    public void signupRequest(View view) {
        Intent intent = new Intent(this, SignUpActivity.class);
        startActivity(intent);

        // close this activity
        finish();
    }


    /**
     * Function to navigate to forgot password
     * @param view
     */
    public void forgotRequest(View view) {
        Intent intent = new Intent(this, ForgotPasswordActivity.class);
        startActivity(intent);
    }


    /**
     * Function to navigate to Welcome Activity
     * * @param view
     */
    public static void goToWelcomeActivity(Activity activity, Context context) {

        // Navigate to Home Activity on success
        Intent intent = new Intent(context, WelcomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        activity.startActivity(intent);

        // close this activity
        ActivityCompat.finishAffinity(activity);
    }


    /**
     * Function to navigate to Create Account Activity
     * * @param view
     */
    public static void goToCreateAccount(Activity activity, Context context) {

        Intent intent = new Intent(context, CreateAccountActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        activity.startActivity(intent);

        // close this activity
        ActivityCompat.finishAffinity(activity);
    }


    /**
     * Function to navigate to Main Activity
     * * @param view
     */
    public static void goToHomepage(Activity activity, Context context) {

        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        activity.startActivity(intent);

        // close this activity
        ActivityCompat.finishAffinity(activity);
    }


    /**
     * Start Gmail Auth methods
     */
    private void getIdToken() {
        // Show an account picker to let the user choose a Google account from the device.
        // If the GoogleSignInOptions only asks for IDToken and/or profile and/or email then no
        // consent screen will be shown here.
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_GET_TOKEN);
    }


    private void signOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        Log.d(TAG, "signOut:onResult:" + status);
                        updateUI(false);
                    }
                });
    }


    private void revokeAccess() {
        Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        Log.d(TAG, "revokeAccess:onResult:" + status);
                        updateUI(false);
                    }
                });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_GET_TOKEN) {

            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);

            if (result.isSuccess()) {
                GoogleSignInAccount account = result.getSignInAccount();
                String idToken = account.getIdToken();
                String scope =  "oauth2:https://www.googleapis.com/auth/userinfo.profile";

                /**
                 * Fetch Access TOken
                 */
                new GetUsernameTask(LoginActivity.this, account.getEmail(), scope).execute();

                updateUI(true);
                signOut();

            } else {

                // Show signed-out UI.
                updateUI(false);
            }

        } else {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }


    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }


    private void updateUI(boolean signedIn) {
        if (signedIn) {
            //Toast.makeText(this, "Sign In", Toast.LENGTH_LONG).show();
        } else {
            //Toast.makeText(this, "Sign out", Toast.LENGTH_LONG).show();

        }
    }


    /**
     * Validates that there is a reasonable server client ID in strings.xml, this is only needed
     * to make sure users of this sample follow the README.
     */
    private void validateServerClientID() {
        String serverClientId = BuildProperties.googleDeveloperId;
        String suffix = ".apps.googleusercontent.com";
        if (!serverClientId.trim().endsWith(suffix)) {
            String message = "Invalid server client ID in strings.xml, must end with " + suffix;

            Log.w(TAG, message);
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        }
    }


    /**
     * Function to signin with Facebook
     * @param token
     */
    public static void signinWithFB(final Activity activity, final Context context, String token) {

        // Url construction
        final String url = ServiceUrls.KEY_LOGIN_FB;

        // Progress Dialog Popup
        final ProgressDialog popup = new ProgressDialog(activity);
        popup.setMessage(context.getString(R.string.fetching_data_message));
        popup.show();

        // Set Data
        HashMap<String, Object> data = new HashMap<>();
        data.put(StringConstants.API_KEY_TOKEN, token);

        // Set Headers
        final HashMap<String, String> headers = new HashMap<>();


        if (Validation.isConnected(context)) {

            // API Call
            APICallService.PostAPICall(activity, activity, url, data, headers, new APIInterface() {
                @Override
                public void onSuccess(JSONObject response) {

                    try {

                        // Dismiss item_loader
                        popup.dismiss();

                        HashMap<String, Object> data = new HashMap<>();
                        data.put(StringConstants.KEY_USERNAME, response.getJSONObject(StringConstants.KEY_USER).get(StringConstants.API_KEY_EMAIL));
                        data.put(StringConstants.KEY_PASSWORD, response.getJSONObject(StringConstants.KEY_USER).get(StringConstants.API_KEY_PASSWORD));
                        data.put(StringConstants.KEY_GRANT_TYPE, StringConstants.KEY_PASSWORD);

                        // Call Login API
                        loginRequest(activity, context, data);

                    }  catch (Exception e) {

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
                    ErrorHandlers.handleAPIError(activity);
                }
            });

        } else {

            // If internet connection is not available
            ErrorHandlers.handleInternetConnectionFailure(activity);
        }
    }


    /**
     * Function to signin with Google
     * @param token
     */
    public static void signinWithGoogle(final Activity activity, final Context context, String token) {

        // Url construction
        final String url = ServiceUrls.KEY_LOGIN_GOOGLE;

        // Progress Dialog Popup
        final ProgressDialog popup = new ProgressDialog(activity);
        popup.setMessage(context.getString(R.string.fetching_data_message));
        popup.show();

        // Set Data
        HashMap<String, Object> data = new HashMap<>();
        data.put(StringConstants.API_KEY_TOKEN, token);

        // Set Headers
        final HashMap<String, String> headers = new HashMap<>();



        if (Validation.isConnected(context)) {

            // API Call
            APICallService.PostAPICall(activity, activity, url, data, headers, new APIInterface() {
                @Override
                public void onSuccess(JSONObject response) {

                    try {

                        // Dismiss item_loader
                        popup.dismiss();

                        HashMap<String, Object> data = new HashMap<>();
                        data.put(StringConstants.KEY_USERNAME, response.getJSONObject(StringConstants.KEY_USER).get(StringConstants.API_KEY_EMAIL));
                        data.put(StringConstants.KEY_PASSWORD, response.getJSONObject(StringConstants.KEY_USER).get(StringConstants.API_KEY_PASSWORD));
                        data.put(StringConstants.KEY_GRANT_TYPE, StringConstants.KEY_PASSWORD);

                        // Call Login API
                        loginRequest(activity, context, data);

                    }  catch (Exception e) {

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
                    ErrorHandlers.handleAPIError(activity);
                }
            });

        } else {

            // If internet connection is not available
            ErrorHandlers.handleInternetConnectionFailure(activity);
        }
    }


    /**
     * Function to call Login API
     * @param data
     */
    public static void loginRequest(final Activity activity, final Context context, HashMap<String, Object> data) {

        // Url construction
        final String url = ServiceUrls.KEY_LOGIN;

        // Set Headers
        final HashMap<String, String> headers = new HashMap<>();
        headers.put(StaticConstants.KEY_CONTENT_TYPE, StaticConstants.KEY_URLENCODED);


        if (Validation.isConnected(context)) {

            // Progress Dialog Popup
            final ProgressDialog popup = new ProgressDialog(activity);
            popup.setMessage(context.getString(R.string.signing_in_message));
            popup.show();


            // API Call
            APICallService.TokenAPICall(context, url, data, headers, new APIInterface() {
                @Override
                public void onSuccess(JSONObject response) {

                    try {

                        // Dismiss item_loader
                        popup.dismiss();
                        

                        // Token
                        SharedPreferenceService.storeUserDetails(context, StringConstants.KEY_TOKEN,
                                response.getString(StringConstants.KEY_TOKEN));

                        /**
                         *  Check if patient token present in local database
                         */
                        checkProfileDataLocalDB(activity, context, response);

                    }  catch (Exception e) {

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

                                case 400:
                                    ErrorHandlers.handleEmailPasswordNotMatchError(activity);
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

            // If internet connection is not available
            ErrorHandlers.handleInternetConnectionFailure(activity);
        }
    }


    /**
     * Function to check if patient id exists in local database
     * @param response
     */
    public static void checkProfileDataLocalDB(final Activity activity, final Context context, JSONObject response) {

        try {

            /**
             * Check for Profile in Database
             */
            List<Profile> profiles = Profile.find(Profile.class,
                    "patient_id = ?", response.getString(StringConstants.KEY_ID));

            Profile profile;

            if (profiles == null || profiles.size() == 0) {

                /**
                 * Convert False -> false (Fix)
                 */
                boolean isAccountCreated = false;
                boolean isOnboardingComplete = false;
                if(response.getString(StringConstants.API_KEY_IS_ACCOUNT_CREATED).equalsIgnoreCase("True")) {
                    isAccountCreated = true;
                }

                if(response.getString(StringConstants.API_KEY_IS_ONBOARDING_COMPLETE).equalsIgnoreCase("True")) {
                    isOnboardingComplete = true;
                }


                /**
                 * Check fields before manipulation (Blood Group and Date of Birth)
                 */
                String bloodGroup = response.getString(StringConstants.API_KEY_BLOOD_GROUP);
                if(!bloodGroup.isEmpty()) {
                    bloodGroup = BloodGroup.valueOf(bloodGroup).getBloodGroup();
                }

                String dob = response.getString(StringConstants.API_KEY_DOB);
                String age = null;
                if(!dob.isEmpty()) {
                    age = DateTimeUtils.calculateAgeFromDOB(DateTimeUtils.convertDateLoginToTimestamp(dob));
                }

                /**
                 * Profile does not exists in Local Database
                 */
                profile = new Profile(
                        response.getString(StringConstants.KEY_ID),
                        response.getString(StringConstants.API_KEY_NAME),
                        response.getString(StringConstants.API_KEY_USERNAME),
                        response.getString(StringConstants.API_KEY_PHONE),
                        dob,
                        age,
                        response.getString(StringConstants.API_KEY_SEX),
                        bloodGroup,
                        isAccountCreated,
                        isOnboardingComplete);

                profile.save();

                /**
                 * Save Profile data to Shared Preference Memory
                 */
                SharedPreferenceService.storeProfileToSharedPreference(context, profile);

                /**
                 * Sync Database
                 */
                SyncDatabaseService.readMedicalProblemData(activity, context);

            } else {

                /**
                 * Profile exists in Local Database
                 */
                profile = profiles.get(0);

                /**
                 * Save Profile data to Shared Preference Memory
                 */
                SharedPreferenceService.storeProfileToSharedPreference(context, profile);


                /**
                 * Calculate Count For Healthbook Sections
                 */
                List<MedicalProblem> healthbookMedicalProblemList =
                        MedicalProblem.find(MedicalProblem.class,
                                "patient_id = ?", SharedPreferenceService.getValue(context, StringConstants.KEY_PATIENT_ID));
                SharedPreferenceService.storeUserDetails(context, StringConstants.KEY_PROBLEM_COUNT, String.valueOf(healthbookMedicalProblemList.size()));

                List<Allergy> allergyList =
                        Allergy.find(Allergy.class,
                                "patient_id = ?", SharedPreferenceService.getValue(context, StringConstants.KEY_PATIENT_ID));
                SharedPreferenceService.storeUserDetails(context, StringConstants.KEY_ALLERGY_COUNT, String.valueOf(allergyList.size()));

                List<FamilyHistory> familyHistoryList =
                        FamilyHistory.find(FamilyHistory.class,
                                "patient_id = ?", SharedPreferenceService.getValue(context, StringConstants.KEY_PATIENT_ID));
                SharedPreferenceService.storeUserDetails(context, StringConstants.KEY_FAMILY_COUNT, String.valueOf(familyHistoryList.size()));

                List<Medication> medicationList =
                        Medication.find(Medication.class,
                                "patient_id = ?", SharedPreferenceService.getValue(context, StringConstants.KEY_PATIENT_ID));
                SharedPreferenceService.storeUserDetails(context, StringConstants.KEY_MEDICATION_COUNT, String.valueOf(medicationList.size()));

                List<Lifestyle> lifestyleList =
                        Lifestyle.find(Lifestyle.class,
                                "patient_id = ?", SharedPreferenceService.getValue(context, StringConstants.KEY_PATIENT_ID));
                SharedPreferenceService.storeUserDetails(context, StringConstants.KEY_LIFESTYLE_COUNT, String.valueOf(lifestyleList.size()));

                List<SurgicalHistory> surgicalHistoryList =
                        SurgicalHistory.find(SurgicalHistory.class,
                                "patient_id = ?", SharedPreferenceService.getValue(context, StringConstants.KEY_PATIENT_ID));
                SharedPreferenceService.storeUserDetails(context, StringConstants.KEY_SURGICAL_COUNT, String.valueOf(surgicalHistoryList.size()));

                List<ChildhoodHistory> childhoodHistoryList =
                        ChildhoodHistory.find(ChildhoodHistory.class,
                                "patient_id = ?", SharedPreferenceService.getValue(context, StringConstants.KEY_PATIENT_ID));
                SharedPreferenceService.storeUserDetails(context, StringConstants.KEY_CHILDHOOD_COUNT, String.valueOf(childhoodHistoryList.size()));

                List<Gynaecology> gynaecologyList =
                        Gynaecology.find(Gynaecology.class,
                                "patient_id = ?", SharedPreferenceService.getValue(context, StringConstants.KEY_PATIENT_ID));
                SharedPreferenceService.storeUserDetails(context, StringConstants.KEY_GYNAECOLOGY_COUNT, String.valueOf(gynaecologyList.size()));


                /**
                 * Next Step
                 */
                nextStep(activity, context);
            }

        } catch (Exception error) {

            // Log error
            Crashlytics.logException(error);
        }
    }


    /**
     * Function to determine next activity based on flags set in Shared Preference
     */
    public static void nextStep(Activity activity, Context context) {

        /**
         * Log User Credentials to Crashlytics
         */
        LogUser.logUser(context);

        /**
         * Next Step -> (isAccountCreated = true && isOnboardingComplete = true -> Main Activity)
         *                else (isAccountCreated = true && isOnboardingComplete = false -> Welcome Activity)
         *                 else (isAccountCreated = false -> Create Account Activity)
         */


        if(Boolean.valueOf(SharedPreferenceService.getValue(context, StringConstants.KEY_IS_ACCOUNT_CREATED)) &&
                Boolean.valueOf(SharedPreferenceService.getValue(context, StringConstants.KEY_IS_ONBOARDING_COMPLETE))) {

            // Go to Homepage
            goToHomepage(activity, context);

        } else if(Boolean.valueOf(SharedPreferenceService.getValue(context, StringConstants.KEY_IS_ACCOUNT_CREATED))) {

            // Go to Onboarding Section
            goToWelcomeActivity(activity, context);

        } else {

            // Go to Create Account
            goToCreateAccount(activity, context);
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
