package com.careons.app.Patient.Commons;

import com.careons.app.Patient.Services.SharedPreferenceService;
import com.careons.app.Shared.Utils.Validation;
import com.orm.SugarApp;

public class AppManager extends SugarApp {

    @Override
    public void onCreate() {
        super.onCreate();


        String appVersion = SharedPreferenceService.getValue(getApplicationContext(), "APP_VERSION");

        /**
         * Check current app version
         */
        if (Validation.isEmpty(appVersion) || !appVersion.equals("1.0.8")) {

            // Destroy User Session
            SharedPreferenceService.destroyUserSession(getApplicationContext());

            // Store App Version
            SharedPreferenceService.storeUserDetails(getApplicationContext(), "APP_VERSION", "1.0.8");
        }
    }
}
