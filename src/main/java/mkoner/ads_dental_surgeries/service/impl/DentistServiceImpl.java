package mkoner.ads_dental_surgeries.service.impl;

import mkoner.ads_dental_surgeries.model.Dentist;
import mkoner.ads_dental_surgeries.model.Role;
import mkoner.ads_dental_surgeries.model.User;
import mkoner.ads_dental_surgeries.repository.DentistRepository;
import mkoner.ads_dental_surgeries.repository.RoleRepository;
import mkoner.ads_dental_surgeries.service.DentistService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DentistServiceImpl implements DentistService {

    private final DentistRepository dentistRepository;
    private final RoleRepository roleRepository;

    public DentistServiceImpl(DentistRepository dentistRepository, RoleRepository roleRepository) {
        this.dentistRepository = dentistRepository;
        this.roleRepository = roleRepository;
    }

    public List<Dentist> getAllDentists() {
        return dentistRepository.findAll();
    }

    public Dentist getDentistById(Long id) {
        return dentistRepository.findById(id).orElse(null);
    }

    @Transactional
    public Dentist saveDentist(Dentist dentist) {
        String roleName = dentist.getRole().getRoleName();
        Role role = roleRepository.findByRoleName(roleName)
                .orElseGet(() -> roleRepository.save(new Role(roleName)));

        dentist.setRole(role);
        return dentistRepository.save(dentist);
    }

    public void deleteDentist(Long id) {
        dentistRepository.deleteById(id);
    }
}

