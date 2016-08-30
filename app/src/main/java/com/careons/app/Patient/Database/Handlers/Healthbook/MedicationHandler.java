package com.careons.app.Patient.Database.Handlers.Healthbook;

import android.app.AlertDialog;
import android.content.Context;
import android.databinding.ViewDataBinding;
import android.os.Build;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.careons.app.Patient.Commons.StaticConstants;
import com.careons.app.Patient.Commons.StringConstants;
import com.careons.app.Patient.Database.Models.Healthbook.Medication;
import com.careons.app.Patient.Fragments.HealthBook.MedicationFragment;
import com.careons.app.R;
import com.careons.app.Patient.Services.SharedPreferenceService;
import com.careons.app.Shared.Utils.DateTimeUtils;
import com.careons.app.Shared.Utils.Validation;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class MedicationHandler
        implements DatePickerDialog.OnDateSetListener, View.OnClickListener {

    private Context context;
    private Medication medication;
    private ViewDataBinding dataCard;

    private LinearLayout loader, cardContent, viewContent, updateContent,
            startDateDispLinearLayout, endDateDispLinearLayout, durationDispLinearLayout,
            editableStartDateLinearLayout, editableEndDateLinearLayout, editableDurationLinearLayout;
    private Toolbar toolbar;
    private CheckBox contiMedicationUpdate;
    private TextView start_date_textview, end_date_textview, commentsTextView;
    private Spinner units_spinner, doses_spinner, frequency_spinner, durationSpinner;
    private EditText updateComments;

    private boolean isDateFirstclicked = true;
    private int rrr = 0;
    long lastTouchTime = -1;

    // Constructor
    public MedicationHandler(final Medication medication, final ViewDataBinding dataCard) {

        this.medication = medication;
        this.dataCard = dataCard;

        // Get context
        context = dataCard.getRoot().getContext();

        // Initialize card states
        cardContent = (LinearLayout) dataCard.getRoot().findViewById(R.id.card_content);
        viewContent = (LinearLayout) dataCard.getRoot().findViewById(R.id.view_content);
        updateContent = (LinearLayout) dataCard.getRoot().findViewById(R.id.update_content);
        loader = (LinearLayout) dataCard.getRoot().findViewById(R.id.loader);
        startDateDispLinearLayout = (LinearLayout) dataCard.getRoot().findViewById(R.id.startDateDispLinearLayout);
        endDateDispLinearLayout = (LinearLayout) dataCard.getRoot().findViewById(R.id.endDateDispLinearLayout);
        durationDispLinearLayout = (LinearLayout) dataCard.getRoot().findViewById(R.id.durationDispLinearLayout);
        editableStartDateLinearLayout = (LinearLayout) dataCard.getRoot().findViewById(R.id.editableStartDateLinearLayout);
        editableEndDateLinearLayout = (LinearLayout) dataCard.getRoot().findViewById(R.id.editableEndDateLinearLayout);
        editableDurationLinearLayout = (LinearLayout) dataCard.getRoot().findViewById(R.id.editableDurationLinearLayout);

        // Initialize Date fields
        start_date_textview = (TextView) dataCard.getRoot().findViewById(R.id.start_date_textview);
        end_date_textview = (TextView) dataCard.getRoot().findViewById(R.id.end_date_textview);
        commentsTextView = (TextView) dataCard.getRoot().findViewById(R.id.commentsTextView);
        contiMedicationUpdate = (CheckBox) dataCard.getRoot().findViewById(R.id.contiMedicationUpdate);
        units_spinner = (Spinner) dataCard.getRoot().findViewById(R.id.units_spinner);
        doses_spinner = (Spinner) dataCard.getRoot().findViewById(R.id.doses_spinner);
        frequency_spinner = (Spinner) dataCard.getRoot().findViewById(R.id.frequency_spinner);
        durationSpinner = (Spinner) dataCard.getRoot().findViewById(R.id.duration_spinner);

        updateComments = (EditText) dataCard.getRoot().findViewById(R.id.commentsUpdate);
        // On Focus change listener on username
        updateComments.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                MedicationFragment.setFABVisibility(!hasFocus);

                if (!hasFocus) {
                    // Shut off keypad
                    InputMethodManager imm = (InputMethodManager) dataCard.getRoot().getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(updateComments.getWindowToken(), 0);
                }
            }
        });


        // On Focus change listener on start date
        start_date_textview.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    Validation.validateDate(start_date_textview, dataCard.getRoot().getContext());
                }
            }
        });

        // On Focus change listener on end date
        end_date_textview.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    Validation.validateDate(end_date_textview, dataCard.getRoot().getContext());
                }
            }
        });

        if (Validation.isEmpty(medication.getComments())) {
            commentsTextView.setVisibility(View.GONE);
        } else {
            commentsTextView.setVisibility(View.VISIBLE);
        }

        /**
         * Check if still continuing medication
         */
        manageIsSufferingDisplay(medication.isContinuingMedication());


        // Set Listener
        start_date_textview.setOnClickListener(this);
        end_date_textview.setOnClickListener(this);
        contiMedicationUpdate.setOnClickListener(this);

        // Bind duration spinner
        bindSpinner();



        /**
         *  Manage Card States
         */
        manageCardInitialState();


        // Inflate Menu
        toolbar = (Toolbar) dataCard.getRoot().findViewById(R.id.card_toolbar);
        toolbar.getMenu().clear();
        toolbar.inflateMenu(R.menu.menu_card);


        /**
         * Show Dropdown Menu
         */
        showCardDropdownMenu();
        // Menu icon click listener
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {

                switch (menuItem.getItemId()) {

                    case R.id.action_expand:
                        toggleCardStates();
                        break;

                    case R.id.action_edit:
                        showUpdateContentCard();
                        showCardCancelMenu();
                        break;

                    case R.id.action_delete:
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
                                MedicationFragment.deleteMedicationHistory(context, String.valueOf(medication.getpId()));
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

                        break;

                    case R.id.action_cancel:
                        //  Updated = True -> Show View Card and Edit Menu
                        showViewContentCard();
                        showCardEditMenu();
                        break;

                    default:
                        break;
                }
                return true;
            }
        });

        cardContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                rrr++;
                long thisTime = System.currentTimeMillis();
                if (rrr == 3 && thisTime - lastTouchTime < 1500) {
                    if (thisTime - lastTouchTime < 2000) {
                        // Double click
                        lastTouchTime = -1;
                        rrr = 0;
                        try {
                            Toast.makeText(dataCard.getRoot().getContext(), dataCard.getRoot().getContext().getString(R.string.click_on_edit_text), Toast.LENGTH_LONG).show();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        // too slow
                        rrr = 0;
                        lastTouchTime = -1;
                    }
                } else if (rrr == 1) {
                    lastTouchTime = thisTime;
                } else if (rrr > 4) {
                    rrr = 0;
                    lastTouchTime = thisTime;
                }


            }
        });

    }

















    /**
     * Function to manage is suffering flag
     */
    public void manageIsSufferingDisplay(boolean isSuffering) {

        /**
         * Check if still suffering
         */
        if(isSuffering) {

            // True -> Show Duration, Hide Start and End Date Layout
            durationDispLinearLayout.setVisibility(View.VISIBLE);
            startDateDispLinearLayout.setVisibility(View.GONE);
            endDateDispLinearLayout.setVisibility(View.GONE);

            editableDurationLinearLayout.setVisibility(View.VISIBLE);
            editableStartDateLinearLayout.setVisibility(View.GONE);
            editableEndDateLinearLayout.setVisibility(View.GONE);

        } else {

            // False -> Hide Duration, Show Start and End Date Layout
            durationDispLinearLayout.setVisibility(View.GONE);
            startDateDispLinearLayout.setVisibility(View.VISIBLE);
            endDateDispLinearLayout.setVisibility(View.VISIBLE);

            editableDurationLinearLayout.setVisibility(View.GONE);
            editableStartDateLinearLayout.setVisibility(View.VISIBLE);
            editableEndDateLinearLayout.setVisibility(View.VISIBLE);
        }

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.start_date_textview:
                isDateFirstclicked = true;
                openDatePicker();
                break;

            case R.id.end_date_textview:
                isDateFirstclicked = false;
                openDatePicker();
                break;

            case R.id.contiMedicationUpdate:
                if (contiMedicationUpdate.isChecked()) {
                    editableDurationLinearLayout.setVisibility(View.VISIBLE);
                    editableStartDateLinearLayout.setVisibility(View.GONE);
                    editableEndDateLinearLayout.setVisibility(View.GONE);
                } else {
                    editableDurationLinearLayout.setVisibility(View.GONE);
                    editableStartDateLinearLayout.setVisibility(View.VISIBLE);
                    editableEndDateLinearLayout.setVisibility(View.VISIBLE);
                }
                break;

            default:
                break;
        }
    }


    /**
     * Function to toggle card content view on click
     *
     * @param view
     */
    public void toggleContent(View view) {
        // Toggle card states
        toggleCardStates();
    }


    /**
     * Function to save card content on click
     *
     * @param view
     */
    public void save(View view) {
        if (contiMedicationUpdate.isChecked()) {
            if (Validation.isSelectDose(dataCard.getRoot().getContext(), doses_spinner)
                    && Validation.isSelectUnit(dataCard.getRoot().getContext(), units_spinner)
                    && Validation.isSelectFrequency(dataCard.getRoot().getContext(), frequency_spinner)
                    && Validation.isSelectDuration(dataCard.getRoot().getContext(),durationSpinner)) {

                saveData(view);
            }
        } else if (Validation.isSelectDose(dataCard.getRoot().getContext(), doses_spinner)
                && Validation.isSelectUnit(dataCard.getRoot().getContext(), units_spinner)
                && Validation.isSelectFrequency(dataCard.getRoot().getContext(), frequency_spinner)
                && !contiMedicationUpdate.isChecked() && Validation.validateDate(start_date_textview, dataCard.getRoot().getContext()) && Validation.validateDate(end_date_textview, dataCard.getRoot().getContext())) {

            saveData(view);
        }
    }


    /**
     * Function to save card content on click
     *
     * @param view
     */
    public void saveData(View view) {

        // Show View Card Content
        showViewContentCard();

        // Shut off keypad
        InputMethodManager imm = (InputMethodManager) dataCard.getRoot().getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

        // Initialize updated place holders
        TextView nameOfMedicationUpdate = (TextView) dataCard.getRoot().findViewById(R.id.toolbar_title);
        TextView start_date_textview = (TextView) dataCard.getRoot().findViewById(R.id.start_date_textview);
        CheckBox contiMedicationUpdate = (CheckBox) dataCard.getRoot().findViewById(R.id.contiMedicationUpdate);
        TextView end_date_textview = (TextView) dataCard.getRoot().findViewById(R.id.end_date_textview);
        EditText comments = (EditText) dataCard.getRoot().findViewById(R.id.commentsUpdate);

        /**
         * Create a data object
         */
        HashMap<String, String> updatedDataObject = new HashMap<>();
        updatedDataObject.put(StringConstants.KEY_MEDICATION_ID, medication.getProblemId());
        updatedDataObject.put(StringConstants.KEY_MEDICATION_NAME, nameOfMedicationUpdate.getText().toString());
        updatedDataObject.put(StringConstants.KEY_MEDICATION_UNITS, units_spinner.getSelectedItem().toString());
        updatedDataObject.put(StringConstants.KEY_MEDICATION_DOSES, doses_spinner.getSelectedItem().toString());
        updatedDataObject.put(StringConstants.KEY_MEDICATION_FREQUENCY, frequency_spinner.getSelectedItem().toString());
        updatedDataObject.put(StringConstants.KEY_MEDICATION_CONTINUING, String.valueOf(contiMedicationUpdate.isChecked()));

        /**
         * Check for Duration to set Start Date
         */
        if (contiMedicationUpdate.isChecked()) {

            updatedDataObject.put(StringConstants.KEY_MEDICATION_SINCE_WHEN,
                    String.valueOf(DateTimeUtils.calculateTimestampFromDuration(durationSpinner.getSelectedItemPosition())));

        } else {

            updatedDataObject.put(StringConstants.KEY_MEDICATION_SINCE_WHEN,
                    String.valueOf(DateTimeUtils.convertDateSimpleToTimestamp(start_date_textview.getText().toString())));
        }

        updatedDataObject.put(StringConstants.KEY_END_DATE,
                String.valueOf(DateTimeUtils.convertDateSimpleToTimestamp(end_date_textview.getText().toString())));
        updatedDataObject.put(StringConstants.KEY_MEDICATION_COMMENTS, comments.getText().toString());


        List<Medication> medicationList =
                Medication.find(Medication.class, "patient_id = ? and problem_id = ?",
                        SharedPreferenceService.getValue(context, StringConstants.KEY_PATIENT_ID),
                        String.valueOf((medication.getProblemId())));

        /**
         * Check if record exist in local database
         */
        if (medicationList.size() > 0) {

            updatedDataObject.put(StringConstants.KEY_P_ID, medication.getpId());
            MedicationFragment.updateMedication(context, updatedDataObject);

        } else {

            MedicationFragment.createMedication(context, updatedDataObject);
        }

        // Collapse Card
        toggleCardStates();
    }




















    /**
     * Function to implement date picker
     */
    public void openDatePicker() {
        Calendar now = Calendar.getInstance();
        DatePickerDialog dpd = DatePickerDialog.newInstance(this,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );
        if (isDateFirstclicked) {
            if (!Validation.isEmpty(end_date_textview.getText().toString())) {
                Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(DateTimeUtils.convertDateToLongForHealthBook(end_date_textview.getText().toString()));
                dpd = DatePickerDialog.newInstance(this,
                        cal.get(Calendar.YEAR),
                        cal.get(Calendar.MONTH),
                        cal.get(Calendar.DAY_OF_MONTH)
                );
                dpd.setMaxDate(cal);
                dpd.setYearRange(StaticConstants.MIN_DATE, cal.get(Calendar.YEAR));
            } else {
                dpd.setMaxDate(now);
                dpd.setYearRange(StaticConstants.MIN_DATE, now.get(Calendar.YEAR));
            }
        } else {
            if (!Validation.isEmpty(start_date_textview.getText().toString())) {
                Calendar cal2 = Calendar.getInstance();
                cal2.setTimeInMillis(DateTimeUtils.convertDateToLongForHealthBook(start_date_textview.getText().toString()));
                dpd = DatePickerDialog.newInstance(this,
                        cal2.get(Calendar.YEAR),
                        cal2.get(Calendar.MONTH),
                        cal2.get(Calendar.DAY_OF_MONTH)
                );
                dpd.setMinDate(cal2);
            } else {
                dpd.setYearRange(StaticConstants.MIN_DATE, now.get(Calendar.YEAR));
            }
            dpd.setMaxDate(now);
        }
        dpd.show(MedicationFragment.getInstance().getActivity().getFragmentManager(), StringConstants.KEY_DATEPICKER_DIALOG);
        dpd.setAccentColor(R.color.hb_medical_problem);
        dpd.showYearPickerFirst(true);
    }


    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {

        long timestamp = DateTimeUtils.convertTimeToTimestamp(year, monthOfYear, dayOfMonth, 0, 0);

        if (isDateFirstclicked) {

            // Start Date
            start_date_textview.setError(null);
            start_date_textview.setText(DateTimeUtils.convertTimestampToDate(timestamp));

        } else {

            // End Date
            end_date_textview.setError(null);
            end_date_textview.setText(DateTimeUtils.convertTimestampToDate(timestamp));
        }
    }
























    /**
     * Function to handle states of a single entity
     */
    public void toggleCardStates() {
        ViewGroup.LayoutParams params = cardContent.getLayoutParams();
        if (params.height == 0) {
            expandEntity(params);
        } else {
            collapseEntity(params);
        }
    }


    /**
     * Function to expand entity
     *
     * @param params
     */
    private void expandEntity(ViewGroup.LayoutParams params) {

        /**
         * Check if Still Suffering flag is set
         */
        manageIsSufferingDisplay(medication.isContinuingMedication());

        // Updated = True -> Show View Card and Edit Card Menu
        showViewContentCard();
        showCardEditMenu();
        // State transitions
        if (Build.VERSION.SDK_INT > 19) {
            TransitionManager.beginDelayedTransition(cardContent);
            params.height =
                    LinearLayout.LayoutParams.WRAP_CONTENT;
            cardContent.setLayoutParams(params);
        } else {
            params.height =
                    LinearLayout.LayoutParams.WRAP_CONTENT;
            cardContent.setLayoutParams(params);
            Animation in =
                    AnimationUtils.loadAnimation(context, android.R.anim.fade_in);
            in.setDuration(1000);
            cardContent.startAnimation(in);
        }
    }


    /**
     * Function to collapse entity
     *
     * @param params
     */
    private void collapseEntity(ViewGroup.LayoutParams params) {
        /**
         * Show Dropdown Menu on Collapse
         */
        showCardDropdownMenu();
        // State transitions
        if (Build.VERSION.SDK_INT > 19) {
            TransitionManager.beginDelayedTransition(cardContent);
            params.height = 0;
            cardContent.setLayoutParams(params);
        } else {
            params.height = 0;
            cardContent.setLayoutParams(params);
            Animation out =
                    AnimationUtils.loadAnimation(context, android.R.anim.fade_out);
            out.setDuration(2000);
            cardContent.startAnimation(out);
        }
    }


    /**
     * Function to manage state of the card on load
     */
    public void manageCardInitialState() {
        // Updated = true -> Update state - OFF, View state - ON
        showViewContentCard();
    }


    /**
     * Function to show Dropdown Menu
     */
    public void showCardDropdownMenu() {
        toolbar.getMenu().setGroupVisible(R.id.toggle_menu, true);
        toolbar.getMenu().setGroupVisible(R.id.edit_menu, false);
        toolbar.getMenu().setGroupVisible(R.id.delete_menu, false);
        toolbar.getMenu().setGroupVisible(R.id.cancel_menu, false);
    }


    /**
     * Function to show Edit and Delete Menu
     */
    public void showCardEditMenu() {
        toolbar.getMenu().setGroupVisible(R.id.toggle_menu, false);
        toolbar.getMenu().setGroupVisible(R.id.cancel_menu, false);
        toolbar.getMenu().setGroupVisible(R.id.edit_menu, true);
        toolbar.getMenu().setGroupVisible(R.id.delete_menu, true);
    }


    /**
     * Function to show Cancel Menu
     */
    public void showCardCancelMenu() {
        toolbar.getMenu().setGroupVisible(R.id.toggle_menu, false);
        toolbar.getMenu().setGroupVisible(R.id.edit_menu, false);
        toolbar.getMenu().setGroupVisible(R.id.delete_menu, false);
        toolbar.getMenu().setGroupVisible(R.id.cancel_menu, true);
    }


    /**
     * Function to show content
     */
    public void showViewContentCard() {
        updateContent.setVisibility(View.GONE);
        viewContent.setVisibility(View.VISIBLE);
    }


    /**
     * Function to enable content
     */
    public void showUpdateContentCard() {
        updateContent.setVisibility(View.VISIBLE);
        viewContent.setVisibility(View.GONE);
    }


    /**
     * Function to bind values to the spinner
     */
    public void bindSpinner() {

        // No of times
        // Create an ArrayAdapter using the string array and a spinner layout
        ArrayAdapter<CharSequence> adapter1;
        adapter1 = ArrayAdapter.createFromResource(context, R.array.units, R.layout.item_spinner_healthbook);
        // Specify the layout to use when the list of choices appears
        adapter1.setDropDownViewResource(R.layout.item_spinner_healthbook_dropdown);
        // Apply the adapter to the spinner
        units_spinner.setAdapter(adapter1);
        int selectionPosition= adapter1.getPosition(medication.getUnits());
        units_spinner.setSelection(selectionPosition);


        // Doses
        // Create an ArrayAdapter using the string array and a spinner layout
        ArrayAdapter<CharSequence> adapter2;
        adapter2 = ArrayAdapter.createFromResource(context, R.array.doses, R.layout.item_spinner_healthbook);
        // Specify the layout to use when the list of choices appears
        adapter2.setDropDownViewResource(R.layout.item_spinner_healthbook_dropdown);
        // Apply the adapter to the spinner
        doses_spinner.setAdapter(adapter2);
        selectionPosition= adapter2.getPosition(medication.getDoses());
        doses_spinner.setSelection(selectionPosition);
        // Frequency


        // Create an ArrayAdapter using the string array and a spinner layout
        ArrayAdapter<CharSequence> adapter3;
        adapter3 = ArrayAdapter.createFromResource(context, R.array.frequency, R.layout.item_spinner_healthbook);
        // Specify the layout to use when the list of choices appears
        adapter3.setDropDownViewResource(R.layout.item_spinner_healthbook_dropdown);
        // Apply the adapter to the spinner
        frequency_spinner.setAdapter(adapter3);
        selectionPosition= adapter3.getPosition(medication.getFrequency());
        frequency_spinner.setSelection(selectionPosition);



        // Create an ArrayAdapter using the string array and a spinner layout
        ArrayAdapter<CharSequence> adapter4;
        adapter4 = ArrayAdapter.createFromResource(context, R.array.duration, R.layout.item_spinner_healthbook);
        // Specify the layout to use when the list of choices appears
        adapter4.setDropDownViewResource(R.layout.item_spinner_healthbook_dropdown);
        // Apply the adapter to the spinner
        durationSpinner.setAdapter(adapter4);
        // Set spinner position

        int selectionDuration = adapter4.getPosition(medication.getDuration());
        durationSpinner.setSelection(selectionDuration);
    }
}
