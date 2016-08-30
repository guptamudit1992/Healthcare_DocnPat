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
import com.careons.app.Patient.Activity.Main.HealthBook.AddChildhoodActivity;
import com.careons.app.Patient.Activity.Main.HealthBook.HealthbookActivity;
import com.careons.app.Patient.Adapters.Adapter;
import com.careons.app.Patient.Commons.ServiceUrls;
import com.careons.app.Patient.Commons.StaticConstants;
import com.careons.app.Patient.Commons.StringConstants;
import com.careons.app.Patient.Database.Models.Healthbook.ChildhoodHistory;
import com.careons.app.Shared.Interfaces.APIInterface;
import com.careons.app.R;
import com.careons.app.Patient.Services.APICallService;
import com.careons.app.Patient.Services.SharedPreferenceService;
import com.careons.app.Shared.Utils.DateTimeUtils;
import com.careons.app.Shared.Utils.ErrorHandlers;
import com.careons.app.Shared.Utils.Utils;
import com.careons.app.Shared.Utils.Validation;
import com.careons.app.databinding.CardChildhoodHistoryBinding;
import com.crashlytics.android.Crashlytics;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ChildhoodFragment extends Fragment {

    private static Activity activity;
    private static ChildhoodFragment childhoodFragment;

    private View rootView;
    private static RecyclerView childhoodHistoryList;
    private static LinearLayout loader;
    private static FloatingActionButton fab;

    private static Adapter<ChildhoodHistory, CardChildhoodHistoryBinding> childhoodHistoryBindingAdapter;
    private static ArrayList<ChildhoodHistory> healthbookChildhoodHistory = new ArrayList<>();
    private static final int mRequestCode = 1000;

    // Initial problem List
    private static List<HashMap<String, String>> initialChildhoodHistoryList = new ArrayList<>();


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_health_book_placeholder, container, false);

        // Initialize instance
        activity = getActivity();
        childhoodFragment = this;

        // Loader
        loader = (LinearLayout) rootView.findViewById(R.id.loader);

        // Floating Action Button
        fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent mIntent = new Intent(getActivity(), AddChildhoodActivity.class);
                startActivityForResult(mIntent, mRequestCode);
            }
        });

        //Set fab Background Color
        fab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.darker_ch)));

        //Set Toolbar Color
        HealthbookActivity.setToolbarColor();

        // Recycler view
        childhoodHistoryList = (RecyclerView) rootView.findViewById(R.id.medical_problem_recycler_view);
        childhoodHistoryList.setHasFixedSize(true);

        /**
         * Populate recycler view
         */
        readChildhoodHistoryList(getContext());


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

        readChildhoodHistoryList(getActivity());
    }


    /**
     * Function to fetch instance of Childhood history
     * @return
     */
    public static ChildhoodFragment getInstance() {
        return childhoodFragment;
    }


    /**
     * Function to fetch instance of Childhood History
     * @return
     */
    public static ChildhoodFragment newInstance() {
        childhoodFragment = new ChildhoodFragment();
        return childhoodFragment;
    }


    /**
     * Function to initialize Childhood History List
     */
    public static void fetchInitialProblemList() {

        initialChildhoodHistoryList.clear();

        HashMap<String, String> initialMedicalProblem1 = new HashMap<>();
        initialMedicalProblem1.put(StringConstants.KEY_ID, "168");
        initialMedicalProblem1.put(StringConstants.KEY_NAME, getInstance().getString(R.string.childhood_1));
        initialMedicalProblem1.put(StringConstants.KEY_SEARCH_LIST_HYPERLINK, getInstance().getString(R.string.childhood_url_1));
        initialChildhoodHistoryList.add(initialMedicalProblem1);

        HashMap<String, String> initialMedicalProblem2 = new HashMap<>();
        initialMedicalProblem2.put(StringConstants.KEY_ID, "625");
        initialMedicalProblem2.put(StringConstants.KEY_NAME, getInstance().getString(R.string.childhood_2));
        initialMedicalProblem2.put(StringConstants.KEY_SEARCH_LIST_HYPERLINK, getInstance().getString(R.string.childhood_url_2));
        initialChildhoodHistoryList.add(initialMedicalProblem2);

        HashMap<String, String> initialMedicalProblem3 = new HashMap<>();
        initialMedicalProblem3.put(StringConstants.KEY_ID, "241");
        initialMedicalProblem3.put(StringConstants.KEY_NAME, getInstance().getString(R.string.childhood_3));
        initialMedicalProblem3.put(StringConstants.KEY_SEARCH_LIST_HYPERLINK, getInstance().getString(R.string.childhood_url_3));
        initialChildhoodHistoryList.add(initialMedicalProblem3);

        HashMap<String, String> initialMedicalProblem4 = new HashMap<>();
        initialMedicalProblem4.put(StringConstants.KEY_ID, "240");
        initialMedicalProblem4.put(StringConstants.KEY_NAME, getInstance().getString(R.string.childhood_4));
        initialMedicalProblem4.put(StringConstants.KEY_SEARCH_LIST_HYPERLINK, getInstance().getString(R.string.childhood_url_4));
        initialChildhoodHistoryList.add(initialMedicalProblem4);

        HashMap<String, String> initialMedicalProblem5 = new HashMap<>();
        initialMedicalProblem5.put(StringConstants.KEY_ID, "2220");
        initialMedicalProblem5.put(StringConstants.KEY_NAME, getInstance().getString(R.string.childhood_5));
        initialMedicalProblem5.put(StringConstants.KEY_SEARCH_LIST_HYPERLINK, getInstance().getString(R.string.childhood_url_5));
        initialChildhoodHistoryList.add(initialMedicalProblem5);

        HashMap<String, String> initialMedicalProblem6 = new HashMap<>();
        initialMedicalProblem6.put(StringConstants.KEY_ID, "421");
        initialMedicalProblem6.put(StringConstants.KEY_NAME, getInstance().getString(R.string.childhood_6));
        initialMedicalProblem6.put(StringConstants.KEY_SEARCH_LIST_HYPERLINK, getInstance().getString(R.string.childhood_url_6));
        initialChildhoodHistoryList.add(initialMedicalProblem6);

        HashMap<String, String> initialMedicalProblem7 = new HashMap<>();
        initialMedicalProblem7.put(StringConstants.KEY_ID, "1285");
        initialMedicalProblem7.put(StringConstants.KEY_NAME, getInstance().getString(R.string.childhood_7));
        initialMedicalProblem7.put(StringConstants.KEY_SEARCH_LIST_HYPERLINK, getInstance().getString(R.string.childhood_url_7));
        initialChildhoodHistoryList.add(initialMedicalProblem7);

        HashMap<String, String> initialMedicalProblem8 = new HashMap<>();
        initialMedicalProblem8.put(StringConstants.KEY_ID, "1366");
        initialMedicalProblem8.put(StringConstants.KEY_NAME, getInstance().getString(R.string.childhood_8));
        initialMedicalProblem8.put(StringConstants.KEY_SEARCH_LIST_HYPERLINK, getInstance().getString(R.string.childhood_url_8));
        initialChildhoodHistoryList.add(initialMedicalProblem8);

        HashMap<String, String> initialMedicalProblem9 = new HashMap<>();
        initialMedicalProblem9.put(StringConstants.KEY_ID, "164");
        initialMedicalProblem9.put(StringConstants.KEY_NAME, getInstance().getString(R.string.childhood_9));
        initialMedicalProblem9.put(StringConstants.KEY_SEARCH_LIST_HYPERLINK, getInstance().getString(R.string.childhood_url_9));
        initialChildhoodHistoryList.add(initialMedicalProblem9);
    }







    /**
     * Function to create childhood problem history
     * @param context
     */
    public static void createChildhoodHistory(final Context context, final HashMap<String, String> data) {

        // Switch to loader state
        loader.setVisibility(View.VISIBLE);
        childhoodHistoryList.setVisibility(View.GONE);

        // Url
        String url = ServiceUrls.KEY_HEALTHBOOK_CHILDHOOD_HISTORY
                .concat(StringConstants.KEY_CREATE);

        // Set data
        final HashMap<String, Object> postData = new HashMap<>();
        postData.put(StringConstants.KEY_PATIENT_ID, SharedPreferenceService.getValue(context, StringConstants.KEY_PATIENT_ID));
        postData.put(StringConstants.KEY_PROBLEMS_ID, data.get(StringConstants.KEY_PROBLEMS_ID));
        postData.put(StringConstants.KEY_IS_SUFFERING,
                Boolean.valueOf(data.get(StringConstants.KEY_IS_SUFFERING)) ? 1 : 0);
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
                        ChildhoodHistory childhoodHistory = new ChildhoodHistory(
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
                        childhoodHistory.save();

                        // Read childhood problem history list
                        readChildhoodHistoryList(context);


                    } catch (Exception e) {

                        // Show content
                        loader.setVisibility(View.GONE);
                        childhoodHistoryList.setVisibility(View.VISIBLE);

                        // Log error
                        ErrorHandlers.handleError(activity);
                        Crashlytics.logException(e);
                    }
                }

                @Override
                public void onError(VolleyError error) {

                    // Show content
                    loader.setVisibility(View.GONE);
                    childhoodHistoryList.setVisibility(View.VISIBLE);
                }
            });

        } else {

            // Show content
            loader.setVisibility(View.GONE);
            childhoodHistoryList.setVisibility(View.VISIBLE);

            // Internet Not Available
            ErrorHandlers.handleInternetConnectionFailure(activity);
        }
    }


    /**
     * Function to fetch childhood history data
     */
    public static void readChildhoodHistoryList(final Context context) {

        // Initialize list of Ids
        List<String> problems = new ArrayList<>();

        // Fetch initial problem list
        fetchInitialProblemList();

        // Empty object holder
        healthbookChildhoodHistory.clear();

        // Read from database table
        List<ChildhoodHistory> childhoodHistories =
                ChildhoodHistory.find(ChildhoodHistory.class,
                        "patient_id = ?", SharedPreferenceService.getValue(context, StringConstants.KEY_PATIENT_ID));

        /**
         * Check if database has entries filed
         */
        if(childhoodHistories.size() > 0) {

            for(int i = 0; i < childhoodHistories.size(); i++) {

                ChildhoodHistory childhoodHistory = childhoodHistories.get(i);
                healthbookChildhoodHistory.add(childhoodHistory);
                problems.add(childhoodHistory.getProblemId());
            }
        }

        /**
         * Save Count in Shared Preference
         */
        SharedPreferenceService.storeUserDetails(context, StringConstants.KEY_PROBLEM_COUNT,
                String.valueOf(healthbookChildhoodHistory.size()));



        /**
         * Read initial problem list
         */
        for (int i = 0; i < initialChildhoodHistoryList.size(); i++) {
            HashMap<String, String> problem = initialChildhoodHistoryList.get(i);
            if (!(problems.indexOf(problem.get(StringConstants.KEY_ID)) > -1)) {

                ChildhoodHistory childhoodHistory = new ChildhoodHistory(
                        SharedPreferenceService.getValue(context, StringConstants.KEY_PATIENT_ID),
                        "1",
                        problem.get(StringConstants.KEY_ID),
                        problem.get(StringConstants.KEY_NAME),
                        problem.get(StringConstants.KEY_SEARCH_LIST_HYPERLINK), "", true, "", false);

                healthbookChildhoodHistory.add(childhoodHistory);
            }
        }

        // Set adapter
        childhoodHistoryBindingAdapter =
                new Adapter<>(healthbookChildhoodHistory, R.layout.card_childhood_history,
                        StaticConstants.CHILDHOOD_ADAPTER);

        childhoodHistoryList.setLayoutManager(new LinearLayoutManager(context));
        childhoodHistoryList.setAdapter(childhoodHistoryBindingAdapter);


        // Show content
        loader.setVisibility(View.GONE);
        childhoodHistoryList.setVisibility(View.VISIBLE);
    }


    /**
     * Function to childhood problem history
     * @param context
     * @param data
     */
    public static void updateChildhoodHistory(final Context context, final HashMap<String, String> data) {

        // Switch to loader state
        loader.setVisibility(View.VISIBLE);
        childhoodHistoryList.setVisibility(View.GONE);

        // Url
        String url = ServiceUrls.KEY_HEALTHBOOK_CHILDHOOD_HISTORY
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

                        // Update in Local Database
                        ChildhoodHistory childhoodHistory =
                                ChildhoodHistory.find(ChildhoodHistory.class,
                                        "p_id = ?", data.get(StringConstants.KEY_P_ID)).get(0);

                        childhoodHistory.setStartDate(
                                DateTimeUtils.convertTimestampToDate(Long.valueOf(data.get(StringConstants.KEY_START_DATE))));
                        childhoodHistory.setStillSuffering(Boolean.valueOf(data.get(StringConstants.KEY_IS_SUFFERING)));
                        childhoodHistory.setComments(data.get(StringConstants.KEY_COMMENT));
                        childhoodHistory.save();

                        // Notify adapter
                        readChildhoodHistoryList(context);

                    } catch (Exception e) {

                        // Show content
                        loader.setVisibility(View.GONE);
                        childhoodHistoryList.setVisibility(View.VISIBLE);

                        // Log error
                        ErrorHandlers.handleError(activity);
                        Crashlytics.logException(e);
                    }
                }

                @Override
                public void onError(VolleyError error) {

                    // Show content
                    loader.setVisibility(View.GONE);
                    childhoodHistoryList.setVisibility(View.VISIBLE);
                }
            });

        } else {

            // Show content
            loader.setVisibility(View.GONE);
            childhoodHistoryList.setVisibility(View.VISIBLE);

            // Internet Not Available
            ErrorHandlers.handleInternetConnectionFailure(activity);
        }
    }


    /**
     * Function to delete a childhood problem
     * @param context
     * @param pId
     */
    public static void deleteChildhoodHistory(final Context context, final String pId) {

        // Switch to loader state
        loader.setVisibility(View.VISIBLE);
        childhoodHistoryList.setVisibility(View.GONE);

        // Url Creation
        String url = ServiceUrls.KEY_HEALTHBOOK_CHILDHOOD_HISTORY
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
                        ChildhoodHistory childhoodHistory =
                                ChildhoodHistory.find(ChildhoodHistory.class, "p_id = ?", pId).get(0);

                        childhoodHistory.delete();

                        // Notify adapter
                        readChildhoodHistoryList(context);

                    } catch (Exception e) {

                        // Show content
                        loader.setVisibility(View.GONE);
                        childhoodHistoryList.setVisibility(View.VISIBLE);

                        // Log error
                        ErrorHandlers.handleError(activity);
                        Crashlytics.logException(e);
                    }
                }

                @Override
                public void onError(VolleyError error) {

                    // Show content
                    loader.setVisibility(View.GONE);
                    childhoodHistoryList.setVisibility(View.VISIBLE);
                }
            });

        } else {

            // Show content
            loader.setVisibility(View.GONE);
            childhoodHistoryList.setVisibility(View.VISIBLE);

            // Internet Not Available
            ErrorHandlers.handleInternetConnectionFailure(activity);
        }
    }
}
