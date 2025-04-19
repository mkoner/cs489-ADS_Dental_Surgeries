package mkoner.ads_dental_surgeries.service;

import mkoner.ads_dental_surgeries.dto.surgery.SurgeryRequestDTO;
import mkoner.ads_dental_surgeries.dto.surgery.SurgeryResponseDTO;
import mkoner.ads_dental_surgeries.model.Surgery;

import java.util.List;

public interface SurgeryService {
    List<SurgeryResponseDTO> getAllSurgeries();
    SurgeryResponseDTO getSurgeryById(Long id);
    SurgeryResponseDTO saveSurgery(SurgeryRequestDTO surgery);
    void deleteSurgery(Long id);
    List<SurgeryResponseDTO> findByCity(String city);
    SurgeryResponseDTO updateSurgery(Long id, SurgeryRequestDTO surgery);
}

