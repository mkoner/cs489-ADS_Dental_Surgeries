package mkoner.ads_dental_surgeries.service;

import mkoner.ads_dental_surgeries.dto.dentist.DentistFilterDTO;
import mkoner.ads_dental_surgeries.dto.dentist.DentistRequestDTO;
import mkoner.ads_dental_surgeries.dto.dentist.DentistResponseDTO;
import mkoner.ads_dental_surgeries.dto.dentist.DentistUpdateDTO;
import mkoner.ads_dental_surgeries.model.Dentist;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface DentistService {
    List<DentistResponseDTO> getAllDentists();
    DentistResponseDTO getDentistById(Long id);
    DentistResponseDTO saveDentist(DentistRequestDTO dentist);
    void deleteDentist(Long id);
    DentistResponseDTO updateDentist(Long id, DentistUpdateDTO dentist);
    Page<DentistResponseDTO> getFilteredDentists(DentistFilterDTO filterDTO, Pageable pageable);
}

