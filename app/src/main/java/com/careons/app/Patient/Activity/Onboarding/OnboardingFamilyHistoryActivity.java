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
import com.careons.app.Patient.Activity.Main.HealthBook.AddFamilyActivity;
import com.careons.app.Patient.Activity.Signup.WelcomeActivity;
import com.careons.app.Patient.Adapters.Adapter;
import com.careons.app.Patient.Commons.ServiceUrls;
import com.careons.app.Patient.Commons.StaticConstants;
import com.careons.app.Patient.Commons.StringConstants;
import com.careons.app.Patient.Database.Models.Healthbook.FamilyHistory;
import com.careons.app.Shared.Interfaces.APIInterface;
import com.careons.app.R;
import com.careons.app.Patient.Services.APICallService;
import com.careons.app.Patient.Services.SharedPreferenceService;
import com.careons.app.Shared.Utils.ErrorHandlers;
import com.careons.app.Shared.Utils.Utils;
import com.careons.app.Shared.Utils.Validation;
import com.careons.app.databinding.CardFamilyBinding;
import com.crashlytics.android.Crashlytics;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class OnboardingFamilyHistoryActivity extends AppCompatActivity {

    private static Activity activity;
    private static OnboardingFamilyHistoryActivity onboardingFamilyHistoryActivity;

    private static View root;
    private static RecyclerView familyList;
    private static LinearLayout loader, cta_layout;
    private static Adapter<FamilyHistory, CardFamilyBinding> familyBindingAdapter;
    private static ArrayList<FamilyHistory> healthbookFamilyList = new ArrayList<>();

    // Initial problem List
    private static List<HashMap<String, String>> initialFamilyProblems = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding_family_history);

        // Initialize instance
        activity = this;
        onboardingFamilyHistoryActivity = this;

        // Toolbar initialize
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Status Bar Color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.darker_fh));
        }

        // Loader
        loader = (LinearLayout) findViewById(R.id.loader);

        // Bottom Layout
        cta_layout  = (LinearLayout) findViewById(R.id.cta_layout);

        // Initialize Recycler View
        familyList = (RecyclerView) findViewById(R.id.fh_recycler_view);
        familyList.setHasFixedSize(true);


        /**
         * Populate recycler view
         */
        readFamilyHistoryProblemList(getApplicationContext());


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
            Intent mIntent = new Intent(getApplicationContext(), AddFamilyActivity.class);
            mIntent.putExtra(StringConstants.KEY_INONBOARDING, true);
            startActivityForResult(mIntent, 1000);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case 1000:
                if(resultCode == Activity.RESULT_OK) {
                    readFamilyHistoryProblemList(getApplicationContext());
                }
                break;

            default:
                break;
        }
    }


    @Override
    public void onBackPressed() {

        super.onBackPressed();
    }


    /**
     * Function to fetch instance of the current activity
     * @return
     */
    public static OnboardingFamilyHistoryActivity getInstance() {
        return onboardingFamilyHistoryActivity;
    }


    /**
     * Function to initialize Family History Problem List
     */
    public static void fetchInitialProblemList() {

        initialFamilyProblems.clear();

        HashMap<String, String> initialMedicalProblem1 = new HashMap<>();
        initialMedicalProblem1.put(StringConstants.KEY_ID, "626");
        initialMedicalProblem1.put(StringConstants.KEY_NAME, getInstance().getResources().getString(R.string.medical_problem_1));
        initialMedicalProblem1.put(StringConstants.KEY_SEARCH_LIST_HYPERLINK, getInstance().getResources().getString(R.string.medical_problem_url_1));
        initialFamilyProblems.add(initialMedicalProblem1);

        HashMap<String, String> initialMedicalProblem2 = new HashMap<>();
        initialMedicalProblem2.put(StringConstants.KEY_ID, "2015");
        initialMedicalProblem2.put(StringConstants.KEY_NAME, getInstance().getResources().getString(R.string.medical_problem_2));
        initialMedicalProblem2.put(StringConstants.KEY_SEARCH_LIST_HYPERLINK, getInstance().getResources().getString(R.string.medical_problem_url_2));
        initialFamilyProblems.add(initialMedicalProblem2);

        HashMap<String, String> initialMedicalProblem3 = new HashMap<>();
        initialMedicalProblem3.put(StringConstants.KEY_ID, "386");
        initialMedicalProblem3.put(StringConstants.KEY_NAME, getInstance().getResources().getString(R.string.medical_problem_3));
        initialMedicalProblem3.put(StringConstants.KEY_SEARCH_LIST_HYPERLINK, getInstance().getResources().getString(R.string.medical_problem_url_3));
        initialFamilyProblems.add(initialMedicalProblem3);

        HashMap<String, String> initialMedicalProblem4 = new HashMap<>();
        initialMedicalProblem4.put(StringConstants.KEY_ID, "2074");
        initialMedicalProblem4.put(StringConstants.KEY_NAME, getInstance().getResources().getString(R.string.medical_problem_4));
        initialMedicalProblem4.put(StringConstants.KEY_SEARCH_LIST_HYPERLINK, getInstance().getResources().getString(R.string.medical_problem_url_4));
        initialFamilyProblems.add(initialMedicalProblem4);

        HashMap<String, String> initialMedicalProblem5 = new HashMap<>();
        initialMedicalProblem5.put(StringConstants.KEY_ID, "170");
        initialMedicalProblem5.put(StringConstants.KEY_NAME, getInstance().getResources().getString(R.string.medical_problem_5));
        initialMedicalProblem5.put(StringConstants.KEY_SEARCH_LIST_HYPERLINK, getInstance().getResources().getString(R.string.medical_problem_url_5));
        initialFamilyProblems.add(initialMedicalProblem5);

        HashMap<String, String> initialMedicalProblem6 = new HashMap<>();
        initialMedicalProblem6.put(StringConstants.KEY_ID, "1228");
        initialMedicalProblem6.put(StringConstants.KEY_NAME, getInstance().getResources().getString(R.string.medical_problem_6));
        initialMedicalProblem6.put(StringConstants.KEY_SEARCH_LIST_HYPERLINK, getInstance().getResources().getString(R.string.medical_problem_url_6));
        initialFamilyProblems.add(initialMedicalProblem6);

        HashMap<String, String> initialMedicalProblem7 = new HashMap<>();
        initialMedicalProblem7.put(StringConstants.KEY_ID, "150");
        initialMedicalProblem7.put(StringConstants.KEY_NAME, getInstance().getResources().getString(R.string.medical_problem_7));
        initialMedicalProblem7.put(StringConstants.KEY_SEARCH_LIST_HYPERLINK, getInstance().getResources().getString(R.string.medical_problem_url_7));
        initialFamilyProblems.add(initialMedicalProblem7);

        HashMap<String, String> initialMedicalProblem8 = new HashMap<>();
        initialMedicalProblem8.put(StringConstants.KEY_ID, "1080");
        initialMedicalProblem8.put(StringConstants.KEY_NAME, getInstance().getResources().getString(R.string.medical_problem_8));
        initialMedicalProblem8.put(StringConstants.KEY_SEARCH_LIST_HYPERLINK, getInstance().getResources().getString(R.string.medical_problem_url_8));
        initialFamilyProblems.add(initialMedicalProblem8);

        HashMap<String, String> initialMedicalProblem9 = new HashMap<>();
        initialMedicalProblem9.put(StringConstants.KEY_ID, "973");
        initialMedicalProblem9.put(StringConstants.KEY_NAME, getInstance().getResources().getString(R.string.medical_problem_9));
        initialMedicalProblem9.put(StringConstants.KEY_SEARCH_LIST_HYPERLINK, getInstance().getResources().getString(R.string.medical_problem_url_9));
        initialFamilyProblems.add(initialMedicalProblem9);

        HashMap<String, String> initialMedicalProblem10 = new HashMap<>();
        initialMedicalProblem10.put(StringConstants.KEY_ID, "350");
        initialMedicalProblem10.put(StringConstants.KEY_NAME, getInstance().getResources().getString(R.string.medical_problem_10));
        initialMedicalProblem10.put(StringConstants.KEY_SEARCH_LIST_HYPERLINK, getInstance().getResources().getString(R.string.medical_problem_url_10));
        initialFamilyProblems.add(initialMedicalProblem10);
    }











    /**
     * Function to create family history problem
     *
     * @param context
     */
    public static void createFamilyHistoryProblem(final Context context, final HashMap<String, String> data) {

        // Set initial states
        loader.setVisibility(View.VISIBLE);
        familyList.setVisibility(View.GONE);

        // Url
        String url = ServiceUrls.KEY_HEALTHBOOK_FAMILY_HISTORY.concat(StringConstants.KEY_CREATE);

        // Set data
        final HashMap<String, Object> postData = new HashMap<>();
        postData.put(StringConstants.KEY_PATIENT_ID, SharedPreferenceService.getValue(context, StringConstants.KEY_PATIENT_ID));
        postData.put(StringConstants.KEY_PROBLEMS_ID, data.get(StringConstants.KEY_PROBLEMS_ID));
        postData.put(StringConstants.KEY_PROBLEM_NAME,data.get(StringConstants.KEY_PROBLEM_NAME));
        postData.put(StringConstants.KEY_FAMILY_FATHER_SUFFERING, Boolean.valueOf(data.get(StringConstants.KEY_FAMILY_FATHER_SUFFERING)));
        postData.put(StringConstants.KEY_FAMILY_FATHER_AGE, data.get(StringConstants.KEY_FAMILY_FATHER_AGE));
        postData.put(StringConstants.KEY_FAMILY_MOTHER_SUFFERING, Boolean.valueOf(data.get(StringConstants.KEY_FAMILY_MOTHER_SUFFERING)));
        postData.put(StringConstants.KEY_FAMILY_MOTHER_AGE, data.get(StringConstants.KEY_FAMILY_MOTHER_AGE));
        postData.put(StringConstants.KEY_FAMILY_BROTHER_SUFFERING, Boolean.valueOf(data.get(StringConstants.KEY_FAMILY_BROTHER_SUFFERING)));
        postData.put(StringConstants.KEY_FAMILY_BROTHER_AGE, data.get(StringConstants.KEY_FAMILY_BROTHER_AGE));
        postData.put(StringConstants.KEY_FAMILY_SISTER_SUFFERING, Boolean.valueOf(data.get(StringConstants.KEY_FAMILY_SISTER_SUFFERING)));
        postData.put(StringConstants.KEY_FAMILY_SISTER_AGE, data.get(StringConstants.KEY_FAMILY_SISTER_AGE));
        postData.put(StringConstants.KEY_FAMILY_GF_SUFFERING, Boolean.valueOf(data.get(StringConstants.KEY_FAMILY_GF_SUFFERING)));
        postData.put(StringConstants.KEY_FAMILY_GF_AGE, data.get(StringConstants.KEY_FAMILY_GF_AGE));
        postData.put(StringConstants.KEY_FAMILY_GM_SUFFERING, Boolean.valueOf(data.get(StringConstants.KEY_FAMILY_GM_SUFFERING)));
        postData.put(StringConstants.KEY_FAMILY_GM_AGE, data.get(StringConstants.KEY_FAMILY_GM_AGE));
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

                        FamilyHistory familyHistory = new FamilyHistory(SharedPreferenceService.getValue(context, StringConstants.KEY_PATIENT_ID),
                                response.getString(StringConstants.KEY_P_ID),
                                data.get(StringConstants.KEY_PROBLEMS_ID),
                                data.get(StringConstants.KEY_PROBLEM_NAME),
                                data.get(StringConstants.KEY_SEARCH_LIST_HYPERLINK),
                                Boolean.valueOf(data.get(StringConstants.KEY_FAMILY_FATHER_SUFFERING)),
                                data.get(StringConstants.KEY_FAMILY_FATHER_AGE),
                                Boolean.valueOf(data.get(StringConstants.KEY_FAMILY_MOTHER_SUFFERING)),
                                data.get(StringConstants.KEY_FAMILY_MOTHER_AGE),
                                Boolean.valueOf(data.get(StringConstants.KEY_FAMILY_BROTHER_SUFFERING)),
                                data.get(StringConstants.KEY_FAMILY_BROTHER_AGE),
                                Boolean.valueOf(data.get(StringConstants.KEY_FAMILY_SISTER_SUFFERING)),
                                data.get(StringConstants.KEY_FAMILY_SISTER_AGE),
                                Boolean.valueOf(data.get(StringConstants.KEY_FAMILY_GF_SUFFERING)),
                                data.get(StringConstants.KEY_FAMILY_GF_AGE),
                                Boolean.valueOf(data.get(StringConstants.KEY_FAMILY_GM_SUFFERING)),
                                data.get(StringConstants.KEY_FAMILY_GM_AGE),
                                data.get(StringConstants.KEY_COMMENT),
                                true
                        );

                        // Save to database
                        familyHistory.save();

                        // Read family history problem list
                        readFamilyHistoryProblemList(context);


                    } catch (Exception e) {

                        // Show content
                        loader.setVisibility(View.GONE);
                        familyList.setVisibility(View.VISIBLE);

                        // Log error
                        ErrorHandlers.handleError(activity);
                        Crashlytics.logException(e);
                    }
                }

                @Override
                public void onError(VolleyError error) {

                    // Show content
                    loader.setVisibility(View.GONE);
                    familyList.setVisibility(View.VISIBLE);

                    // Log error
                    ErrorHandlers.handleAPIError(activity);
                    Crashlytics.logException(error);
                }
            });

        } else {

            // Show content
            loader.setVisibility(View.GONE);
            familyList.setVisibility(View.VISIBLE);

            // Internet Not Available
            ErrorHandlers.handleInternetConnectionFailure(activity);
        }
    }


    /**
     * Function to fetch family history problem data
     */
    public static void readFamilyHistoryProblemList(final Context context) {

        // Initialize list of Ids
        List<String> problems = new ArrayList<>();

        // Show Loader, Hide content
        //loader.setVisibility(View.VISIBLE);
        familyList.setVisibility(View.GONE);

        // Fetch initial problem list
        fetchInitialProblemList();

        // Clear Medical Problem List
        healthbookFamilyList.clear();

        /**
         * Read from database table
         */
        List<FamilyHistory> familyHistories =
                FamilyHistory.find(FamilyHistory.class,
                        "patient_id = ?", SharedPreferenceService.getValue(context, StringConstants.KEY_PATIENT_ID));

        // Check if database has entries filed else display initial problem list
        if (familyHistories.size() > 0) {
            for(int i = 0; i < familyHistories.size(); i++) {
                FamilyHistory familyHistory = familyHistories.get(i);

                // Add to medical problem list
                healthbookFamilyList.add(familyHistory);
                problems.add(familyHistory.getProblemId());
            }
        }

        /**
         * Save Count in Shared Preference
         */
        SharedPreferenceService.storeUserDetails(context, StringConstants.KEY_FAMILY_COUNT,
                String.valueOf(healthbookFamilyList.size()));


        /**
         * Show initial list of family history problems
         */
        for (int i = 0; i < initialFamilyProblems.size(); i++) {
            HashMap<String, String> problem = initialFamilyProblems.get(i);
            if (!(problems.indexOf(problem.get(StringConstants.KEY_ID)) > -1)) {

                FamilyHistory familyHistory = new FamilyHistory(
                        SharedPreferenceService.getValue(context, StringConstants.KEY_PATIENT_ID),
                        "1",
                        problem.get(StringConstants.KEY_ID),
                        problem.get(StringConstants.KEY_NAME),
                        problem.get(StringConstants.KEY_SEARCH_LIST_HYPERLINK),
                        false, "", false, "", false, "", false, "", false, "", false, "", "", false);

                healthbookFamilyList.add(familyHistory);
            }
        }


        // Set adapter
        familyBindingAdapter = new Adapter<>(healthbookFamilyList, R.layout.card_family,
                StaticConstants.ONBOARDING_FAMILY_ADAPTER);

        familyList.setLayoutManager(new LinearLayoutManager(context));
        familyList.setAdapter(familyBindingAdapter);

        // Show content
        loader.setVisibility(View.GONE);
        familyList.setVisibility(View.VISIBLE);
    }


    /**
     * Function to update existing family history problem
     *
     * @param context
     * @param data
     */
    public static void updateFamilyHistoryProblem(final Context context, final HashMap<String, String> data) {

        // Set initial states
        loader.setVisibility(View.VISIBLE);
        familyList.setVisibility(View.GONE);

        // Url
        String url = ServiceUrls.KEY_HEALTHBOOK_FAMILY_HISTORY
                .concat(StringConstants.KEY_UPDATE)
                .concat(data.get(StringConstants.KEY_P_ID));

        // Set data
        final HashMap<String, Object> postData = new HashMap<>();
        postData.put(StringConstants.KEY_PATIENT_ID, SharedPreferenceService.getValue(context, StringConstants.KEY_PATIENT_ID));
        postData.put(StringConstants.KEY_PROBLEMS_ID, data.get(StringConstants.KEY_PROBLEMS_ID));
        postData.put(StringConstants.KEY_FAMILY_FATHER_SUFFERING, Boolean.valueOf(data.get(StringConstants.KEY_FAMILY_FATHER_SUFFERING)));
        postData.put(StringConstants.KEY_FAMILY_FATHER_AGE, data.get(StringConstants.KEY_FAMILY_FATHER_AGE));
        postData.put(StringConstants.KEY_FAMILY_MOTHER_SUFFERING, Boolean.valueOf(data.get(StringConstants.KEY_FAMILY_MOTHER_SUFFERING)));
        postData.put(StringConstants.KEY_FAMILY_MOTHER_AGE, data.get(StringConstants.KEY_FAMILY_MOTHER_AGE));
        postData.put(StringConstants.KEY_FAMILY_BROTHER_SUFFERING, Boolean.valueOf(data.get(StringConstants.KEY_FAMILY_BROTHER_SUFFERING)));
        postData.put(StringConstants.KEY_FAMILY_BROTHER_AGE, data.get(StringConstants.KEY_FAMILY_BROTHER_AGE));
        postData.put(StringConstants.KEY_FAMILY_SISTER_SUFFERING, Boolean.valueOf(data.get(StringConstants.KEY_FAMILY_SISTER_SUFFERING)));
        postData.put(StringConstants.KEY_FAMILY_SISTER_AGE, data.get(StringConstants.KEY_FAMILY_SISTER_AGE));
        postData.put(StringConstants.KEY_FAMILY_GF_SUFFERING, Boolean.valueOf(data.get(StringConstants.KEY_FAMILY_GF_SUFFERING)));
        postData.put(StringConstants.KEY_FAMILY_GF_AGE, data.get(StringConstants.KEY_FAMILY_GF_AGE));
        postData.put(StringConstants.KEY_FAMILY_GM_SUFFERING, Boolean.valueOf(data.get(StringConstants.KEY_FAMILY_GM_SUFFERING)));
        postData.put(StringConstants.KEY_FAMILY_GM_AGE, data.get(StringConstants.KEY_FAMILY_GM_AGE));
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
                        FamilyHistory familyHistory =
                                FamilyHistory.find(FamilyHistory.class, "p_id = ?", data.get(StringConstants.KEY_P_ID)).get(0);

                        // Update the object values
                        familyHistory.setIsFatherStillSuffering(Boolean.valueOf(data.get(StringConstants.KEY_FAMILY_FATHER_SUFFERING)));
                        familyHistory.setFatherAge(data.get(StringConstants.KEY_FAMILY_FATHER_AGE));
                        familyHistory.setIsMotherStillSuffering(Boolean.valueOf(data.get(StringConstants.KEY_FAMILY_MOTHER_SUFFERING)));
                        familyHistory.setMotherAge(data.get(StringConstants.KEY_FAMILY_MOTHER_AGE));
                        familyHistory.setIsBrotherStillSuffering(Boolean.valueOf(data.get(StringConstants.KEY_FAMILY_BROTHER_SUFFERING)));
                        familyHistory.setBrotherAge(data.get(StringConstants.KEY_FAMILY_BROTHER_AGE));
                        familyHistory.setIsSisterStillSuffering(Boolean.valueOf(data.get(StringConstants.KEY_FAMILY_SISTER_SUFFERING)));
                        familyHistory.setSisterAge(data.get(StringConstants.KEY_FAMILY_SISTER_AGE));
                        familyHistory.setIsGrandFatherStillSuffering(Boolean.valueOf(data.get(StringConstants.KEY_FAMILY_GF_SUFFERING)));
                        familyHistory.setGrandFatherAge(data.get(StringConstants.KEY_FAMILY_GF_AGE));
                        familyHistory.setIsGrandMotherStillSuffering(Boolean.valueOf(data.get(StringConstants.KEY_FAMILY_GM_SUFFERING)));
                        familyHistory.setGrandMotherAge(data.get(StringConstants.KEY_FAMILY_GM_AGE));
                        familyHistory.setComments(data.get(StringConstants.KEY_COMMENT));


                        // Update database entry
                        familyHistory.save();

                        // Read medical problem list
                        readFamilyHistoryProblemList(context);

                    } catch (Exception e) {

                        // Show content
                        loader.setVisibility(View.GONE);
                        familyList.setVisibility(View.VISIBLE);

                        // Log error
                        ErrorHandlers.handleError(activity);
                        Crashlytics.logException(e);
                    }
                }

                @Override
                public void onError(VolleyError error) {

                    // Show content
                    loader.setVisibility(View.GONE);
                    familyList.setVisibility(View.VISIBLE);
                }
            });

        } else {

            // Show content
            loader.setVisibility(View.GONE);
            familyList.setVisibility(View.VISIBLE);

            // Internet Not Available
            ErrorHandlers.handleInternetConnectionFailure(activity);
        }
    }


    /**
     * Function to delete a family history problem
     *
     * @param context
     * @param pId
     */
    public static void deleteFamilyHistoryProblem(final Context context, final String pId) {

        // Set initial states
        loader.setVisibility(View.VISIBLE);
        familyList.setVisibility(View.GONE);

        // Url Creation
        String url = ServiceUrls.KEY_HEALTHBOOK_FAMILY_HISTORY
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
                        FamilyHistory healthbookFamilyModel =
                                FamilyHistory.find(FamilyHistory.class, "p_id = ?", pId).get(0);

                        healthbookFamilyModel.delete();

                        // Notify adapter
                        readFamilyHistoryProblemList(context);


                    } catch (Exception e) {

                        // Show content
                        loader.setVisibility(View.GONE);
                        familyList.setVisibility(View.VISIBLE);

                        // Log error
                        ErrorHandlers.handleAPIError(activity);
                        Crashlytics.logException(e);
                    }
                }

                @Override
                public void onError(VolleyError error) {

                    // Show content
                    loader.setVisibility(View.GONE);
                    familyList.setVisibility(View.VISIBLE);
                }
            });

        } else {

            // Show content
            loader.setVisibility(View.GONE);
            familyList.setVisibility(View.VISIBLE);

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
     * Function to navigate to Lifestyle onboarding
     * @param view
     */
    public void gotoLifestyle(View view) {

        Intent intent = new Intent(this, OnboardingLifestyleActivity.class);
        startActivity(intent);

        // close this activity
        //finish();
    }
}
