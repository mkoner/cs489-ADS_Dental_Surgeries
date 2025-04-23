package mkoner.ads_dental_surgeries.service;

import mkoner.ads_dental_surgeries.dto.patient.PatientFilterDTO;
import mkoner.ads_dental_surgeries.dto.patient.PatientRequestDTO;
import mkoner.ads_dental_surgeries.dto.patient.PatientResponseDTO;
import mkoner.ads_dental_surgeries.dto.patient.PatientUpdateDTO;
import mkoner.ads_dental_surgeries.model.Patient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PatientService {
    List<PatientResponseDTO> getAllPatients();
    PatientResponseDTO getPatientById(Long id);
    PatientResponseDTO savePatient(PatientRequestDTO patient);
    void deletePatient(Long id);
    PatientResponseDTO updatePatient(Long id, PatientUpdateDTO patient);
    Page<PatientResponseDTO> getFilteredPatientsWithPagination(PatientFilterDTO filterDTO, Pageable pageable);
}

