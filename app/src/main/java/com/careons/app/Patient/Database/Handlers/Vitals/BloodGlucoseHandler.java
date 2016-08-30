package com.careons.app.Patient.Database.Handlers.Vitals;

import android.app.AlertDialog;
import android.content.Context;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.careons.app.Patient.Commons.StaticConstants;
import com.careons.app.Patient.Commons.StringConstants;
import com.careons.app.Patient.Database.Models.Vitals.BloodGlucose;
import com.careons.app.Patient.Fragments.Vitals.BGFragment;
import com.careons.app.R;
import com.careons.app.Shared.Utils.DateTimeUtils;
import com.careons.app.Shared.Utils.VitalUtils;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.util.Calendar;
import java.util.HashMap;

public class BloodGlucoseHandler
        implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    TextView titleTextView, dateTextView, timeTextView, checkupRandom, checkupFasting, checkupPostMeal;
    EditText bloodGlucoseEditText;

    private static Context context;
    private static String checkupSelected;


    /**
     * Function to handle click event on edit
     * @param view
     */
    public void onUpdate(View view) {

        context = view.getContext();

        // Fetch Data
        ImageView updateImageView = (ImageView) view.findViewById(R.id.update_imageView);
        final String bgId = updateImageView.getTag().toString();
        BloodGlucose bloodGlucose = BloodGlucose.find(BloodGlucose.class, "bg_id = ?" , bgId).get(0);


        // Initialize dialog
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater =  LayoutInflater.from(context);
        final View dialogView = inflater.inflate(R.layout.dialog_vitals_bg, null);
        builder.setView(dialogView);
        final AlertDialog dialog = builder.create();


        // Initialize data fields
        TextView saveEntryTextView;
        ImageView cancelEntryImageView;
        titleTextView = (TextView) dialogView.findViewById(R.id.dialog_title);
        bloodGlucoseEditText = (EditText) dialogView.findViewById(R.id.bg_edittext);
        dateTextView = (TextView) dialogView.findViewById(R.id.date_textview);
        timeTextView = (TextView) dialogView.findViewById(R.id.time_textview);
        checkupRandom = (TextView) dialogView.findViewById(R.id.checkup_random);
        checkupFasting = (TextView) dialogView.findViewById(R.id.checkup_fasting);
        checkupPostMeal = (TextView) dialogView.findViewById(R.id.checkup_post_meal);

        // Substitute values
        titleTextView.setText(context.getResources().getString(R.string.vitals_bg_update));
        bloodGlucoseEditText.setText(bloodGlucose.getBloodGlucose());
        dateTextView.setText(bloodGlucose.getDate());
        timeTextView.setText(bloodGlucose.getTime());

        if(bloodGlucose.getCheckup().equalsIgnoreCase(context.getString(R.string.random))) {
            unSelectDialogCheckup();
            selectDialogCheckup(checkupRandom);
            checkupSelected = context.getString(R.string.random);
            ((TextView) dialogView.findViewById(R.id.selector_desc)).setText(context.getString(R.string.random_desc));
        } else  if(bloodGlucose.getCheckup().equalsIgnoreCase(context.getString(R.string.pre_meal))) {
            unSelectDialogCheckup();
            selectDialogCheckup(checkupFasting);
            checkupSelected = context.getString(R.string.pre_meal);
            ((TextView) dialogView.findViewById(R.id.selector_desc)).setText(context.getString(R.string.pre_meal_desc));
        } else {
            unSelectDialogCheckup();
            selectDialogCheckup(checkupPostMeal);
            checkupSelected = context.getString(R.string.post_meal);
            ((TextView) dialogView.findViewById(R.id.selector_desc)).setText(context.getString(R.string.post_meal_desc));
        }

        checkupRandom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                unSelectDialogCheckup();
                selectDialogCheckup(checkupRandom);
                checkupSelected = context.getString(R.string.random);
                ((TextView) dialogView.findViewById(R.id.selector_desc)).setText(context.getString(R.string.random_desc));
            }
        });

        checkupFasting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                unSelectDialogCheckup();
                selectDialogCheckup(checkupFasting);
                checkupSelected = context.getString(R.string.pre_meal);
                ((TextView) dialogView.findViewById(R.id.selector_desc)).setText(context.getString(R.string.pre_meal_desc));
            }
        });

        checkupPostMeal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                unSelectDialogCheckup();
                selectDialogCheckup(checkupPostMeal);
                checkupSelected = context.getString(R.string.post_meal);
                ((TextView) dialogView.findViewById(R.id.selector_desc)).setText(context.getString(R.string.post_meal_desc));
            }
        });


        // Attach click listeners
        dateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDatePicker();
            }
        });

        timeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openTimePicker();
            }
        });

        // Save Entry to Vitals
        saveEntryTextView = (TextView) dialogView.findViewById(R.id.save_entry);
        saveEntryTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if(bloodGlucoseEditText.getText().toString().isEmpty()) {

                    // Systolic BP not entered
                    bloodGlucoseEditText.setError(dialogView.getResources().getString(R.string.required));

                } else {

                    // Create vitals entry
                    HashMap<String, String> data = new HashMap();
                    data.put(StringConstants.KEY_BG_ID, bgId);
                    data.put(StringConstants.KEY_BLOOD_GLUCOSE, bloodGlucoseEditText.getText().toString());
                    data.put(StringConstants.KEY_CHECKUP, checkupSelected);
                    data.put(StringConstants.KEY_TAG, VitalUtils.calculateBGRangeTag(context,
                            Integer.parseInt(data.get(StringConstants.KEY_BLOOD_GLUCOSE)),
                            checkupSelected));

                    data.put(StringConstants.KEY_DATE, dateTextView.getText().toString());
                    data.put(StringConstants.KEY_TIME, timeTextView.getText().toString());
                    data.put(StringConstants.KEY_TIMESTAMP,
                            String.valueOf(
                                    DateTimeUtils.convertDateToTimestamp(
                                            data.get(StringConstants.KEY_DATE)
                                                    .concat(" ")
                                                    .concat(data.get(StringConstants.KEY_TIME))
                                    )
                            )
                    );
                    BGFragment.updateVitalsEntry(context, data);

                    // Dismiss dialog
                    dialog.dismiss();
                }
            }
        });

        // Cancel
        cancelEntryImageView = (ImageView) dialogView.findViewById(R.id.cancel);
        cancelEntryImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }


    /**
     * Function to implement date picker
     */
    public void openDatePicker() {

        Calendar now = Calendar.getInstance();
        DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(
                this,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );

        datePickerDialog.setMaxDate(now);
        datePickerDialog.setYearRange(StaticConstants.MIN_DATE, now.get(Calendar.YEAR));
        datePickerDialog.show(BGFragment.getInstance().getActivity().getFragmentManager(), StringConstants.KEY_DATEPICKER_DIALOG);
        datePickerDialog.showYearPickerFirst(true);
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {

        // Convert to timestamp
        long timestamp = DateTimeUtils.convertTimeToTimestamp(year, monthOfYear, dayOfMonth, 0, 0);

        // Convert timestamp to Date
        dateTextView.setText(DateTimeUtils.convertTimestampToDate(timestamp));
    }

    /**
     * Function to implement time picker
     */
    public void openTimePicker() {

        Calendar now = Calendar.getInstance();
        TimePickerDialog timePickerDialog = TimePickerDialog.newInstance(
                this,
                now.get(Calendar.HOUR_OF_DAY),
                now.get(Calendar.MINUTE),
                false
        );

        timePickerDialog.show(BGFragment.getInstance().getActivity().getFragmentManager(), StringConstants.KEY_TIMEPICKER_DIALOG);
    }

    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute, int second) {

        // Convert to timestamp
        long timestamp = DateTimeUtils.convertTimeToTimestamp(0, 0, 0, hourOfDay, minute);
        String displayTime;
        String hour, min;


        // Check minute display
        if(minute < 10) {
            min = "0" + String.valueOf(minute);
        } else {
            min = String.valueOf(minute);
        }

        // Check for am/pm
        if(hourOfDay > 12) {

            if((hourOfDay - 12) < 10) {
                displayTime = "0" + (hourOfDay-12) + ":" + min + ":00 " + context.getString(R.string.pm);
            } else {
                displayTime = (hourOfDay-12) + ":" + min + ":00 " + context.getString(R.string.pm);
            }

        } else {

            if(hourOfDay < 10) {
                displayTime = "0" + hourOfDay + ":" + min + ":00 " + context.getString(R.string.am);
            } else {
                displayTime = hourOfDay + ":" + min + ":00 " + context.getString(R.string.am);
            }
        }

        // Convert timestamp to Time
        timeTextView.setText(displayTime);
    }

    /**
     * Function to unselect checkup options in dialog box
     */
    public void unSelectDialogCheckup() {

        checkupRandom.setBackgroundColor(context.getResources().getColor(R.color.colorPrimary));
        checkupRandom.setTextColor(context.getResources().getColor(R.color.white));
        checkupFasting.setBackgroundColor(context.getResources().getColor(R.color.colorPrimary));
        checkupFasting.setTextColor(context.getResources().getColor(R.color.white));
        checkupPostMeal.setBackgroundColor(context.getResources().getColor(R.color.colorPrimary));
        checkupPostMeal.setTextColor(context.getResources().getColor(R.color.white));
    }

    /**
     * Function to unselect checkup options in dialog box
     */
    public void selectDialogCheckup(TextView textView) {

        textView.setBackgroundColor(context.getResources().getColor(R.color.white));
        textView.setTextColor(context.getResources().getColor(R.color.colorPrimary));
    }







    /**
     * Function to handle click event on delete
     * @param view
     */
    public void onDelete(View view) {

        final Context context = view.getContext();

        // Fetch Data
        ImageView deleteImageView = (ImageView) view.findViewById(R.id.delete_imageView);
        final String bgId = deleteImageView.getTag().toString();


        // Initialize dialog
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater =  LayoutInflater.from(context);
        final View dialogView = inflater.inflate(R.layout.dialog_delete, null);
        builder.setView(dialogView);
        final AlertDialog dialog = builder.create();
        dialog.show();


        CardView acceptCardView = (CardView) dialogView.findViewById(R.id.cta_accept);
        CardView cancelCardView = (CardView) dialogView.findViewById(R.id.cta_cancel);
        ImageView cancelImage = (ImageView) dialogView.findViewById(R.id.cancel);

        // Delete entry
        acceptCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Call Delete API
                BGFragment.deleteVitalsEntry(context, bgId);
                dialog.dismiss();
            }
        });


        // Cancel
        cancelCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
            }
        });

        cancelImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
            }
        });
    }
}
