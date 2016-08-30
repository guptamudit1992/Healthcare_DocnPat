package com.careons.app.Shared.Components.Crashlytics;

import android.content.Context;

import com.careons.app.Patient.Commons.StringConstants;
import com.careons.app.Patient.Services.SharedPreferenceService;
import com.crashlytics.android.Crashlytics;

public class LogUser {

    /**
     * Log user with Crashlytics
     */
    public static void logUser(Context context) {

        // You can call any combination of these three methods
        Crashlytics.setUserIdentifier(SharedPreferenceService.getValue(context, StringConstants.KEY_PATIENT_ID));
        Crashlytics.setUserEmail(SharedPreferenceService.getValue(context, StringConstants.KEY_EMAIL));
        Crashlytics.setUserName(SharedPreferenceService.getValue(context, StringConstants.KEY_USERNAME));
    }
}
