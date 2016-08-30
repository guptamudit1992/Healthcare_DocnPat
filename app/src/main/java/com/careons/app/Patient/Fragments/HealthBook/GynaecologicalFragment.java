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
import com.careons.app.Patient.Activity.Main.HealthBook.AddGynaecologicalActivity;
import com.careons.app.Patient.Activity.Main.HealthBook.HealthbookActivity;
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

public class GynaecologicalFragment extends Fragment {

    private static Activity activity;
    private static GynaecologicalFragment gynaecologicalFragment;

    private View rootView;
    private static RecyclerView gynaecologicalList;
    private static LinearLayout loader;
    private static FloatingActionButton fab;

    private static Adapter<Gynaecology, CardGynaecologicalBinding> gynaecologicalBindingAdapter;
    private static ArrayList<Gynaecology> gynaecologyArrayList = new ArrayList<>();

    // Initial problem List
    private static List<HashMap<String, String>> initialGynaecologicalList = new ArrayList<>();


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_health_book_placeholder, container, false);

        // Initialize instance
        activity = getActivity();
        gynaecologicalFragment = this;

        // Loader
        loader = (LinearLayout) rootView.findViewById(R.id.loader);

        // Floating Action Button
        fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent mIntent = new Intent(getActivity(), AddGynaecologicalActivity.class);
                startActivityForResult(mIntent, 1000);
            }
        });

        //Set fab Background Color
        fab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.darker_gh)));

        //Set Toolbar Color
        HealthbookActivity.setToolbarColor();

        // Recycler view
        gynaecologicalList = (RecyclerView) rootView.findViewById(R.id.medical_problem_recycler_view);
        gynaecologicalList.setHasFixedSize(true);

        /**
         * Populate recycler view
         */
        readGynaecologicalProblemList(getContext());


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
        readGynaecologicalProblemList(getActivity());
    }


    /**
     * Function to fetch instance of Gynaecological Problem
     * @return
     */
    public static GynaecologicalFragment getInstance() {
        return gynaecologicalFragment;
    }


    /**
     * Function to fetch instance of Gynaecological Problem
     * @return
     */
    public static GynaecologicalFragment newInstance() {
        gynaecologicalFragment = new GynaecologicalFragment();
        return gynaecologicalFragment;
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
                        StaticConstants.GYNAECOLOGICAL_ADAPTER);

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
}
