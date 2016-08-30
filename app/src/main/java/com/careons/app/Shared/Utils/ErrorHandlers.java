package com.careons.app.Shared.Utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.careons.app.Patient.Activity.Onboarding.AppOnboardingActivity;
import com.careons.app.Patient.Activity.Signup.ForgotPasswordActivity;
import com.careons.app.R;
import com.careons.app.Patient.Services.SharedPreferenceService;

public class ErrorHandlers {

    /**
     * Function to handle internet connection failure
     * @param activity
     */
    public static void handleInternetConnectionFailure(Activity activity) {


        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.dialog_error, null);
        builder.setView(dialogView);
        final AlertDialog dialog = builder.create();

        /**
         * Initialize Data Fields
         */
        ImageView errorImageView = (ImageView) dialogView.findViewById(R.id.error_image);
        TextView titleTextView = (TextView) dialogView.findViewById(R.id.error_title);
        TextView descriptionTextView = (TextView) dialogView.findViewById(R.id.error_description);
        TextView ctaPrimaryTextView = (TextView) dialogView.findViewById(R.id.error_primary_cta);


        /**
         * Substitute values
         */
        errorImageView.setImageDrawable(activity.getResources().getDrawable(R.drawable.dp3));
        titleTextView.setText(activity.getString(R.string.dialog_internet_failure_title));
        descriptionTextView.setText(activity.getString(R.string.dialog_internet_failure));
        ctaPrimaryTextView.setText(activity.getString(R.string.action_ok));


        /**
         * Set Actions on CTA - Primary
         */
        ctaPrimaryTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
            }
        });


        // Show dialog
        dialog.show();
    }


    /**
     * Function to handle API failure
     * @param activity
     */
    public static void handleAPIError(final Activity activity) {

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.dialog_error, null);
        builder.setView(dialogView);
        final AlertDialog dialog = builder.create();

        /**
         * Initialize Data Fields
         */
        ImageView errorImageView = (ImageView) dialogView.findViewById(R.id.error_image);
        TextView titleTextView = (TextView) dialogView.findViewById(R.id.error_title);
        TextView descriptionTextView = (TextView) dialogView.findViewById(R.id.error_description);
        TextView ctaPrimaryTextView = (TextView) dialogView.findViewById(R.id.error_primary_cta);


        /**
         * Substitute values
         */
        errorImageView.setImageDrawable(activity.getResources().getDrawable(R.drawable.dp3));
        titleTextView.setText(activity.getString(R.string.dialog_exception_title));
        descriptionTextView.setText(activity.getString(R.string.dialog_exception));
        ctaPrimaryTextView.setText(activity.getString(R.string.action_ok));


        /**
         * Set Actions on CTA - Primary
         */
        ctaPrimaryTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
            }
        });


        // Show dialog
        dialog.show();
    }


    /**
     * Function to handle API Authentication failure
     * @param activity
     */
    public static void handleAPIAuthFailure(final Activity activity) {


        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.dialog_error, null);
        builder.setView(dialogView);
        final AlertDialog dialog = builder.create();

        /**
         * Initialize Data Fields
         */
        ImageView errorImageView = (ImageView) dialogView.findViewById(R.id.error_image);
        TextView titleTextView = (TextView) dialogView.findViewById(R.id.error_title);
        TextView descriptionTextView = (TextView) dialogView.findViewById(R.id.error_description);
        TextView ctaPrimaryTextView = (TextView) dialogView.findViewById(R.id.error_primary_cta);


        /**
         * Substitute values
         */
        errorImageView.setImageDrawable(activity.getResources().getDrawable(R.drawable.dp3));
        titleTextView.setText(activity.getString(R.string.dialog_auth_failure_title));
        descriptionTextView.setText(activity.getString(R.string.dialog_auth_failure));
        ctaPrimaryTextView.setText(activity.getString(R.string.action_ok));


        /**
         * Set Actions on CTA - Primary
         */
        ctaPrimaryTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Dismiss Dialog
                dialog.dismiss();

                // Destroy User Session
                SharedPreferenceService.destroyUserSession(activity);

                Intent intent = new Intent(activity, AppOnboardingActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                activity.startActivity(intent);
                activity.finish();
            }
        });


        // Show dialog
        dialog.show();
    }


    /**
     * Function to handle other failure
     * @param activity
     */
    public static void handleError(Activity activity) {

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.dialog_error, null);
        builder.setView(dialogView);
        final AlertDialog dialog = builder.create();

        /**
         * Initialize Data Fields
         */
        ImageView errorImageView = (ImageView) dialogView.findViewById(R.id.error_image);
        TextView titleTextView = (TextView) dialogView.findViewById(R.id.error_title);
        TextView descriptionTextView = (TextView) dialogView.findViewById(R.id.error_description);
        TextView ctaPrimaryTextView = (TextView) dialogView.findViewById(R.id.error_primary_cta);


        /**
         * Substitute values
         */
        errorImageView.setImageDrawable(activity.getResources().getDrawable(R.drawable.dp3));
        titleTextView.setText(activity.getString(R.string.dialog_exception_title));
        descriptionTextView.setText(activity.getString(R.string.dialog_exception));
        ctaPrimaryTextView.setText(activity.getString(R.string.action_ok));


        /**
         * Set Actions on CTA - Primary
         */
        ctaPrimaryTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
            }
        });


        // Show dialog
        dialog.show();
    }


    /**
     * Function to handle email exists but conflict failure
     * @param activity
     */
    public static void handleEmailExistsConflictFailure(final Activity activity) {


        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.dialog_error, null);
        builder.setView(dialogView);
        final AlertDialog dialog = builder.create();

        /**
         * Initialize Data Fields
         */
        ImageView errorImageView = (ImageView) dialogView.findViewById(R.id.error_image);
        TextView titleTextView = (TextView) dialogView.findViewById(R.id.error_title);
        TextView descriptionTextView = (TextView) dialogView.findViewById(R.id.error_description);
        TextView ctaPrimaryTextView = (TextView) dialogView.findViewById(R.id.error_primary_cta);
        CardView ctaSecondaryCardView = (CardView) dialogView.findViewById(R.id.cta_cancel);
        TextView ctaSecondaryTextView = (TextView) dialogView.findViewById(R.id.error_secondary_cta);


        /**
         * Substitute values
         */
        errorImageView.setImageDrawable(activity.getResources().getDrawable(R.drawable.dp3));
        titleTextView.setText(activity.getString(R.string.dialog_exception_title));
        descriptionTextView.setText(activity.getString(R.string.dialog_email_id_exist_exception));
        ctaPrimaryTextView.setText(activity.getString(R.string.action_ok));

        // Enable Forgot Password
        ctaSecondaryTextView.setText(activity.getString(R.string.forgot_password));
        ctaSecondaryCardView.setVisibility(View.VISIBLE);
        ctaSecondaryTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(activity, ForgotPasswordActivity.class);
                activity.startActivity(intent);
                activity.finish();
            }
        });


        /**
         * Set Actions on CTA - Primary
         */
        ctaPrimaryTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
            }
        });


        // Show dialog
        dialog.show();
    }


    /**
     * Function to handle login email password do not match failure
     * @param activity
     */
    public static void handleEmailPasswordNotMatchError(Activity activity) {


        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.dialog_error, null);
        builder.setView(dialogView);
        final AlertDialog dialog = builder.create();

        /**
         * Initialize Data Fields
         */
        ImageView errorImageView = (ImageView) dialogView.findViewById(R.id.error_image);
        TextView titleTextView = (TextView) dialogView.findViewById(R.id.error_title);
        TextView descriptionTextView = (TextView) dialogView.findViewById(R.id.error_description);
        TextView ctaPrimaryTextView = (TextView) dialogView.findViewById(R.id.error_primary_cta);


        /**
         * Substitute values
         */
        errorImageView.setImageDrawable(activity.getResources().getDrawable(R.drawable.dp3));
        titleTextView.setText(activity.getString(R.string.dialog_exception_title));
        descriptionTextView.setText(activity.getString(R.string.dialog_email_password_exception));
        ctaPrimaryTextView.setText(activity.getString(R.string.action_ok));


        /**
         * Set Actions on CTA - Primary
         */
        ctaPrimaryTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
            }
        });


        // Show dialog
        dialog.show();
    }


    /**
     * Function to handle Email not registered failure
     * @param activity
     */
    public static void handleEmailNotRegisteredError(Activity activity) {


        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.dialog_error, null);
        builder.setView(dialogView);
        final AlertDialog dialog = builder.create();

        /**
         * Initialize Data Fields
         */
        ImageView errorImageView = (ImageView) dialogView.findViewById(R.id.error_image);
        TextView titleTextView = (TextView) dialogView.findViewById(R.id.error_title);
        TextView descriptionTextView = (TextView) dialogView.findViewById(R.id.error_description);
        TextView ctaPrimaryTextView = (TextView) dialogView.findViewById(R.id.error_primary_cta);


        /**
         * Substitute values
         */
        errorImageView.setImageDrawable(activity.getResources().getDrawable(R.drawable.dp3));
        titleTextView.setText(activity.getString(R.string.dialog_exception_title));
        descriptionTextView.setText(activity.getString(R.string.dialog_email_not_registered_exception));
        ctaPrimaryTextView.setText(activity.getString(R.string.action_ok));


        /**
         * Set Actions on CTA - Primary
         */
        ctaPrimaryTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
            }
        });


        // Show dialog
        dialog.show();
    }
}
