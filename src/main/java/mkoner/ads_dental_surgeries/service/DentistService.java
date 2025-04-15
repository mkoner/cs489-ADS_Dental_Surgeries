package mkoner.ads_dental_surgeries.service;

import mkoner.ads_dental_surgeries.model.Dentist;

import java.util.List;

public interface DentistService {
    List<Dentist> getAllDentists();
    Dentist getDentistById(Long id);
    Dentist saveDentist(Dentist dentist);
    void deleteDentist(Long id);
}

