package com.careons.app.Shared.Components.GoogleOAuth;

import android.app.Activity;
import android.os.AsyncTask;

import com.careons.app.Patient.Activity.Signup.LoginActivity;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;

import java.io.IOException;

public class GetUsernameTask extends AsyncTask<Void, Void, Void> {

    Activity mActivity;
    String mScope;
    String mEmail;
    private static String token;


    public GetUsernameTask(Activity activity, String name, String scope) {
        this.mActivity = activity;
        this.mScope = scope;
        this.mEmail = name;
    }


    /**
     * Executes the asynchronous job. This runs when you call execute()
     * on the AsyncTask instance.
     */
    @Override
    protected Void doInBackground(Void... params) {
        try {

            token = fetchToken();
            if (token != null) {

                // **Insert the good stuff here.**
                // Use the token to access the user's Google data.
            }
        } catch (IOException e) {

            // The fetchToken() method handles Google-specific exceptions,
            // so this indicates something went wrong at a higher level.
            // TIP: Check for network connectivity before starting the AsyncTask.
        }
        return null;
    }


    /**
     * Post Execute
     * @param param
     */
    protected void onPostExecute(Void param) {

        /**
         * Call API Google Login
         */
        LoginActivity.signinWithGoogle(mActivity, mActivity.getApplicationContext(), token);
    }


    /**
     * Gets an authentication token from Google and handles any
     * GoogleAuthException that may occur.
     */
    protected String fetchToken() throws IOException {

        try {

            return GoogleAuthUtil.getToken(mActivity, mEmail, mScope);

        } catch (UserRecoverableAuthException userRecoverableException) {

            // TODO: Handle Exception

            // GooglePlayServices.apk is either old, disabled, or not present
            // so we need to show the user some UI in the activity to recover.
            //mActivity.handleException(userRecoverableException);

        } catch (GoogleAuthException fatalException) {

            // TODO: Handle Exception

            // Some other type of unrecoverable exception has occurred.
            // Report and log the error as appropriate for your app.
        }

        return null;
    }
}

