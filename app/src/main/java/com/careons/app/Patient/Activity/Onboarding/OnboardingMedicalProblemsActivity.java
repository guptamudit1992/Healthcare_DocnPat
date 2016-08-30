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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.android.volley.VolleyError;
import com.careons.app.Patient.Activity.Main.HealthBook.AddMedicalProblemActivity;
import com.careons.app.Patient.Activity.Signup.WelcomeActivity;
import com.careons.app.Patient.Adapters.Adapter;
import com.careons.app.Patient.Commons.ServiceUrls;
import com.careons.app.Patient.Commons.StaticConstants;
import com.careons.app.Patient.Commons.StringConstants;
import com.careons.app.Patient.Database.Models.Healthbook.MedicalProblem;
import com.careons.app.Shared.Interfaces.APIInterface;
import com.careons.app.R;
import com.careons.app.Patient.Services.APICallService;
import com.careons.app.Patient.Services.SharedPreferenceService;
import com.careons.app.Shared.Utils.DateTimeUtils;
import com.careons.app.Shared.Utils.ErrorHandlers;
import com.careons.app.Shared.Utils.Utils;
import com.careons.app.Shared.Utils.Validation;
import com.careons.app.databinding.CardMedicalProblemBinding;
import com.crashlytics.android.Crashlytics;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class OnboardingMedicalProblemsActivity extends AppCompatActivity {

    private static Activity activity;
    private static OnboardingMedicalProblemsActivity onboardingMedicalProblemsActivity;

    private static View root;
    private static RecyclerView medicalProblemList;
    private static LinearLayout loader, cta_layout;


    private static Adapter<MedicalProblem, CardMedicalProblemBinding> medicalProblemBindingAdapter;
    private static ArrayList<MedicalProblem> healthbookMedicalProblems = new ArrayList<>();

    // Initial problem List
    private static List<HashMap<String, String>> initialMedicalProblems = new ArrayList<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding_medical_problems);


        // Initialize instance
        activity = this;
        onboardingMedicalProblemsActivity = this;

        // Toolbar initialize
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Status Bar Color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.darker_mp));
        }

        // Loader
        loader = (LinearLayout) findViewById(R.id.loader);

        // Bottom Layout
        cta_layout  = (LinearLayout) findViewById(R.id.cta_layout);

        // Initialize Recycler View
        medicalProblemList = (RecyclerView) findViewById(R.id.mp_recycler_view);
        medicalProblemList.setHasFixedSize(true);

        /**xt
         * Populate recycler view
         */
        readMedicalProblemList(getApplicationContext());


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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_onboarding, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_add) {

            // Search and Add Problem
            Intent mIntent = new Intent(getApplicationContext(), AddMedicalProblemActivity.class);
            mIntent.putExtra(StringConstants.KEY_INONBOARDING, true);
            startActivityForResult(mIntent, 1000);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

        super.onBackPressed();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case 1000:
                if(resultCode == Activity.RESULT_OK) {
                    readMedicalProblemList(getApplicationContext());
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

    /**
     * Function to fetch instance of the current activity
     * @return
     */
    public static OnboardingMedicalProblemsActivity getInstance() {
        return onboardingMedicalProblemsActivity;
    }

    /**
     * Function to initialize Medical Problem List
     */
    public static void fetchInitialProblemList() {

        initialMedicalProblems.clear();

        HashMap<String, String> initialMedicalProblem1 = new HashMap<>();
        initialMedicalProblem1.put(StringConstants.KEY_ID, "626");
        initialMedicalProblem1.put(StringConstants.KEY_NAME, getInstance().getResources().getString(R.string.medical_problem_1));
        initialMedicalProblem1.put(StringConstants.KEY_SEARCH_LIST_HYPERLINK, getInstance().getResources().getString(R.string.medical_problem_url_1));
        initialMedicalProblems.add(initialMedicalProblem1);

        HashMap<String, String> initialMedicalProblem2 = new HashMap<>();
        initialMedicalProblem2.put(StringConstants.KEY_ID, "2015");
        initialMedicalProblem2.put(StringConstants.KEY_NAME, getInstance().getResources().getString(R.string.medical_problem_2));
        initialMedicalProblem2.put(StringConstants.KEY_SEARCH_LIST_HYPERLINK, getInstance().getResources().getString(R.string.medical_problem_url_2));
        initialMedicalProblems.add(initialMedicalProblem2);

        HashMap<String, String> initialMedicalProblem3 = new HashMap<>();
        initialMedicalProblem3.put(StringConstants.KEY_ID, "386");
        initialMedicalProblem3.put(StringConstants.KEY_NAME, getInstance().getResources().getString(R.string.medical_problem_3));
        initialMedicalProblem3.put(StringConstants.KEY_SEARCH_LIST_HYPERLINK, getInstance().getResources().getString(R.string.medical_problem_url_3));
        initialMedicalProblems.add(initialMedicalProblem3);

        HashMap<String, String> initialMedicalProblem4 = new HashMap<>();
        initialMedicalProblem4.put(StringConstants.KEY_ID, "2074");
        initialMedicalProblem4.put(StringConstants.KEY_NAME, getInstance().getResources().getString(R.string.medical_problem_4));
        initialMedicalProblem4.put(StringConstants.KEY_SEARCH_LIST_HYPERLINK, getInstance().getResources().getString(R.string.medical_problem_url_4));
        initialMedicalProblems.add(initialMedicalProblem4);

        HashMap<String, String> initialMedicalProblem5 = new HashMap<>();
        initialMedicalProblem5.put(StringConstants.KEY_ID, "170");
        initialMedicalProblem5.put(StringConstants.KEY_NAME, getInstance().getResources().getString(R.string.medical_problem_5));
        initialMedicalProblem5.put(StringConstants.KEY_SEARCH_LIST_HYPERLINK, getInstance().getResources().getString(R.string.medical_problem_url_5));
        initialMedicalProblems.add(initialMedicalProblem5);

        HashMap<String, String> initialMedicalProblem6 = new HashMap<>();
        initialMedicalProblem6.put(StringConstants.KEY_ID, "1228");
        initialMedicalProblem6.put(StringConstants.KEY_NAME, getInstance().getResources().getString(R.string.medical_problem_6));
        initialMedicalProblem6.put(StringConstants.KEY_SEARCH_LIST_HYPERLINK, getInstance().getResources().getString(R.string.medical_problem_url_6));
        initialMedicalProblems.add(initialMedicalProblem6);

        HashMap<String, String> initialMedicalProblem7 = new HashMap<>();
        initialMedicalProblem7.put(StringConstants.KEY_ID, "150");
        initialMedicalProblem7.put(StringConstants.KEY_NAME, getInstance().getResources().getString(R.string.medical_problem_7));
        initialMedicalProblem7.put(StringConstants.KEY_SEARCH_LIST_HYPERLINK, getInstance().getResources().getString(R.string.medical_problem_url_7));
        initialMedicalProblems.add(initialMedicalProblem7);

        HashMap<String, String> initialMedicalProblem8 = new HashMap<>();
        initialMedicalProblem8.put(StringConstants.KEY_ID, "1080");
        initialMedicalProblem8.put(StringConstants.KEY_NAME, getInstance().getResources().getString(R.string.medical_problem_8));
        initialMedicalProblem8.put(StringConstants.KEY_SEARCH_LIST_HYPERLINK, getInstance().getResources().getString(R.string.medical_problem_url_8));
        initialMedicalProblems.add(initialMedicalProblem8);

        HashMap<String, String> initialMedicalProblem9 = new HashMap<>();
        initialMedicalProblem9.put(StringConstants.KEY_ID, "973");
        initialMedicalProblem9.put(StringConstants.KEY_NAME, getInstance().getResources().getString(R.string.medical_problem_9));
        initialMedicalProblem9.put(StringConstants.KEY_SEARCH_LIST_HYPERLINK, getInstance().getResources().getString(R.string.medical_problem_url_9));
        initialMedicalProblems.add(initialMedicalProblem9);

        HashMap<String, String> initialMedicalProblem10 = new HashMap<>();
        initialMedicalProblem10.put(StringConstants.KEY_ID, "350");
        initialMedicalProblem10.put(StringConstants.KEY_NAME, getInstance().getResources().getString(R.string.medical_problem_10));
        initialMedicalProblem10.put(StringConstants.KEY_SEARCH_LIST_HYPERLINK, getInstance().getResources().getString(R.string.medical_problem_url_10));
        initialMedicalProblems.add(initialMedicalProblem10);
    }









    /**
     * Function to create medical problem
     * @param context
     */
    public static void createMedicalProblem(final Context context, final HashMap<String, String> data) {

        // Set initial states
        loader.setVisibility(View.VISIBLE);
        medicalProblemList.setVisibility(View.GONE);


        // Url
        String url = ServiceUrls.KEY_HEALTHBOOK_MEDICAL_PROBLEMS.concat(StringConstants.KEY_CREATE);

        // Set data
        final HashMap<String, Object> postData = new HashMap<>();
        postData.put(StringConstants.KEY_PATIENT_ID, SharedPreferenceService.getValue(context, StringConstants.KEY_PATIENT_ID));
        postData.put(StringConstants.KEY_PROBLEMS_ID, data.get(StringConstants.KEY_PROBLEMS_ID));
        postData.put(StringConstants.KEY_IS_SUFFERING, Boolean.valueOf(data.get(StringConstants.KEY_IS_SUFFERING)) ? 1 : 0);
        postData.put(StringConstants.KEY_START_DATE,
                DateTimeUtils.convertTimestampToUTC(Long.valueOf(data.get(StringConstants.KEY_START_DATE))));
        postData.put(StringConstants.KEY_END_DATE,
                DateTimeUtils.convertTimestampToUTC(Long.valueOf(data.get(StringConstants.KEY_END_DATE))));
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
                        MedicalProblem medicalProblem = new MedicalProblem(
                                SharedPreferenceService.getValue(context, StringConstants.KEY_PATIENT_ID),
                                response.getString(StringConstants.KEY_P_ID),
                                data.get(StringConstants.KEY_PROBLEMS_ID),
                                data.get(StringConstants.KEY_PROBLEM_NAME),
                                data.get(StringConstants.KEY_SEARCH_LIST_HYPERLINK),
                                Boolean.valueOf(data.get(StringConstants.KEY_IS_SUFFERING)),
                                "",
                                startDate,
                                endDate,
                                data.get(StringConstants.KEY_COMMENT),
                                true
                        );

                        // Save to Database Table
                        medicalProblem.save();

                        // Read medical problem list
                        readMedicalProblemList(context);

                    } catch (Exception e) {

                        // Show Content
                        loader.setVisibility(View.GONE);
                        medicalProblemList.setVisibility(View.VISIBLE);

                        // Log error
                        ErrorHandlers.handleError(activity);
                        Crashlytics.logException(e);
                    }
                }

                @Override
                public void onError(VolleyError error) {

                    // Show Content
                    loader.setVisibility(View.GONE);
                    medicalProblemList.setVisibility(View.VISIBLE);
                }
            });

        } else {

            // Show Content
            loader.setVisibility(View.GONE);
            medicalProblemList.setVisibility(View.VISIBLE);

            // Internet Not Available
            ErrorHandlers.handleInternetConnectionFailure(activity);
        }
    }


    /**
     * Function to fetch medical problem data
     */
    public static void readMedicalProblemList(final Context context) {

        // Initialize list of Ids
        List<String> problems = new ArrayList<>();

        // Switch to loader state
        //loader.setVisibility(View.VISIBLE);
        medicalProblemList.setVisibility(View.GONE);


        // Fetch initial problems list
        fetchInitialProblemList();

        // Empty object holder
        healthbookMedicalProblems.clear();

        // Read from database table
        List<MedicalProblem> healthbookMedicalProblemList =
                MedicalProblem.find(MedicalProblem.class,
                        "patient_id = ?", SharedPreferenceService.getValue(context, StringConstants.KEY_PATIENT_ID));

        /**
         * Check if database has entries filed
         */
        if(healthbookMedicalProblemList.size() > 0) {

            for(int i = 0; i < healthbookMedicalProblemList.size(); i++) {

                MedicalProblem medicalProblem = healthbookMedicalProblemList.get(i);

                /**
                 * Check if Suffering -> Calculate Duration and Set End Date to null
                 */
                if(medicalProblem.isStillSuffering()) {

                    String duration = DateTimeUtils.calculateDurationFromStartDate(
                            DateTimeUtils.convertDateSimpleToTimestamp(medicalProblem.getStartDate()));

                    medicalProblem.setDuration(duration);
                    medicalProblem.setEndDate("");
                }

                // Add to medical problem list
                healthbookMedicalProblems.add(medicalProblem);
                problems.add(medicalProblem.getProblemId());
            }
        }

        /**
         * Save Count in Shared Preference
         */
        SharedPreferenceService.storeUserDetails(context, StringConstants.KEY_PROBLEM_COUNT,
                String.valueOf(healthbookMedicalProblems.size()));



        /**
         * Read initial problem list
         */
        for (int i = 0; i < initialMedicalProblems.size(); i++) {
            HashMap<String, String> problem = initialMedicalProblems.get(i);
            if (!(problems.indexOf(problem.get(StringConstants.KEY_ID)) > -1)) {
                MedicalProblem medicalProblem = new MedicalProblem(
                        SharedPreferenceService.getValue(context, StringConstants.KEY_PATIENT_ID),
                        "1",
                        problem.get(StringConstants.KEY_ID),
                        problem.get(StringConstants.KEY_NAME),
                        problem.get(StringConstants.KEY_SEARCH_LIST_HYPERLINK),
                        true, "",
                        "", "", "", false
                );

                healthbookMedicalProblems.add(medicalProblem);
            }
        }

        // Set adapter
        medicalProblemBindingAdapter =
                new Adapter<>(healthbookMedicalProblems, R.layout.card_medical_problem,
                        StaticConstants.ONBOARDING_MEDICAL_PROBLEM_ADAPTER);

        medicalProblemList.setLayoutManager(new LinearLayoutManager(context));
        medicalProblemList.setAdapter(medicalProblemBindingAdapter);


        // Show Content
        loader.setVisibility(View.GONE);
        medicalProblemList.setVisibility(View.VISIBLE);
    }


    /**
     * Function to updateFamilyHistoryProblem medical problem
     * @param context
     * @param data
     */
    public static void updateMedicalProblem(final Context context, final HashMap<String, String> data) {

        // Set initial states
        loader.setVisibility(View.VISIBLE);
        medicalProblemList.setVisibility(View.GONE);

        // Url
        String url = ServiceUrls.KEY_HEALTHBOOK_MEDICAL_PROBLEMS
                .concat(StringConstants.KEY_UPDATE)
                .concat(data.get(StringConstants.KEY_P_ID).toString());

        // Set Data
        final HashMap<String, Object> postData = new HashMap<>();
        postData.put(StringConstants.KEY_PROBLEMS_ID, Integer.valueOf(data.get(StringConstants.KEY_PROBLEMS_ID).toString()));
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


                        /**
                         * Update values in local database
                         */
                        MedicalProblem medicalProblem =
                                MedicalProblem.find(MedicalProblem.class, "p_id = ?", data.get(StringConstants.KEY_P_ID).toString()).get(0);

                        medicalProblem.setStartDate(startDate);
                        medicalProblem.setEndDate(endDate);
                        medicalProblem.setIsStillSuffering(Boolean.valueOf(data.get(StringConstants.KEY_IS_SUFFERING).toString()));
                        medicalProblem.setComments(data.get(StringConstants.KEY_COMMENT).toString());

                        // Update database entry
                        medicalProblem.save();

                        // Read medical problem list
                        readMedicalProblemList(context);


                    } catch (Exception e) {

                        // Show Content
                        loader.setVisibility(View.GONE);
                        medicalProblemList.setVisibility(View.VISIBLE);

                        // Log error
                        ErrorHandlers.handleError(activity);
                        Crashlytics.logException(e);
                    }
                }

                @Override
                public void onError(VolleyError error) {

                    // Show Content
                    loader.setVisibility(View.GONE);
                    medicalProblemList.setVisibility(View.VISIBLE);
                }
            });

        } else {

            // Show Content
            loader.setVisibility(View.GONE);
            medicalProblemList.setVisibility(View.VISIBLE);

            // Internet Not Available
            ErrorHandlers.handleInternetConnectionFailure(activity);
        }
    }


    /**
     * Function to delete a medical problem
     * @param context
     * @param pId
     */
    public static void deleteMedicalProblem(final Context context, final String pId) {

        // Set initial states
        loader.setVisibility(View.VISIBLE);
        medicalProblemList.setVisibility(View.GONE);

        // Url Creation
        String url = ServiceUrls.KEY_HEALTHBOOK_MEDICAL_PROBLEMS
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
                        MedicalProblem healthbookMedicalProblem =
                                MedicalProblem.find(MedicalProblem.class, "p_id = ?", pId).get(0);

                        healthbookMedicalProblem.delete();

                        // Notify adapter
                        readMedicalProblemList(context);


                    } catch (Exception e) {

                        // Show Content
                        loader.setVisibility(View.GONE);
                        medicalProblemList.setVisibility(View.VISIBLE);

                        // Log error
                        ErrorHandlers.handleError(activity);
                        Crashlytics.logException(e);
                    }
                }

                @Override
                public void onError(VolleyError error) {

                    // Show Content
                    loader.setVisibility(View.GONE);
                    medicalProblemList.setVisibility(View.VISIBLE);
                }
            });

        } else {

            // Show Content
            loader.setVisibility(View.GONE);
            medicalProblemList.setVisibility(View.VISIBLE);

            // Internet Not Available
            ErrorHandlers.handleInternetConnectionFailure(activity);
        }
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
     * Function to proceed to next step
     * @param view
     */
    public void gotoFamilyHistory(View view) {

        Intent intent = new Intent(this, OnboardingFamilyHistoryActivity.class);
        startActivity(intent);

        // close this activity
        //finish();
    }
}
