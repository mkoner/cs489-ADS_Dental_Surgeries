package mkoner.ads_dental_surgeries.service.impl;

import mkoner.ads_dental_surgeries.model.Dentist;
import mkoner.ads_dental_surgeries.model.Patient;
import mkoner.ads_dental_surgeries.model.Role;
import mkoner.ads_dental_surgeries.repository.PatientRepository;
import mkoner.ads_dental_surgeries.repository.RoleRepository;
import mkoner.ads_dental_surgeries.service.PatientService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PatientServiceImpl implements PatientService {

    private final PatientRepository patientRepository;
    private final RoleRepository roleRepository;

    public PatientServiceImpl(PatientRepository patientRepository, RoleRepository roleRepository) {
        this.patientRepository = patientRepository;
        this.roleRepository = roleRepository;
    }

    public List<Patient> getAllPatients() {
        return patientRepository.findAll();
    }

    public Patient getPatientById(Long id) {
        return patientRepository.findById(id).orElse(null);
    }


    @Transactional
    public Patient savePatient(Patient patient) {
        String roleName = patient.getRole().getRoleName();
        Role role = roleRepository.findByRoleName(roleName)
                .orElseGet(() -> roleRepository.save(new Role(roleName)));

        patient.setRole(role);
        return patientRepository.save(patient);
    }

    public void deletePatient(Long id) {
        patientRepository.deleteById(id);
    }
}

