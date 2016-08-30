package com.careons.app.Patient.Activity.Onboarding;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.android.volley.VolleyError;
import com.careons.app.Patient.Activity.Signup.WelcomeActivity;
import com.careons.app.Patient.Adapters.Adapter;
import com.careons.app.Patient.Commons.ServiceUrls;
import com.careons.app.Patient.Commons.StaticConstants;
import com.careons.app.Patient.Commons.StringConstants;
import com.careons.app.Patient.Database.Models.Healthbook.Lifestyle;
import com.careons.app.Shared.Interfaces.APIInterface;
import com.careons.app.R;
import com.careons.app.Patient.Services.APICallService;
import com.careons.app.Patient.Services.SharedPreferenceService;
import com.careons.app.Shared.Utils.DateTimeUtils;
import com.careons.app.Shared.Utils.ErrorHandlers;
import com.careons.app.Shared.Utils.Utils;
import com.careons.app.Shared.Utils.Validation;
import com.careons.app.databinding.CardLifestyleBinding;
import com.crashlytics.android.Crashlytics;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class OnboardingLifestyleActivity extends AppCompatActivity {

    private static Activity activity;
    private static OnboardingLifestyleActivity onboardingLifestyleActivity;

    private static View root;
    private static RecyclerView lifestyleHistoryList;
    private static LinearLayout loader;
    private static LinearLayout menCtaLinearLayout, womenCtaLinearLayout, cta_layout;

    private static Adapter<Lifestyle, CardLifestyleBinding> lifestyleBindingAdapter;
    private static ArrayList<Lifestyle> healthbookLifestyleArrayList = new ArrayList<>();

    // Initial problem
    private static SparseArray<String> initialLifestyleHistoryList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding_lifestyle);

        // Initialize instance
        activity = this;
        onboardingLifestyleActivity = this;

        // Initialize toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Status Bar Color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.darker_lf));
        }

        // Initialize Layouts
        menCtaLinearLayout = (LinearLayout) findViewById(R.id.men_lf_cta);
        womenCtaLinearLayout = (LinearLayout) findViewById(R.id.women_lf_cta);
        cta_layout = (LinearLayout) findViewById(R.id.cta_layout);

        // Check Gender to show CTA
        if(SharedPreferenceService.getValue(getApplicationContext(),
                StringConstants.KEY_GENDER).equalsIgnoreCase(StringConstants.KEY_MALE)) {

            // MALE
            menCtaLinearLayout.setVisibility(View.VISIBLE);
            womenCtaLinearLayout.setVisibility(View.GONE);

        } else {

            // FEMALE
            menCtaLinearLayout.setVisibility(View.GONE);
            womenCtaLinearLayout.setVisibility(View.VISIBLE);
        }

        // Loader
        loader = (LinearLayout) findViewById(R.id.loader);

        // Initialize Recycler View
        lifestyleHistoryList = (RecyclerView) findViewById(R.id.lf_recycler_view);
        lifestyleHistoryList.setHasFixedSize(true);

        /**
         * Populate recycler view
         */
        readLifestyleHistoryList(getApplicationContext());


        /**
         * Function to toggle CTA with keyboard
         */
        root = findViewById(R.id.root);
        root.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {

                Utils.toggleCTAKeyboard(root, cta_layout);
            }
        });
    }




    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case 1000:

                if(resultCode == Activity.RESULT_OK){
                    readLifestyleHistoryList(getApplicationContext());
                }
                break;

            default:
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {

        super.onBackPressed();
    }





    /**
     * Function to fetch instance of the current activity
     * @return
     */
    public static OnboardingLifestyleActivity getInstance() {
        return onboardingLifestyleActivity;
    }


    /**
     * Function to initialize Lifestyle Problem List
     */
    public static void fetchInitialProblemList() {

        initialLifestyleHistoryList = new SparseArray<String>() {
            {
                put(1, getInstance().getResources().getString(R.string.lifestyle_1));
                put(2, getInstance().getResources().getString(R.string.lifestyle_2));
                put(3, getInstance().getResources().getString(R.string.lifestyle_3));
                put(4, getInstance().getResources().getString(R.string.lifestyle_4));
                put(5, getInstance().getResources().getString(R.string.lifestyle_5));
                put(6, getInstance().getResources().getString(R.string.lifestyle_6));
                put(7, getInstance().getResources().getString(R.string.lifestyle_7));
                put(8, getInstance().getResources().getString(R.string.lifestyle_8));
                put(9, getInstance().getResources().getString(R.string.lifestyle_9));
                put(10, getInstance().getResources().getString(R.string.lifestyle_10));
            }
        };
    }







    /**
     * Function to create lifestyle problem
     * @param context
     */
    public static void createLifestyleHistory(final Context context, final HashMap<String, String> data) {

        // Set initial states
        loader.setVisibility(View.VISIBLE);
        lifestyleHistoryList.setVisibility(View.GONE);

        // Url
        String url = ServiceUrls.KEY_HEALTHBOOK_LIFESTYLE.concat(StringConstants.KEY_CREATE);

        // Set data
        final HashMap<String, Object> postData = new HashMap<>();
        postData.put(StringConstants.KEY_PATIENT_ID, SharedPreferenceService.getValue(context, StringConstants.KEY_PATIENT_ID));
        postData.put(StringConstants.API_KEY_LIFESTYLE_ID, data.get(StringConstants.KEY_PROBLEMS_ID));
        postData.put(StringConstants.KEY_IS_SUFFERING, Boolean.valueOf(data.get(StringConstants.KEY_IS_SUFFERING)) ? 1 : 0);
        postData.put(StringConstants.KEY_START_DATE, DateTimeUtils.convertTimestampToUTC(Long.valueOf(data.get(StringConstants.KEY_START_DATE))));
        postData.put(StringConstants.KEY_END_DATE, DateTimeUtils.convertTimestampToUTC(Long.valueOf(data.get(StringConstants.KEY_END_DATE))));
        postData.put(StringConstants.KEY_COMMENT, data.get(StringConstants.KEY_COMMENT));


        // Set Headers
        final HashMap<String, String> headers = new HashMap<>();
        headers.put(StaticConstants.KEY_AUTHORIZATION,
                StaticConstants.KEY_BEARER
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
                        String startDate = data.get(StringConstants.KEY_START_DATE);
                        if(!startDate.isEmpty()) {
                            startDate = DateTimeUtils.convertTimestampToDate(Long.valueOf(startDate));
                        }

                        String endDate = data.get(StringConstants.KEY_END_DATE);
                        if(!endDate.isEmpty()) {
                            endDate = DateTimeUtils.convertTimestampToDate(Long.valueOf(endDate));
                        }


                        // Create in Local Database
                        Lifestyle lifestyle = new Lifestyle(
                                SharedPreferenceService.getValue(context, StringConstants.KEY_PATIENT_ID),
                                response.getString(StringConstants.KEY_P_ID),
                                data.get(StringConstants.KEY_PROBLEMS_ID),
                                data.get(StringConstants.KEY_PROBLEM_NAME),
                                Boolean.valueOf(data.get(StringConstants.KEY_IS_SUFFERING)),
                                "",
                                startDate,
                                endDate,
                                data.get(StringConstants.KEY_COMMENT),
                                true
                        );

                        // Save to Database Table
                        lifestyle.save();

                        // Read medical problem list
                        readLifestyleHistoryList(context);


                    } catch (Exception e) {

                        // Show content
                        loader.setVisibility(View.GONE);
                        lifestyleHistoryList.setVisibility(View.VISIBLE);

                        // Log error
                        ErrorHandlers.handleError(activity);
                        Crashlytics.logException(e);
                    }
                }

                @Override
                public void onError(VolleyError error) {

                    // Show content
                    loader.setVisibility(View.GONE);
                    lifestyleHistoryList.setVisibility(View.VISIBLE);
                }
            });

        } else {

            // Show content
            loader.setVisibility(View.GONE);
            lifestyleHistoryList.setVisibility(View.VISIBLE);

            // Internet Not Available
            ErrorHandlers.handleInternetConnectionFailure(activity);
        }
    }


    /**
     * Function to fetch medical problem data
     */
    public static void readLifestyleHistoryList(final Context context) {

        // Fetch initial problem list
        fetchInitialProblemList();

        // Empty object holder
        healthbookLifestyleArrayList.clear();

        // Read from database table
        List<Lifestyle> lifestyleList =
                Lifestyle.find(Lifestyle.class,
                        "patient_id = ?", SharedPreferenceService.getValue(context, StringConstants.KEY_PATIENT_ID));

        // Check if database has entries filed else display initial problem list
        if(lifestyleList.size() > 0) {

            for(int i = 0; i < lifestyleList.size(); i++) {

                Lifestyle lifestyle = lifestyleList.get(i);

                /**
                 * Check if Suffering -> Calculate Duration and Set End Date to null
                 */
                if(lifestyle.isStillSuffering()) {

                    String duration = DateTimeUtils.calculateDurationFromStartDate(
                            DateTimeUtils.convertDateSimpleToTimestamp(lifestyle.getStartDate()));

                    lifestyle.setDuration(duration);
                    lifestyle.setEndDate("");
                }

                // Add to lifestyle list
                healthbookLifestyleArrayList.add(lifestyle);

                /**
                 * Remove selected problems from initial problem list
                 */
                initialLifestyleHistoryList.remove(Integer.parseInt(lifestyle.getProblemId()));
            }
        }


        /**
         * Save Count in Shared Preference
         */
        SharedPreferenceService.storeUserDetails(context, StringConstants.KEY_LIFESTYLE_COUNT,
                String.valueOf(healthbookLifestyleArrayList.size()));


        for (int i = 0; i < initialLifestyleHistoryList.size(); i++) {

            Lifestyle lifestyle = new Lifestyle(
                    SharedPreferenceService.getValue(context, StringConstants.KEY_PATIENT_ID),
                    "1", String.valueOf(initialLifestyleHistoryList.keyAt(i)),
                    initialLifestyleHistoryList.valueAt(i), true, "", "", "", "", false);

            healthbookLifestyleArrayList.add(lifestyle);
        }


        // Set adapter
        lifestyleBindingAdapter =
                new Adapter<>(healthbookLifestyleArrayList, R.layout.card_lifestyle,
                        StaticConstants.ONBOARDING_LIFESTYLE_ADAPTER);

        lifestyleHistoryList.setLayoutManager(new LinearLayoutManager(context));
        lifestyleHistoryList.setAdapter(lifestyleBindingAdapter);


        // Show content
        loader.setVisibility(View.GONE);
        lifestyleHistoryList.setVisibility(View.VISIBLE);
    }


    /**
     * Function to lifestyle problem
     * @param context
     * @param data
     */
    public static void updateLifestyleHistory(final Context context, final HashMap<String, String> data) {

        // Set initial states
        loader.setVisibility(View.VISIBLE);
        lifestyleHistoryList.setVisibility(View.GONE);

        // Url
        String url = ServiceUrls.KEY_HEALTHBOOK_LIFESTYLE
                .concat(StringConstants.KEY_UPDATE)
                .concat(data.get(StringConstants.KEY_P_ID));

        // Set Data
        final HashMap<String, Object> postData = new HashMap<>();
        postData.put(StringConstants.API_KEY_LIFESTYLE_ID, Integer.valueOf(data.get(StringConstants.KEY_PROBLEMS_ID).toString()));
        postData.put(StringConstants.KEY_IS_SUFFERING, (Boolean.valueOf(data.get(StringConstants.KEY_IS_SUFFERING).toString()) ? 1 : 0));
        postData.put(StringConstants.KEY_START_DATE,
                DateTimeUtils.convertTimestampToUTC(Long.valueOf(data.get(StringConstants.KEY_START_DATE).toString())));
        postData.put(StringConstants.KEY_END_DATE,
                DateTimeUtils.convertTimestampToUTC(Long.valueOf(data.get(StringConstants.KEY_END_DATE).toString())));
        postData.put(StringConstants.KEY_COMMENT, data.get(StringConstants.KEY_COMMENT));

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

                        // Fetch object from Database
                        Lifestyle lifestyle =
                                Lifestyle.find(Lifestyle.class, "p_id = ?", data.get(StringConstants.KEY_P_ID)).get(0);

                        /**
                         * Check Start Date and End Date
                         */
                        String startDate = data.get(StringConstants.KEY_START_DATE);
                        if(!startDate.isEmpty()) {
                            startDate = DateTimeUtils.convertTimestampToDate(Long.valueOf(startDate));
                        }

                        String endDate = data.get(StringConstants.KEY_END_DATE);
                        if(!endDate.isEmpty()) {
                            endDate = DateTimeUtils.convertTimestampToDate(Long.valueOf(endDate));
                        }


                        lifestyle.setStillSuffering(Boolean.valueOf(data.get(StringConstants.KEY_IS_SUFFERING)));
                        lifestyle.setStartDate(startDate);
                        lifestyle.setEndDate(endDate);
                        lifestyle.setComments(data.get(StringConstants.KEY_COMMENT));


                        // Update database entry
                        lifestyle.save();

                        // Notify adapter
                        readLifestyleHistoryList(context);

                    } catch (Exception e) {

                        // Show content
                        loader.setVisibility(View.GONE);
                        lifestyleHistoryList.setVisibility(View.VISIBLE);

                        // Log error
                        ErrorHandlers.handleError(activity);
                        Crashlytics.logException(e);
                    }
                }

                @Override
                public void onError(VolleyError error) {

                    // Show content
                    loader.setVisibility(View.GONE);
                    lifestyleHistoryList.setVisibility(View.VISIBLE);
                }
            });

        } else {

            // Show content
            loader.setVisibility(View.GONE);
            lifestyleHistoryList.setVisibility(View.VISIBLE);

            // Internet Not Available
            ErrorHandlers.handleInternetConnectionFailure(activity);
        }
    }


    /**
     * Function to delete a lifestyle problem
     * @param context
     * @param pId
     */
    public static void deleteLifestyleHistory(final Context context, final String pId) {

        // Set initial states
        loader.setVisibility(View.VISIBLE);
        lifestyleHistoryList.setVisibility(View.GONE);

        // Url Creation
        String url = ServiceUrls.KEY_HEALTHBOOK_LIFESTYLE
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
                        Lifestyle lifestyle =
                                Lifestyle.find(Lifestyle.class, "p_id = ?", pId).get(0);

                        lifestyle.delete();

                        // Notify adapter
                        readLifestyleHistoryList(context);

                    } catch (Exception e) {

                        // Show content
                        loader.setVisibility(View.GONE);
                        lifestyleHistoryList.setVisibility(View.VISIBLE);

                        // Log error
                        ErrorHandlers.handleError(activity);
                        Crashlytics.logException(e);
                    }
                }

                @Override
                public void onError(VolleyError error) {

                    // Show content
                    loader.setVisibility(View.GONE);
                    lifestyleHistoryList.setVisibility(View.VISIBLE);
                }
            });

        } else {

            // Show content
            loader.setVisibility(View.GONE);
            lifestyleHistoryList.setVisibility(View.VISIBLE);

            // Internet Not Available
            ErrorHandlers.handleInternetConnectionFailure(activity);
        }
    }






    /**
     * Function to skip Homepage module
     * @param view
     */
    public void gotoHomepage(View view) {

        /**
         * Update Profile Details - Onboarding Complete
         */
        WelcomeActivity.updateOnboardingComplete(this);
    }


    /**
     * Function to skip onboarding module
     * @param view
     */
    public void skipOnboarding(View view) {

        /**
         * Update Profile Details - Onboarding Complete
         */
        WelcomeActivity.updateOnboardingComplete(this);
    }


    /**
     * Function to navigate to Onboarding List (Male) or Gynaecological Section (Female)
     * @param view
     */
    public void nextStep(View view) {

        Intent intent = new Intent(this, OnboardingGynaecologicalIssuesActivity.class);
        startActivity(intent);

        // close this activity
        //finish();
    }

    /**
     * Function to set bottom layout visibility
     * @param visibility
     */
    public static void setBottomLayoutVisibility(boolean visibility){
        cta_layout.setVisibility(visibility == true ? View.VISIBLE : View.GONE);
    }
}
