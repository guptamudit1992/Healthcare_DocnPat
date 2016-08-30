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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.careons.app.Patient.Commons.StaticConstants;
import com.careons.app.Patient.Commons.StringConstants;
import com.careons.app.Patient.Database.Models.Healthbook.Allergy;
import com.careons.app.Patient.Fragments.HealthBook.AllergyFragment;
import com.careons.app.R;
import com.careons.app.Patient.Services.SharedPreferenceService;
import com.careons.app.Shared.Utils.DateTimeUtils;
import com.careons.app.Shared.Utils.Validation;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class AllergyHandler implements DatePickerDialog.OnDateSetListener {

    private Context context;
    private Allergy allergy;
    private ViewDataBinding dataCard;

    private LinearLayout loader, cardContent, viewContent, updateContent;
    private Toolbar toolbar;
    private TextView dateOfOnset, disp_comments;

    private Spinner kindOfReactionSpinner;

    private int rrr = 0;
    long lastTouchTime = -1;


    // Constructor
    public AllergyHandler(final Allergy allergy, final ViewDataBinding dataCard) {

        this.allergy = allergy;
        this.dataCard = dataCard;

        // Get context
        context = dataCard.getRoot().getContext();


        cardContent = (LinearLayout) dataCard.getRoot().findViewById(R.id.card_content);
        viewContent = (LinearLayout) dataCard.getRoot().findViewById(R.id.view_content);
        updateContent = (LinearLayout) dataCard.getRoot().findViewById(R.id.update_content);
        loader = (LinearLayout) dataCard.getRoot().findViewById(R.id.loader);

        dateOfOnset = (TextView) dataCard.getRoot().findViewById(R.id.update_date);
        disp_comments = (TextView) dataCard.getRoot().findViewById(R.id.disp_comments);
        disp_comments.setVisibility(Validation.isEmpty(allergy.getComment()) ? View.GONE : View.VISIBLE);
        kindOfReactionSpinner = (Spinner) dataCard.getRoot().findViewById(R.id.kindOfReaction_spinner);

        // On Focus change listener on start date
        dateOfOnset.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    Validation.validateStartDate(dateOfOnset, dataCard.getRoot().getContext());
                }
            }
        });

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
                                AllergyFragment.deleteAllergy(context, allergy.getpId());

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


        dateOfOnset = (TextView) dataCard.getRoot().findViewById(R.id.update_date);
        dateOfOnset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDatePicker();
            }
        });

        //Bind Spinner
        bindSpinner();
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
        if (Validation.isSelectReaction(dataCard.getRoot().getContext(), kindOfReactionSpinner) && Validation.validateStartDate(dateOfOnset, dataCard.getRoot().getContext())) {
            saveDate(view);
        }
    }


    /**
     * Function to save card content on click
     *
     * @param view
     */
    public void saveDate(View view) {

        // Show View Card Content
        showViewContentCard();


        EditText updateComments = (EditText) dataCard.getRoot().findViewById(R.id.update_comments);

        HashMap<String, String> updatedDataObject = new HashMap<>();
        updatedDataObject.put(StringConstants.KEY_ALLERGY_ID, String.valueOf(allergy.getAllergyId()));
        updatedDataObject.put(StringConstants.KEY_ALLERGY_TYPE, String.valueOf(allergy.getType()));
        updatedDataObject.put(StringConstants.KEY_ALLERGY_KIND_OF_REACTION, kindOfReactionSpinner.getSelectedItem().toString());
        updatedDataObject.put(StringConstants.KEY_START_DATE,
                String.valueOf(DateTimeUtils.convertDateSimpleToTimestamp(dateOfOnset.getText().toString())));
        updatedDataObject.put(StringConstants.KEY_ALLERGY_COMMENTS, updateComments.getText().toString());

        // Shut off keypad
        InputMethodManager imm = (InputMethodManager) dataCard.getRoot().getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);


        /**
         * Check if exists in local Database
         */
        List<Allergy> mAllergyDBList = Allergy.find(Allergy.class, "patient_id = ? and allergy_id = ?",
                SharedPreferenceService.getValue(context, StringConstants.KEY_PATIENT_ID),
                String.valueOf(allergy.getAllergyId()));

        if (mAllergyDBList.size() > 0) {

            // Found -> Update Record
            updatedDataObject.put(StringConstants.KEY_ALLERGY_P_ID, allergy.getpId());
            AllergyFragment.updateAllergy(context, updatedDataObject);

        } else {

            // Not Found -> Create Record
            AllergyFragment.createAllergyRecords(context, updatedDataObject);
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
        dpd.show(AllergyFragment.getInstance().getActivity().getFragmentManager(), StringConstants.KEY_DATEPICKER_DIALOG);
        //dpd.setAccentColor(R.color.hb_medical_problem);
        dpd.showYearPickerFirst(true);
        dpd.setMaxDate(now);
        dpd.setYearRange(StaticConstants.MIN_DATE, now.get(Calendar.YEAR));
    }


    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {

        long timestamp = DateTimeUtils.convertTimeToTimestamp(year, monthOfYear, dayOfMonth, 0, 0);
        dateOfOnset.setError(null);
        dateOfOnset.setText(DateTimeUtils.convertTimestampToDate(timestamp));
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

        // Updated = True -> Show View Card and Edit Card Menu
        showViewContentCard();
        showCardEditMenu();

        // State transitions
        if (Build.VERSION.SDK_INT > 19) {

            TransitionManager.beginDelayedTransition(cardContent);
            params.height = LinearLayout.LayoutParams.WRAP_CONTENT;
            cardContent.setLayoutParams(params);

        } else {

            params.height = LinearLayout.LayoutParams.WRAP_CONTENT;
            cardContent.setLayoutParams(params);
            Animation in = AnimationUtils.loadAnimation(context, android.R.anim.fade_in);
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
        // Create an ArrayAdapter using the string array and a spinner layout
        ArrayAdapter<CharSequence> adapter;
        adapter = ArrayAdapter.createFromResource(dataCard.getRoot().getContext(), R.array.kind_of_reaction, R.layout.item_spinner_healthbook);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(R.layout.item_spinner_healthbook_dropdown);
        // Apply the adapter to the spinner
        kindOfReactionSpinner.setAdapter(adapter);
        int selectionPosition = adapter.getPosition(allergy.getReaction());
        kindOfReactionSpinner.setSelection(selectionPosition);

    }
}

