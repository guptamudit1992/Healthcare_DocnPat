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
import com.careons.app.Patient.Activity.Main.HealthBook.AddMedicalProblemActivity;
import com.careons.app.Patient.Activity.Main.HealthBook.HealthbookActivity;
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

public class MedicalProblemFragment extends Fragment {

    private static Activity activity;
    private static MedicalProblemFragment medicalProblemFragment;

    private View rootView;
    private static RecyclerView medicalProblemList;
    private static LinearLayout loader;
    private static FloatingActionButton fab;

    private static Adapter<MedicalProblem, CardMedicalProblemBinding> medicalProblemBindingAdapter;
    private static ArrayList<MedicalProblem> healthbookMedicalProblems = new ArrayList<>();

    // Initial problem List
    private static List<HashMap<String, String>> initialMedicalProblems = new ArrayList<>();


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_health_book_placeholder, container, false);

        // Initialize instance
        activity = getActivity();
        medicalProblemFragment = this;

        // Loader
        loader = (LinearLayout) rootView.findViewById(R.id.loader);

        // Floating Action Button
        fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent mIntent = new Intent(getActivity(), AddMedicalProblemActivity.class);
                startActivityForResult(mIntent, 1000);
            }
        });

        //Set fab Background Color
        fab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.darker_mp)));

        //Set Toolbar Color
        HealthbookActivity.setToolbarColor();

        // Recycler view
        medicalProblemList = (RecyclerView) rootView.findViewById(R.id.medical_problem_recycler_view);
        medicalProblemList.setHasFixedSize(true);

        /**
         * Populate recycler view
         */
        readMedicalProblemList(getContext());


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

        readMedicalProblemList(getActivity());
    }


    /**
     * Function to fetch instance of Medical Problem
     * @return
     */
    public static MedicalProblemFragment getInstance() {
        return medicalProblemFragment;
    }


    /**
     * Function to fetch instance of Medical Problem
     * @return
     */
    public static MedicalProblemFragment newInstance() {
        medicalProblemFragment = new MedicalProblemFragment();
        return medicalProblemFragment;
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

                        // Show content
                        loader.setVisibility(View.GONE);
                        medicalProblemList.setVisibility(View.VISIBLE);


                        // Log error
                        ErrorHandlers.handleError(activity);
                        Crashlytics.logException(e);
                    }
                }

                @Override
                public void onError(VolleyError error) {

                    // Show content
                    loader.setVisibility(View.GONE);
                    medicalProblemList.setVisibility(View.VISIBLE);
                }
            });

        } else {

            // Show content
            loader.setVisibility(View.GONE);
            medicalProblemList.setVisibility(View.VISIBLE);

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
        loader.setVisibility(View.VISIBLE);
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
                        StaticConstants.MEDICAL_PROBLEM_ADAPTER);

        medicalProblemList.setLayoutManager(new LinearLayoutManager(context));
        medicalProblemList.setAdapter(medicalProblemBindingAdapter);


        // Show content
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



                        // Fetch object from Database
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

                        // Show content
                        loader.setVisibility(View.GONE);
                        medicalProblemList.setVisibility(View.VISIBLE);

                        // Log error
                        ErrorHandlers.handleError(activity);
                        Crashlytics.logException(e);
                    }
                }

                @Override
                public void onError(VolleyError error) {

                    // Show content
                    loader.setVisibility(View.GONE);
                    medicalProblemList.setVisibility(View.VISIBLE);
                }
            });

        } else {

            // Show content
            loader.setVisibility(View.GONE);
            medicalProblemList.setVisibility(View.VISIBLE);

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

                        // Show content
                        loader.setVisibility(View.GONE);
                        medicalProblemList.setVisibility(View.VISIBLE);

                        // Log error
                        ErrorHandlers.handleError(activity);
                        Crashlytics.logException(e);
                    }
                }

                @Override
                public void onError(VolleyError error) {

                    // Show content
                    loader.setVisibility(View.GONE);
                    medicalProblemList.setVisibility(View.VISIBLE);
                }
            });

        } else {

            // Show content
            loader.setVisibility(View.GONE);
            medicalProblemList.setVisibility(View.VISIBLE);

            ErrorHandlers.handleInternetConnectionFailure(activity);
        }
    }
}
