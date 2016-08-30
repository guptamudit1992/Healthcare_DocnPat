package com.careons.app.Patient.Services;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;

import com.android.volley.VolleyError;
import com.careons.app.Patient.Activity.Signup.LoginActivity;
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
import com.careons.app.Patient.Database.Models.Healthbook.Pregnancy;
import com.careons.app.Patient.Database.Models.Healthbook.SurgicalHistory;
import com.careons.app.Patient.Database.Models.Upload.Album;
import com.careons.app.Patient.Database.Models.Upload.UploadImage;
import com.careons.app.Patient.Database.Models.Vitals.BMI;
import com.careons.app.Patient.Database.Models.Vitals.BloodGlucose;
import com.careons.app.Patient.Database.Models.Vitals.BloodPressure;
import com.careons.app.Patient.Enums.AlbumTag;
import com.careons.app.Shared.Interfaces.APIInterface;
import com.careons.app.R;
import com.careons.app.Shared.Utils.DateTimeUtils;
import com.careons.app.Shared.Utils.ErrorHandlers;
import com.careons.app.Shared.Utils.Validation;
import com.crashlytics.android.Crashlytics;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

public class SyncDatabaseService {

    // Progress Dialog Popup
    private static ProgressDialog popup;


    /**
     * Function to read Healthbook - Medical Problems
     *
     * @param activity
     * @param context
     */
    public static void readMedicalProblemData(final Activity activity, final Context context) {

        // Progress Dialog Popup
        popup = new ProgressDialog(activity);
        popup.setMessage(context.getString(R.string.syncing_message));
        popup.show();

        if (Validation.isConnected(context)) {

            String url = ServiceUrls.KEY_HEALTHBOOK_MEDICAL_PROBLEMS
                    .concat(StringConstants.KEY_READ_SELECTIVE_PROBLEMS);

            final HashMap<String, String> headers = new HashMap<>();
            headers.put(StaticConstants.KEY_AUTHORIZATION,
                    StaticConstants.KEY_BEARER
                            .concat(" ")
                            .concat(SharedPreferenceService.getValue(context, StringConstants.KEY_TOKEN)));

            // API Call
            APICallService.GetAPICall(activity, context, url, null, headers, new APIInterface() {
                @Override
                public void onSuccess(JSONObject response) {

                    try {

                        if (response.has(StringConstants.KEY_DATA)) {

                            JSONArray jsonArray = response.getJSONArray(StringConstants.KEY_DATA);

                            /**
                             * Save Count in Shared Preference
                             */
                            SharedPreferenceService.storeUserDetails(context, StringConstants.KEY_PROBLEM_COUNT,
                                    String.valueOf(jsonArray.length()));

                            if (jsonArray.length() > 0) {

                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject = (JSONObject) jsonArray.get(i);


                                    // Fetch and convert start date from UTC to simple date format
                                    String startDate = jsonObject.getString(StringConstants.KEY_START_DATE);
                                    if (!startDate.isEmpty()) {
                                        startDate = DateTimeUtils.convertTimestampToDate(
                                                DateTimeUtils.convertDateUTCToTimestamp(startDate));
                                    }

                                    // Fetch and convert end date from UTC to simple date format
                                    String endDate = jsonObject.getString(StringConstants.KEY_END_DATE);
                                    if (!endDate.isEmpty()) {
                                        endDate = DateTimeUtils.convertTimestampToDate(
                                                DateTimeUtils.convertDateUTCToTimestamp(endDate));
                                    }


                                    MedicalProblem medicalProblem = new MedicalProblem(
                                            SharedPreferenceService.getValue(context, StringConstants.KEY_PATIENT_ID),
                                            jsonObject.getString(StringConstants.KEY_P_ID),
                                            jsonObject.getString(StringConstants.KEY_PROBLEMS_ID),
                                            jsonObject.getString(StringConstants.KEY_PROBLEM_NAME),
                                            jsonObject.getString(StringConstants.KEY_SEARCH_LIST_HYPERLINK),
                                            jsonObject.getBoolean(StringConstants.KEY_IS_SUFFERING),
                                            "",
                                            startDate,
                                            endDate,
                                            jsonObject.getString(StringConstants.KEY_COMMENT),
                                            true
                                    );

                                    medicalProblem.save();
                                }
                            }

                            /**
                             * Fetch Allergy Data
                             */
                            readAllergyProblemData(activity, context);
                        }

                    } catch (Exception e) {

                        // Log error
                        ErrorHandlers.handleError(activity);
                        Crashlytics.logException(e);
                    }
                }

                @Override
                public void onError(VolleyError error) {


                }
            });

        } else {

            ErrorHandlers.handleInternetConnectionFailure(activity);
        }
    }


    /**
     * Function to read Healthbook - Allergy
     *
     * @param activity
     * @param context
     */
    public static void readAllergyProblemData(final Activity activity, final Context context) {

        if (Validation.isConnected(context)) {

            String url = ServiceUrls.KEY_HEALTHBOOK_ALLERGIES
                    .concat(StringConstants.KEY_READ_SELECTIVE_ALLERGIES);

            final HashMap<String, String> headers = new HashMap<>();
            headers.put(StaticConstants.KEY_AUTHORIZATION,
                    StaticConstants.KEY_BEARER
                            .concat(" ")
                            .concat(SharedPreferenceService.getValue(context, StringConstants.KEY_TOKEN)));


            // API Call
            APICallService.GetAPICall(activity, context, url, null, headers, new APIInterface() {
                @Override
                public void onSuccess(JSONObject response) {

                    try {

                        if (response.has(StringConstants.KEY_DATA)) {

                            JSONArray jsonArray = response.getJSONArray(StringConstants.KEY_DATA);
                            /**
                             * Save Count in Shared Preference
                             */
                            SharedPreferenceService.storeUserDetails(context, StringConstants.KEY_ALLERGY_COUNT,
                                    String.valueOf(jsonArray.length()));

                            if (jsonArray.length() > 0) {

                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject = (JSONObject) jsonArray.get(i);

                                    /**
                                     * Convert type from int to string
                                     */
                                    String type = jsonObject.getInt(StringConstants.KEY_ALLERGY_TYPE) == 0 ?
                                            context.getString(R.string.allergy_food) :
                                            (jsonObject.getInt(StringConstants.KEY_ALLERGY_TYPE) == 1 ?
                                                    context.getString(R.string.allergy_environment) : context.getString(R.string.allergy_medication));


                                    Allergy allergy = new Allergy(
                                            SharedPreferenceService.getValue(context, StringConstants.KEY_PATIENT_ID),
                                            jsonObject.getString(StringConstants.KEY_P_ID),
                                            jsonObject.getString(StringConstants.KEY_ALLERGY_ID),
                                            jsonObject.getString(StringConstants.KEY_ALLERGY_NAME),
                                            jsonObject.getString(StringConstants.KEY_SEARCH_LIST_HYPERLINK),
                                            type,
                                            jsonObject.getString(StringConstants.KEY_ALLERGY_KIND_OF_REACTION),
                                            DateTimeUtils.convertTimestampToDate(
                                                    DateTimeUtils.convertDateUTCToTimestamp(jsonObject.getString(StringConstants.KEY_START_DATE))),
                                            jsonObject.getString(StringConstants.KEY_ALLERGY_COMMENTS));

                                    allergy.save();
                                }
                            }

                            /**
                             * Fetch Family Data
                             */
                            readFamilyData(activity, context);
                        }

                    } catch (Exception e) {

                        // Log error
                        ErrorHandlers.handleError(activity);
                        Crashlytics.logException(e);
                    }
                }

                @Override
                public void onError(VolleyError error) {


                }
            });

        } else {

            ErrorHandlers.handleInternetConnectionFailure(activity);
        }
    }


    /**
     * Function to read Healthbook - Family History
     *
     * @param activity
     * @param context
     */
    public static void readFamilyData(final Activity activity, final Context context) {

        if (Validation.isConnected(context)) {

            String url = ServiceUrls.KEY_HEALTHBOOK_FAMILY_HISTORY
                    .concat(StringConstants.KEY_READ_SELECTIVE_FAMILY);

            final HashMap<String, String> headers = new HashMap<>();
            headers.put(StaticConstants.KEY_AUTHORIZATION,
                    StaticConstants.KEY_BEARER
                            .concat(" ")
                            .concat(SharedPreferenceService.getValue(context, StringConstants.KEY_TOKEN)));

            // API Call
            APICallService.GetAPICall(activity, context, url, null, headers, new APIInterface() {
                @Override
                public void onSuccess(JSONObject response) {

                    try {

                        if (response.has(StringConstants.KEY_DATA)) {

                            JSONArray jsonArray = response.getJSONArray(StringConstants.KEY_DATA);

                            /**
                             * Save Count in Shared Preference
                             */
                            SharedPreferenceService.storeUserDetails(context, StringConstants.KEY_FAMILY_COUNT,
                                    String.valueOf(jsonArray.length()));

                            if (jsonArray.length() > 0) {

                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject = (JSONObject) jsonArray.get(i);

                                    FamilyHistory familyHistory = new FamilyHistory(SharedPreferenceService.getValue(context, StringConstants.KEY_PATIENT_ID),
                                            jsonObject.getString(StringConstants.KEY_P_ID),
                                            jsonObject.getString(StringConstants.KEY_PROBLEMS_ID),
                                            jsonObject.getString(StringConstants.KEY_PROBLEM_NAME),
                                            jsonObject.getString(StringConstants.KEY_SEARCH_LIST_HYPERLINK),
                                            Boolean.valueOf(jsonObject.getString(StringConstants.KEY_FAMILY_FATHER_SUFFERING)),
                                            jsonObject.getString(StringConstants.KEY_FAMILY_FATHER_AGE),
                                            Boolean.valueOf(jsonObject.getString(StringConstants.KEY_FAMILY_MOTHER_SUFFERING)),
                                            jsonObject.getString(StringConstants.KEY_FAMILY_MOTHER_AGE),
                                            Boolean.valueOf(jsonObject.getString(StringConstants.KEY_FAMILY_BROTHER_SUFFERING)),
                                            jsonObject.getString(StringConstants.KEY_FAMILY_BROTHER_AGE),
                                            Boolean.valueOf(jsonObject.getString(StringConstants.KEY_FAMILY_SISTER_SUFFERING)),
                                            jsonObject.getString(StringConstants.KEY_FAMILY_SISTER_AGE),
                                            Boolean.valueOf(jsonObject.getString(StringConstants.KEY_FAMILY_GF_SUFFERING)),
                                            jsonObject.getString(StringConstants.KEY_FAMILY_GF_AGE),
                                            Boolean.valueOf(jsonObject.getString(StringConstants.KEY_FAMILY_GM_SUFFERING)),
                                            jsonObject.getString(StringConstants.KEY_FAMILY_GM_AGE),
                                            jsonObject.getString(StringConstants.KEY_COMMENT),
                                            true
                                    );

                                    familyHistory.save();
                                }
                            }

                            /**
                             * Fetch Medication Data
                             */
                            readMedicationData(activity, context);
                        }

                    } catch (Exception e) {

                        // Log error
                        ErrorHandlers.handleError(activity);
                        Crashlytics.logException(e);
                    }
                }

                @Override
                public void onError(VolleyError error) {

                }
            });

        } else {

            ErrorHandlers.handleInternetConnectionFailure(activity);
        }
    }


    /**
     * Function to read Healthbook - Medication
     *
     * @param activity
     * @param context
     */
    public static void readMedicationData(final Activity activity, final Context context) {

        if (Validation.isConnected(context)) {

            String url = ServiceUrls.KEY_HEALTHBOOK_MEDICATION
                    .concat(StringConstants.KEY_READ_SELECTIVE_MEDICATION);

            final HashMap<String, String> headers = new HashMap<>();
            headers.put(StaticConstants.KEY_AUTHORIZATION,
                    StaticConstants.KEY_BEARER
                            .concat(" ")
                            .concat(SharedPreferenceService.getValue(context, StringConstants.KEY_TOKEN)));

            // API Call
            APICallService.GetAPICall(activity, context, url, null, headers, new APIInterface() {
                @Override
                public void onSuccess(JSONObject response) {

                    try {

                        if (response.has(StringConstants.KEY_DATA)) {

                            JSONArray jsonArray = response.getJSONArray(StringConstants.KEY_DATA);

                            /**
                             * Save Count in Shared Preference
                             */
                            SharedPreferenceService.storeUserDetails(context, StringConstants.KEY_MEDICATION_COUNT,
                                    String.valueOf(jsonArray.length()));

                            if (jsonArray.length() > 0) {

                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject = (JSONObject) jsonArray.get(i);

                                    // Fetch and convert start date from UTC to simple date format
                                    String startDate = jsonObject.getString(StringConstants.KEY_START_DATE);
                                    if (!startDate.isEmpty()) {
                                        startDate = DateTimeUtils.convertTimestampToDate(
                                                DateTimeUtils.convertDateUTCToTimestamp(startDate));
                                    }

                                    // Fetch and convert end date from UTC to simple date format
                                    String endDate = jsonObject.getString(StringConstants.KEY_END_DATE);
                                    if (!endDate.isEmpty()) {
                                        endDate = DateTimeUtils.convertTimestampToDate(
                                                DateTimeUtils.convertDateUTCToTimestamp(endDate));
                                    }

                                    // Save data
                                    Medication medication = new Medication(
                                            SharedPreferenceService.getValue(context, StringConstants.KEY_PATIENT_ID),
                                            jsonObject.getString(StringConstants.KEY_P_ID),
                                            jsonObject.getString(StringConstants.KEY_MEDICATION_ID),
                                            jsonObject.getString(StringConstants.KEY_MEDICATION_NAME),
                                            jsonObject.getString(StringConstants.KEY_SEARCH_LIST_HYPERLINK),
                                            startDate,
                                            jsonObject.getString(StringConstants.KEY_MEDICATION_UNITS),
                                            jsonObject.getString(StringConstants.KEY_MEDICATION_DOSES),
                                            jsonObject.getString(StringConstants.KEY_MEDICATION_FREQUENCY),
                                            Boolean.valueOf(jsonObject.getString(StringConstants.KEY_MEDICATION_CONTINUING)),
                                            "",
                                            endDate,
                                            jsonObject.getString(StringConstants.KEY_COMMENT)
                                    );

                                    medication.save();
                                }
                            }

                            /**
                             * Fetch Lifestyle Data
                             */
                            readLifestyleData(activity, context);
                        }

                    } catch (Exception e) {

                        // Log error
                        ErrorHandlers.handleError(activity);
                        Crashlytics.logException(e);
                    }
                }

                @Override
                public void onError(VolleyError error) {

                }
            });

        } else {

            ErrorHandlers.handleInternetConnectionFailure(activity);
        }
    }


    /**
     * Function to read Healthbook - Lifestyle
     *
     * @param activity
     * @param context
     */
    public static void readLifestyleData(final Activity activity, final Context context) {

        if (Validation.isConnected(context)) {

            String url = ServiceUrls.KEY_HEALTHBOOK_LIFESTYLE
                    .concat(StringConstants.KEY_READ_SELECTIVE_LIFESTYLE);

            final HashMap<String, String> headers = new HashMap<>();
            headers.put(StaticConstants.KEY_AUTHORIZATION,
                    StaticConstants.KEY_BEARER
                            .concat(" ")
                            .concat(SharedPreferenceService.getValue(context, StringConstants.KEY_TOKEN)));


            // API Call
            APICallService.GetAPICall(activity, context, url, null, headers, new APIInterface() {
                @Override
                public void onSuccess(JSONObject response) {

                    try {

                        if (response.has(StringConstants.KEY_DATA)) {

                            JSONArray jsonArray = response.getJSONArray(StringConstants.KEY_DATA);

                            /**
                             * Save Count in Shared Preference
                             */
                            SharedPreferenceService.storeUserDetails(context, StringConstants.KEY_LIFESTYLE_COUNT,
                                    String.valueOf(jsonArray.length()));

                            if (jsonArray.length() > 0) {

                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject = (JSONObject) jsonArray.get(i);


                                    // Fetch and convert start date from UTC to simple date format
                                    String startDate = jsonObject.getString(StringConstants.KEY_START_DATE);
                                    if (!startDate.isEmpty()) {
                                        startDate = DateTimeUtils.convertTimestampToDate(
                                                DateTimeUtils.convertDateUTCToTimestamp(startDate));
                                    }

                                    // Fetch and convert end date from UTC to simple date format
                                    String endDate = jsonObject.getString(StringConstants.KEY_END_DATE);
                                    if (!endDate.isEmpty()) {
                                        endDate = DateTimeUtils.convertTimestampToDate(
                                                DateTimeUtils.convertDateUTCToTimestamp(endDate));
                                    }


                                    // Save to local database
                                    Lifestyle lifestyle = new Lifestyle(
                                            SharedPreferenceService.getValue(context, StringConstants.KEY_PATIENT_ID),
                                            jsonObject.getString(StringConstants.KEY_P_ID),
                                            jsonObject.getString(StringConstants.API_KEY_LIFESTYLE_ID),
                                            jsonObject.getString(StringConstants.API_KEY_LIFESTYLE_NAME),
                                            jsonObject.getBoolean(StringConstants.KEY_IS_SUFFERING),
                                            "",
                                            startDate,
                                            endDate,
                                            jsonObject.getString(StringConstants.KEY_COMMENT),
                                            true
                                    );

                                    // Save to Database Table
                                    lifestyle.save();
                                }
                            }

                            /**
                             * Fetch Surgical History Data
                             */
                            readSurgicalData(activity, context);
                        }

                    } catch (Exception e) {

                        // Log error
                        ErrorHandlers.handleError(activity);
                        Crashlytics.logException(e);
                    }
                }

                @Override
                public void onError(VolleyError error) {

                }
            });

        } else {

            ErrorHandlers.handleInternetConnectionFailure(activity);
        }
    }


    /**
     * Function to read Healthbook - Surgical History
     *
     * @param activity
     * @param context
     */
    public static void readSurgicalData(final Activity activity, final Context context) {

        if (Validation.isConnected(context)) {

            String url = ServiceUrls.KEY_HEALTHBOOK_SURGICAL_HISTORY
                    .concat(StringConstants.KEY_READ_SELECTIVE_SURGICAL);

            final HashMap<String, String> headers = new HashMap<>();
            headers.put(StaticConstants.KEY_AUTHORIZATION,
                    StaticConstants.KEY_BEARER
                            .concat(" ")
                            .concat(SharedPreferenceService.getValue(context, StringConstants.KEY_TOKEN)));

            // API Call
            APICallService.GetAPICall(activity, context, url, null, headers, new APIInterface() {
                @Override
                public void onSuccess(JSONObject response) {

                    try {

                        if (response.has(StringConstants.KEY_DATA)) {

                            JSONArray jsonArray = response.getJSONArray(StringConstants.KEY_DATA);

                            /**
                             * Save Count in Shared Preference
                             */
                            SharedPreferenceService.storeUserDetails(context, StringConstants.KEY_SURGICAL_COUNT,
                                    String.valueOf(jsonArray.length()));

                            if (jsonArray.length() > 0) {

                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject = (JSONObject) jsonArray.get(i);

                                    // Create in Local Database
                                    SurgicalHistory surgicalHistory = new SurgicalHistory(
                                            SharedPreferenceService.getValue(context, StringConstants.KEY_PATIENT_ID),
                                            jsonObject.getString(StringConstants.KEY_P_ID),
                                            jsonObject.getString(StringConstants.KEY_SURGICAL_ID),
                                            jsonObject.getString(StringConstants.API_KEY_SURGERY_NAME),
                                            jsonObject.getString(StringConstants.KEY_SEARCH_LIST_HYPERLINK),
                                            StringConstants.KEY_SURGICAL_CATEGORY,
                                            DateTimeUtils.convertTimestampToDate(
                                                    DateTimeUtils.convertDateUTCToTimestamp(jsonObject.getString(StringConstants.API_KEY_SURGERY_DATE))),
                                            Boolean.valueOf(jsonObject.getString(StringConstants.KEY_SURGICAL_IS_SOLVED)),
                                            jsonObject.getString(StringConstants.KEY_COMMENT),
                                            true
                                    );

                                    // Save to Database Table
                                    surgicalHistory.save();
                                }
                            }

                            /**
                             * Fetch Investigative Data
                             */
                            readInvestigativeData(activity, context);
                        }

                    } catch (Exception e) {

                        // Log error
                        ErrorHandlers.handleError(activity);
                        Crashlytics.logException(e);
                    }
                }

                @Override
                public void onError(VolleyError error) {

                }
            });

        } else {

            ErrorHandlers.handleInternetConnectionFailure(activity);
        }
    }


    /**
     * Function to read Healthbook - Investigative History
     *
     * @param activity
     * @param context
     */
    public static void readInvestigativeData(final Activity activity, final Context context) {

        if (Validation.isConnected(context)) {

            String url = ServiceUrls.KEY_HEALTHBOOK_INVESTIGATIVE_HISTORY
                    .concat(StringConstants.KEY_READ_SELECTIVE_INVESTIGATIVE);

            final HashMap<String, String> headers = new HashMap<>();
            headers.put(StaticConstants.KEY_AUTHORIZATION,
                    StaticConstants.KEY_BEARER
                            .concat(" ")
                            .concat(SharedPreferenceService.getValue(context, StringConstants.KEY_TOKEN)));

            // API Call
            APICallService.GetAPICall(activity, context, url, null, headers, new APIInterface() {
                @Override
                public void onSuccess(JSONObject response) {

                    try {

                        if (response.has(StringConstants.KEY_DATA)) {

                            JSONArray jsonArray = response.getJSONArray(StringConstants.KEY_DATA);

                            /**
                             * Save Count in Shared Preference
                             */
                            SharedPreferenceService.storeUserDetails(context, StringConstants.KEY_SURGICAL_COUNT,
                                    String.valueOf(Integer.parseInt(
                                            SharedPreferenceService.getValue(context, StringConstants.KEY_SURGICAL_COUNT)) + jsonArray.length()));

                            if (jsonArray.length() > 0) {

                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject = (JSONObject) jsonArray.get(i);

                                    // Create in Local Database
                                    SurgicalHistory surgicalHistory = new SurgicalHistory(
                                            SharedPreferenceService.getValue(context, StringConstants.KEY_PATIENT_ID),
                                            jsonObject.getString(StringConstants.KEY_P_ID),
                                            jsonObject.getString(StringConstants.KEY_INVESTIGATIVE_ID),
                                            jsonObject.getString(StringConstants.API_KEY_INVESTIGATIVE_NAME),
                                            jsonObject.getString(StringConstants.KEY_SEARCH_LIST_HYPERLINK),
                                            StringConstants.KEY_INVESTIGATE_CATEGORY,
                                            DateTimeUtils.convertTimestampToDate(
                                                    DateTimeUtils.convertDateUTCToTimestamp(
                                                            jsonObject.getString(StringConstants.KEY_INVESTIGATE_TEST_DATE))),
                                            Boolean.valueOf(jsonObject.getString(StringConstants.KEY_SURGICAL_IS_SOLVED)),
                                            jsonObject.getString(StringConstants.KEY_COMMENT),
                                            true
                                    );

                                    // Save to Database Table
                                    surgicalHistory.save();
                                }
                            }

                            /**
                             * Fetch Pregnant Data
                             */
                            readPregnancyData(activity, context);
                        }

                    } catch (Exception e) {

                        // Log error
                        ErrorHandlers.handleError(activity);
                        Crashlytics.logException(e);
                    }
                }

                @Override
                public void onError(VolleyError error) {

                }
            });

        } else {

            ErrorHandlers.handleInternetConnectionFailure(activity);
        }
    }


    /**
     * Function to read Healthbook - Pregnancy
     *
     * @param activity
     * @param context
     */
    public static void readPregnancyData(final Activity activity, final Context context) {

        if (Validation.isConnected(context)) {

            String url = ServiceUrls.KEY_HEALTHBOOK_PREGNANCY
                    .concat(StringConstants.KEY_READ_SELECTIVE_PREGNANCYS);

            final HashMap<String, String> headers = new HashMap<>();
            headers.put(StaticConstants.KEY_AUTHORIZATION, StaticConstants.KEY_BEARER
                    .concat(" ")
                    .concat(SharedPreferenceService.getValue(context, StringConstants.KEY_TOKEN)));

            // API Call
            APICallService.GetAPICall(activity, context, url, null, headers, new APIInterface() {
                @Override
                public void onSuccess(JSONObject response) {

                    try {

                        if (response.has(StringConstants.KEY_DATA)) {

                            JSONArray jsonArray = response.getJSONArray(StringConstants.KEY_DATA);
                            if (jsonArray.length() > 0) {

                                for (int i = 0; i < jsonArray.length(); i++) {

                                    JSONObject jsonObject = (JSONObject) jsonArray.get(i);

                                    Pregnancy model = new Pregnancy();
                                    model.setPatientId(SharedPreferenceService.getValue(context, StringConstants.KEY_PATIENT_ID));
                                    model.setpId(jsonObject.getString(StringConstants.KEY_P_ID));
                                    model.setAgeOfMenses(jsonObject.getString(StringConstants.KEY_PREGNANCY_MENSES_AGE));
                                    model.setDateOfLastMensesCycle(
                                            DateTimeUtils.convertTimestampToDate(
                                                    DateTimeUtils.convertDateUTCToTimestamp(jsonObject.getString(StringConstants.KEY_PREGNANCY_LMD))));
                                    model.setHaveYouPregnant(Boolean.valueOf(jsonObject.getString(StringConstants.KEY_PREGNANCY_IS_EVER_PREGNANCY)));

                                    if (model.isHaveYouPregnant()) {

                                        model.setNoOfFullTermPregnant(jsonObject.getString(StringConstants.KEY_PREGNANCY_NO_OF_FTP));
                                        model.setNoOfPreTermPregnant(jsonObject.getString(StringConstants.KEY_PREGNANCY_NO_OF_PTP));
                                        model.setNoOfAbortions(jsonObject.getString(StringConstants.KEY_PREGNANCY_NO_OF_ABORTIONS));
                                        model.setNoOfLivingChildren(jsonObject.getString(StringConstants.KEY_PREGNANCY_NO_OF_LIVING_CHILDREN));

                                    } else {

                                        model.setNoOfFullTermPregnant("");
                                        model.setNoOfPreTermPregnant("");
                                        model.setNoOfAbortions("");
                                        model.setNoOfLivingChildren("");
                                    }

                                    // Save to Database Table
                                    model.save();
                                }
                            }

                            /**
                             * Fetch Childhood History Data
                             */
                            readChildhoodData(activity, context);
                        }

                    } catch (Exception e) {

                        // Log error
                        ErrorHandlers.handleError(activity);
                        Crashlytics.logException(e);
                    }
                }

                @Override
                public void onError(VolleyError error) {

                }
            });

        } else {

            ErrorHandlers.handleInternetConnectionFailure(activity);
        }
    }


    /**
     * Function to read Healthbook - Childhood
     *
     * @param activity
     * @param context
     */
    public static void readChildhoodData(final Activity activity, final Context context) {

        if (Validation.isConnected(context)) {

            String url = ServiceUrls.KEY_HEALTHBOOK_CHILDHOOD_HISTORY
                    .concat(StringConstants.KEY_READ_SELECTIVE_CHILDHOOD);

            final HashMap<String, String> headers = new HashMap<>();
            headers.put(StaticConstants.KEY_AUTHORIZATION,
                    StaticConstants.KEY_BEARER
                            .concat(" ")
                            .concat(SharedPreferenceService.getValue(context, StringConstants.KEY_TOKEN)));

            // API Call
            APICallService.GetAPICall(activity, context, url, null, headers, new APIInterface() {
                @Override
                public void onSuccess(JSONObject response) {

                    try {

                        if (response.has(StringConstants.KEY_DATA)) {

                            JSONArray jsonArray = response.getJSONArray(StringConstants.KEY_DATA);

                            /**
                             * Save Count in Shared Preference
                             */
                            SharedPreferenceService.storeUserDetails(context, StringConstants.KEY_CHILDHOOD_COUNT,
                                    String.valueOf(jsonArray.length()));

                            if (jsonArray.length() > 0) {

                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject = (JSONObject) jsonArray.get(i);

                                    // Create in Local Database
                                    ChildhoodHistory childhoodHistory = new ChildhoodHistory(
                                            SharedPreferenceService.getValue(context, StringConstants.KEY_PATIENT_ID),
                                            jsonObject.getString(StringConstants.KEY_P_ID),
                                            jsonObject.getString(StringConstants.KEY_PROBLEMS_ID),
                                            jsonObject.getString(StringConstants.KEY_PROBLEM_NAME),
                                            jsonObject.getString(StringConstants.KEY_SEARCH_LIST_HYPERLINK),
                                            DateTimeUtils.convertTimestampToDate(
                                                    DateTimeUtils.convertDateUTCToTimestamp(jsonObject.getString(StringConstants.KEY_START_DATE))),
                                            Boolean.valueOf(jsonObject.getString(StringConstants.KEY_IS_SUFFERING)),
                                            jsonObject.getString(StringConstants.KEY_COMMENT),
                                            true
                                    );

                                    // Save to Database Table
                                    childhoodHistory.save();
                                }
                            }

                            /**
                             * Fetch Gynaecology History Data
                             */
                            readGynaecologyData(activity, context);
                        }

                    } catch (Exception e) {

                        // Log error
                        ErrorHandlers.handleError(activity);
                        Crashlytics.logException(e);
                    }
                }

                @Override
                public void onError(VolleyError error) {

                }
            });

        } else {

            ErrorHandlers.handleInternetConnectionFailure(activity);
        }
    }


    /**
     * Function to read Healthbook - Gynaecology
     *
     * @param activity
     * @param context
     */
    public static void readGynaecologyData(final Activity activity, final Context context) {

        if (Validation.isConnected(context)) {

            String url = ServiceUrls.KEY_HEALTHBOOK_GYNAECOLOGY
                    .concat(StringConstants.KEY_READ_SELECTIVE_GYNAECOLOGY);

            final HashMap<String, String> headers = new HashMap<>();
            headers.put(StaticConstants.KEY_AUTHORIZATION,
                    StaticConstants.KEY_BEARER
                            .concat(" ")
                            .concat(SharedPreferenceService.getValue(context, StringConstants.KEY_TOKEN)));

            // API Call
            APICallService.GetAPICall(activity, context, url, null, headers, new APIInterface() {
                @Override
                public void onSuccess(JSONObject response) {

                    try {

                        if (response.has(StringConstants.KEY_DATA)) {

                            JSONArray jsonArray = response.getJSONArray(StringConstants.KEY_DATA);

                            /**
                             * Save Count in Shared Preference
                             */
                            SharedPreferenceService.storeUserDetails(context, StringConstants.KEY_GYNAECOLOGY_COUNT,
                                    String.valueOf(jsonArray.length()));

                            if (jsonArray.length() > 0) {

                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject = (JSONObject) jsonArray.get(i);

                                    // Create in Local Database
                                    Gynaecology gynaecology = new Gynaecology(
                                            SharedPreferenceService.getValue(context, StringConstants.KEY_PATIENT_ID),
                                            jsonObject.getString(StringConstants.KEY_P_ID),
                                            jsonObject.getString(StringConstants.KEY_PROBLEMS_ID),
                                            jsonObject.getString(StringConstants.KEY_PROBLEM_NAME),
                                            jsonObject.getString(StringConstants.KEY_SEARCH_LIST_HYPERLINK),
                                            DateTimeUtils.convertTimestampToDate(
                                                    DateTimeUtils.convertDateUTCToTimestamp(jsonObject.getString(StringConstants.KEY_START_DATE))),
                                            Boolean.valueOf(jsonObject.getString(StringConstants.KEY_IS_SUFFERING)),
                                            jsonObject.getString(StringConstants.KEY_COMMENT),
                                            true
                                    );
                                    // Save to Database Table
                                    gynaecology.save();
                                }
                            }

                            /**
                             * Fetch Blood Pressure Data
                             */
                            readBloodPressureData(activity, context);
                        }

                    } catch (Exception e) {

                        // Log error
                        ErrorHandlers.handleError(activity);
                        Crashlytics.logException(e);
                    }
                }

                @Override
                public void onError(VolleyError error) {

                }
            });

        } else {

            ErrorHandlers.handleInternetConnectionFailure(activity);
        }
    }


    /**
     * Function to read Vitals - Blood Pressure records
     *
     * @param context
     */
    public static void readBloodPressureData(final Activity activity, final Context context) {

        if (Validation.isConnected(context)) {

            // Url construction
            String url = ServiceUrls.KEY_BLOOD_PRESSURE
                    .concat(StringConstants.KEY_READ_SELECTIVE_BLOOD_PRESSURE);

            // Set Headers
            final HashMap<String, String> headers = new HashMap<>();
            headers.put(StaticConstants.KEY_AUTHORIZATION,
                    StaticConstants.KEY_BEARER
                            .concat(" ")
                            .concat(SharedPreferenceService
                                    .getValue(context, StringConstants.KEY_TOKEN)));

            /**
             * API Call
             */
            APICallService.GetAPICall(activity, context, url, null, headers, new APIInterface() {
                @Override
                public void onSuccess(JSONObject response) {

                    try {

                        if (response.has(StringConstants.KEY_DATA)) {

                            JSONArray jsonArray = response.getJSONArray(StringConstants.KEY_DATA);
                            if (jsonArray.length() > 0) {

                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                                    BloodPressure bloodPressure = new BloodPressure(
                                            SharedPreferenceService.getValue(context, StringConstants.KEY_PATIENT_ID),
                                            jsonObject.getString(StringConstants.KEY_BP_ID),
                                            jsonObject.getString(StringConstants.KEY_SYSTOLIC_BP),
                                            jsonObject.getString(StringConstants.KEY_DIASTOLIC_BP),
                                            jsonObject.getString(StringConstants.KEY_TAG),
                                            DateTimeUtils.convertTimestampToDate(jsonObject.getLong(StringConstants.KEY_TIMESTAMP)),
                                            DateTimeUtils.convertTimestampToTime(jsonObject.getLong(StringConstants.KEY_TIMESTAMP)),
                                            jsonObject.getLong(StringConstants.KEY_TIMESTAMP)
                                    );

                                    // Save to Database
                                    bloodPressure.save();
                                }
                            }

                            /**
                             * Fetch Blood Glucose Data
                             */
                            readBloodGlucoseData(activity, context);
                        }
                    } catch (Exception e) {

                        // Log error
                        ErrorHandlers.handleError(activity);
                        Crashlytics.logException(e);
                    }
                }

                @Override
                public void onError(VolleyError error) {

                }
            });
        } else {

            // Log error
            ErrorHandlers.handleInternetConnectionFailure(activity);
        }
    }


    /**
     * Function to read Vitals - Blood Glucose records
     *
     * @param context
     * @return
     */
    public static void readBloodGlucoseData(final Activity activity, final Context context) {

        if (Validation.isConnected(context)) {

            // Url construction
            String url = ServiceUrls.KEY_BLOOD_GLUCOSE
                    .concat(StringConstants.KEY_READ_SELECTIVE_BLOOD_GLUCOSE);

            // Set Headers
            final HashMap<String, String> headers = new HashMap<>();
            headers.put(StaticConstants.KEY_AUTHORIZATION,
                    StaticConstants.KEY_BEARER
                            .concat(" ")
                            .concat(SharedPreferenceService
                                    .getValue(context, StringConstants.KEY_TOKEN)));


            // API Call
            APICallService.GetAPICall(activity, context, url, null, headers, new APIInterface() {
                @Override
                public void onSuccess(JSONObject response) {

                    try {

                        if (response.has(StringConstants.KEY_DATA)) {

                            JSONArray jsonArray = response.getJSONArray(StringConstants.KEY_DATA);

                            if (jsonArray.length() > 0) {
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                                    BloodGlucose bloodGlucose = new BloodGlucose(
                                            SharedPreferenceService.getValue(context, StringConstants.KEY_PATIENT_ID),
                                            jsonObject.getString(StringConstants.KEY_BG_ID),
                                            jsonObject.getString(StringConstants.API_KEY_BLOOD_GLUCOSE),
                                            jsonObject.getString(StringConstants.API_KEY_CHECKUP),
                                            jsonObject.getString(StringConstants.KEY_TAG),
                                            DateTimeUtils.convertTimestampToDate(jsonObject.getLong(StringConstants.KEY_TIMESTAMP)),
                                            DateTimeUtils.convertTimestampToTime(jsonObject.getLong(StringConstants.KEY_TIMESTAMP)),
                                            jsonObject.getLong(StringConstants.KEY_TIMESTAMP)
                                    );

                                    // Save to Database
                                    bloodGlucose.save();
                                }
                            }

                            /**
                             * Read BMI Data
                             */
                            readBMIData(activity, context);
                        }
                    } catch (Exception e) {

                        // Log error
                        ErrorHandlers.handleError(activity);
                        Crashlytics.logException(e);
                    }
                }

                @Override
                public void onError(VolleyError error) {

                }
            });


        } else {

            // No Internet Connection
            ErrorHandlers.handleInternetConnectionFailure(activity);
        }
    }


    /**
     * Function to read Vitals - BMI records
     *
     * @param context
     * @return
     */
    public static void readBMIData(final Activity activity, final Context context) {

        if (Validation.isConnected(context)) {

            // Url construction
            String url = ServiceUrls.KEY_BMI
                    .concat(StringConstants.KEY_READ_SELECTIVE_BMI);

            // Set Headers
            final HashMap<String, String> headers = new HashMap<>();
            //headers.put(StaticConstants.KEY_CONTENT_TYPE, StaticConstants.KEY_APPLICATION_JSON);
            headers.put(StaticConstants.KEY_AUTHORIZATION,
                    StaticConstants.KEY_BEARER
                            .concat(" ")
                            .concat(SharedPreferenceService
                                    .getValue(context, StringConstants.KEY_TOKEN)));

            // API Call
            APICallService.GetAPICall(activity, context, url, null, headers, new APIInterface() {
                @Override
                public void onSuccess(JSONObject response) {

                    try {

                        if (response.has(StringConstants.KEY_DATA)) {

                            JSONArray jsonArray = response.getJSONArray(StringConstants.KEY_DATA);

                            if (jsonArray.length() > 0) {
                                for (int i = 0; i < jsonArray.length(); i++) {

                                    JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                                    BMI bmi = new BMI(
                                            SharedPreferenceService.getValue(context, StringConstants.KEY_PATIENT_ID),
                                            jsonObject.getString(StringConstants.KEY_BMI_ID),
                                            String.format("%.2f", jsonObject.getDouble(StringConstants.API_KEY_BMI)),
                                            jsonObject.getString(StringConstants.KEY_TAG),
                                            String.valueOf(Integer.parseInt(jsonObject.getString(StringConstants.API_KEY_BMI_HEIGHT)) / 12),
                                            String.valueOf(Integer.parseInt(jsonObject.getString(StringConstants.API_KEY_BMI_HEIGHT)) % 12),
                                            jsonObject.getString(StringConstants.API_KEY_BMI_WEIGHT),
                                            DateTimeUtils.convertTimestampToDate(jsonObject.getLong(StringConstants.KEY_TIMESTAMP)),
                                            jsonObject.getLong(StringConstants.KEY_TIMESTAMP)
                                    );

                                    // Save to Database
                                    bmi.save();
                                }
                            }
                        }

                        /**
                         * Read BMI Data
                         */
                        readUploadDocumentsData(activity, context);

                    } catch (Exception e) {

                        // Log error
                        ErrorHandlers.handleError(activity);
                        Crashlytics.logException(e);
                    }
                }

                @Override
                public void onError(VolleyError error) {

                }
            });


        } else {

            // No Internet Connection
            ErrorHandlers.handleInternetConnectionFailure(activity);
        }
    }


    /**
     * Function to read Upload Document records
     *
     * @param context
     * @return
     */
    public static void readUploadDocumentsData(final Activity activity, final Context context) {

        if (Validation.isConnected(context)) {

            // Url construction
            String url = ServiceUrls.KEY_UPLOAD_DOCUMENTS
                    .concat(StringConstants.KEY_READ_SELECTIVE_UD);

            // Set Headers
            final HashMap<String, String> headers = new HashMap<>();
            headers.put(StaticConstants.KEY_AUTHORIZATION,
                    StaticConstants.KEY_BEARER
                            .concat(" ")
                            .concat(SharedPreferenceService
                                    .getValue(context, StringConstants.KEY_TOKEN)));

            // API Call
            APICallService.GetAPICall(activity, context, url, null, headers, new APIInterface() {
                @Override
                public void onSuccess(JSONObject response) {


                    try {

                        /*if (response.has(StringConstants.KEY_DATA)) {

                            new GetBlobImage(activity, response).execute();
                        }*/

                        System.out.println("Check Sync Gallery Response - " + response);


                        JSONArray jsonArray = response.getJSONArray(StringConstants.KEY_DATA);

                        if (jsonArray.length() > 0) {
                            for (int i = 0; i < jsonArray.length(); i++) {

                                JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                                boolean isMultiple = false;
                                int count = 1;

                                if (jsonObject.getJSONArray(StringConstants.API_KEY_IMAGE_ID_URI).length() > 0) {
                                    isMultiple = true;
                                    count = jsonObject.getJSONArray(StringConstants.API_KEY_IMAGE_ID_URI).length();
                                }

                                Album album = new Album(
                                        SharedPreferenceService.getValue(activity, StringConstants.KEY_PATIENT_ID),
                                        jsonObject.getString(StringConstants.KEY_ID),
                                        jsonObject.getString(StringConstants.API_KEY_NAME),
                                        jsonObject.getLong(StringConstants.KEY_TIMESTAMP),
                                        AlbumTag.valueOf(jsonObject.getString(StringConstants.KEY_TAG)).getAlbumTag(),
                                        isMultiple,
                                        count,
                                        jsonObject.getString(StringConstants.KEY_COMMENT)
                                );

                                // Save to Database
                                album.save();

                                /**
                                 * Populate Upload Images
                                 */
                                JSONArray imagePaths = jsonObject.getJSONArray(StringConstants.API_KEY_IMAGE_ID_URI);
                                for (int j = 0; j < imagePaths.length(); j++) {

                                    JSONObject imagePath = imagePaths.getJSONObject(j);

                                    // Save to image to database
                                    UploadImage uploadImage = new UploadImage(
                                            jsonObject.getString(StringConstants.KEY_ID),
                                            imagePath.getString(StringConstants.KEY_ID),
                                            "",
                                            imagePath.getString(StringConstants.API_KEY_IMAGE_URI_SINGLE)
                                    );
                                    uploadImage.save();
                                }
                            }
                        }


                        // Dismiss popup
                        popup.dismiss();

                        /**
                         * Next Step
                         */
                        LoginActivity.nextStep(activity, context);

                    } catch (Exception e) {

                        // Dismiss popup
                        popup.dismiss();


                        // Log error
                        ErrorHandlers.handleError(activity);
                        Crashlytics.logException(e);
                    }
                }

                @Override
                public void onError(VolleyError error) {


                    // Dismiss popup
                    popup.dismiss();
                }
            });


        } else {

            // No Internet Connection
            ErrorHandlers.handleInternetConnectionFailure(activity);
        }
    }
}
