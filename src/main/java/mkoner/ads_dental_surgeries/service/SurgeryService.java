package mkoner.ads_dental_surgeries.service;

import mkoner.ads_dental_surgeries.model.Surgery;

import java.util.List;

public interface SurgeryService {
    List<Surgery> getAllSurgeries();
    Surgery getSurgeryById(Long id);
    Surgery saveSurgery(Surgery surgery);
    void deleteSurgery(Long id);
    List<Surgery> findByCity(String city);
}

