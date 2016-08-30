package com.careons.app.Patient.Fragments.HealthBook;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;

import com.android.volley.VolleyError;
import com.careons.app.Patient.Activity.Main.HealthBook.AddSurgicalActivity;
import com.careons.app.Patient.Activity.Main.HealthBook.HealthbookActivity;
import com.careons.app.Patient.Adapters.Adapter;
import com.careons.app.Patient.Commons.ServiceUrls;
import com.careons.app.Patient.Commons.StaticConstants;
import com.careons.app.Patient.Commons.StringConstants;
import com.careons.app.Patient.Database.Models.Healthbook.SurgicalHistory;
import com.careons.app.Shared.Interfaces.APIInterface;
import com.careons.app.R;
import com.careons.app.Patient.Services.APICallService;
import com.careons.app.Patient.Services.SharedPreferenceService;
import com.careons.app.Shared.Utils.DateTimeUtils;
import com.careons.app.Shared.Utils.ErrorHandlers;
import com.careons.app.Shared.Utils.Utils;
import com.careons.app.Shared.Utils.Validation;
import com.careons.app.databinding.CardSurgicalHistoryBinding;
import com.crashlytics.android.Crashlytics;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SurgicalFragment extends Fragment {

    private static Activity activity;
    private static SurgicalFragment surgicalFragment;

    private View rootView;
    private static RecyclerView surgicalHistoryList;
    private static LinearLayout loader;
    private static FloatingActionButton fab;

    private static Adapter<SurgicalHistory, CardSurgicalHistoryBinding> surgicalBindingAdapter;
    private static ArrayList<SurgicalHistory> healthbookSurgical = new ArrayList<>();

    // Initial surgical history List
    private static List<HashMap<String, String>> initialSurgicalHistoryList = new ArrayList<>();


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_health_book_placeholder, container, false);

        // Initialize instance
        activity = getActivity();
        surgicalFragment = this;

        // Loader
        loader = (LinearLayout) rootView.findViewById(R.id.loader);

        // Floating Action Button
        fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent mIntent = new Intent(getActivity(), AddSurgicalActivity.class);
                startActivityForResult(mIntent, 1000);
            }
        });

        //Set fab Background Color
        fab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.darker_sh)));
        //Set Toolbar Color
        HealthbookActivity.setToolbarColor();

        // Recycler view
        surgicalHistoryList = (RecyclerView) rootView.findViewById(R.id.medical_problem_recycler_view);
        surgicalHistoryList.setHasFixedSize(true);

        /**
         * Populate recycler view
         */
        readSurgicalHistoryList(getContext());


        /**
         * Function to toggle CTA with keyboard
         */
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {

                Utils.toggleFABKeyboard(rootView, fab);
            }
        });

        return rootView;
    }














    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        readSurgicalHistoryList(getActivity());
    }


    /**
     * Function to fetch instance of Surgical History
     *
     * @return
     */
    public static SurgicalFragment getInstance() {
        return surgicalFragment;
    }


    /**
     * Function to fetch instance of Surgical History
     *
     * @return
     */
    public static SurgicalFragment newInstance() {
        surgicalFragment = new SurgicalFragment();
        return surgicalFragment;
    }


    /**
     * Function to initialize Surgical History List
     */
    public static void fetchInitialProblemList() {

        initialSurgicalHistoryList.clear();

        HashMap<String, String> initialMedicalProblem1 = new HashMap<>();
        initialMedicalProblem1.put(StringConstants.KEY_ID, "1680");
        initialMedicalProblem1.put(StringConstants.KEY_NAME, getInstance().getString(R.string.surgical_1));
        initialMedicalProblem1.put(StringConstants.KEY_SEARCH_LIST_HYPERLINK, "");
        initialSurgicalHistoryList.add(initialMedicalProblem1);

        HashMap<String, String> initialMedicalProblem2 = new HashMap<>();
        initialMedicalProblem2.put(StringConstants.KEY_ID, "1681");
        initialMedicalProblem2.put(StringConstants.KEY_NAME, getInstance().getString(R.string.surgical_2));
        initialMedicalProblem2.put(StringConstants.KEY_SEARCH_LIST_HYPERLINK, "");
        initialSurgicalHistoryList.add(initialMedicalProblem2);

        HashMap<String, String> initialMedicalProblem3 = new HashMap<>();
        initialMedicalProblem3.put(StringConstants.KEY_ID, "111");
        initialMedicalProblem3.put(StringConstants.KEY_NAME, getInstance().getString(R.string.surgical_3));
        initialMedicalProblem3.put(StringConstants.KEY_SEARCH_LIST_HYPERLINK, getInstance().getString(R.string.surgical_url_3));
        initialSurgicalHistoryList.add(initialMedicalProblem3);

        HashMap<String, String> initialMedicalProblem4 = new HashMap<>();
        initialMedicalProblem4.put(StringConstants.KEY_ID, "1683");
        initialMedicalProblem4.put(StringConstants.KEY_NAME, getInstance().getString(R.string.surgical_4));
        initialMedicalProblem4.put(StringConstants.KEY_SEARCH_LIST_HYPERLINK, "");
        initialSurgicalHistoryList.add(initialMedicalProblem4);

        HashMap<String, String> initialMedicalProblem5 = new HashMap<>();
        initialMedicalProblem5.put(StringConstants.KEY_ID, "773");
        initialMedicalProblem5.put(StringConstants.KEY_NAME, getInstance().getString(R.string.surgical_5));
        initialMedicalProblem5.put(StringConstants.KEY_SEARCH_LIST_HYPERLINK, "");
        initialSurgicalHistoryList.add(initialMedicalProblem5);

        HashMap<String, String> initialMedicalProblem6 = new HashMap<>();
        initialMedicalProblem6.put(StringConstants.KEY_ID, "1684");
        initialMedicalProblem6.put(StringConstants.KEY_NAME, getInstance().getString(R.string.surgical_6));
        initialMedicalProblem6.put(StringConstants.KEY_SEARCH_LIST_HYPERLINK, "");
        initialSurgicalHistoryList.add(initialMedicalProblem6);

        HashMap<String, String> initialMedicalProblem7 = new HashMap<>();
        initialMedicalProblem7.put(StringConstants.KEY_ID, "1686");
        initialMedicalProblem7.put(StringConstants.KEY_NAME, getInstance().getString(R.string.surgical_7));
        initialMedicalProblem7.put(StringConstants.KEY_SEARCH_LIST_HYPERLINK, "");
        initialSurgicalHistoryList.add(initialMedicalProblem7);

        HashMap<String, String> initialMedicalProblem8 = new HashMap<>();
        initialMedicalProblem8.put(StringConstants.KEY_ID, "802");
        initialMedicalProblem8.put(StringConstants.KEY_NAME, getInstance().getString(R.string.surgical_8));
        initialMedicalProblem8.put(StringConstants.KEY_SEARCH_LIST_HYPERLINK, "");
        initialSurgicalHistoryList.add(initialMedicalProblem8);

        HashMap<String, String> initialMedicalProblem9 = new HashMap<>();
        initialMedicalProblem9.put(StringConstants.KEY_ID, "1685");
        initialMedicalProblem9.put(StringConstants.KEY_NAME, getInstance().getString(R.string.surgical_9));
        initialMedicalProblem9.put(StringConstants.KEY_SEARCH_LIST_HYPERLINK, "");
        initialSurgicalHistoryList.add(initialMedicalProblem9);

        HashMap<String, String> initialMedicalProblem10 = new HashMap<>();
        initialMedicalProblem10.put(StringConstants.KEY_ID, "1477");
        initialMedicalProblem10.put(StringConstants.KEY_NAME, getInstance().getString(R.string.surgical_10));
        initialMedicalProblem10.put(StringConstants.KEY_SEARCH_LIST_HYPERLINK, "");
        initialSurgicalHistoryList.add(initialMedicalProblem10);

        HashMap<String, String> initialMedicalProblem11 = new HashMap<>();
        initialMedicalProblem11.put(StringConstants.KEY_ID, "255");
        initialMedicalProblem11.put(StringConstants.KEY_NAME, getInstance().getString(R.string.surgical_11));
        initialMedicalProblem11.put(StringConstants.KEY_SEARCH_LIST_HYPERLINK, "");
        initialSurgicalHistoryList.add(initialMedicalProblem11);

        HashMap<String, String> initialMedicalProblem12 = new HashMap<>();
        initialMedicalProblem12.put(StringConstants.KEY_ID, "1279");
        initialMedicalProblem12.put(StringConstants.KEY_NAME, getInstance().getString(R.string.surgical_12));
        initialMedicalProblem12.put(StringConstants.KEY_SEARCH_LIST_HYPERLINK, "");
        initialSurgicalHistoryList.add(initialMedicalProblem12);
    }










    /**
     * Function to create surgical history
     *
     * @param context
     */
    public static void createSurgicalHistory(final Context context, final HashMap<String, String> data) {

        // Switch to loader state
        loader.setVisibility(View.VISIBLE);
        surgicalHistoryList.setVisibility(View.GONE);

        // Set data
        final HashMap<String, Object> postData = new HashMap<>();

        // Url
        String url;
        if(data.get(StringConstants.KEY_PROBLEM_CATEGORY)
                .equalsIgnoreCase(StringConstants.KEY_INVESTIGATE_CATEGORY)) {

            url = ServiceUrls.KEY_HEALTHBOOK_INVESTIGATIVE_HISTORY
                    .concat(StringConstants.KEY_CREATE);

            postData.put(StringConstants.KEY_INVESTIGATIVE_ID, data.get(StringConstants.KEY_PROBLEMS_ID));
            postData.put(StringConstants.KEY_INVESTIGATE_TEST_DATE,
                    DateTimeUtils.convertTimestampToUTC(Long.valueOf(data.get(StringConstants.KEY_START_DATE))));

        } else {

            url = ServiceUrls.KEY_HEALTHBOOK_SURGICAL_HISTORY
                    .concat(StringConstants.KEY_CREATE);

            postData.put(StringConstants.KEY_SURGICAL_ID, data.get(StringConstants.KEY_PROBLEMS_ID));
            postData.put(StringConstants.API_KEY_SURGERY_DATE,
                    DateTimeUtils.convertTimestampToUTC(Long.valueOf(data.get(StringConstants.KEY_START_DATE))));
        }



        postData.put(StringConstants.KEY_PATIENT_ID, SharedPreferenceService.getValue(context, StringConstants.KEY_PATIENT_ID));
        postData.put(StringConstants.KEY_SURGICAL_CATEGORY, data.get(StringConstants.KEY_SURGICAL_CATEGORY));
        postData.put(StringConstants.KEY_SURGICAL_IS_SOLVED, Boolean.valueOf(data.get(StringConstants.KEY_IS_SUFFERING)) ? 1 : 0);
        postData.put(StringConstants.API_KEY_SURGERY_DATE,
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
                        SurgicalHistory surgicalHistory = new SurgicalHistory(
                                SharedPreferenceService.getValue(context, StringConstants.KEY_PATIENT_ID),
                                response.getString(StringConstants.KEY_P_ID),
                                data.get(StringConstants.KEY_PROBLEMS_ID),
                                data.get(StringConstants.KEY_PROBLEM_NAME),
                                data.get(StringConstants.KEY_SEARCH_LIST_HYPERLINK),
                                data.get(StringConstants.KEY_PROBLEM_CATEGORY),
                                DateTimeUtils.convertTimestampToDate(Long.valueOf(data.get(StringConstants.KEY_START_DATE))),
                                Boolean.valueOf(data.get(StringConstants.KEY_IS_SUFFERING)),
                                data.get(StringConstants.KEY_COMMENT),
                                true
                        );

                        // Save to Database Table
                        surgicalHistory.save();

                        // Read surgical history list
                        readSurgicalHistoryList(context);

                    } catch (Exception e) {

                        // Show content
                        loader.setVisibility(View.GONE);
                        surgicalHistoryList.setVisibility(View.VISIBLE);

                        // Log error
                        ErrorHandlers.handleAPIError(activity);
                        Crashlytics.logException(e);
                    }
                }

                @Override
                public void onError(VolleyError error) {

                    // Show content
                    loader.setVisibility(View.GONE);
                    surgicalHistoryList.setVisibility(View.VISIBLE);
                }
            });

        } else {

             // Show content
            loader.setVisibility(View.GONE);
            surgicalHistoryList.setVisibility(View.VISIBLE);

            // Internet Not Available
            ErrorHandlers.handleInternetConnectionFailure(activity);
        }
    }


    /**
     * Function to fetch surgical history data
     */
    public static void readSurgicalHistoryList(final Context context) {

        // Initialize list of Ids
        List<String> problems = new ArrayList<>();

        // Fetch initial problem list
        fetchInitialProblemList();

        // Empty object holder
        healthbookSurgical.clear();

        // Read from database table
        List<SurgicalHistory> surgicalHistories =
                SurgicalHistory.find(SurgicalHistory.class,
                        "patient_id = ?", SharedPreferenceService.getValue(context, StringConstants.KEY_PATIENT_ID));

        /**
         * Check if database has entries filed
         */
        if (surgicalHistories.size() > 0) {

            for (int i = 0; i < surgicalHistories.size(); i++) {

                healthbookSurgical.add(surgicalHistories.get(i));
                problems.add(surgicalHistories.get(i).getProblemId());
            }
        }


        /**
         * Save Count in Shared Preference
         */
        SharedPreferenceService.storeUserDetails(context, StringConstants.KEY_SURGICAL_COUNT,
                String.valueOf(healthbookSurgical.size()));


        /**
         * Read initial problem list
         */
        for (int i = 0; i < initialSurgicalHistoryList.size(); i++) {

            HashMap<String, String> problem = initialSurgicalHistoryList.get(i);
            if (!(problems.indexOf(problem.get(StringConstants.KEY_ID)) > -1)) {
                SurgicalHistory surgicalHistory = new SurgicalHistory(
                        SharedPreferenceService.getValue(context, StringConstants.KEY_PATIENT_ID),
                        "1",
                        problem.get(StringConstants.KEY_ID),
                        problem.get(StringConstants.KEY_NAME),
                        problem.get(StringConstants.KEY_SEARCH_LIST_HYPERLINK),
                        StringConstants.KEY_SURGICAL_CATEGORY, "", true, "", false
                );

                healthbookSurgical.add(surgicalHistory);
            }
        }


        // Set adapter
        surgicalBindingAdapter =
                new Adapter<>(healthbookSurgical, R.layout.card_surgical_history, StaticConstants.SURGICAL_ADAPTER);

        surgicalHistoryList.setLayoutManager(new LinearLayoutManager(context));
        surgicalHistoryList.setAdapter(surgicalBindingAdapter);


        // Show content
        loader.setVisibility(View.GONE);
        surgicalHistoryList.setVisibility(View.VISIBLE);
    }


    /**
     * Function to update surgical history
     *
     * @param context
     * @param data
     */
    public static void updateSurgicalHistory(final Context context, final HashMap<String, String> data) {

        // Switch to loader state
        loader.setVisibility(View.VISIBLE);
        surgicalHistoryList.setVisibility(View.GONE);

        // Set Data
        final HashMap<String, Object> postData = new HashMap<>();

        // Url
        String url;
        if(data.get(StringConstants.KEY_PROBLEM_CATEGORY)
                .equalsIgnoreCase(StringConstants.KEY_INVESTIGATE_CATEGORY)) {

            url = ServiceUrls.KEY_HEALTHBOOK_INVESTIGATIVE_HISTORY
                    .concat(StringConstants.KEY_UPDATE)
                    .concat(data.get(StringConstants.KEY_P_ID));

            postData.put(StringConstants.KEY_INVESTIGATIVE_ID, Integer.valueOf(data.get(StringConstants.KEY_PROBLEMS_ID)));
            postData.put(StringConstants.KEY_INVESTIGATE_TEST_DATE,
                    DateTimeUtils.convertTimestampToUTC(Long.valueOf(data.get(StringConstants.KEY_START_DATE))));

        } else {

            url = ServiceUrls.KEY_HEALTHBOOK_SURGICAL_HISTORY
                    .concat(StringConstants.KEY_UPDATE)
                    .concat(data.get(StringConstants.KEY_P_ID));

            postData.put(StringConstants.KEY_SURGICAL_ID, Integer.valueOf(data.get(StringConstants.KEY_PROBLEMS_ID)));
            postData.put(StringConstants.API_KEY_SURGERY_DATE,
                    DateTimeUtils.convertTimestampToUTC(Long.valueOf(data.get(StringConstants.KEY_START_DATE))));
        }



        postData.put(StringConstants.KEY_SURGICAL_IS_SOLVED, (Boolean.valueOf(data.get(StringConstants.KEY_IS_SUFFERING)) ? 1 : 0));
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

                        // Update in Local Database
                        SurgicalHistory surgicalHistory =
                                SurgicalHistory.find(SurgicalHistory.class, "p_id = ?", data.get(StringConstants.KEY_P_ID)).get(0);

                        surgicalHistory.setStartDate(
                                DateTimeUtils.convertTimestampToDate(Long.valueOf(data.get(StringConstants.KEY_START_DATE))));
                        surgicalHistory.setStillSuffering(Boolean.valueOf(data.get(StringConstants.KEY_IS_SUFFERING)));
                        surgicalHistory.setComments(data.get(StringConstants.KEY_COMMENT));
                        surgicalHistory.save();

                        // Notify adapter
                        readSurgicalHistoryList(context);

                    } catch (Exception e) {

                        // Show content
                        loader.setVisibility(View.GONE);
                        surgicalHistoryList.setVisibility(View.VISIBLE);

                        // Log error
                        ErrorHandlers.handleError(activity);
                        Crashlytics.logException(e);
                    }
                }

                @Override
                public void onError(VolleyError error) {

                    // Show content
                    loader.setVisibility(View.GONE);
                    surgicalHistoryList.setVisibility(View.VISIBLE);
                }
            });

        } else {

            // Show content
            loader.setVisibility(View.GONE);
            surgicalHistoryList.setVisibility(View.VISIBLE);

            // Internet Not Available
            ErrorHandlers.handleInternetConnectionFailure(activity);
        }
    }


    /**
     * Function to delete a surgical history
     *
     * @param context
     * @param pId
     */
    public static void deleteSurgicalHistory(final Context context, final String pId, final String category) {

        // Switch to loader state
        loader.setVisibility(View.VISIBLE);
        surgicalHistoryList.setVisibility(View.GONE);

        // Url
        String url;
        if(category.equalsIgnoreCase(StringConstants.KEY_INVESTIGATE_CATEGORY)) {

            url = ServiceUrls.KEY_HEALTHBOOK_INVESTIGATIVE_HISTORY
                    .concat(StringConstants.KEY_DELETE)
                    .concat(StringConstants.KEY_SEPARATOR)
                    .concat(pId);
        } else {
            url = ServiceUrls.KEY_HEALTHBOOK_SURGICAL_HISTORY
                    .concat(StringConstants.KEY_DELETE)
                    .concat(StringConstants.KEY_SEPARATOR)
                    .concat(pId);
        }


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
                        SurgicalHistory surgicalHistory =
                                SurgicalHistory.find(SurgicalHistory.class, "p_id = ?", pId).get(0);

                        surgicalHistory.delete();

                        // Notify adapter
                        readSurgicalHistoryList(context);

                    } catch (Exception e) {

                        // Show content
                        loader.setVisibility(View.GONE);
                        surgicalHistoryList.setVisibility(View.VISIBLE);

                        // Log error
                        ErrorHandlers.handleError(activity);
                        Crashlytics.logException(e);
                    }
                }

                @Override
                public void onError(VolleyError error) {

                    // Show content
                    loader.setVisibility(View.GONE);
                    surgicalHistoryList.setVisibility(View.VISIBLE);
                }
            });

        } else {

            // Show content
            loader.setVisibility(View.GONE);
            surgicalHistoryList.setVisibility(View.VISIBLE);

            // Internet Not Available
            ErrorHandlers.handleInternetConnectionFailure(activity);
        }
    }
}
