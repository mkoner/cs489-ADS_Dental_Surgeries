package mkoner.ads_dental_surgeries.service.impl;

import mkoner.ads_dental_surgeries.model.Surgery;
import mkoner.ads_dental_surgeries.repository.SurgeryRepository;
import mkoner.ads_dental_surgeries.service.SurgeryService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SurgeryServiceImpl implements SurgeryService {

    private final SurgeryRepository surgeryRepository;

    public SurgeryServiceImpl(SurgeryRepository surgeryRepository) {
        this.surgeryRepository = surgeryRepository;
    }

    public List<Surgery> getAllSurgeries() {
        return surgeryRepository.findAll();
    }

    public Surgery getSurgeryById(Long id) {
        return surgeryRepository.findById(id).orElse(null);
    }

    public Surgery saveSurgery(Surgery surgery) {
        return surgeryRepository.save(surgery);
    }

    public void deleteSurgery(Long id) {
        surgeryRepository.deleteById(id);
    }

    public List<Surgery> findByCity(String city) {
        return surgeryRepository.findSurgeriesByAddressCityIgnoreCase(city);
    }
}

