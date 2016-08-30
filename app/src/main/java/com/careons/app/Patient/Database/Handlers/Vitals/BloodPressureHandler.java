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
import com.careons.app.Patient.Database.Models.Vitals.BloodPressure;
import com.careons.app.Patient.Fragments.Vitals.BPFragment;
import com.careons.app.R;
import com.careons.app.Shared.Utils.DateTimeUtils;
import com.careons.app.Shared.Utils.VitalUtils;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.util.Calendar;
import java.util.HashMap;

public class BloodPressureHandler
        implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    TextView titleTextView, dateTextView, timeTextView;
    EditText systolicEdittext, diastolicEditText;

    private static Context context;


    /**
     * Function to handle click event on edit
     * @param view
     */
    public void onUpdate(View view) {

        context = view.getContext();

        // Fetch Data
        ImageView updateImageView = (ImageView) view.findViewById(R.id.update_imageView);
        final String bpId = updateImageView.getTag().toString();
        BloodPressure bloodPressure = BloodPressure.find(BloodPressure.class, "bp_id = ?" , bpId).get(0);

        // Initialize dialog
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater =  LayoutInflater.from(context);
        final View dialogView = inflater.inflate(R.layout.dialog_vitals_bp, null);
        builder.setView(dialogView);
        final AlertDialog dialog = builder.create();

        // Initialize data fields
        TextView saveEntryTextView;
        ImageView cancelEntryImageView;
        titleTextView = (TextView) dialogView.findViewById(R.id.dialog_title);
        systolicEdittext = (EditText) dialogView.findViewById(R.id.systolic_edittext);
        diastolicEditText = (EditText) dialogView.findViewById(R.id.diastolic_edittext);
        dateTextView = (TextView) dialogView.findViewById(R.id.date_textview);
        timeTextView = (TextView) dialogView.findViewById(R.id.time_textview);

        // Substitute values
        titleTextView.setText(context.getResources().getString(R.string.vitals_bp_update));
        systolicEdittext.setText(bloodPressure.getSystolicBp());
        diastolicEditText.setText(bloodPressure.getDiastolicBp());
        dateTextView.setText(bloodPressure.getDate());
        timeTextView.setText(bloodPressure.getTime());

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

                if(systolicEdittext.getText().toString().isEmpty()) {

                    // Systolic BP not entered
                    systolicEdittext.setError(context.getResources().getString(R.string.required));

                } else if (diastolicEditText.getText().toString().isEmpty()) {

                    // Diastolic BP not entered
                    diastolicEditText.setError(context.getResources().getString(R.string.required));

                } else {

                    // Create vitals entry
                    HashMap<String, String> data = new HashMap();
                    data.put(StringConstants.KEY_BP_ID, bpId);
                    data.put(StringConstants.KEY_SYSTOLIC_BP, systolicEdittext.getText().toString());
                    data.put(StringConstants.KEY_DIASTOLIC_BP, diastolicEditText.getText().toString());
                    data.put(StringConstants.KEY_TAG,
                            VitalUtils.calculateBPRangeTag(
                                    context,
                                    Integer.parseInt(systolicEdittext.getText().toString()),
                                    Integer.parseInt(diastolicEditText.getText().toString())));

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

                    BPFragment.updateVitalsEntry(context, data);

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
        datePickerDialog.show(BPFragment.getInstance().getActivity().getFragmentManager(), StringConstants.KEY_DATEPICKER_DIALOG);
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

        timePickerDialog.show(BPFragment.getInstance().getActivity().getFragmentManager(), StringConstants.KEY_TIMEPICKER_DIALOG);
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
     * Function to handle click event on delete
     * @param view
     */
    public void onDelete(View view) {

        final Context context = view.getContext();

        // Fetch Data
        ImageView deleteImageView = (ImageView) view.findViewById(R.id.delete_imageView);
        final String bpId = deleteImageView.getTag().toString();


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
                BPFragment.deleteVitalsEntry(context, bpId);
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

