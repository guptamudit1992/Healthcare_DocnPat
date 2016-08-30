package com.careons.app.Patient.Adapters;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.careons.app.Patient.Commons.StaticConstants;
import com.careons.app.Patient.Database.Handlers.Consultation.ChatHandler;
import com.careons.app.Patient.Database.Handlers.Consultation.ConsultationHandler;
import com.careons.app.Patient.Database.Handlers.HealthSearch.HealthEducationHandler;
import com.careons.app.Patient.Database.Handlers.Healthbook.AllergyHandler;
import com.careons.app.Patient.Database.Handlers.Healthbook.ChildhoodHistoryHandler;
import com.careons.app.Patient.Database.Handlers.Healthbook.FamilyHistoryHandler;
import com.careons.app.Patient.Database.Handlers.Healthbook.GynaecologyHandler;
import com.careons.app.Patient.Database.Handlers.Healthbook.LifestyleHandler;
import com.careons.app.Patient.Database.Handlers.Healthbook.MedicalProblemHandler;
import com.careons.app.Patient.Database.Handlers.Healthbook.MedicationHandler;
import com.careons.app.Patient.Database.Handlers.Healthbook.SurgicalHistoryHandler;
import com.careons.app.Patient.Database.Handlers.Upload.AlbumHandler;
import com.careons.app.Patient.Database.Handlers.Upload.GalleryGridHandler;
import com.careons.app.Patient.Database.Handlers.Vitals.BMIHandler;
import com.careons.app.Patient.Database.Handlers.Vitals.BloodGlucoseHandler;
import com.careons.app.Patient.Database.Handlers.Vitals.BloodPressureHandler;
import com.careons.app.Patient.Database.Models.Chat.Chat;
import com.careons.app.Patient.Database.Models.Consultation.Consultation;
import com.careons.app.Patient.Database.Models.Healthbook.Allergy;
import com.careons.app.Patient.Database.Models.Healthbook.ChildhoodHistory;
import com.careons.app.Patient.Database.Models.Healthbook.FamilyHistory;
import com.careons.app.Patient.Database.Models.Healthbook.Gynaecology;
import com.careons.app.Patient.Database.Models.Healthbook.Lifestyle;
import com.careons.app.Patient.Database.Models.Healthbook.MedicalProblem;
import com.careons.app.Patient.Database.Models.Healthbook.Medication;
import com.careons.app.Patient.Database.Models.Healthbook.SurgicalHistory;
import com.careons.app.Patient.Database.Models.Upload.Album;
import com.careons.app.Patient.Database.Models.Upload.UploadImage;
import com.careons.app.Patient.Database.Models.Vitals.BMI;
import com.careons.app.Patient.Database.Models.Vitals.BloodGlucose;
import com.careons.app.Patient.Database.Models.Vitals.BloodPressure;
import com.careons.app.Patient.Models.HealthSearch.HealthEducation;
import com.careons.app.Patient.Models.Upload.ImageModel;
import com.careons.app.databinding.CardAllergyBinding;
import com.careons.app.databinding.CardChatBinding;
import com.careons.app.databinding.CardChatDoctorBinding;
import com.careons.app.databinding.CardChildhoodHistoryBinding;
import com.careons.app.databinding.CardFamilyBinding;
import com.careons.app.databinding.CardGalleryBinding;
import com.careons.app.databinding.CardGridGalleryBinding;
import com.careons.app.databinding.CardGynaecologicalBinding;
import com.careons.app.databinding.CardHsBinding;
import com.careons.app.databinding.CardLifestyleBinding;
import com.careons.app.databinding.CardMedicalProblemBinding;
import com.careons.app.databinding.CardMedicationBinding;
import com.careons.app.databinding.CardSurgicalHistoryBinding;
import com.careons.app.databinding.ItemQuickUploadBinding;
import com.careons.app.databinding.ListItemSearchOptionBinding;
import com.careons.app.databinding.ListItemSymptomsBinding;
import com.careons.app.databinding.ListItemSymptomsSelectedBinding;
import com.careons.app.databinding.ListItemVitalsBgBinding;
import com.careons.app.databinding.ListItemVitalsBmiBinding;
import com.careons.app.databinding.ListItemVitalsBpBinding;

public class ObjectHolder<X,Y> extends RecyclerView.ViewHolder {


    public ObjectHolder(View itemView) {
        super(itemView);
    }


    /**
     * Function to get View Data Binding instance
     * @return
     */
    public ViewDataBinding getBinding() {
        return DataBindingUtil.getBinding(itemView);
    }


    /**
     * Function to bind data object to adapter
     * @param object
     * @param _binding
     * @param case_constant
     */
    public void bindConnection(X object, Y _binding, int case_constant) {

        switch (case_constant) {

            case StaticConstants.ONBOARDING_MEDICAL_PROBLEM_ADAPTER:
                CardMedicalProblemBinding cardOnboardingMedicalProblemBinding = (CardMedicalProblemBinding) _binding;
                cardOnboardingMedicalProblemBinding.setData((MedicalProblem) object);
                MedicalProblemHandler onboardingMedicalProblemHandler =
                        new MedicalProblemHandler((MedicalProblem) object,
                                cardOnboardingMedicalProblemBinding, StaticConstants.ONBOARDING_MEDICAL_PROBLEM_ADAPTER);
                cardOnboardingMedicalProblemBinding.setHandlers(onboardingMedicalProblemHandler);
                break;

            case StaticConstants.ONBOARDING_FAMILY_ADAPTER:
                CardFamilyBinding cardOnboardingFamilyBinding = (CardFamilyBinding) _binding;
                cardOnboardingFamilyBinding.setData((FamilyHistory) object);
                FamilyHistoryHandler onboardingFamilyHistoryHandler =
                        new FamilyHistoryHandler((FamilyHistory) object,
                                cardOnboardingFamilyBinding, StaticConstants.ONBOARDING_FAMILY_ADAPTER);
                cardOnboardingFamilyBinding.setHandlers(onboardingFamilyHistoryHandler);
                break;

            case StaticConstants.ONBOARDING_LIFESTYLE_ADAPTER:
                CardLifestyleBinding cardOnboardingLifestyleBinding = (CardLifestyleBinding) _binding;
                cardOnboardingLifestyleBinding.setData((Lifestyle) object);
                LifestyleHandler onboardingLifestyleHandler =
                        new LifestyleHandler((Lifestyle) object,
                                cardOnboardingLifestyleBinding, StaticConstants.ONBOARDING_LIFESTYLE_ADAPTER);
                cardOnboardingLifestyleBinding.setHandlers(onboardingLifestyleHandler);
                break;

            case StaticConstants.ONBOARDING_GYNAECOLOGICAL_ADAPTER:
                CardGynaecologicalBinding cardOnboardingGynaecologicalBinding = (CardGynaecologicalBinding) _binding;
                cardOnboardingGynaecologicalBinding.setData((Gynaecology) object);
                GynaecologyHandler onboardingGynaecologyHandler =
                        new GynaecologyHandler((Gynaecology) object,
                                cardOnboardingGynaecologicalBinding, StaticConstants.ONBOARDING_GYNAECOLOGICAL_ADAPTER);
                cardOnboardingGynaecologicalBinding.setHandlers(onboardingGynaecologyHandler);
                break;

            case StaticConstants.MEDICAL_PROBLEM_ADAPTER:
                CardMedicalProblemBinding cardMedicalProblemBinding = (CardMedicalProblemBinding) _binding;
                cardMedicalProblemBinding.setData((MedicalProblem) object);
                MedicalProblemHandler medicalProblemHandler =
                        new MedicalProblemHandler((MedicalProblem) object,
                                cardMedicalProblemBinding, StaticConstants.MEDICAL_PROBLEM_ADAPTER);
                cardMedicalProblemBinding.setHandlers(medicalProblemHandler);
                break;

            case StaticConstants.FAMILY_ADAPTER:
                CardFamilyBinding cardFamilyBinding = (CardFamilyBinding) _binding;
                cardFamilyBinding.setData((FamilyHistory) object);
                FamilyHistoryHandler familyHistoryHandler =
                        new FamilyHistoryHandler((FamilyHistory) object,
                                cardFamilyBinding, StaticConstants.FAMILY_ADAPTER);
                cardFamilyBinding.setHandlers(familyHistoryHandler);
                break;

            case StaticConstants.LIFESTYLE_ADAPTER:
                CardLifestyleBinding cardLifestyleBinding = (CardLifestyleBinding) _binding;
                cardLifestyleBinding.setData((Lifestyle) object);
                LifestyleHandler lifestyleHandler =
                        new LifestyleHandler((Lifestyle) object,
                                cardLifestyleBinding, StaticConstants.LIFESTYLE_ADAPTER);
                cardLifestyleBinding.setHandlers(lifestyleHandler);
                break;

            case StaticConstants.GYNAECOLOGICAL_ADAPTER:
                CardGynaecologicalBinding cardGynaecologicalBinding = (CardGynaecologicalBinding) _binding;
                cardGynaecologicalBinding.setData((Gynaecology) object);
                GynaecologyHandler gynaecologyHandler =
                        new GynaecologyHandler((Gynaecology) object,
                                cardGynaecologicalBinding, StaticConstants.GYNAECOLOGICAL_ADAPTER);
                cardGynaecologicalBinding.setHandlers(gynaecologyHandler);
                break;


            case StaticConstants.ALLERGY_ADAPTER:
                CardAllergyBinding cardAllergyBinding = (CardAllergyBinding) _binding;
                cardAllergyBinding.setData((Allergy) object);
                AllergyHandler allergyHandler = new AllergyHandler((Allergy) object, cardAllergyBinding);
                cardAllergyBinding.setHandlers(allergyHandler);
                break;

            case StaticConstants.CHILDHOOD_ADAPTER:
                CardChildhoodHistoryBinding cardChildhoodHistoryBinding = (CardChildhoodHistoryBinding) _binding;
                cardChildhoodHistoryBinding.setData((ChildhoodHistory) object);
                ChildhoodHistoryHandler childhoodHistoryHandler = new ChildhoodHistoryHandler((ChildhoodHistory) object, cardChildhoodHistoryBinding);
                cardChildhoodHistoryBinding.setHandlers(childhoodHistoryHandler);
                break;

            case StaticConstants.SURGICAL_ADAPTER:
                CardSurgicalHistoryBinding cardSurgicalHistoryBinding = (CardSurgicalHistoryBinding) _binding;
                cardSurgicalHistoryBinding.setData((SurgicalHistory) object);
                SurgicalHistoryHandler surgicalHistoryHandler = new SurgicalHistoryHandler((SurgicalHistory) object, cardSurgicalHistoryBinding);
                cardSurgicalHistoryBinding.setHandlers(surgicalHistoryHandler);
                break;

            case StaticConstants.MEDICATION_ADAPTER:
                CardMedicationBinding cardMedicationBinding = (CardMedicationBinding) _binding;
                cardMedicationBinding.setData((Medication) object);
                MedicationHandler medicationHandler = new MedicationHandler((Medication) object, cardMedicationBinding);
                cardMedicationBinding.setHandlers(medicationHandler);
                break;


            case StaticConstants.HEALTH_SEARCH_ADAPTER:
                CardHsBinding cardHsBinding = (CardHsBinding) _binding;
                cardHsBinding.setData((HealthEducation) object);
                HealthEducationHandler healthEducationHandler = new HealthEducationHandler();
                cardHsBinding.setHandlers(healthEducationHandler);
                break;

            case StaticConstants.LIST_SEARCH_ADAPTER:
                ListItemSearchOptionBinding listItemSearchOptionBinding = (ListItemSearchOptionBinding) _binding;
                listItemSearchOptionBinding.setData((HealthEducation) object);
                HealthEducationHandler healthEducationHandlerList = new HealthEducationHandler();
                listItemSearchOptionBinding.setHandlers(healthEducationHandlerList);
                break;

            case StaticConstants.QUICK_UPLOAD_ADAPTER:
                ItemQuickUploadBinding itemQuickUploadBinding = (ItemQuickUploadBinding) _binding;
                itemQuickUploadBinding.setData((ImageModel) object);
                break;

            case StaticConstants.GALLERY_ADAPTER:
                CardGalleryBinding cardGalleryBinding = (CardGalleryBinding) _binding;
                cardGalleryBinding.setData((Album) object);
                AlbumHandler albumHandler = new AlbumHandler((Album) object, cardGalleryBinding);
                cardGalleryBinding.setHandlers(albumHandler);
                break;

            case StaticConstants.GALLERY_GRID_ADAPTER:
                CardGridGalleryBinding cardGridGalleryBinding = (CardGridGalleryBinding) _binding;
                cardGridGalleryBinding.setData((UploadImage) object);
                GalleryGridHandler galleryGridHandler = new GalleryGridHandler((UploadImage) object, cardGridGalleryBinding);
                cardGridGalleryBinding.setHandlers(galleryGridHandler);
                break;

            case StaticConstants.VITALS_BLOOD_PRESSURE_ADAPTER:
                ListItemVitalsBpBinding cardVitalsBpBinding = (ListItemVitalsBpBinding) _binding;
                cardVitalsBpBinding.setData((BloodPressure) object);
                BloodPressureHandler bloodPressureHandler = new BloodPressureHandler();
                cardVitalsBpBinding.setHandlers(bloodPressureHandler);
                break;

            case StaticConstants.VITALS_BLOOD_GLUCOSE_ADAPTER:
                ListItemVitalsBgBinding cardVitalsBgBinding = (ListItemVitalsBgBinding) _binding;
                cardVitalsBgBinding.setData((BloodGlucose) object);
                BloodGlucoseHandler bloodGlucoseHandler = new BloodGlucoseHandler();
                cardVitalsBgBinding.setHandlers(bloodGlucoseHandler);
                break;

            case StaticConstants.VITALS_BMI_ADAPTER:
                ListItemVitalsBmiBinding cardVitalsBmiBinding = (ListItemVitalsBmiBinding) _binding;
                cardVitalsBmiBinding.setData((BMI) object);
                BMIHandler bmiHandler = new BMIHandler();
                cardVitalsBmiBinding.setHandlers(bmiHandler);
                break;

            case StaticConstants.CHAT_ADAPTER:
                CardChatBinding chatBinding = (CardChatBinding) _binding;
                chatBinding.setData((Chat) object);
                ChatHandler chatHandler = new ChatHandler();
                chatBinding.setHandlers(chatHandler);
                break;

            case StaticConstants.CHAT_DOCTOR_ADAPTER:
                CardChatDoctorBinding cardChatDoctorBinding = (CardChatDoctorBinding) _binding;
                cardChatDoctorBinding.setData((Chat) object);
                ChatHandler chatHandler1 = new ChatHandler();
                cardChatDoctorBinding.setHandlers(chatHandler1);
                break;

            case StaticConstants.SYMPTOMS_LIST_ADAPTER:
                ListItemSymptomsBinding listItemSymptomsBinding = (ListItemSymptomsBinding) _binding;
                listItemSymptomsBinding.setData((Consultation) object);
                ConsultationHandler consultationHandler = new ConsultationHandler();
                listItemSymptomsBinding.setHandlers(consultationHandler);
                break;

            case StaticConstants.SELECTED_SYMPTOMS_LIST_ADAPTER:
                ListItemSymptomsSelectedBinding listItemSymptomsSelectedBinding = (ListItemSymptomsSelectedBinding) _binding;
                listItemSymptomsSelectedBinding.setData((Consultation) object);
                break;

            default:
                break;
        }
    }
}