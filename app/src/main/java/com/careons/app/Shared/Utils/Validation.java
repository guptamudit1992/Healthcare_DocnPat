package com.careons.app.Shared.Utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.careons.app.Patient.Commons.StaticConstants;
import com.careons.app.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Validation {

    /**
     * Function to check if device has internet connection
     * @param context
     * @return
     */
    public static boolean isConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        return activeNetwork != null;
    }


    /**
     * Function to check if string is empty
     * @param string
     * @return
     */
    public static boolean isEmpty(String string) {

        return (string == null || string.trim().isEmpty() ) ? true : false;
    }


    /**
     * Function to regex match if email is valid
     * @param string
     * @return
     */
    public static boolean isValidEmail(String string) {

        String regex = StaticConstants.EMAIL_REGEX;
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(string);
        return matcher.matches();
    }


    /**
     * Function to regex match if mobile is valid
     * @param string
     * @return
     */
    public static boolean isValidMobile(String string) {

        String regex = StaticConstants.MOBILE_REGEX;
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(string);
        return matcher.matches();
    }


    /**
     * Function to regex match if password is valid
     * @param string
     * @return
     */
    public static boolean isValidPassword(String string) {

        return string.trim().length() > 5;

    }

    /**
     * Function to validate date
     */
    public static boolean validateDate(TextView textView, Context context) {

        boolean isValidPassword = false;
        String text = textView.getText().toString();

        // Validate Username
        if(Validation.isEmpty(text)) {

            textView.setError(context.getString(R.string.err_msg_date));

        } else {

            isValidPassword = true;
            textView.setError(null);
        }

        return isValidPassword;
    }

    /**
     * Function to validate date
     */
    public static boolean validateStartDate(TextView textView, Context context) {

        boolean isValidPassword = false;
        String text = textView.getText().toString();

        // Validate Username
        if(Validation.isEmpty(text)) {

            textView.setError(context.getString(R.string.err_msg_start_date));

        } else {

            isValidPassword = true;
            textView.setError(null);
        }

        return isValidPassword;
    }

    /**
     * Function to validate DOB
     */
    public static boolean validateDOB(TextView textView, Context context) {

        boolean isValidDOB = false;
        String text = textView.getText().toString();

        // Validate Username
        if(Validation.isEmpty(text)) {

            textView.setError(context.getString(R.string.err_msg_dob_date));

        } else {

            isValidDOB = true;
            textView.setError(null);
        }

        return isValidDOB;
    }


    /**
     * Function to validate Date of Detection
     */
    public static boolean validateDateOfDetection(TextView textView, Context context) {

        boolean isValidDateOfDetection = false;
        String text = textView.getText().toString();

        // Validate Username
        if (Validation.isEmpty(text)) {

            textView.setError(context.getString(R.string.err_msg_detection_date));

        } else {

            isValidDateOfDetection = true;
            textView.setError(null);
        }

        return isValidDateOfDetection;
    }

    /**
     * Function to validate Duration
     * @return
     */
    public static boolean isSelectDuration(Context context, Spinner spinner) {

        boolean isValidDuration= true;

        if(spinner.getSelectedItem().toString().equals(context.getString(R.string.duration))) {
            isValidDuration = false;
            Toast.makeText(context, context.getString(R.string.select_duration), Toast.LENGTH_SHORT).show();
        }
        return isValidDuration;
    }

    /**
     * Function to validate Duration
     * @return
     */
    public static boolean isSelectReaction(Context context, Spinner spinner) {

        boolean isValidDuration= true;

        if(spinner.getSelectedItem().toString().equals(context.getString(R.string.reaction))) {
            isValidDuration = false;
            Toast.makeText(context, context.getString(R.string.select_reaction), Toast.LENGTH_SHORT).show();
        }
        return isValidDuration;
    }

    /**
     * Function to validate Dose
     * @return
     */
    public static boolean isSelectDose(Context context, Spinner spinner) {

        boolean isValidDuration= true;

        if(spinner.getSelectedItem().toString().equals(context.getString(R.string.dose))) {
            isValidDuration = false;
            Toast.makeText(context, context.getString(R.string.select_dose), Toast.LENGTH_SHORT).show();
        }
        return isValidDuration;
    }

    /**
     * Function to validate Unit
     * @return
     */
    public static boolean isSelectUnit(Context context, Spinner spinner) {

        boolean isValidDuration= true;

        if(spinner.getSelectedItem().toString().equals(context.getString(R.string.unit))) {
            isValidDuration = false;
            Toast.makeText(context, context.getString(R.string.select_unit), Toast.LENGTH_SHORT).show();
        }
        return isValidDuration;
    }

    /**
     * Function to validate Frequency
     * @return
     */
    public static boolean isSelectFrequency(Context context, Spinner spinner) {

        boolean isValidDuration= true;

        if(spinner.getSelectedItem().toString().equals(context.getString(R.string.frequency))) {
            isValidDuration = false;
            Toast.makeText(context, context.getString(R.string.select_frequency), Toast.LENGTH_SHORT).show();
        }
        return isValidDuration;
    }
}
