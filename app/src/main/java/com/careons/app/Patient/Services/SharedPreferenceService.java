package com.careons.app.Patient.Services;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

import com.careons.app.Patient.Commons.StaticConstants;
import com.careons.app.Patient.Commons.StringConstants;
import com.careons.app.Patient.Database.Models.Profile.Profile;

public class SharedPreferenceService {

    /**
     * Function to initialize user session in shared preference memory
     * @param context
     * @param key
     * @param value
     */
    public static void storeUserDetails(Context context, String key, String value) {
        SharedPreferences sharedPreferences =
                context.getSharedPreferences(StaticConstants.SESSION_OBJECT, Context.MODE_PRIVATE);
        Editor editor = sharedPreferences.edit();

        try {

            // Store key - value pair in Shared Preference
            editor.putString(key, value);

        } catch (Exception error) {
            Log.e(StringConstants.EXCEPTION_ERROR, error.toString());
        }

        // Commit to Shared Preference Cache
        editor.commit();
    }


    /**
     * Function to save Profile details from Database to Shared Preference Memory
     * @param context
     * @param profile
     */
    public static void storeProfileToSharedPreference(Context context, Profile profile) {

        SharedPreferences sharedPreferences =
                context.getSharedPreferences(StaticConstants.SESSION_OBJECT, Context.MODE_PRIVATE);
        Editor editor = sharedPreferences.edit();

        try {

            editor.putString(StringConstants.KEY_PATIENT_ID, profile.getPatientId());
            editor.putString(StringConstants.KEY_USERNAME, profile.getUsername());
            editor.putString(StringConstants.KEY_EMAIL, profile.getEmail());
            editor.putString(StringConstants.KEY_PHONE, profile.getPhone());

            editor.putString(StringConstants.KEY_IS_ACCOUNT_CREATED, String.valueOf(profile.isAccountCreated()));
            editor.putString(StringConstants.KEY_IS_ONBOARDING_COMPLETE, String.valueOf(profile.isOnboardingComplete()));
            editor.putString(StringConstants.KEY_GENDER, profile.getGender());
            editor.putString(StringConstants.KEY_BLOOD_GROUP, profile.getBloodGroup());

            editor.putString(StringConstants.KEY_DOB, profile.getDob());
            editor.putString(StringConstants.KEY_AGE, profile.getAge());

        } catch (Exception error) {

            // Handle Error
        }

        // Commit to Shared Preference Cache
        editor.apply();
    }


    /**
     * Function to fetch value corresponding to key in Shared Preference
     * @param context
     * @param key
     * @return
     */
    public static String getValue(Context context, String key) {
        SharedPreferences sharedPreferences =
                context.getSharedPreferences(StaticConstants.SESSION_OBJECT, Context.MODE_PRIVATE);
        String value = sharedPreferences.getString(key, null);
        return value;
    }


    /**
     * Function to clear user session from shared preference memory
     * @param context
     */
    public static void destroyUserSession(Context context) {
        android.content.SharedPreferences preferences =
                context.getSharedPreferences(StaticConstants.SESSION_OBJECT, Context.MODE_PRIVATE);
        Editor editor = preferences.edit();
        editor.clear();
        editor.commit();
    }
}
