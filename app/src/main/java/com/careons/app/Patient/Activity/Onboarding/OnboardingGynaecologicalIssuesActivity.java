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
import com.careons.app.Patient.Activity.Main.HealthBook.AddGynaecologicalActivity;
import com.careons.app.Patient.Activity.Signup.WelcomeActivity;
import com.careons.app.Patient.Adapters.Adapter;
import com.careons.app.Patient.Commons.ServiceUrls;
import com.careons.app.Patient.Commons.StaticConstants;
import com.careons.app.Patient.Commons.StringConstants;
import com.careons.app.Patient.Database.Models.Healthbook.Gynaecology;
import com.careons.app.Shared.Interfaces.APIInterface;
import com.careons.app.R;
import com.careons.app.Patient.Services.APICallService;
import com.careons.app.Patient.Services.SharedPreferenceService;
import com.careons.app.Shared.Utils.DateTimeUtils;
import com.careons.app.Shared.Utils.ErrorHandlers;
import com.careons.app.Shared.Utils.Utils;
import com.careons.app.Shared.Utils.Validation;
import com.careons.app.databinding.CardGynaecologicalBinding;
import com.crashlytics.android.Crashlytics;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class OnboardingGynaecologicalIssuesActivity extends AppCompatActivity {

    private static Activity activity;
    private static OnboardingGynaecologicalIssuesActivity onboardingGynaecologicalIssuesActivity;

    private static View root;
    private static RecyclerView gynaecologicalList;
    private static LinearLayout loader, cta_layout;
    private static Adapter<Gynaecology, CardGynaecologicalBinding> gynaecologicalBindingAdapter;
    private static ArrayList<Gynaecology> gynaecologyArrayList = new ArrayList<>();

    // Initial problem List
    private static List<HashMap<String, String>> initialGynaecologicalList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding_gynaecological_issues);

        // Initialize instance
        activity = this;
        onboardingGynaecologicalIssuesActivity = this;

        // Toolbar initialize
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Status Bar Color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.darker_gh));
        }

        // Loader
        loader = (LinearLayout) findViewById(R.id.loader);

        // Bottom Layout
        cta_layout  = (LinearLayout) findViewById(R.id.cta_layout);

        // Initialize Recycler View
        gynaecologicalList = (RecyclerView) findViewById(R.id.gi_recycler_view);
        gynaecologicalList.setHasFixedSize(true);

        /**
         * Populate recycler view
         */
        readGynaecologicalProblemList(getApplicationContext());

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
            Intent mIntent = new Intent(getApplicationContext(), AddGynaecologicalActivity.class);
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
                if(resultCode == Activity.RESULT_OK){
                    readGynaecologicalProblemList(getApplicationContext());
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
    public static OnboardingGynaecologicalIssuesActivity getInstance() {
        return onboardingGynaecologicalIssuesActivity;
    }


    /**
     * Function to initialize Medical Problem List
     */
    public static void fetchInitialProblemList() {

        initialGynaecologicalList.clear();

        HashMap<String, String> initialMedicalProblem1 = new HashMap<>();
        initialMedicalProblem1.put(StringConstants.KEY_ID, "2221");
        initialMedicalProblem1.put(StringConstants.KEY_NAME, getInstance().getString(R.string.gynae_1));
        initialMedicalProblem1.put(StringConstants.KEY_SEARCH_LIST_HYPERLINK, getInstance().getString(R.string.gynae_url_1));
        initialGynaecologicalList.add(initialMedicalProblem1);

        HashMap<String, String> initialMedicalProblem2 = new HashMap<>();
        initialMedicalProblem2.put(StringConstants.KEY_ID, "2222");
        initialMedicalProblem2.put(StringConstants.KEY_NAME, getInstance().getString(R.string.gynae_2));
        initialMedicalProblem2.put(StringConstants.KEY_SEARCH_LIST_HYPERLINK, getInstance().getString(R.string.gynae_url_2));
        initialGynaecologicalList.add(initialMedicalProblem2);

        HashMap<String, String> initialMedicalProblem3 = new HashMap<>();
        initialMedicalProblem3.put(StringConstants.KEY_ID, "2223");
        initialMedicalProblem3.put(StringConstants.KEY_NAME, getInstance().getString(R.string.gynae_3));
        initialMedicalProblem3.put(StringConstants.KEY_SEARCH_LIST_HYPERLINK, getInstance().getString(R.string.gynae_url_3));
        initialGynaecologicalList.add(initialMedicalProblem3);

        HashMap<String, String> initialMedicalProblem4 = new HashMap<>();
        initialMedicalProblem4.put(StringConstants.KEY_ID, "1477");
        initialMedicalProblem4.put(StringConstants.KEY_NAME, getInstance().getString(R.string.gynae_4));
        initialMedicalProblem4.put(StringConstants.KEY_SEARCH_LIST_HYPERLINK, getInstance().getString(R.string.gynae_url_4));
        initialGynaecologicalList.add(initialMedicalProblem4);

        HashMap<String, String> initialMedicalProblem5 = new HashMap<>();
        initialMedicalProblem5.put(StringConstants.KEY_ID, "2224");
        initialMedicalProblem5.put(StringConstants.KEY_NAME, getInstance().getString(R.string.gynae_5));
        initialMedicalProblem5.put(StringConstants.KEY_SEARCH_LIST_HYPERLINK, getInstance().getString(R.string.gynae_url_5));
        initialGynaecologicalList.add(initialMedicalProblem5);

        HashMap<String, String> initialMedicalProblem6 = new HashMap<>();
        initialMedicalProblem6.put(StringConstants.KEY_ID, "1502");
        initialMedicalProblem6.put(StringConstants.KEY_NAME, getInstance().getString(R.string.gynae_6));
        initialMedicalProblem6.put(StringConstants.KEY_SEARCH_LIST_HYPERLINK, getInstance().getString(R.string.gynae_url_6));
        initialGynaecologicalList.add(initialMedicalProblem6);

        HashMap<String, String> initialMedicalProblem7 = new HashMap<>();
        initialMedicalProblem7.put(StringConstants.KEY_ID, "2225");
        initialMedicalProblem7.put(StringConstants.KEY_NAME, getInstance().getString(R.string.gynae_7));
        initialMedicalProblem7.put(StringConstants.KEY_SEARCH_LIST_HYPERLINK, getInstance().getString(R.string.gynae_url_7));
        initialGynaecologicalList.add(initialMedicalProblem7);

        HashMap<String, String> initialMedicalProblem8 = new HashMap<>();
        initialMedicalProblem8.put(StringConstants.KEY_ID, "2226");
        initialMedicalProblem8.put(StringConstants.KEY_NAME, getInstance().getString(R.string.gynae_8));
        initialMedicalProblem8.put(StringConstants.KEY_SEARCH_LIST_HYPERLINK, getInstance().getString(R.string.gynae_url_8));
        initialGynaecologicalList.add(initialMedicalProblem8);

        HashMap<String, String> initialMedicalProblem9 = new HashMap<>();
        initialMedicalProblem9.put(StringConstants.KEY_ID, "1047");
        initialMedicalProblem9.put(StringConstants.KEY_NAME, getInstance().getString(R.string.gynae_9));
        initialMedicalProblem9.put(StringConstants.KEY_SEARCH_LIST_HYPERLINK, getInstance().getString(R.string.gynae_url_9));
        initialGynaecologicalList.add(initialMedicalProblem9);

        HashMap<String, String> initialMedicalProblem10 = new HashMap<>();
        initialMedicalProblem10.put(StringConstants.KEY_ID, "1122");
        initialMedicalProblem10.put(StringConstants.KEY_NAME, getInstance().getString(R.string.gynae_10));
        initialMedicalProblem10.put(StringConstants.KEY_SEARCH_LIST_HYPERLINK, getInstance().getString(R.string.gynae_url_10));
        initialGynaecologicalList.add(initialMedicalProblem10);
    }















    /**
     * Function to create gynaecological problem
     * @param context
     */
    public static void createGynaecologicalProblem(final Context context, final HashMap<String, String> data) {

        // Switch to loader state
        loader.setVisibility(View.VISIBLE);
        gynaecologicalList.setVisibility(View.GONE);

        // Url
        String url = ServiceUrls.KEY_HEALTHBOOK_GYNAECOLOGY.concat(StringConstants.KEY_CREATE);

        // Set data
        final HashMap<String, Object> postData = new HashMap<>();
        postData.put(StringConstants.KEY_PATIENT_ID, SharedPreferenceService.getValue(context, StringConstants.KEY_PATIENT_ID));
        postData.put(StringConstants.KEY_PROBLEMS_ID, data.get(StringConstants.KEY_PROBLEMS_ID));
        postData.put(StringConstants.KEY_PROBLEM_NAME, data.get(StringConstants.KEY_PROBLEM_NAME));
        postData.put(StringConstants.KEY_IS_SUFFERING, Boolean.valueOf(data.get(StringConstants.KEY_IS_SUFFERING)));
        postData.put(StringConstants.KEY_START_DATE,
                DateTimeUtils.convertTimestampToUTC(Long.valueOf(data.get(StringConstants.KEY_START_DATE))));
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
                        // Create in Local Database
                        Gynaecology gynaecology = new Gynaecology(
                                SharedPreferenceService.getValue(context, StringConstants.KEY_PATIENT_ID),
                                response.getString(StringConstants.KEY_P_ID),
                                data.get(StringConstants.KEY_PROBLEMS_ID),
                                data.get(StringConstants.KEY_PROBLEM_NAME),
                                data.get(StringConstants.KEY_SEARCH_LIST_HYPERLINK),
                                DateTimeUtils.convertTimestampToDate(Long.valueOf(data.get(StringConstants.KEY_START_DATE))),
                                Boolean.valueOf(data.get(StringConstants.KEY_IS_SUFFERING)),
                                data.get(StringConstants.KEY_COMMENT),
                                true
                        );


                        // Save to Database Table
                        gynaecology.save();

                        // Read gynaecological problem list
                        readGynaecologicalProblemList(context);

                    } catch (Exception e) {

                        // Show content
                        loader.setVisibility(View.GONE);
                        gynaecologicalList.setVisibility(View.VISIBLE);

                        // Log error
                        ErrorHandlers.handleError(activity);
                        Crashlytics.logException(e);
                    }
                }

                @Override
                public void onError(VolleyError error) {

                    // Show content
                    loader.setVisibility(View.GONE);
                    gynaecologicalList.setVisibility(View.VISIBLE);
                }
            });

        } else {

            // Show content
            loader.setVisibility(View.GONE);
            gynaecologicalList.setVisibility(View.VISIBLE);

            // Internet Not Available
            ErrorHandlers.handleInternetConnectionFailure(activity);
        }
    }


    /**
     * Function to fetch Gynaecological data
     */
    public static void readGynaecologicalProblemList(final Context context) {

        // Initialize list of Ids
        List<String> problems = new ArrayList<>();

        // Fetch initial problem list
        fetchInitialProblemList();

        // Clear Medical Problem List
        gynaecologyArrayList.clear();

        /**
         * Read from database table
         */
        List<Gynaecology> gynaecologies =
                Gynaecology.find(Gynaecology.class,
                        "patient_id = ?", SharedPreferenceService.getValue(context, StringConstants.KEY_PATIENT_ID));


        /**
         * Check if database has entries filed
         */
        if(gynaecologies.size() > 0) {
            for(int i = 0; i < gynaecologies.size(); i++) {

                Gynaecology gynaecology = gynaecologies.get(i);

                // Add to gynaecology problem list
                gynaecologyArrayList.add(gynaecology);
                problems.add(gynaecology.getProblemId());
            }
        }

        /**
         * Save Count in Shared Preference
         */
        SharedPreferenceService.storeUserDetails(context, StringConstants.KEY_GYNAECOLOGY_COUNT,
                String.valueOf(gynaecologyArrayList.size()));


        /**
         * Show initial list of gynaecological problems
         */
        for(int i = 0; i < initialGynaecologicalList.size(); i++) {

            HashMap<String, String> problem = initialGynaecologicalList.get(i);
            if (!(problems.indexOf(problem.get(StringConstants.KEY_ID)) > -1)) {
                Gynaecology gynaecology = new Gynaecology(
                        SharedPreferenceService.getValue(context, StringConstants.KEY_PATIENT_ID),
                        "1",
                        problem.get(StringConstants.KEY_ID),
                        problem.get(StringConstants.KEY_NAME),
                        problem.get(StringConstants.KEY_SEARCH_LIST_HYPERLINK)
                        , "", true, "", false
                );

                gynaecologyArrayList.add(gynaecology);
            }
        }



        // Set adapter
        gynaecologicalBindingAdapter =
                new Adapter<>(gynaecologyArrayList, R.layout.card_gynaecological,
                        StaticConstants.ONBOARDING_GYNAECOLOGICAL_ADAPTER);

        gynaecologicalList.setLayoutManager(new LinearLayoutManager(context));
        gynaecologicalList.setAdapter(gynaecologicalBindingAdapter);


        // Show content
        loader.setVisibility(View.GONE);
        gynaecologicalList.setVisibility(View.VISIBLE);
    }


    /**
     * Function to gynaecological problem
     * @param context
     * @param data
     */
    public static void updateGynaecologicalProblem(final Context context, final HashMap<String, String> data) {

        // Switch to loader state
        loader.setVisibility(View.VISIBLE);
        gynaecologicalList.setVisibility(View.GONE);

        // Url
        String url = ServiceUrls.KEY_HEALTHBOOK_GYNAECOLOGY
                .concat(StringConstants.KEY_UPDATE)
                .concat(data.get(StringConstants.KEY_P_ID));

        // Set Data
        final HashMap<String, Object> postData = new HashMap<>();
        postData.put(StringConstants.KEY_PROBLEMS_ID, Integer.valueOf(data.get(StringConstants.KEY_PROBLEMS_ID)));
        postData.put(StringConstants.KEY_IS_SUFFERING, (Boolean.valueOf(data.get(StringConstants.KEY_IS_SUFFERING)) ? 1 : 0));
        postData.put(StringConstants.KEY_START_DATE,
                DateTimeUtils.convertTimestampToUTC(Long.valueOf(data.get(StringConstants.KEY_START_DATE))));
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
                        Gynaecology gynaecology =
                                Gynaecology.find(Gynaecology.class, "p_id = ?", data.get(StringConstants.KEY_P_ID)).get(0);

                        // Update values
                        gynaecology.setStartDate(
                                DateTimeUtils.convertTimestampToDate(Long.valueOf(data.get(StringConstants.KEY_START_DATE))));
                        gynaecology.setStillSuffering(Boolean.valueOf(data.get(StringConstants.KEY_IS_SUFFERING)));
                        gynaecology.setComments(data.get(StringConstants.KEY_COMMENT));

                        // Update database entry
                        gynaecology.save();

                        // Read gynaecological problem list
                        readGynaecologicalProblemList(context);


                    } catch (Exception e) {

                        // Show content
                        loader.setVisibility(View.GONE);
                        gynaecologicalList.setVisibility(View.VISIBLE);

                        // Log error
                        ErrorHandlers.handleError(activity);
                        Crashlytics.logException(e);
                    }
                }

                @Override
                public void onError(VolleyError error) {

                    // Show content
                    loader.setVisibility(View.GONE);
                    gynaecologicalList.setVisibility(View.VISIBLE);
                }
            });

        } else {

            // Show content
            loader.setVisibility(View.GONE);
            gynaecologicalList.setVisibility(View.VISIBLE);

            // Internet Not Available
            ErrorHandlers.handleInternetConnectionFailure(activity);
        }
    }


    /**
     * Function to delete a gynaecological problem
     * @param context
     * @param pId
     */
    public static void deleteGynaecologicalProblem(final Context context, final String pId) {

        // Switch to loader state
        loader.setVisibility(View.VISIBLE);
        gynaecologicalList.setVisibility(View.GONE);

        // Url Creation
        String url = ServiceUrls.KEY_HEALTHBOOK_GYNAECOLOGY
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
                        Gynaecology gynaecology = Gynaecology.find(Gynaecology.class, "p_id = ?", pId).get(0);

                        gynaecology.delete();

                        // Notify adapter
                        readGynaecologicalProblemList(context);

                    } catch (Exception e) {

                        // Show content
                        loader.setVisibility(View.GONE);
                        gynaecologicalList.setVisibility(View.VISIBLE);

                        // Log error
                        ErrorHandlers.handleError(activity);
                        Crashlytics.logException(e);
                    }
                }

                @Override
                public void onError(VolleyError error) {

                    // Show content
                    loader.setVisibility(View.GONE);
                    gynaecologicalList.setVisibility(View.VISIBLE);
                }
            });

        } else {

            // Show content
            loader.setVisibility(View.GONE);
            gynaecologicalList.setVisibility(View.VISIBLE);

            // Internet Not Available
            ErrorHandlers.handleInternetConnectionFailure(activity);
        }
    }





    /**
     * Function to navigate to onboarding list
     * @param view
     */
    public void gotoOnboardingList(View view) {

        /**
         * Update Profile Details - Onboarding Complete
         */
        WelcomeActivity.updateOnboardingComplete(this);
    }


    /**
     * Function to set bottom layout visibility
     * @param visibility
     */
    public static void setBottomLayoutVisibility(boolean visibility){
        cta_layout.setVisibility(visibility == true ? View.VISIBLE : View.GONE);
    }
}
