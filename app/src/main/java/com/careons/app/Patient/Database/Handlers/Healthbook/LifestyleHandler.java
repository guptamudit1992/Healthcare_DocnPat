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

import com.careons.app.Patient.Activity.Onboarding.OnboardingLifestyleActivity;
import com.careons.app.Patient.Commons.StaticConstants;
import com.careons.app.Patient.Commons.StringConstants;
import com.careons.app.Patient.Database.Models.Healthbook.Lifestyle;
import com.careons.app.Patient.Fragments.HealthBook.LifestyleFragment;
import com.careons.app.R;
import com.careons.app.Patient.Services.SharedPreferenceService;
import com.careons.app.Shared.Utils.DateTimeUtils;
import com.careons.app.Shared.Utils.Validation;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class LifestyleHandler
        implements DatePickerDialog.OnDateSetListener, View.OnClickListener {

    private static Context context;
    private Lifestyle lifestyle;
    private ViewDataBinding dataCard;
    private static int sender;

    private LinearLayout loader, cardContent, viewContent, updateContent,
            startDateDisplayLinearLayout, endDateDisplayLinearLayout, durationDisplayLinearLayout,
            startDateEditLinearLayout, endDateEditLinearLayout, durationEditLinearLayout;
    private Toolbar toolbar;
    private TextView startDateEditTextView, endDateEditTextView, comments;
    private CheckBox updateStatus;
    private Spinner durationSpinner;
    private EditText updateComments;

    private long startDateEpoch, endDateEpoch;
    private boolean isDateFirstclicked = true;
    private int rrr = 0;
    long lastTouchTime = -1;

    // Constructor
    public LifestyleHandler(final Lifestyle lifestyle, final ViewDataBinding dataCard, final int sender) {

        this.lifestyle = lifestyle;
        this.dataCard = dataCard;
        LifestyleHandler.sender = sender;

        // Get context
        context = dataCard.getRoot().getContext();

        // Initialize layouts
        cardContent = (LinearLayout) dataCard.getRoot().findViewById(R.id.card_content);
        viewContent = (LinearLayout) dataCard.getRoot().findViewById(R.id.view_content);
        updateContent = (LinearLayout) dataCard.getRoot().findViewById(R.id.update_content);
        loader = (LinearLayout) dataCard.getRoot().findViewById(R.id.loader);
        startDateDisplayLinearLayout = (LinearLayout) dataCard.getRoot().findViewById(R.id.startDateDispLinearLayout);
        endDateDisplayLinearLayout = (LinearLayout) dataCard.getRoot().findViewById(R.id.endDateDispLinearLayout);
        durationDisplayLinearLayout = (LinearLayout) dataCard.getRoot().findViewById(R.id.durationDispLinearLayout);
        startDateEditLinearLayout = (LinearLayout) dataCard.getRoot().findViewById(R.id.editableStartDateLinearLayout);
        endDateEditLinearLayout = (LinearLayout) dataCard.getRoot().findViewById(R.id.editableEndDateLinearLayout);
        durationEditLinearLayout = (LinearLayout) dataCard.getRoot().findViewById(R.id.editableDurationLinearLayout);

        // Initialize fields
        durationSpinner = (Spinner) dataCard.getRoot().findViewById(R.id.duration_spinner);
        startDateEditTextView = (TextView) dataCard.getRoot().findViewById(R.id.editable_start_date);
        endDateEditTextView = (TextView) dataCard.getRoot().findViewById(R.id.editableTextViewEndDate);

        // Initialize loaders
        loader = (LinearLayout) dataCard.getRoot().findViewById(R.id.loader);

        // Initialize data fields
        comments = (TextView) dataCard.getRoot().findViewById(R.id.disp_comments);
        comments.setVisibility(Validation.isEmpty(lifestyle.getComments()) ? View.GONE : View.VISIBLE);
        updateStatus = (CheckBox) dataCard.getRoot().findViewById(R.id.update_status);

        updateComments = (EditText) dataCard.getRoot().findViewById(R.id.update_comments);
        // On Focus change listener on username
        updateComments.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (sender == StaticConstants.ONBOARDING_LIFESTYLE_ADAPTER) {
                    OnboardingLifestyleActivity.setBottomLayoutVisibility(!hasFocus);
                    if(!hasFocus){
                        // Shut off keypad
                        InputMethodManager imm = (InputMethodManager) dataCard.getRoot().getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(updateComments.getWindowToken(), 0);
                    }

                }
            }
        });


        // Set Listener
        startDateEditTextView.setOnClickListener(this);
        endDateEditTextView.setOnClickListener(this);
        updateStatus.setOnClickListener(this);


        /**
         * Bind Duration Spinner
         */
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
                                if (sender == StaticConstants.LIFESTYLE_ADAPTER) {

                                    LifestyleFragment.deleteLifestyleHistory(context, String.valueOf(lifestyle.getpId()));

                                } else {

                                    OnboardingLifestyleActivity.deleteLifestyleHistory(context, String.valueOf(lifestyle.getpId()));
                                }

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
                        // Check if card content is updated
                        if (lifestyle.isUpdate()) {

                            //  Updated = True -> Show View Card and Edit Menu
                            showViewContentCard();
                            showCardEditMenu();

                        } else {

                            //  Updated = False -> Collapse Content Card
                            toggleCardStates();
                        }
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
     * Function to toggle card content view on click
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

        /**
         * Check Validations
         */
        if(updateStatus.isChecked()) {
            if (Validation.isSelectDuration(dataCard.getRoot().getContext(),durationSpinner)){
                saveDate(view);
            }
        } else if(!updateStatus.isChecked()
                && Validation.validateDate(startDateEditTextView, dataCard.getRoot().getContext())
                && Validation.validateDate(endDateEditTextView, dataCard.getRoot().getContext())) {

            saveDate(view);
        }
    }


    /**
     * Function to save card content on click
     * @param view
     */
    public void saveDate(View view) {

        // Show View Card Content
        showViewContentCard();

        // Shut off keypad
        InputMethodManager imm = (InputMethodManager) dataCard.getRoot().getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

        // Fetch content
        CheckBox updateStatus = (CheckBox) dataCard.getRoot().findViewById(R.id.update_status);
        EditText updateComments = (EditText) dataCard.getRoot().findViewById(R.id.update_comments);


        // Create data object
        HashMap<String, String> updatedDataObject = new HashMap<>();
        updatedDataObject.put(StringConstants.KEY_PROBLEMS_ID, lifestyle.getProblemId());
        updatedDataObject.put(StringConstants.KEY_PROBLEM_NAME, lifestyle.getProblemName());

        /**
         * Check for Duration to set Start Date
         */
        if (updateStatus.isChecked()) {

            // Calculate Start Date from Duration
            updatedDataObject.put(StringConstants.KEY_START_DATE,
                    String.valueOf(DateTimeUtils.calculateTimestampFromDuration(durationSpinner.getSelectedItemPosition())));
        } else {

            updatedDataObject.put(StringConstants.KEY_START_DATE,
                    String.valueOf(DateTimeUtils.convertDateSimpleToTimestamp(startDateEditTextView.getText().toString())));
        }

        updatedDataObject.put(StringConstants.KEY_END_DATE,
                String.valueOf(DateTimeUtils.convertDateSimpleToTimestamp(endDateEditTextView.getText().toString())));
        updatedDataObject.put(StringConstants.KEY_IS_SUFFERING, String.valueOf(updateStatus.isChecked()));
        updatedDataObject.put(StringConstants.KEY_COMMENT, updateComments.getText().toString());



        // Check if value present in local database
        List<Lifestyle> lifestyleList =
                Lifestyle.find(Lifestyle.class, "patient_id = ? and problem_id = ?",
                        SharedPreferenceService.getValue(context, StringConstants.KEY_PATIENT_ID),
                        String.valueOf(lifestyle.getProblemId()));


        /**
         * Check Calling activity to proceed to next step
         */
        if(sender == StaticConstants.ONBOARDING_LIFESTYLE_ADAPTER) {

            // From Onboarding -> Green tick, collapse card
            toggleCardStates();

            if(lifestyleList.size() > 0) {

                // Found -> Update Record
                updatedDataObject.put(StringConstants.KEY_P_ID, lifestyle.getpId());
                OnboardingLifestyleActivity.updateLifestyleHistory(context, updatedDataObject);

            } else {

                // Not Found -> Create Record
                OnboardingLifestyleActivity.createLifestyleHistory(context, updatedDataObject);
            }

        } else {

            // From Healthbook -> Update Lifestyle Problems List
            if(lifestyleList.size() > 0) {

                // Found -> Update Record
                updatedDataObject.put(StringConstants.KEY_P_ID, lifestyle.getpId());
                LifestyleFragment.updateLifestyleHistory(context, updatedDataObject);

            } else {

                // Not Found -> Create Record
                LifestyleFragment.createLifestyleHistory(context, updatedDataObject);
            }
        }
    }








    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.editable_start_date:
                isDateFirstclicked = true;
                openDatePicker();
                break;

            case R.id.editableTextViewEndDate:
                isDateFirstclicked = false;
                openDatePicker();
                break;

            case R.id.update_status:
                manageIsSufferingDisplay(updateStatus.isChecked());
                break;

            default:
                break;
        }
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
            durationEditLinearLayout.setVisibility(View.VISIBLE);
            startDateEditLinearLayout.setVisibility(View.GONE);
            endDateEditLinearLayout.setVisibility(View.GONE);

            durationDisplayLinearLayout.setVisibility(View.VISIBLE);
            startDateDisplayLinearLayout.setVisibility(View.GONE);
            endDateDisplayLinearLayout.setVisibility(View.GONE);

        } else {

            // False -> Hide Duration, Show Start and End Date Layout
            durationEditLinearLayout.setVisibility(View.GONE);
            startDateEditLinearLayout.setVisibility(View.VISIBLE);
            endDateEditLinearLayout.setVisibility(View.VISIBLE);

            durationDisplayLinearLayout.setVisibility(View.GONE);
            startDateDisplayLinearLayout.setVisibility(View.VISIBLE);
            endDateDisplayLinearLayout.setVisibility(View.VISIBLE);
        }

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

        Calendar cal = null;
        if (isDateFirstclicked) {

            if (!Validation.isEmpty(endDateEditTextView.getText().toString())) {
                cal = Calendar.getInstance();
                cal.setTimeInMillis(DateTimeUtils.convertDateToLongForHealthBook(endDateEditTextView.getText().toString()));
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
            if (!Validation.isEmpty(startDateEditTextView.getText().toString())) {
                Calendar cal2 = Calendar.getInstance();
                cal2.setTimeInMillis(DateTimeUtils.convertDateToLongForHealthBook(startDateEditTextView.getText().toString()));
                dpd = DatePickerDialog.newInstance(this,
                        cal2.get(Calendar.YEAR),
                        cal2.get(Calendar.MONTH),
                        cal2.get(Calendar.DAY_OF_MONTH)
                );

                dpd.setMinDate(cal2);
            }else{
                dpd.setYearRange(StaticConstants.MIN_DATE, now.get(Calendar.YEAR));
            }
            dpd.setMaxDate(now);
        }

        /**
         * Check Calling Activity or Fragment
         */
        if (sender == StaticConstants.ONBOARDING_LIFESTYLE_ADAPTER) {
            // From Onboarding
            dpd.show(OnboardingLifestyleActivity.getInstance().getFragmentManager(), StringConstants.KEY_DATEPICKER_DIALOG);
        } else {
            // From Healthbook
            dpd.show(LifestyleFragment.getInstance().getActivity().getFragmentManager(), StringConstants.KEY_DATEPICKER_DIALOG);
        }

        //dpd.setAccentColor(R.color.light_grey);
        dpd.showYearPickerFirst(true);
    }


    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        /**
         * Check for Start Date or End Date
         */
        if(isDateFirstclicked) {

            // Start Date
            startDateEpoch = DateTimeUtils.convertTimeToTimestamp(year, monthOfYear, dayOfMonth, 0, 0);
            startDateEditTextView.setError(null);
            startDateEditTextView.setText(DateTimeUtils.convertTimestampToDate(startDateEpoch));

        } else {

            // End Date
            endDateEpoch = DateTimeUtils.convertTimeToTimestamp(year, monthOfYear, dayOfMonth, 0, 0);
            endDateEditTextView.setError(null);
            endDateEditTextView.setText(DateTimeUtils.convertTimestampToDate(endDateEpoch));
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
     * @param params
     */
    private void expandEntity(ViewGroup.LayoutParams params) {

        /**
         * Check if Still Suffering flag is set
         */
        manageIsSufferingDisplay(lifestyle.isStillSuffering());

        // Check if card is updated
        if(lifestyle.isUpdate()) {

            // Updated = True -> Show View Card and Edit Card Menu
            showViewContentCard();
            showCardEditMenu();

        } else {

            // Updated = False -> Show Update Card and Cancel Card Menu
            showUpdateContentCard();
            showCardCancelMenu();
        }

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
     * @param params
     */
    private void collapseEntity(ViewGroup.LayoutParams params) {

        /**
         * Show Dropdown Menu on Collapse
         */
        showCardDropdownMenu();

        // Shut off keypad
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(dataCard.getRoot().getWindowToken(), 0);


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

        if(lifestyle.isUpdate()) {

            // Updated = true -> Update state - OFF, View state - ON
            showViewContentCard();

        } else {

            // Updated = false -> Update state - ON, View state - OFF
            showUpdateContentCard();
        }
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

        // Check calling activity
        if(sender == StaticConstants.LIFESTYLE_ADAPTER || lifestyle.isUpdate()) {

            // Onboarding -> Edit - Visible, Delete - Hidden
            toolbar.getMenu().setGroupVisible(R.id.edit_menu, true);
            toolbar.getMenu().setGroupVisible(R.id.delete_menu, true);

        } else {

            // Onboarding -> Edit - Visible, Delete - Visible
            toolbar.getMenu().setGroupVisible(R.id.edit_menu, true);
            toolbar.getMenu().setGroupVisible(R.id.delete_menu, false);
        }
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
     * Function to enable updateFamilyHistoryProblem content
     */
    public void showUpdateContentCard() {

        updateContent.setVisibility(View.VISIBLE);
        viewContent.setVisibility(View.GONE);
    }


    /**
     * Function to bind values to the spinner
     */
    public void bindSpinner() {
        // Create an ArrayAdapter using the string array and a spinner layout
        ArrayAdapter<CharSequence> adapter;
        adapter = ArrayAdapter.createFromResource(context, R.array.duration, R.layout.item_spinner_healthbook);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(R.layout.item_spinner_healthbook_dropdown);
        // Apply the adapter to the spinner
        durationSpinner.setAdapter(adapter);


        // Set spinner position
        int selectionPosition= adapter.getPosition(lifestyle.getDuration());
        durationSpinner.setSelection(selectionPosition);
    }
}
