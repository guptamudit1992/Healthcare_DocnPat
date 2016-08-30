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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.careons.app.Patient.Activity.Onboarding.OnboardingFamilyHistoryActivity;
import com.careons.app.Patient.Commons.StaticConstants;
import com.careons.app.Patient.Commons.StringConstants;
import com.careons.app.Patient.Database.Models.Healthbook.FamilyHistory;
import com.careons.app.Patient.Fragments.HealthBook.FamilyFragment;
import com.careons.app.R;
import com.careons.app.Patient.Services.SharedPreferenceService;
import com.careons.app.Shared.Utils.Validation;

import java.util.HashMap;
import java.util.List;

public class FamilyHistoryHandler implements View.OnClickListener {

    private Context context;
    private FamilyHistory familyHistory;
    private ViewDataBinding dataCard;
    private static int sender;

    private LinearLayout loader, cardContent, viewContent, updateContent,
            linearLayout1, linearLayout2, linearLayout3, linearLayout4, linearLayout5, linearLayout6;
    private Toolbar toolbar;
    private CheckBox updateCheckbox1, updateCheckbox2, updateCheckbox3, updateCheckbox4, updateCheckbox5, updateCheckbox6;
    private EditText updateAge1, updateAge2, updateAge3, updateAge4, updateAge5, updateAge6;
    private TextView ageUpdateTitle1, ageUpdateTitle2, ageUpdateTitle3, ageUpdateTitle4, ageUpdateTitle5, ageUpdateTitle6, dispComments;
    private int rrr = 0;
    long lastTouchTime = -1;


    // Constructor
    public FamilyHistoryHandler(final FamilyHistory familyHistory, final ViewDataBinding dataCard, final int sender) {

        this.familyHistory = familyHistory;
        this.dataCard = dataCard;
        FamilyHistoryHandler.sender = sender;

        // Get context
        context = dataCard.getRoot().getContext();

        // Initialize layouts
        cardContent = (LinearLayout) dataCard.getRoot().findViewById(R.id.card_content);
        viewContent = (LinearLayout) dataCard.getRoot().findViewById(R.id.view_content);
        updateContent = (LinearLayout) dataCard.getRoot().findViewById(R.id.update_content);
        loader = (LinearLayout) dataCard.getRoot().findViewById(R.id.loader);

        // Initialize fields
        updateCheckbox1 = (CheckBox) dataCard.getRoot().findViewById(R.id.updateCheckbox1);
        updateCheckbox2 = (CheckBox) dataCard.getRoot().findViewById(R.id.updateCheckbox2);
        updateCheckbox3 = (CheckBox) dataCard.getRoot().findViewById(R.id.updateCheckbox3);
        updateCheckbox4 = (CheckBox) dataCard.getRoot().findViewById(R.id.updateCheckbox4);
        updateCheckbox5 = (CheckBox) dataCard.getRoot().findViewById(R.id.updateCheckbox5);
        updateCheckbox6 = (CheckBox) dataCard.getRoot().findViewById(R.id.updateCheckbox6);

        updateCheckbox1.setOnClickListener(this);
        updateCheckbox2.setOnClickListener(this);
        updateCheckbox3.setOnClickListener(this);
        updateCheckbox4.setOnClickListener(this);
        updateCheckbox5.setOnClickListener(this);
        updateCheckbox6.setOnClickListener(this);

        ageUpdateTitle1 = (TextView) dataCard.getRoot().findViewById(R.id.ageUpdateTitle1);
        ageUpdateTitle2 = (TextView) dataCard.getRoot().findViewById(R.id.ageUpdateTitle2);
        ageUpdateTitle3 = (TextView) dataCard.getRoot().findViewById(R.id.ageUpdateTitle3);
        ageUpdateTitle4 = (TextView) dataCard.getRoot().findViewById(R.id.ageUpdateTitle4);
        ageUpdateTitle5 = (TextView) dataCard.getRoot().findViewById(R.id.ageUpdateTitle5);
        ageUpdateTitle6 = (TextView) dataCard.getRoot().findViewById(R.id.ageUpdateTitle6);
        ageUpdateTitle1.setVisibility(familyHistory.isFatherStillSuffering() ? View.VISIBLE : View.GONE);
        ageUpdateTitle2.setVisibility(familyHistory.isMotherStillSuffering() ? View.VISIBLE : View.GONE);
        ageUpdateTitle3.setVisibility(familyHistory.isBrotherStillSuffering() ? View.VISIBLE : View.GONE);
        ageUpdateTitle4.setVisibility(familyHistory.isSisterStillSuffering() ? View.VISIBLE : View.GONE);
        ageUpdateTitle5.setVisibility(familyHistory.isGrandFatherStillSuffering() ? View.VISIBLE : View.GONE);
        ageUpdateTitle6.setVisibility(familyHistory.isGrandMotherStillSuffering() ? View.VISIBLE : View.GONE);


        updateAge1 = (EditText) dataCard.getRoot().findViewById(R.id.updateAge1);
        updateAge2 = (EditText) dataCard.getRoot().findViewById(R.id.updateAge2);
        updateAge3 = (EditText) dataCard.getRoot().findViewById(R.id.updateAge3);
        updateAge4 = (EditText) dataCard.getRoot().findViewById(R.id.updateAge4);
        updateAge5 = (EditText) dataCard.getRoot().findViewById(R.id.updateAge5);
        updateAge6 = (EditText) dataCard.getRoot().findViewById(R.id.updateAge6);

        updateAge1.setVisibility(familyHistory.isFatherStillSuffering() ? View.VISIBLE : View.GONE);
        updateAge2.setVisibility(familyHistory.isMotherStillSuffering() ? View.VISIBLE : View.GONE);
        updateAge3.setVisibility(familyHistory.isBrotherStillSuffering() ? View.VISIBLE : View.GONE);
        updateAge4.setVisibility(familyHistory.isSisterStillSuffering() ? View.VISIBLE : View.GONE);
        updateAge5.setVisibility(familyHistory.isGrandFatherStillSuffering() ? View.VISIBLE : View.GONE);
        updateAge6.setVisibility(familyHistory.isGrandMotherStillSuffering() ? View.VISIBLE : View.GONE);


        linearLayout1 = (LinearLayout) dataCard.getRoot().findViewById(R.id.linearLayout1);
        linearLayout2 = (LinearLayout) dataCard.getRoot().findViewById(R.id.linearLayout2);
        linearLayout3 = (LinearLayout) dataCard.getRoot().findViewById(R.id.linearLayout3);
        linearLayout4 = (LinearLayout) dataCard.getRoot().findViewById(R.id.linearLayout4);
        linearLayout5 = (LinearLayout) dataCard.getRoot().findViewById(R.id.linearLayout5);
        linearLayout6 = (LinearLayout) dataCard.getRoot().findViewById(R.id.linearLayout6);

        linearLayout1.setVisibility(familyHistory.isFatherStillSuffering() ? View.VISIBLE : View.GONE);
        linearLayout2.setVisibility(familyHistory.isMotherStillSuffering() ? View.VISIBLE : View.GONE);
        linearLayout3.setVisibility(familyHistory.isBrotherStillSuffering() ? View.VISIBLE : View.GONE);
        linearLayout4.setVisibility(familyHistory.isSisterStillSuffering() ? View.VISIBLE : View.GONE);
        linearLayout5.setVisibility(familyHistory.isGrandFatherStillSuffering() ? View.VISIBLE : View.GONE);
        linearLayout6.setVisibility(familyHistory.isGrandMotherStillSuffering() ? View.VISIBLE : View.GONE);

        dispComments = (TextView) dataCard.getRoot().findViewById(R.id.dispComments);
        dispComments.setVisibility(Validation.isEmpty(familyHistory.getComments()) ? View.GONE : View.VISIBLE);

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
                                if (sender == StaticConstants.FAMILY_ADAPTER) {

                                    FamilyFragment.deleteFamilyHistoryProblem(dataCard.getRoot().getContext(),
                                            String.valueOf(familyHistory.getpId()));
                                } else {

                                    OnboardingFamilyHistoryActivity.deleteFamilyHistoryProblem(dataCard.getRoot().getContext(),
                                            String.valueOf(familyHistory.getpId()));
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
                        if (familyHistory.isUpdate()) {

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





    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.updateCheckbox1:
                ageUpdateTitle1.setVisibility(updateCheckbox1.isChecked() ? View.VISIBLE : View.GONE);
                updateAge1.setVisibility(updateCheckbox1.isChecked() ? View.VISIBLE : View.GONE);
                if (updateCheckbox1.isChecked()) {
                    updateAge1.requestFocus();
                }
                break;
            case R.id.updateCheckbox2:
                ageUpdateTitle2.setVisibility(updateCheckbox2.isChecked() ? View.VISIBLE : View.GONE);
                updateAge2.setVisibility(updateCheckbox2.isChecked() ? View.VISIBLE : View.GONE);
                if (updateCheckbox2.isChecked()) {
                    updateAge2.requestFocus();
                }
                break;
            case R.id.updateCheckbox3:
                ageUpdateTitle3.setVisibility(updateCheckbox3.isChecked() ? View.VISIBLE : View.GONE);
                updateAge3.setVisibility(updateCheckbox3.isChecked() ? View.VISIBLE : View.GONE);
                if (updateCheckbox3.isChecked()) {
                    updateAge3.requestFocus();
                }
                break;
            case R.id.updateCheckbox4:
                ageUpdateTitle4.setVisibility(updateCheckbox4.isChecked() ? View.VISIBLE : View.GONE);
                updateAge4.setVisibility(updateCheckbox4.isChecked() ? View.VISIBLE : View.GONE);
                if (updateCheckbox4.isChecked()) {
                    updateAge4.requestFocus();
                }
                break;
            case R.id.updateCheckbox5:
                ageUpdateTitle5.setVisibility(updateCheckbox5.isChecked() ? View.VISIBLE : View.GONE);
                updateAge5.setVisibility(updateCheckbox5.isChecked() ? View.VISIBLE : View.GONE);
                if (updateCheckbox5.isChecked()) {
                    updateAge5.requestFocus();
                }
                break;
            case R.id.updateCheckbox6:
                ageUpdateTitle6.setVisibility(updateCheckbox6.isChecked() ? View.VISIBLE : View.GONE);
                updateAge6.setVisibility(updateCheckbox6.isChecked() ? View.VISIBLE : View.GONE);
                if (updateCheckbox6.isChecked()) {
                    updateAge6.requestFocus();
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
        if (updateCheckbox1.isChecked() || updateCheckbox2.isChecked() || updateCheckbox3.isChecked() || updateCheckbox4.isChecked() || updateCheckbox5.isChecked() || updateCheckbox6.isChecked()) {
            boolean executed = true;
            if (updateCheckbox1.isChecked() && !Validation.validateDateOfDetection(updateAge1, dataCard.getRoot().getContext())) {
                executed = false;
                return;
            }
            if (updateCheckbox2.isChecked() && !Validation.validateDateOfDetection(updateAge2, dataCard.getRoot().getContext())) {
                executed = false;
                return;
            }
            if (updateCheckbox3.isChecked() && !Validation.validateDateOfDetection(updateAge3, dataCard.getRoot().getContext())) {
                executed = false;
                return;
            }
            if (updateCheckbox4.isChecked() && !Validation.validateDateOfDetection(updateAge4, dataCard.getRoot().getContext())) {
                executed = false;
                return;
            }
            if (updateCheckbox5.isChecked() && !Validation.validateDateOfDetection(updateAge5, dataCard.getRoot().getContext())) {
                executed = false;
                return;
            }
            if (updateCheckbox6.isChecked() && !Validation.validateDateOfDetection(updateAge6, dataCard.getRoot().getContext())) {
                executed = false;
                return;
            }

            if (executed) {
                // Show View Card Content
                showViewContentCard();

                // Shut off keypad
                InputMethodManager imm = (InputMethodManager) dataCard.getRoot().getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

                // Fetch values
                TextView updateProblemName = (TextView) dataCard.getRoot().findViewById(R.id.toolbar_title);
                TextView hyperlink = (TextView) dataCard.getRoot().findViewById(R.id.hyperlink);

                CheckBox checkbox1 = (CheckBox) dataCard.getRoot().findViewById(R.id.updateCheckbox1);
                CheckBox checkbox2 = (CheckBox) dataCard.getRoot().findViewById(R.id.updateCheckbox2);
                CheckBox checkbox3 = (CheckBox) dataCard.getRoot().findViewById(R.id.updateCheckbox3);
                CheckBox checkbox4 = (CheckBox) dataCard.getRoot().findViewById(R.id.updateCheckbox4);
                CheckBox checkbox5 = (CheckBox) dataCard.getRoot().findViewById(R.id.updateCheckbox5);
                CheckBox checkbox6 = (CheckBox) dataCard.getRoot().findViewById(R.id.updateCheckbox6);

                EditText updateAge1 = (EditText) dataCard.getRoot().findViewById(R.id.updateAge1);
                EditText updateAge2 = (EditText) dataCard.getRoot().findViewById(R.id.updateAge2);
                EditText updateAge3 = (EditText) dataCard.getRoot().findViewById(R.id.updateAge3);
                EditText updateAge4 = (EditText) dataCard.getRoot().findViewById(R.id.updateAge4);
                EditText updateAge5 = (EditText) dataCard.getRoot().findViewById(R.id.updateAge5);
                EditText updateAge6 = (EditText) dataCard.getRoot().findViewById(R.id.updateAge6);


                EditText updateComments = (EditText) dataCard.getRoot().findViewById(R.id.update_comments);

                HashMap<String, String> updatedDataObject = new HashMap<>();
                updatedDataObject.put(StringConstants.KEY_PROBLEMS_ID, String.valueOf(familyHistory.getProblemId()));
                updatedDataObject.put(StringConstants.KEY_PROBLEM_NAME, updateProblemName.getText().toString());
                updatedDataObject.put(StringConstants.KEY_SEARCH_LIST_HYPERLINK, hyperlink.getText().toString());
                updatedDataObject.put(StringConstants.KEY_FAMILY_FATHER_SUFFERING, String.valueOf(checkbox1.isChecked()));
                updatedDataObject.put(StringConstants.KEY_FAMILY_FATHER_AGE, updateAge1.getText().toString());
                updatedDataObject.put(StringConstants.KEY_FAMILY_MOTHER_SUFFERING, String.valueOf(checkbox2.isChecked()));
                updatedDataObject.put(StringConstants.KEY_FAMILY_MOTHER_AGE, updateAge2.getText().toString());
                updatedDataObject.put(StringConstants.KEY_FAMILY_BROTHER_SUFFERING, String.valueOf(checkbox3.isChecked()));
                updatedDataObject.put(StringConstants.KEY_FAMILY_BROTHER_AGE, updateAge3.getText().toString());
                updatedDataObject.put(StringConstants.KEY_FAMILY_SISTER_SUFFERING, String.valueOf(checkbox4.isChecked()));
                updatedDataObject.put(StringConstants.KEY_FAMILY_SISTER_AGE, updateAge4.getText().toString());
                updatedDataObject.put(StringConstants.KEY_FAMILY_GF_SUFFERING, String.valueOf(checkbox5.isChecked()));
                updatedDataObject.put(StringConstants.KEY_FAMILY_GF_AGE, updateAge5.getText().toString());
                updatedDataObject.put(StringConstants.KEY_FAMILY_GM_SUFFERING, String.valueOf(checkbox6.isChecked()));
                updatedDataObject.put(StringConstants.KEY_FAMILY_GM_AGE, updateAge6.getText().toString());
                updatedDataObject.put(StringConstants.KEY_COMMENT, updateComments.getText().toString());


                // Check if value present in local database
                List<FamilyHistory> familyHistoryList =
                        FamilyHistory.find(FamilyHistory.class, "patient_id = ? and p_id = ?",
                                SharedPreferenceService.getValue(context, StringConstants.KEY_PATIENT_ID),
                                String.valueOf(familyHistory.getpId()));


                // Check Calling activity to proceed to next step
                if (sender == StaticConstants.ONBOARDING_FAMILY_ADAPTER) {

                    // From Onboarding -> Green tick, collapse card
                    toggleCardStates();

                    if (familyHistoryList.size() > 0) {

                        // Found -> Update Record
                        updatedDataObject.put(StringConstants.KEY_P_ID, familyHistory.getpId());
                        OnboardingFamilyHistoryActivity.updateFamilyHistoryProblem(context, updatedDataObject);

                    } else {

                        // Not Found -> Create Record
                        OnboardingFamilyHistoryActivity.createFamilyHistoryProblem(context, updatedDataObject);
                    }

                } else {

                    // From Healthbook -> Update Family History List
                    if (familyHistoryList.size() > 0) {

                        // Found -> Update Record
                        updatedDataObject.put(StringConstants.KEY_P_ID, familyHistory.getpId());
                        FamilyFragment.updateFamilyHistoryProblem(context, updatedDataObject);

                    } else {

                        // Not Found -> Create Record
                        FamilyFragment.createFamilyHistoryProblem(context, updatedDataObject);
                    }
                }
            }
        }else{
            Toast.makeText(dataCard.getRoot().getContext(),"Please select minimum one value",Toast.LENGTH_SHORT).show();
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

        // Check if card is updated
        if (familyHistory.isUpdate()) {

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
            Animation out = AnimationUtils.loadAnimation(context, android.R.anim.fade_out);
            out.setDuration(2000);
            cardContent.startAnimation(out);
        }
    }


    /**
     * Function to manage state of the card on load
     */
    public void manageCardInitialState() {

        if (familyHistory.isUpdate()) {

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
        if (sender == StaticConstants.FAMILY_ADAPTER || familyHistory.isUpdate()) {

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
}