package com.careons.app.Patient.Commons;

public class StringConstants {


    // API keys
    public static final String KEY_DATA = "Data";
    public static final String KEY_PATIENT_ID = "patientId";
    public static final String KEY_ID = "Id";
    public static final String KEY_NAME = "name";
    public static final String KEY_USERNAME = "username";
    public static final String KEY_EMAIL_ID = "email_id";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_PHONE = "phone";
    public static final String KEY_GRANT_TYPE = "grant_type";
    public static final String KEY_CONFIRM_PASSWORD = "confirm_password";
    public static final String KEY_DOB = "dob";
    public static final String KEY_DOB_TIMESTAMP = "dob_timestamp";
    public static final String KEY_AGE = "age";
    public static final String KEY_GENDER = "gender";
    public static final String KEY_MALE = "male";
    public static final String KEY_FEMALE = "female";
    public static final String KEY_HEIGHT = "height";
    public static final String KEY_HEIGHT_FT = "heightFt";
    public static final String KEY_HEIGHT_INC = "heightInc";
    public static final String KEY_WEIGHT = "weight";
    public static final String KEY_BLOOD_GROUP= "bloodGroup";
    public static final String KEY_IMEI = "imei";
    public static final String KEY_TOKEN = "access_token";
    public static final String KEY_IS_ACCOUNT_CREATED = "isAccountCreated";
    public static final String KEY_IS_ONBOARDING_COMPLETE = "isOnboardingComplete";
    public static final String KEY_IS_BLOOD_PRESSURE_DATA_AVAILABLE = "isBloodPressureDataAvailable";
    public static final String KEY_IS_BLOOD_GLUCOSE_DATA_AVAILABLE = "isBloodGlucoseDataAvailable";
    public static final String KEY_IS_BMI_DATA_AVAILABLE = "isBMIDataAvailable";
    public static final String KEY_IS_ALBUM_DATA_AVAILABLE = "isAlbumDataAvailable";
    public static final String KEY_USER = "user";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_REQUEST_CODE = "requestCode";


    // API URLs
    public static final String KEY_CREATE = "Create";
    public static final String KEY_READ = "GetAll";
    public static final String KEY_UPDATE = "Update/";
    public static final String KEY_DELETE = "delete";
    public static final String KEY_SEPARATOR = "/";



    // Healthbook
    public static final String KEY_READ_SELECTIVE_PROBLEMS = "GetPatientProblems";
    public static final String KEY_READ_SELECTIVE_ALLERGIES = "GetPatientAllergies";
    public static final String KEY_READ_SELECTIVE_FAMILY = "GetPatientFamilyHistories";
    public static final String KEY_READ_SELECTIVE_MEDICATION = "GetPatientMedicines";
    public static final String KEY_READ_SELECTIVE_LIFESTYLE = "GetPatientSocialHistories";
    public static final String KEY_READ_SELECTIVE_SURGICAL = "GetPatientSurgicalProcedures";
    public static final String KEY_READ_SELECTIVE_INVESTIGATIVE = "GetPatientInvestigativeProcedures";
    public static final String KEY_READ_SELECTIVE_PREGNANCYS = "GetPatientPregnancy";
    public static final String KEY_READ_SELECTIVE_CHILDHOOD = "GetPatientChildhoodHistories";
    public static final String KEY_READ_SELECTIVE_GYNAECOLOGY = "GetPatientGynaecologicalIssues";

    public static final String KEY_P_ID = "Id";
    public static final String KEY_PROBLEMS_ID = "ProblemsId";
    public static final String KEY_PROBLEM_NAME = "ProblemName";
    public static final String KEY_PROBLEM_CATEGORY = "ProblemCategory";
    public static final String KEY_PROBLEM_COUNT = "ProblemCount";
    public static final String KEY_START_DATE = "StartDate";
    public static final String KEY_END_DATE = "EndDate";
    public static final String KEY_IS_SUFFERING = "IsSuffering";
    public static final String KEY_COMMENT = "Comment";

    // LifeStyle
    public static final String KEY_DURATION = "Duration";
    public static final String KEY_LIFESTYLE_COUNT = "LifestyleCount";

    // Allergy
    public static String KEY_ALLERGY_P_ID = "p_id";
    public static final String KEY_ALLERGY_ID = "AllergiesId";
    public static final String KEY_ALLERGY_TYPE = "AllergyType";
    public static final String KEY_ALLERGY_NAME = "AllergyName";
    public static final String KEY_ALLERGY_KIND_OF_REACTION = "Reaction";
    public static final String KEY_ALLERGY_DATE_OF_ONSET = "StartDate";
    public static final String KEY_ALLERGY_COMMENTS = "Comment";
    public static final String KEY_ALLERGY_COUNT = "AllergyCount";
    public static final String KEY_SEVERITY = "Severity";

    // Surgical
    public static final String KEY_SURGICAL_ID = "SurgicalProceduresId";
    public static final String KEY_SURGICAL_DATE = "SurgeryDate";
    public static final String KEY_SURGICAL_NAME = "SurgicalName";
    public static final String KEY_SURGICAL_IS_SOLVED = "IsSolved";
    public static final String KEY_SURGICAL_COUNT = "SurgicalCount";
    public static final String KEY_SURGICAL_CATEGORY = "Surgical";
    public static final String KEY_INVESTIGATIVE_ID = "InvestigativeProceduresId";
    public static final String KEY_INVESTIGATE_CATEGORY = "Investigative";
    public static final String KEY_INVESTIGATE_TEST_DATE = "LabTestDate";

    // Family
    public static String KEY_FAMILY_FATHER_SUFFERING = "IsFather";
    public static String KEY_FAMILY_FATHER_AGE = "FatherAge";
    public static String KEY_FAMILY_MOTHER_SUFFERING = "IsMother";
    public static String KEY_FAMILY_MOTHER_AGE = "MotherAge";
    public static String KEY_FAMILY_BROTHER_SUFFERING = "IsBrother";
    public static String KEY_FAMILY_BROTHER_AGE = "BrotherAge";
    public static String KEY_FAMILY_SISTER_SUFFERING = "IsSister";
    public static String KEY_FAMILY_SISTER_AGE = "SisterAge";
    public static String KEY_FAMILY_GF_SUFFERING = "IsGrandfather";
    public static String KEY_FAMILY_GF_AGE = "GrandfatherAge";
    public static String KEY_FAMILY_GM_SUFFERING = "IsGrandmother";
    public static String KEY_FAMILY_GM_AGE = "GrandmotherAge";
    public static String KEY_FAMILY_COUNT = "FamilyCount";

    // Medication
    public static String KEY_MEDICATION_ID = "MedicinesId";
    public static String KEY_MEDICATION_NAME = "MedicinesName";
    public static String KEY_MEDICATION_SINCE_WHEN = "sinceWhen";
    public static String KEY_MEDICATION_UNITS = "Unit";
    public static String KEY_MEDICATION_DOSES = "Dose";
    public static String KEY_MEDICATION_FREQUENCY = "Frequency";
    public static String KEY_MEDICATION_CONTINUING = "ContinuingMedicine";
    public static String KEY_MEDICATION_DUURATION = "duration";
    public static String KEY_MEDICATION_END_DATE = "EndDate";
    public static String KEY_MEDICATION_COMMENTS = "comment";
    public static String KEY_MEDICATION_COUNT = "MedicationCount";

    // Childhood
    public static String KEY_CHILDHOOD_COUNT = "ChildhoodCount";

    // Pregnancy
    public static final String KEY_PREGNANCY_MENSES_AGE = "MensesAge";
    public static final String KEY_PREGNANCY_LMD = "LastMenstrualDate";
    public static final String KEY_PREGNANCY_IS_EVER_PREGNANCY = "IsEverPregnant";
    public static final String KEY_PREGNANCY_NO_OF_PREG = "NoOfPregnancy";
    public static final String KEY_PREGNANCY_NO_OF_FTP = "NoOfFullTermPregnancy";
    public static final String KEY_PREGNANCY_NO_OF_PTP = "NoOfPreTermPregnancy";
    public static final String KEY_PREGNANCY_NO_OF_ABORTIONS = "NoOfAbortions";
    public static final String KEY_PREGNANCY_NO_OF_LIVING_CHILDREN = "NoOfLivingChildren";

    // Gynaecology
    public static String KEY_GYNAECOLOGY_COUNT = "GynaecologyCount";


    // Vitals
    public static final String KEY_READ_SELECTIVE_BLOOD_PRESSURE = "GetPatientBloodPressure";
    public static final String KEY_BP_ID = "Id";
    public static final String KEY_SYSTOLIC_BP = "Systolic";
    public static final String KEY_DIASTOLIC_BP = "Diastolic";
    public static final String KEY_READ_SELECTIVE_BLOOD_GLUCOSE = "GetPatientBloodGlucose";
    public static final String KEY_BG_ID = "Id";
    public static final String KEY_BLOOD_GLUCOSE = "bloodGlucose";
    public static final String KEY_CHECKUP = "checkup";
    public static final String KEY_DATE_BG = "date";
    public static final String KEY_READ_SELECTIVE_BMI = "GetPatientBMI";
    public static final String KEY_BMI_ID = "Id";
    public static final String KEY_BMI = "bmi";
    public static final String KEY_BMI_HEIGHT_FT = "heightFt";
    public static final String KEY_BMI_HEIGHT_INC = "heightInc";
    public static final String KEY_BMI_WEIGHT = "weight";
    public static final String KEY_DATE_BMI = "date";
    public static final String KEY_TAG = "Tag";
    public static final String KEY_DATE = "ReadingDate";
    public static final String KEY_TIME = "ReadingTime";
    public static final String KEY_TIMESTAMP = "Epoch";


    // Health Search
    public static final String KEY_SELECTED_INDEX = "selectedIndex";
    public static final String KEY_HEALTH_SEARCH = "Health Search - ";
    public static final String KEY_TITLE = "title";
    public static final String KEY_URL = "url";
    public static final String KEY_HEALTH_SEARCH_ID = "hsId";
    public static final String KEY_HEALTH_SEARCH_PROBLEM_NAME = "problemName";


    // Gallery
    public static final String KEY_READ_SELECTIVE_UD = "GetUploadDocuments";
    public static final String KEY_ALBUM_ID = "albumId";
    public static final String KEY_POSITION = "position";
    public static final String KEY_ACTION = "action";
    public static final String KEY_VIA = "via";
    public static final String KEY_CATEGORY = "category";
    public static final String KEY_BILLS = "Bills";
    public static final String KEY_MEDICINE = "Medicines";
    public static final String KEY_LAB_REPORTS = "Lab Reports";
    public static final String KEY_PRESCRIPTIONS = "Prescription";
    public static final String KEY_OTHERS = "Others";
    public static final String KEY_IMAGE_ID = "ImagesIds";
    public static final String KEY_DELETE_IMAGES = "deleteimages";


    public static final String KEY_DATEPICKER_DIALOG = "Datepickerdialog";
    public static final String KEY_TIMEPICKER_DIALOG = "Timepickerdialog";
    public static final String KEY_INONBOARDING = "InOnboarding";


    // API Search List
    public static final String KEY_VALUES = "value";
    public static final String KEY_SEARCH_LIST_ID = "Id";
    public static final String KEY_SEARCH_LIST_NAME = "Name";
    public static final String KEY_SEARCH_LIST_CATEGORY = "Category";
    public static final String KEY_SEARCH_LIST_HYPERLINK = "HTMLHyperlink";
    public static final String KEY_PROBLEMS = "problems";
    public static final String KEY_MEDICINES = "medicines";
    public static final String KEY_SURGICAL_PROCEDURES = "surgicalinvestigative";


    // API Response
    public static final String RESPONSE_STATUS = "status";
    public static final String RESPONSE_SUCCESS = "success";
    public static final String RESPONSE_SUGGESTIONS = "suggestions";


    // Exceptions
    public static final String JSON_EXCEPTION_ERROR = "Json Exception";
    public static final String EXCEPTION_ERROR = "Exception";



    // API KEYS
    public static final String API_KEY_TOKEN = "Token";
    public static final String API_KEY_NEW_PASSWORD = "NewPassword";
    public static final String API_KEY_IS_ACCOUNT_CREATED = "ProfileExists";
    public static final String API_KEY_IS_ONBOARDING_COMPLETE = "OnboardingView";
    public static final String API_KEY_PATIENT_ID = "PatientId";
    public static final String API_KEY_USERNAME = "UserName";
    public static final String API_KEY_PASSWORD = "Password";
    public static final String API_KEY_NAME = "Name";
    public static final String API_KEY_EMAIL = "Email";
    public static final String API_KEY_PHONE = "PhoneNumber";
    public static final String API_KEY_BLOOD_GROUP= "BloodGroup";
    public static final String API_KEY_DOB = "DOB";
    public static final String API_KEY_SEX = "Sex";
    public static final String API_KEY_BLOOD_GLUCOSE = "Glucose";
    public static final String API_KEY_CHECKUP = "Checkup";
    public static final String API_KEY_BMI_HEIGHT = "HeightInInches";
    public static final String API_KEY_BMI_WEIGHT = "WeightKg";
    public static final String API_KEY_BMI = "BMI";
    public static final String API_KEY_UPLOAD_DATE = "UploadDateTime";
    public static final String API_KEY_IMAGE_ID_URI = "ImageIdUrl";
    public static final String API_KEY_IMAGE_URI = "ImageUris";
    public static final String API_KEY_IMAGE_URI_SINGLE = "ImageUri";
    public static final String API_KEY_LIFESTYLE_ID = "SocialHistoriesId";
    public static final String API_KEY_LIFESTYLE_NAME = "SocialHistoriesName";
    public static final String API_KEY_SURGERY_DATE = "SurgeryDate";
    public static final String API_KEY_SURGERY_NAME = "SurgicalProceduresName";
    public static final String API_KEY_INVESTIGATIVE_NAME = "InvestigativeProceduresName";
}

