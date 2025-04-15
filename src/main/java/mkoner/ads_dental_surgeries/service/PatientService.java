package mkoner.ads_dental_surgeries.service;

import mkoner.ads_dental_surgeries.model.Patient;

import java.util.List;

public interface PatientService {
    List<Patient> getAllPatients();
    Patient getPatientById(Long id);
    Patient savePatient(Patient patient);
    void deletePatient(Long id);
}

