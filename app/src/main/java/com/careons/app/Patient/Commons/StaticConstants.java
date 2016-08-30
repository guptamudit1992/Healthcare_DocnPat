package com.careons.app.Patient.Commons;

public class StaticConstants {

    // API Headers
    public static final String KEY_CONTENT_TYPE = "Content-Type";
    public static final String KEY_APPLICATION_JSON = "application/json";
    public static final String KEY_URLENCODED = "application/x-www-form-urlencoded";
    public static final String KEY_AUTHORIZATION = "Authorization";
    public static final String KEY_BEARER = "Bearer";
    public static final String KEY_HOST = "host";
    public static final String KEY_HOSTNAME = "careons.search.windows.net";
    public static final String KEY_API_KEY = "api-key";


    // Font Typeface
    public static final String TYPEFACE_FONT_ROBOTO_BOLD = "fonts/Roboto-Bold.ttf";
    public static final String TYPEFACE_FONT_ROBOTO_LIGHT = "fonts/Roboto-Light.ttf";


    // Time Constants
    public static final long SPLASH_SCREEN_TIMEOUT = 2000;
    public static final String KEY_SEARCH_LIST_LIMIT = "10";
    public static final int APP_ONBOARDING_SCREEN_TIMEOUT = 2500;
    public static final int APP_CTA_TIMEOUT = 250;
    public static final int VOLLEY_API_TIMEOUT = 15000;


    // Notification Service
    public static final int NOTIFICATION_ID = 1;
    public static final int NOTIFICATION_PRIORITY = 2;
    public static final int VISIBILITY_PUBLIC = 1;



    // Upload Options
    public static final int REQUEST_IMAGE_CAPTURE = 1;
    public static final int PICK_IMAGE_REQUEST = 2;
    public static final int SEND_CHAT_IMAGE_REQUEST = 3;



    // Regex patterns
    public static final String EMAIL_REGEX = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    public static final String MOBILE_REGEX = "\\d{10}";
    //public static final String PASSWORD_REGEX = "(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\\\S+$).{5,10}";



    // Shared Preferences Object
    public static final String SESSION_OBJECT = "SessionObject";


    // Timestamp Format
    public static final String UTC_DATE_TIME_FORMAT = "yyyy-MM-dd hh:mm:ss.SSS";
    public static final String UTC_DATE_TIME_FORMAT_SHORTHAND = "yyyy-MM-dd hh:mm:s";
    public static final String DEFAULT_DATE_TIME_FORMAT = "dd MMMM yyyy hh:mm:ss";
    public static final String LOGIN_DATE_TIME_FORMAT = "yyyy-MM-dd";
    public static final String SIMPLE_DATE_FORMAT = "dd MMMM yyyy";
    public static final String SHORT_DATE_FORMAT = "dd MMM yyyy";
    public static final String UTC_TIME_FORMAT = "hh:mm:ss";
    public static final String DEFAULT_TIME_FORMAT = "hh:mm:ss a";
    public static final String CHAT_TIME_FORMAT = "hh:mm a";
    public static final String SIMPLE_TIME_FORMAT = "hh:mm:ss a";
    public static final String GRAPH_DATE_TIME_FORMAT = "dd MMM";
	public static final String HEALTHBOOK_DATE_FORMAT = "d-M-yyyy";


    // Error Types
    public static final int ERROR_INTERNET_FAILURE = 1;
    public static final int ERROR_JSON_PARSE_EXCEPTION = 2;
    public static final int ERROR_EXCEPTION = 3;
    public static final int ERROR_API_FAILURE = 4;

    // Graph Updates
    public static final int SOURCE_CREATE = 1;
    public static final int SOURCE_READ = 2;
    public static final int SOURCE_UPDATE = 3;
    public static final int SOURCE_DELETE = 4;


    // Adapter Settings
    public static final int ONBOARDING_MEDICAL_PROBLEM_ADAPTER = 1;
    public static final int ONBOARDING_FAMILY_ADAPTER = 2;
    public static final int ONBOARDING_LIFESTYLE_ADAPTER = 3;
    public static final int ONBOARDING_GYNAECOLOGICAL_ADAPTER = 4;

    public static final int MEDICAL_PROBLEM_ADAPTER = 5;
    public static final int ALLERGY_ADAPTER = 6;
    public static final int SURGICAL_ADAPTER = 7;
    public static final int CHILDHOOD_ADAPTER = 8;
    public static final int GYNAECOLOGICAL_ADAPTER = 9;
    public static final int FAMILY_ADAPTER = 10;
    public static final int LIFESTYLE_ADAPTER = 11;
    public static final int MEDICATION_ADAPTER = 12;

    public static final int VITALS_BLOOD_PRESSURE_ADAPTER = 13;
    public static final int VITALS_BLOOD_GLUCOSE_ADAPTER = 14;
    public static final int VITALS_BMI_ADAPTER = 15;

    public static final int HEALTH_SEARCH_ADAPTER = 16;
    public static final int LIST_SEARCH_ADAPTER = 17;
    public static final int QUICK_UPLOAD_ADAPTER = 18;
    public static final int GALLERY_ADAPTER = 19;
    public static final int GALLERY_GRID_ADAPTER = 20;


    public static final int CHAT_ADAPTER = 21;
    public static final int CHAT_DOCTOR_ADAPTER = 22;
    public static final int SYMPTOMS_LIST_ADAPTER = 23;
    public static final int SELECTED_SYMPTOMS_LIST_ADAPTER = 24;

    // Vitals Graph Scale
    public static final int VITALS_RANDOM = 3;
    public static final int VITALS_PRE_MEAL = 4;
    public static final int VITALS_POST_MEAL = 5;

    // Min Date
    public static final int MIN_DATE = 1970;
}


