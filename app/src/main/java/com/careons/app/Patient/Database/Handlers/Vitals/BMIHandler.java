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
import com.careons.app.Patient.Database.Models.Vitals.BMI;
import com.careons.app.Patient.Fragments.Vitals.BMIFragment;
import com.careons.app.R;
import com.careons.app.Shared.Utils.DateTimeUtils;
import com.careons.app.Shared.Utils.VitalUtils;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.util.Calendar;
import java.util.HashMap;

public class BMIHandler implements DatePickerDialog.OnDateSetListener {

    TextView titleTextView, dateTextView;
    EditText heightFtEditText, heightIncEditText, weightEditText;


    /**
     * Function to handle click event on edit
     * @param view
     */
    public void onUpdate(View view) {

        final Context context = view.getContext();

        // Fetch Data
        ImageView updateImageView = (ImageView) view.findViewById(R.id.update_imageView);
        final String bmiId = updateImageView.getTag().toString();
        BMI bmi = BMI.find(BMI.class, "bmi_id = ?" , bmiId).get(0);


        // Initialize dialog
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater =  LayoutInflater.from(context);
        final View dialogView = inflater.inflate(R.layout.dialog_vitals_bmi, null);
        builder.setView(dialogView);
        final AlertDialog dialog = builder.create();

        // Initialize data fields
        TextView saveEntryTextView;
        ImageView cancelEntryImageView;
        titleTextView = (TextView) dialogView.findViewById(R.id.dialog_title);
        heightFtEditText = (EditText) dialogView.findViewById(R.id.height_ft_edittext);
        heightIncEditText = (EditText) dialogView.findViewById(R.id.height_inches_edittext);
        weightEditText = (EditText) dialogView.findViewById(R.id.weight_edittext);
        dateTextView = (TextView) dialogView.findViewById(R.id.date_textview);

        // Substitute values
        titleTextView.setText(context.getResources().getString(R.string.vitals_bmi_update));
        heightFtEditText.setText(bmi.getHeightFt());
        heightIncEditText.setText(bmi.getHeightInc());
        weightEditText.setText(bmi.getWeight());
        dateTextView.setText(bmi.getDate());

        // Attach click listeners
        dateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDatePicker();
            }
        });


        // Save Entry to Vitals
        saveEntryTextView = (TextView) dialogView.findViewById(R.id.save_entry);
        saveEntryTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Check for data
                if(heightFtEditText.getText().toString().trim().length() > 0) {

                    if(heightIncEditText.getText().toString().trim().length() > 0) {

                        if(weightEditText.getText().toString().trim().length() > 0) {

                            // Create Data object
                            HashMap<String, String> data = new HashMap<>();
                            data.put(StringConstants.KEY_BMI_ID, bmiId);
                            data.put(StringConstants.KEY_BMI,  VitalUtils.calculateBMI(
                                    Integer.parseInt(heightFtEditText.getText().toString()),
                                    Integer.parseInt(heightIncEditText.getText().toString()),
                                    Integer.parseInt(weightEditText.getText().toString())));

                            data.put(StringConstants.KEY_TAG, VitalUtils.calculateBMIRangeTag(context,
                                    Float.valueOf(data.get(StringConstants.KEY_BMI))));

                            data.put(StringConstants.KEY_BMI_HEIGHT_FT, heightFtEditText.getText().toString());
                            data.put(StringConstants.KEY_BMI_HEIGHT_INC, heightIncEditText.getText().toString());
                            data.put(StringConstants.KEY_BMI_WEIGHT, weightEditText.getText().toString());
                            data.put(StringConstants.KEY_DATE, dateTextView.getText().toString());
                            data.put(StringConstants.KEY_TIMESTAMP,
                                    String.valueOf(
                                            DateTimeUtils.convertDateToTimestamp(
                                                    data.get(StringConstants.KEY_DATE)
                                                            .concat(" ")
                                                            .concat(DateTimeUtils.getTime(context))
                                            )
                                    )
                            );

                            BMIFragment.updateVitalsEntry(context, data);

                            // Dialog Dismiss
                            dialog.dismiss();

                        } else {

                            // Weight not entered
                            weightEditText.setError(context.getResources().getString(R.string.required));
                        }
                    } else {

                        // Height in inches not entered
                        heightIncEditText.setError(context.getResources().getString(R.string.required));
                    }
                } else {

                    // Height in feet not entered
                    heightFtEditText.setError(context.getResources().getString(R.string.required));
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
        datePickerDialog.show(BMIFragment.getInstance().getActivity().getFragmentManager(), StringConstants.KEY_DATEPICKER_DIALOG);
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
     * Function to handle click event on delete
     * @param view
     */
    public void onDelete(View view) {

        final Context context = view.getContext();

        // Fetch Data
        ImageView deleteImageView = (ImageView) view.findViewById(R.id.delete_imageView);
        final String bmiId = deleteImageView.getTag().toString();


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
                BMIFragment.deleteVitalsEntry(context, bmiId);
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
