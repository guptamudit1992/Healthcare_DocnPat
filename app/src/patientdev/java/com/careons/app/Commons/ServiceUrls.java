package com.careons.app.Patient.Commons;

public class ServiceUrls {

    /**
     * API Base url endpoint
     */
    public static final String BASE_URL = "http://stagingapi.careons.net/";
    public static final String AZURE_BLOB_URL = "DefaultEndpointsProtocol=https;AccountName=%s;AccountKey=%s";


    /**
     * Login, Signup, Forgot Password and Create Account
     */
    public static final String KEY_LOGIN = BASE_URL.concat("Token");
    public static final String KEY_SIGNUP = BASE_URL.concat("api/Patient/Create");
    public static final String KEY_FORGOT_PASSWORD = BASE_URL.concat("api/patient/resetpassword");
    public static final String KEY_CHANGE_PASSWORD = BASE_URL.concat("api/patient/changepassword");
    public static final String KEY_LOGIN_FB = BASE_URL.concat("api/Patient/FacebookLogin");
    public static final String KEY_LOGIN_GOOGLE = BASE_URL.concat("api/Patient/GoogleLogin");


    /**
     * Profile
     */
    public static final String KEY_PROFILE = BASE_URL.concat("api/Patient/Update");


    /**
     * Healthbook
     */
    public static final String KEY_HEALTHBOOK_MEDICAL_PROBLEMS = BASE_URL.concat("api/PatientProblems/");
    public static final String KEY_HEALTHBOOK_ALLERGIES = BASE_URL.concat("api/PatientAllergies/");
    public static final String KEY_HEALTHBOOK_FAMILY_HISTORY = BASE_URL.concat("api/PatientFamilyHistories/");
    public static final String KEY_HEALTHBOOK_MEDICATION = BASE_URL.concat("api/PatientMedicines/");
    public static final String KEY_HEALTHBOOK_LIFESTYLE = BASE_URL.concat("api/PatientSocialHistories/");
    public static final String KEY_HEALTHBOOK_SURGICAL_HISTORY = BASE_URL.concat("api/PatientSurgicalProcedures/");
    public static final String KEY_HEALTHBOOK_INVESTIGATIVE_HISTORY = BASE_URL.concat("api/PatientInvestigativeProcedures/");
    public static final String KEY_HEALTHBOOK_PREGNANCY = BASE_URL.concat("api/PatientPregnancy/");
    public static final String KEY_HEALTHBOOK_CHILDHOOD_HISTORY = BASE_URL.concat("api/PatientChildhoodHistories/");
    public static final String KEY_HEALTHBOOK_GYNAECOLOGY = BASE_URL.concat("api/PatientGynaecologicalIssues/");

    /**
     * Healthbook Search
     */
    public static final String KEY_HEALTHBOOK_SEARCH = "https://careons.search.windows.net/indexes/%s/docs/suggest?api-version=2015-02-28&search=%s&$top=%s&$select=Name,Id,HTMLHyperlink,Category";


    /**
     * Health Search
     */
    public static final String KEY_HEALTH_SEARCH = "https://careons.search.windows.net/indexes/fulldatabase/docs/suggest?api-version=2015-02-28&search=%s&$top=10&$select=Name,Id,HTMLHyperlink,Category";
    public static final String KEY_HEALTH_SEARCH_FETCH_URL = "https://vsearch.nlm.nih.gov/vivisimo/cgi-bin/query-meta?v%3Aproject=medlineplus&v%3Asources=medlineplus-bundle&query=";


    /**
     * Vitals
     */
    public static final String KEY_BLOOD_PRESSURE = BASE_URL.concat("api/PatientBloodPressure/");
    public static final String KEY_BLOOD_GLUCOSE = BASE_URL.concat("api/PatientBloodGlucose/");
    public static final String KEY_BMI = BASE_URL.concat("api/PatientBMI/");


    /**
     * Upload Documents
     */
    public static final String KEY_UPLOAD_DOCUMENTS = BASE_URL.concat("api/UploadDocuments/");
}

