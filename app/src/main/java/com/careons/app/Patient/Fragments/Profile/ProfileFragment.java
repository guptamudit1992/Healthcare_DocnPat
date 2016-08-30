package com.careons.app.Patient.Fragments.Profile;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.careons.app.Patient.Activity.Main.MainActivity;
import com.careons.app.Patient.Activity.Others.ChangePasswordActivity;
import com.careons.app.Patient.Commons.ServiceUrls;
import com.careons.app.Patient.Commons.StaticConstants;
import com.careons.app.Patient.Commons.StringConstants;
import com.careons.app.Patient.Database.Models.Profile.Profile;
import com.careons.app.Patient.Enums.BloodGroup;
import com.careons.app.Shared.Interfaces.APIInterface;
import com.careons.app.Shared.Interfaces.FragmentInteractionListener;
import com.careons.app.R;
import com.careons.app.Patient.Services.APICallService;
import com.careons.app.Patient.Services.SharedPreferenceService;
import com.careons.app.Shared.Utils.DateTimeUtils;
import com.careons.app.Shared.Utils.Validation;
import com.careons.app.databinding.FragmentProfileBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class ProfileFragment extends Fragment
        implements View.OnClickListener , com.wdullaer.materialdatetimepicker.date.DatePickerDialog.OnDateSetListener, AdapterView.OnItemSelectedListener, AdapterView.OnItemClickListener {

    // Rootview
    private View rootview;

    // Floating action button
    private FloatingActionButton fabEdit, fabSave;

    // Edittext fields
    private EditText nameEdittext, phoneEdittext;

    // Page state Linear layout
    private static LinearLayout loaderLinearLayout, dataLinearLayout;
    private static TextView loaderTextView, emailEdittext, dobTextView, blood_group_textview, changePasswordTextView;
    private static ImageView userImageView;
    private static ProgressBar progressBar;
    private static Spinner bloodGroupSpinner;
    private long dobTimestamp = 0;
    private String dobString = "";

    // Fragment Binder
    private static FragmentProfileBinding binding;

    // Keyboard toggle variable
    private InputMethodManager imm;
    private FragmentInteractionListener mListener;

    private static String url = ServiceUrls.KEY_PROFILE;
    private static HashMap<String, String> headers = new HashMap<>();


    public ProfileFragment() {
        // Required empty public constructor
    }

    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onResume() {
        super.onResume();

        // Set action bar title
        ((AppCompatActivity) getActivity()).getSupportActionBar()
                .setTitle(R.string.navigation_drawer_profile);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        rootview = inflater.inflate(R.layout.fragment_profile, container, false);

        // Initializing keyboard
        imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

        // Initialize Linear layout
        loaderLinearLayout = (LinearLayout) rootview.findViewById(R.id.profile_loader);
        dataLinearLayout = (LinearLayout) rootview.findViewById(R.id.user_details);
        loaderTextView = (TextView) rootview.findViewById(R.id.loader_text);
        progressBar = (ProgressBar) rootview.findViewById(R.id.loader);

        /**
         * Change Password
         */
        changePasswordTextView = (TextView) rootview.findViewById(R.id.change_password);
        changePasswordTextView.setOnClickListener(this);

        // Toggle fab
        nameEdittext = (EditText) rootview.findViewById(R.id.name_edittext);
        // On Focus change listener on username
        nameEdittext.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {

                    validateUsername();
                }
            }
        });

        emailEdittext = (TextView) rootview.findViewById(R.id.email_edittext);
        phoneEdittext = (EditText) rootview.findViewById(R.id.phone_edittext);

        // On Focus change listener on phone no
        phoneEdittext.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {

                    validatePhoneNo();
                }
            }
        });
        dobTextView = (TextView) rootview.findViewById(R.id.dob_textView);
        blood_group_textview = (TextView) rootview.findViewById(R.id.blood_group_textview);
        bloodGroupSpinner = (Spinner) rootview.findViewById(R.id.blood_group_spinner);



        userImageView = (ImageView) rootview.findViewById(R.id.user_image);
        fabEdit = (FloatingActionButton) rootview.findViewById(R.id.fab_edit);
        fabSave = (FloatingActionButton) rootview.findViewById(R.id.fab_save);

        //set clickable false
        dobTextView.setEnabled(false);

        // Set on click listener
        fabEdit.setOnClickListener(this);
        fabSave.setOnClickListener(this);
        dobTextView.setOnClickListener(this);

        //Set on Selected listener
        bloodGroupSpinner.setOnItemSelectedListener(this);

        // Disable fields initially
        disableInput(nameEdittext);
        //disableInput(emailEdittext);
        disableInput(phoneEdittext);

        // bind values to the spinner

        // Create an ArrayAdapter using the string array and a spinner layout
        ArrayAdapter<CharSequence> adapter;
        adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.blood_group, R.layout.item_spinner_profile);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(R.layout.item_spinner_profile);
        // Apply the adapter to the spinner
        bloodGroupSpinner.setAdapter(adapter);

        Profile profile = Profile.find(Profile.class, "patient_id = ?",
                SharedPreferenceService.getValue(getContext(), StringConstants.KEY_PATIENT_ID)).get(0);

        int selectionPosition= adapter.getPosition(profile.getBloodGroup());
        bloodGroupSpinner.setSelection(selectionPosition);


        /**
         * Place Display Pic based on Gender
         */
        if(SharedPreferenceService.getValue(getContext(), StringConstants.KEY_GENDER).equalsIgnoreCase(getString(R.string.male))) {

            userImageView.setImageDrawable(getResources().getDrawable(R.drawable.dp_man));

        } else {

            userImageView.setImageDrawable(getResources().getDrawable(R.drawable.dp_woman));
        }


        /**
         * Fetch API Data
         */
        getProfileData(rootview);

        return rootview;
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }


    // TODO: Rename method, updateFamilyHistoryProblem argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof FragmentInteractionListener) {
            mListener = (FragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.fab_edit:
                fabEdit.hide();
                fabSave.show();
                bloodGroupSpinner.setVisibility(View.VISIBLE);
                blood_group_textview.setVisibility(View.GONE);
                dobTextView.setEnabled(true);
                enableInput(nameEdittext);
                //enableInput(emailEdittext);
                enablePhoneInput(phoneEdittext);
                break;

            case R.id.fab_save:
                if (validateUsername() && validatePhoneNo() && isSelectBloodGroup()) {
                    fabEdit.show();
                    fabSave.hide();
                    bloodGroupSpinner.setVisibility(View.GONE);
                    blood_group_textview.setVisibility(View.VISIBLE);
                    dobTextView.setEnabled(false);
                    disableInput(nameEdittext);
                    //disableInput(emailEdittext);
                    disableInput(phoneEdittext);
                    updateProfile(getActivity().getApplicationContext(), rootview);
                }
                break;

            case R.id.dob_textView:
                openDatePicker();
                break;

            case R.id.change_password:
                Intent intent = new Intent(getActivity(), ChangePasswordActivity.class);
                startActivity(intent);
                break;

            default:
                break;
        }
    }


    /**
     * Function to show error
     * @param typeError
     */
    public void setLoading(int typeError) {
        progressBar.setVisibility(View.GONE);

        switch (typeError) {

            case 1:
                loaderTextView.setText(getString(R.string.err_internet_not_available));
                break;

            case 2:
                loaderTextView.setText(getString(R.string.err_json_parse_exception));
                break;

            case 3:
                loaderTextView.setText(getString(R.string.err_exception));
                break;

            case 4:
                loaderTextView.setText(getString(R.string.err_api_failure));
                break;

            default:
                break;
        }
    }


    /**
     * Function to enable input
     * @param editText
     */
    public void enableInput(EditText editText) {

        editText.setInputType(InputType.TYPE_CLASS_TEXT);
        imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
        editText.setCursorVisible(true);
    }


    /**
     * Function to enable input
     * @param editText
     */
    public void enablePhoneInput(EditText editText) {

        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
        editText.setCursorVisible(true);
    }


    /**
     * Function to disable input
     * @param editText
     */
    public void disableInput(EditText editText) {

        editText.setCursorVisible(false);
        editText.setInputType(InputType.TYPE_NULL);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }


    /**
     * Function  to fetch profile data via API
     * @param rootview
     */
    public void getProfileData(View rootview) {

        List<Profile> profiles = Profile.find(Profile.class, "patient_id = ?",
                SharedPreferenceService.getValue(getContext(), StringConstants.KEY_PATIENT_ID));


        // Check if profile exists for patient id
        if(profiles.size() > 0) {

            Profile profile = profiles.get(0);

            // Initialize Data binding
            binding = DataBindingUtil.bind(rootview);

            binding.setProfile(profile);

            // Update content visibility
            loaderLinearLayout.setVisibility(View.GONE);
            dataLinearLayout.setVisibility(View.VISIBLE);

        } else {

            setLoading(StaticConstants.ERROR_EXCEPTION);
        }
    }



    /**
     * Function to updateFamilyHistoryProblem profile
     */
    public void updateProfile(final Context context, final View rootview) {

        // Set loaders
        dataLinearLayout.setVisibility(View.GONE);
        loaderLinearLayout.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        loaderTextView.setText(getString(R.string.updating));

        if (Validation.isConnected(getContext())) {

            final String name = nameEdittext.getText().toString();
            final String phone = phoneEdittext.getText().toString();
            final String dob = dobTextView.getText().toString();
            final String bloodGroup = bloodGroupSpinner.getSelectedItem().toString();

            // Set Headers
            final HashMap<String, String> headers = new HashMap<>();
            headers.put(StaticConstants.KEY_AUTHORIZATION,
                    StaticConstants.KEY_BEARER
                            .concat(" ")
                            .concat(SharedPreferenceService
                                    .getValue(getContext(), StringConstants.KEY_TOKEN)));;

            // Constructing request body
            final HashMap<String, Object> data = new HashMap<>();
            data.put(StringConstants.KEY_ID, SharedPreferenceService.getValue(getContext(), StringConstants.KEY_PATIENT_ID));
            data.put(StringConstants.API_KEY_NAME, name);
            data.put(StringConstants.API_KEY_PHONE, phone);
            data.put(StringConstants.API_KEY_DOB,
                    dobTimestamp == 0 ? DateTimeUtils.convertTimestampToUTC(DateTimeUtils.convertDateLoginToTimestamp(
                            SharedPreferenceService.getValue(context, StringConstants.KEY_DOB)))
                            : DateTimeUtils.convertTimestampToUTC(dobTimestamp));

            data.put(StringConstants.API_KEY_SEX, SharedPreferenceService.getValue(getContext(), StringConstants.KEY_GENDER));
            data.put(StringConstants.API_KEY_BLOOD_GROUP, bloodGroup.replace(" ", ""));
            data.put(StringConstants.API_KEY_IS_ACCOUNT_CREATED, 1);
            data.put(StringConstants.API_KEY_IS_ONBOARDING_COMPLETE, 1);


            // API Call
            APICallService.PutAPICall(getActivity(), getContext(), url, data, headers, new APIInterface() {
                @Override
                public void onSuccess(JSONObject response) {

                    /**
                     * Update Local Database (Name, Phone)
                     */
                    Profile profile = Profile.find(Profile.class, "patient_id = ?",
                            SharedPreferenceService.getValue(getContext(), StringConstants.KEY_PATIENT_ID)).get(0);
                    profile.setUsername(name);
                    profile.setPhone(phone);

                    if(!Validation.isEmpty(dobString)){
                        profile.setDob(dobString);
                        profile.setAge(DateTimeUtils.calculateAgeFromDOB(Long.valueOf(dobTimestamp)));
                    }

                    try {
                        profile.setBloodGroup(BloodGroup.valueOf(response.getString(StringConstants.API_KEY_BLOOD_GROUP)).getBloodGroup());
                        SharedPreferenceService.storeUserDetails(getContext(), StringConstants.KEY_BLOOD_GROUP, BloodGroup.valueOf(response.getString(StringConstants.API_KEY_BLOOD_GROUP)).getBloodGroup());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    profile.save();
                    dobTextView.setText(profile.getDob());
                    blood_group_textview.setText(bloodGroup);


                    /**
                     * Store values in the Shared Preference (is Onboarding Complete Flag)
                     */
                    SharedPreferenceService.storeUserDetails(getContext(), StringConstants.KEY_USERNAME, name);
                    SharedPreferenceService.storeUserDetails(getContext(), StringConstants.KEY_PHONE, phone);

                    if(!Validation.isEmpty(dobString)) {
                        SharedPreferenceService.storeUserDetails(getContext(), StringConstants.KEY_DOB,
                                dobString);
                        SharedPreferenceService.storeUserDetails(getContext(),
                                StringConstants.KEY_DOB_TIMESTAMP, String.valueOf(dobTimestamp));
                        SharedPreferenceService.storeUserDetails(getContext(),
                                StringConstants.KEY_AGE, DateTimeUtils.calculateAgeFromDOB(Long.valueOf(dobTimestamp)));
                    }
                    //SharedPreferenceService.storeUserDetails(getContext(), StringConstants.KEY_DOB, dobString);


                    // Update Navigation Drawer Header Values
                    MainActivity.updateNavigationDrawerHeaderValues(context);

                    // Update content visibility
                    showContent();
                }

                @Override
                public void onError(VolleyError error) {

                    Toast.makeText(getContext(), getString(R.string.try_again_in_sometime),
                            Toast.LENGTH_LONG).show();

                    // Update content visibility
                    showContent();
                }
            });
        } else {

            // If internet connection is not available
            Toast.makeText(getContext(), getString(R.string.err_internet_not_available),
                    Toast.LENGTH_LONG).show();

            // Update content visibility
            showContent();
        }
    }


    /**
     * Function to show content
     */
    public static void showContent() {

        loaderLinearLayout.setVisibility(View.GONE);
        dataLinearLayout.setVisibility(View.VISIBLE);
    }


    /**
     * Function to validate username
     */
    public boolean validateUsername() {

        boolean isValidUsername = false;
        String username = nameEdittext.getText().toString();

        // Validate Username
        if(Validation.isEmpty(username)) {

            nameEdittext.setError(getString(R.string.err_msg_name));

        } else {

            isValidUsername = true;
            nameEdittext.setError(null);
        }

        return isValidUsername;
    }

    /**
     * Function to validate phone
     */
    public boolean validatePhoneNo() {

        boolean isValidPhone = false;
        String phone = phoneEdittext.getText().toString();

        // Validate Username
        if(!Validation.isEmpty(phone) && phone.length() < 10) {

            phoneEdittext.setError(getString(R.string.err_msg_phone_no));

        } else {

            isValidPhone = true;
            phoneEdittext.setError(null);
        }

        return isValidPhone;
    }

    /**
     * Function to validate Blood Group
     * @return
     */
    public boolean isSelectBloodGroup() {

        boolean isValidBloodGroup= true;

        if(bloodGroupSpinner.getSelectedItem().toString().equals(getString(R.string.s_blood_group))) {
            isValidBloodGroup = false;
            Toast.makeText(getContext(), getString(R.string.select_blood_group),Toast.LENGTH_SHORT).show();
        }
        return isValidBloodGroup;
    }


    /**
     * Function to implement date picker
     */
    public void openDatePicker() {

        Calendar now = Calendar.getInstance();
        com.wdullaer.materialdatetimepicker.date.DatePickerDialog dpd = com.wdullaer.materialdatetimepicker.date.DatePickerDialog.newInstance(
                this,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );
        dpd.show(getActivity().getFragmentManager(), StringConstants.KEY_DATEPICKER_DIALOG);
        //dpd.setAccentColor(R.color.hb_medication_history);
        dpd.showYearPickerFirst(true);
        dpd.setMaxDate(now);
    }

    @Override
    public void onDateSet(com.wdullaer.materialdatetimepicker.date.DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {

       // String dateString = dayOfMonth + " " + (new DateFormatSymbols().getMonths()[monthOfYear]) + " " + year;
        //dobTextView.setText(dateString);

        String month = String.valueOf(monthOfYear+1);
        if(monthOfYear+1 < 10) {

            month = "0"+month;
        }

        String day = String.valueOf(dayOfMonth);
        if(dayOfMonth < 10) {
            day = "0"+day;
        }

        dobString = year + "-" + month + "-" + day;
        dobTextView.setText(dobString);
        // Calculate timestamp for date of birth
        dobTimestamp = DateTimeUtils.convertTimeToTimestamp(year, monthOfYear, dayOfMonth, 0, 0);
    }




    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }
}

