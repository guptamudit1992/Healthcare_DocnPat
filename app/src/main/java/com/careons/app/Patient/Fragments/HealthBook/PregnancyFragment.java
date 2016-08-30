package com.careons.app.Patient.Fragments.HealthBook;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.careons.app.Patient.Commons.ServiceUrls;
import com.careons.app.Patient.Commons.StaticConstants;
import com.careons.app.Patient.Commons.StringConstants;
import com.careons.app.Patient.Database.Models.Healthbook.Pregnancy;
import com.careons.app.Shared.Interfaces.APIInterface;
import com.careons.app.R;
import com.careons.app.Patient.Services.APICallService;
import com.careons.app.Patient.Services.SharedPreferenceService;
import com.careons.app.Shared.Utils.DateTimeUtils;
import com.careons.app.Shared.Utils.ErrorHandlers;
import com.careons.app.Shared.Utils.Validation;
import com.crashlytics.android.Crashlytics;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class PregnancyFragment extends Fragment implements DatePickerDialog.OnDateSetListener {

    private static Activity activity;

    private static LinearLayout loader, contentLayout;

    private TextView ageDisp, menstrualCycleDisp,
            textViewDisp1, textViewDisp2, textViewDisp3, textViewDisp4,
            pregnanatDisp, menstrualCycleDateUpdate, mSaveText;
    private LinearLayout diplayLinearLayout, pragInfoDispLinearLayout, pragInfoUpdateLinearLayout, editableLinearLayout;
    private CheckBox pregCheckboxUpdate;
    private EditText ageUpdate, textViewUpdate1, textViewUpdate2, textViewUpdate3, textViewUpdate4;
    private ImageView editImageView, deleteImageView, cancelImageView;


    private static List<Pregnancy> pregnancyList;
    private static Pregnancy pregnancy;


    public static PregnancyFragment newInstance() {
        return new PregnancyFragment();
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_hb_pregnancy, container, false);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize activity
        activity = getActivity();

        // Initialize layouts
        loader = (LinearLayout) view.findViewById(R.id.loader);
        contentLayout = (LinearLayout) view.findViewById(R.id.pregnancy_content);

        // Initialize
        pragInfoDispLinearLayout = (LinearLayout) view.findViewById(R.id.pragInfoDispLinearLayout);
        diplayLinearLayout = (LinearLayout) view.findViewById(R.id.diplayLinearLayout);
        editableLinearLayout = (LinearLayout) view.findViewById(R.id.editableLinearLayout);
        pragInfoUpdateLinearLayout = (LinearLayout) view.findViewById(R.id.pragInfoUpdateLinearLayout);

        // Initialize Display Data Fields
        ageDisp = (TextView) view.findViewById(R.id.ageDisp);
        menstrualCycleDisp = (TextView) view.findViewById(R.id.menstrualCycleDisp);
        textViewDisp1 = (TextView) view.findViewById(R.id.textViewDisp1);
        textViewDisp2 = (TextView) view.findViewById(R.id.textViewDisp2);
        textViewDisp3 = (TextView) view.findViewById(R.id.textViewDisp3);
        textViewDisp4 = (TextView) view.findViewById(R.id.textViewDisp4);
        pregnanatDisp = (TextView) view.findViewById(R.id.pregnanatDisp);


        // Initialize Update Data Fields
        ageUpdate = (EditText) view.findViewById(R.id.ageUpdate);
        menstrualCycleDateUpdate = (TextView) view.findViewById(R.id.menstrualCycleDateUpdate);
        textViewUpdate1 = (EditText) view.findViewById(R.id.textViewUpadte1);
        textViewUpdate2 = (EditText) view.findViewById(R.id.textViewUpdate2);
        textViewUpdate3 = (EditText) view.findViewById(R.id.textViewUpdate3);
        textViewUpdate4 = (EditText) view.findViewById(R.id.textViewUpdate4);
        mSaveText = (TextView) view.findViewById(R.id.mSaveText);

        editImageView = (ImageView) view.findViewById(R.id.editImageView);
        deleteImageView = (ImageView) view.findViewById(R.id.deleteImageView);
        cancelImageView = (ImageView) view.findViewById(R.id.cancelImageView);
        pregCheckboxUpdate = (CheckBox) view.findViewById(R.id.pregCheckboxUpdate);


        // OnFocus Change listener on Age on Onset
        ageUpdate.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                validateAgeOfOnset(ageUpdate);
            }
        });


        // On Focus change listener on start date
        menstrualCycleDateUpdate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    //Validation.validateStartDate(menstrualCycleDateUpdate, getContext());
                }
            }
        });

        //Set Listener
        menstrualCycleDateUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDatePicker();
            }
        });

        pregCheckboxUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pregCheckboxUpdate.isChecked()) {
                    pragInfoUpdateLinearLayout.setVisibility(View.VISIBLE);
                } else {
                    pragInfoUpdateLinearLayout.setVisibility(View.GONE);
                }
            }
        });

        mSaveText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateAgeOfOnset(ageUpdate) && Validation.validateStartDate(menstrualCycleDateUpdate, getContext())) {
                    diplayLinearLayout.setVisibility(View.VISIBLE);
                    editableLinearLayout.setVisibility(View.VISIBLE);
                    mSaveText.setVisibility(View.GONE);
                    editImageView.setVisibility(View.VISIBLE);
                    cancelImageView.setVisibility(View.GONE);
                    savePregnancyData();
                }
            }
        });

        editImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                diplayLinearLayout.setVisibility(View.GONE);
                editImageView.setVisibility(View.INVISIBLE);
                deleteImageView.setVisibility(View.GONE);
                cancelImageView.setVisibility(View.VISIBLE);
                editableLinearLayout.setVisibility(View.VISIBLE);
                mSaveText.setVisibility(View.VISIBLE);
                if (pregCheckboxUpdate.isChecked()) {
                    pragInfoUpdateLinearLayout.setVisibility(View.VISIBLE);
                } else {
                    pragInfoUpdateLinearLayout.setVisibility(View.GONE);
                }
            }
        });

        deleteImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Initialize dialog
                final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                LayoutInflater inflater =  LayoutInflater.from(getContext());
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
                        deletePregnancyRecord();
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
        });

        cancelImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                diplayLinearLayout.setVisibility(View.VISIBLE);
                editImageView.setVisibility(View.VISIBLE);
                deleteImageView.setVisibility(View.VISIBLE);
                cancelImageView.setVisibility(View.GONE);
                editableLinearLayout.setVisibility(View.GONE);
                mSaveText.setVisibility(View.GONE);
                pragInfoUpdateLinearLayout.setVisibility(View.GONE);
            }
        });

        readPregnancyData();
    }











    /**
     * Function to implement date picker
     */
    public void openDatePicker() {

        Calendar now = Calendar.getInstance();
        DatePickerDialog dpd = DatePickerDialog.newInstance(
                PregnancyFragment.this,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );
        dpd.show(getActivity().getFragmentManager(), "Datepickerdialog");
        dpd.setAccentColor(Color.parseColor("#db5860"));
        dpd.showYearPickerFirst(true);
        dpd.setMaxDate(now);
        dpd.setYearRange(StaticConstants.MIN_DATE, now.get(Calendar.YEAR));
    }


    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {

        long timestamp = DateTimeUtils.convertTimeToTimestamp(year, monthOfYear, dayOfMonth, 0, 0);
        menstrualCycleDateUpdate.setText(DateTimeUtils.convertTimestampToDate(timestamp));
    }


    /**
     * Function to validate Age of Onset of Menses
     */
    public boolean validateAgeOfOnset(EditText editText) {

        if(editText.getText().toString().trim().length() > 0) {

            ageUpdate.setError(null);
            return true;

        } else {

            ageUpdate.setError(getString(R.string.required));
        }

        return false;
    }








    /**
     * Function to fetch Pregnancy data
     */
    public void savePregnancyData() {

        /**
         * Function to check Pregnancy Details in local database
         */
        pregnancyList = Pregnancy.find(Pregnancy.class, "patient_id = ?",
                SharedPreferenceService.getValue(getContext(), StringConstants.KEY_PATIENT_ID));

        if (pregnancyList.size() > 0) {

            /**
             * Function to update Pregnancy Details
             */
            updatePregnancyRecord();

        } else {

            /**
             * Function to add Pregnancy Details
             */
            createPregnancyRecord();
        }
    }


    /**
     * Function to create pregnancy record
     */
    public void createPregnancyRecord() {

        if (Validation.isConnected(getContext())) {

            // Set initial state
            loader.setVisibility(View.VISIBLE);
            contentLayout.setVisibility(View.GONE);

            String url = ServiceUrls.KEY_HEALTHBOOK_PREGNANCY.concat(StringConstants.KEY_CREATE);


            HashMap<String, Object> postData = new HashMap<>();
            postData.put(StringConstants.KEY_PATIENT_ID, SharedPreferenceService.getValue(getActivity(), StringConstants.KEY_PATIENT_ID));
            postData.put(StringConstants.KEY_PREGNANCY_MENSES_AGE, ageUpdate.getText().toString());
            postData.put(StringConstants.KEY_PREGNANCY_LMD,
                    DateTimeUtils.convertTimestampToUTC(DateTimeUtils.convertDateSimpleToTimestamp(menstrualCycleDateUpdate.getText().toString())));
            postData.put(StringConstants.KEY_PREGNANCY_IS_EVER_PREGNANCY, String.valueOf(pregCheckboxUpdate.isChecked()));

            if (pregCheckboxUpdate.isChecked()) {

                postData.put(StringConstants.KEY_PREGNANCY_NO_OF_FTP, textViewUpdate1.getText().toString());
                postData.put(StringConstants.KEY_PREGNANCY_NO_OF_PTP, textViewUpdate2.getText().toString());
                postData.put(StringConstants.KEY_PREGNANCY_NO_OF_ABORTIONS, textViewUpdate3.getText().toString());
                postData.put(StringConstants.KEY_PREGNANCY_NO_OF_LIVING_CHILDREN, textViewUpdate4.getText().toString());

            } else {

                postData.put(StringConstants.KEY_PREGNANCY_NO_OF_FTP, null);
                postData.put(StringConstants.KEY_PREGNANCY_NO_OF_PTP, null);
                postData.put(StringConstants.KEY_PREGNANCY_NO_OF_ABORTIONS, null);
                postData.put(StringConstants.KEY_PREGNANCY_NO_OF_LIVING_CHILDREN, null);

                textViewUpdate1.setText("");
                textViewUpdate2.setText("");
                textViewUpdate3.setText("");
                textViewUpdate4.setText("");

            }


            // Set Headers
            HashMap<String, String> headers = new HashMap<>();
            headers.put(StaticConstants.KEY_AUTHORIZATION,
                    StaticConstants.KEY_BEARER.concat(" ")
                            .concat(SharedPreferenceService.getValue(getContext(), StringConstants.KEY_TOKEN)));


            APICallService.PostAPICall(activity, getContext(), url, postData, headers, new APIInterface() {

                @Override
                public void onSuccess(JSONObject response) {
                    try {

                        /**
                         * Save to local database
                         */
                        Pregnancy pregnancyRecord = new Pregnancy(
                                SharedPreferenceService.getValue(getContext(), StringConstants.KEY_PATIENT_ID),
                                response.getString(StringConstants.KEY_P_ID),
                                ageUpdate.getText().toString(),
                                menstrualCycleDateUpdate.getText().toString(),
                                pregCheckboxUpdate.isChecked(),
                                textViewUpdate1.getText().toString(),
                                textViewUpdate2.getText().toString(),
                                textViewUpdate3.getText().toString(),
                                textViewUpdate4.getText().toString()
                        );

                        pregnancyRecord.save();


                        // Read UI
                        readPregnancyData();

                    } catch (Exception e) {

                        // Show Content
                        loader.setVisibility(View.GONE);
                        contentLayout.setVisibility(View.VISIBLE);

                        // Log error
                        ErrorHandlers.handleError(activity);
                        Crashlytics.logException(e);
                    }
                }

                @Override
                public void onError(VolleyError error) {

                    // Show Content
                    loader.setVisibility(View.GONE);
                    contentLayout.setVisibility(View.VISIBLE);
                }
            });

        } else {

            // Internet Not Available
            ErrorHandlers.handleInternetConnectionFailure(activity);
        }
    }


    /**
     * Function to delete pregnancy record
     */
    public void deletePregnancyRecord() {

        if (Validation.isConnected(getContext())) {

            // Set initial state
            loader.setVisibility(View.VISIBLE);
            contentLayout.setVisibility(View.GONE);

            String url = ServiceUrls.KEY_HEALTHBOOK_PREGNANCY.concat(StringConstants.KEY_DELETE).concat(StringConstants.KEY_SEPARATOR)
                    .concat(SharedPreferenceService.getValue(getActivity(), StringConstants.KEY_PATIENT_ID));;



            HashMap<String, Object> postData = new HashMap<>();
            postData.put(StringConstants.KEY_PATIENT_ID, SharedPreferenceService.getValue(getActivity(), StringConstants.KEY_PATIENT_ID));



            // Set Headers
            HashMap<String, String> headers = new HashMap<>();
            headers.put(StaticConstants.KEY_AUTHORIZATION,
                    StaticConstants.KEY_BEARER.concat(" ")
                            .concat(SharedPreferenceService.getValue(getContext(), StringConstants.KEY_TOKEN)));

            // API Call
            APICallService.DeleteAPICall(activity, getContext(), url, headers, new APIInterface() {
                @Override
                public void onSuccess(JSONObject response) {

                    try {
                        ageUpdate.setText("");
                        menstrualCycleDateUpdate.setText("");
                        pregCheckboxUpdate.setChecked(false);
                        pragInfoUpdateLinearLayout.setVisibility(View.GONE);
                        textViewUpdate1.setText("");
                        textViewUpdate2.setText("");
                        textViewUpdate3.setText("");
                        textViewUpdate4.setText("");
                        /**
                         * Delete to local database
                         */
                        Pregnancy pregnancyRecord =
                                Pregnancy.find(Pregnancy.class, "p_id = ?", pregnancy.getpId()).get(0);

                        pregnancyRecord.delete();
                        // Read UI
                        readPregnancyData();

                    } catch (Exception e) {

                        // Show Content
                        loader.setVisibility(View.GONE);
                        contentLayout.setVisibility(View.VISIBLE);

                        // Log error
                        ErrorHandlers.handleAPIError(activity);
                        Crashlytics.logException(e);
                    }
                }

                @Override
                public void onError(VolleyError error) {

                    loader.setVisibility(View.GONE);
                    contentLayout.setVisibility(View.VISIBLE);

                    // Log error
                    ErrorHandlers.handleError(activity);
                    Crashlytics.logException(error);
                }
            });
        } else {

            // Internet Not Available
            ErrorHandlers.handleInternetConnectionFailure(activity);
        }
    }



    /**
     * Function to fetch Pregnancy data
     */
    public void readPregnancyData() {

        // Read from database table
        pregnancyList = Pregnancy.find(Pregnancy.class, "patient_id = ?",
                SharedPreferenceService.getValue(getContext(), StringConstants.KEY_PATIENT_ID));

        if (pregnancyList.size() > 0) {

            pregnancy = pregnancyList.get(0);

            editImageView.setVisibility(View.VISIBLE);
            deleteImageView.setVisibility(View.VISIBLE);
            diplayLinearLayout.setVisibility(View.VISIBLE);
            editableLinearLayout.setVisibility(View.GONE);
            mSaveText.setVisibility(View.GONE);


            /**
             * Substitute values in Display Section
             */

            ageDisp.setText(pregnancy.getAgeOfMenses());
            menstrualCycleDisp.setText(pregnancy.getDateOfLastMensesCycle());
            pregnanatDisp.setText(pregnancy.isHaveYouPregnant() ? getString(R.string.yes) : getString(R.string.no));

            if (pregnancy.isHaveYouPregnant()) {

                textViewDisp1.setText(pregnancy.getNoOfFullTermPregnant());
                textViewDisp2.setText(pregnancy.getNoOfPreTermPregnant());
                textViewDisp3.setText(pregnancy.getNoOfAbortions());
                textViewDisp4.setText(pregnancy.getNoOfLivingChildren());
                pragInfoDispLinearLayout.setVisibility(View.VISIBLE);

            } else {

                textViewDisp1.setText("");
                textViewDisp2.setText("");
                textViewDisp3.setText("");
                textViewDisp4.setText("");
                pragInfoDispLinearLayout.setVisibility(View.GONE);
            }


            /**
             * Substitute values in Update Section
             */

            ageUpdate.setText(pregnancy.getAgeOfMenses());
            menstrualCycleDateUpdate.setText(pregnancy.getDateOfLastMensesCycle());
            pregCheckboxUpdate.setChecked(pregnancy.isHaveYouPregnant());

            if (pregCheckboxUpdate.isChecked()) {

                textViewUpdate1.setText(pregnancy.getNoOfFullTermPregnant());
                textViewUpdate2.setText(pregnancy.getNoOfPreTermPregnant());
                textViewUpdate3.setText(pregnancy.getNoOfAbortions());
                textViewUpdate4.setText(pregnancy.getNoOfLivingChildren());
                pragInfoDispLinearLayout.setVisibility(View.VISIBLE);

            } else {

                textViewUpdate1.setText("");
                textViewUpdate2.setText("");
                textViewUpdate3.setText("");
                textViewUpdate4.setText("");
                pragInfoDispLinearLayout.setVisibility(View.GONE);
            }


        } else {

            editImageView.setVisibility(View.INVISIBLE);
            deleteImageView.setVisibility(View.INVISIBLE);
            diplayLinearLayout.setVisibility(View.GONE);
            editableLinearLayout.setVisibility(View.VISIBLE);
            mSaveText.setVisibility(View.VISIBLE);
        }

        // Show Content
        loader.setVisibility(View.GONE);
        contentLayout.setVisibility(View.VISIBLE);
    }


    /**
     * Function to update pregnancy record
     */
    public void updatePregnancyRecord() {


        if (Validation.isConnected(getContext())) {

            // Set initial state
            loader.setVisibility(View.VISIBLE);
            contentLayout.setVisibility(View.GONE);

            String url = ServiceUrls.KEY_HEALTHBOOK_PREGNANCY.concat(StringConstants.KEY_UPDATE).concat(pregnancy.getpId());

            HashMap<String, Object> postData = new HashMap<>();
            postData.put(StringConstants.KEY_PATIENT_ID, SharedPreferenceService.getValue(getActivity(), StringConstants.KEY_PATIENT_ID));
            postData.put(StringConstants.KEY_PREGNANCY_MENSES_AGE, ageUpdate.getText().toString());
            postData.put(StringConstants.KEY_PREGNANCY_LMD,
                    DateTimeUtils.convertTimestampToUTC(DateTimeUtils.convertDateSimpleToTimestamp(menstrualCycleDateUpdate.getText().toString())));
            postData.put(StringConstants.KEY_PREGNANCY_IS_EVER_PREGNANCY, String.valueOf(pregCheckboxUpdate.isChecked()));

            if (pregCheckboxUpdate.isChecked()) {

                postData.put(StringConstants.KEY_PREGNANCY_NO_OF_FTP, textViewUpdate1.getText().toString());
                postData.put(StringConstants.KEY_PREGNANCY_NO_OF_PTP, textViewUpdate2.getText().toString());
                postData.put(StringConstants.KEY_PREGNANCY_NO_OF_ABORTIONS, textViewUpdate3.getText().toString());
                postData.put(StringConstants.KEY_PREGNANCY_NO_OF_LIVING_CHILDREN, textViewUpdate4.getText().toString());

            } else {

                postData.put(StringConstants.KEY_PREGNANCY_NO_OF_FTP, null);
                postData.put(StringConstants.KEY_PREGNANCY_NO_OF_PTP, null);
                postData.put(StringConstants.KEY_PREGNANCY_NO_OF_ABORTIONS, null);
                postData.put(StringConstants.KEY_PREGNANCY_NO_OF_LIVING_CHILDREN, null);

                textViewUpdate1.setText("");
                textViewUpdate2.setText("");
                textViewUpdate3.setText("");
                textViewUpdate4.setText("");
            }

            System.out.println("Check Body - " + new JSONObject(postData));

            // Set Headers
            HashMap<String, String> headers = new HashMap<>();
            headers.put(StaticConstants.KEY_AUTHORIZATION,
                    StaticConstants.KEY_BEARER.concat(" ")
                            .concat(SharedPreferenceService.getValue(getContext(), StringConstants.KEY_TOKEN)));


            APICallService.PutAPICall(activity, getContext(), url, postData, headers, new APIInterface() {

                @Override
                public void onSuccess(JSONObject response) {
                    try {

                        /**
                         * Update to local database
                         */
                        Pregnancy pregnancyRecord =
                                Pregnancy.find(Pregnancy.class, "p_id = ?", pregnancy.getpId()).get(0);

                        pregnancyRecord.setAgeOfMenses(ageUpdate.getText().toString());
                        pregnancyRecord.setDateOfLastMensesCycle(menstrualCycleDateUpdate.getText().toString());
                        pregnancyRecord.setHaveYouPregnant(pregCheckboxUpdate.isChecked());
                        pregnancyRecord.setNoOfFullTermPregnant(textViewUpdate1.getText().toString());
                        pregnancyRecord.setNoOfPreTermPregnant(textViewUpdate2.getText().toString());
                        pregnancyRecord.setNoOfAbortions(textViewUpdate3.getText().toString());
                        pregnancyRecord.setNoOfLivingChildren(textViewUpdate4.getText().toString());

                        pregnancyRecord.save();

                        // Update UI
                        readPregnancyData();


                    } catch (Exception e) {

                        // Show Content
                        loader.setVisibility(View.GONE);
                        contentLayout.setVisibility(View.VISIBLE);

                        // Log error
                        ErrorHandlers.handleError(activity);
                        Crashlytics.logException(e);
                    }
                }

                @Override
                public void onError(VolleyError error) {

                    // Show Content
                    loader.setVisibility(View.GONE);
                    contentLayout.setVisibility(View.VISIBLE);
                }
            });

        } else {

            // Internet Not Available
            ErrorHandlers.handleInternetConnectionFailure(activity);
        }
    }
}