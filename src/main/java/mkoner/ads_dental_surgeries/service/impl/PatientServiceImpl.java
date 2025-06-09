package mkoner.ads_dental_surgeries.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mkoner.ads_dental_surgeries.dto.patient.PatientFilterDTO;
import mkoner.ads_dental_surgeries.dto.patient.PatientRequestDTO;
import mkoner.ads_dental_surgeries.dto.patient.PatientResponseDTO;
import mkoner.ads_dental_surgeries.dto.patient.PatientUpdateDTO;
import mkoner.ads_dental_surgeries.exception.custom_exception.BadRequestException;
import mkoner.ads_dental_surgeries.exception.custom_exception.ResourceNotFoundException;
import mkoner.ads_dental_surgeries.filter_specification.PatientSpecification;
import mkoner.ads_dental_surgeries.mapper.AddressMapper;
import mkoner.ads_dental_surgeries.mapper.PatientMapper;
import mkoner.ads_dental_surgeries.model.Patient;
import mkoner.ads_dental_surgeries.model.Role;
import mkoner.ads_dental_surgeries.repository.PatientRepository;
import mkoner.ads_dental_surgeries.repository.RoleRepository;
import mkoner.ads_dental_surgeries.repository.UserRepository;
import mkoner.ads_dental_surgeries.service.PatientService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PatientServiceImpl implements PatientService {

    private final PatientRepository patientRepository;
    private final RoleRepository roleRepository;
    private final PatientMapper patientMapper;
    private final UserRepository userRepository;
    private final AddressMapper addressMapper;
    private final PasswordEncoder passwordEncoder;

    public List<PatientResponseDTO> getAllPatients() {
        log.debug("Fetching all patients from database");
        var patients = patientRepository.findAll().stream()
                .map(patientMapper::mapToPatientResponseDTO).toList();
        log.info("Fetched all {} patients from database successfully", patients.size());
        return patients;
    }

    public PatientResponseDTO getPatientById(Long id) {
        log.debug("Fetching patient with ID {}", id);
        var patient = patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient with id " + id + " not found"));
        log.info("Fetched patient with ID {}", id);
        return patientMapper.mapToPatientResponseDTO(patient);
    }


    @Transactional
    public PatientResponseDTO savePatient(PatientRequestDTO patient) {
        log.info("Attempting to register new patient with email: {}", patient.email());

        userRepository.findByEmailAddress(patient.email())
                .ifPresent(u -> {
                    log.warn("Email '{}' is already in use by another user (ID: {})", patient.email(), u.getUserId());
                    throw new BadRequestException("Email is already in use by another user");
                });

        userRepository.findByPhoneNumber(patient.phoneNumber())
                .ifPresent(u -> {
                    log.warn("Phone number '{}' is already in use by another user (ID: {})", patient.phoneNumber(), u.getUserId());
                    throw new BadRequestException("Phone number is already in use by another user");
                });

        Patient newPatient = patientMapper.mapToPatient(patient);
        newPatient.setPassword(passwordEncoder.encode(newPatient.getPassword()));

        String roleName = newPatient.getRole().getRoleName();
        Role role = roleRepository.findByRoleName(roleName)
                .orElseGet(() -> {
                    log.info("Role '{}' not found, creating new role", roleName);
                    return roleRepository.save(new Role(roleName));
                });

        newPatient.setRole(role);

        Patient savedPatient = patientRepository.save(newPatient);
        log.info("Successfully registered new patient with ID {}", savedPatient.getUserId());

        return patientMapper.mapToPatientResponseDTO(savedPatient);
    }

    public void deletePatient(Long id) {
        log.info("Attempting to delete patient with ID {}", id);
        try {
            patientRepository.deleteById(id);
            log.info("Successfully deleted patient with ID {}", id);
        } catch (DataIntegrityViolationException e) {
            log.warn("Deletion failed for patient ID {} due to data integrity violation: {}", id, e.getMessage());
            throw new BadRequestException("Deletion failed due to associated records");
        } catch (Exception e) {
            log.error("Unexpected error occurred while deleting patient ID {}: {}", id, e.getMessage(), e);
            throw new BadRequestException("Deletion failed");
        }
    }

    @Override
    public PatientResponseDTO updatePatient(Long id, PatientUpdateDTO patientRequestDTO) {
        log.info("Attempting to update patient with ID {}", id);

        var existingPatient = patientRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Patient with ID {} not found", id);
                    return new ResourceNotFoundException("User with id " + id + " not found");
                });

        userRepository.findByEmailAddress(patientRequestDTO.email())
                .filter(user -> !user.getUserId().equals(id))
                .ifPresent(u -> {
                    log.warn("Email '{}' is already in use by another user (ID: {})", patientRequestDTO.email(), u.getUserId());
                    throw new BadRequestException("Email is already in use by another user");
                });

        userRepository.findByPhoneNumber(patientRequestDTO.phoneNumber())
                .filter(user -> !user.getUserId().equals(id))
                .ifPresent(u -> {
                    log.warn("Phone number '{}' is already in use by another user (ID: {})", patientRequestDTO.phoneNumber(), u.getUserId());
                    throw new BadRequestException("Phone number is already in use by another user");
                });

        existingPatient.setFirstName(patientRequestDTO.firstName());
        existingPatient.setLastName(patientRequestDTO.lastName());
        existingPatient.setPhoneNumber(patientRequestDTO.phoneNumber());
        existingPatient.setEmailAddress(patientRequestDTO.email());
        existingPatient.setDateOfBirth(patientRequestDTO.dateOfBirth());
        existingPatient.setAddress(addressMapper.mapToAddress(patientRequestDTO.address()));

        Patient updatedPatient = patientRepository.save(existingPatient);
        log.info("Successfully updated patient with ID {}", updatedPatient.getUserId());

        return patientMapper.mapToPatientResponseDTO(updatedPatient);
    }

    public Page<PatientResponseDTO> getFilteredPatientsWithPagination(PatientFilterDTO filterDTO, Pageable pageable) {
        log.debug("Get patients with filters and pagination: {}", filterDTO);
        Specification<Patient> spec = Specification.where(null);

        if (filterDTO.firstName() != null) spec = spec.and(PatientSpecification.hasFirstName(filterDTO.firstName()));
        if (filterDTO.lastName() != null) spec = spec.and(PatientSpecification.hasLastName(filterDTO.lastName()));
        if (filterDTO.phoneNumber() != null) spec = spec.and(PatientSpecification.hasPhoneNumber(filterDTO.phoneNumber()));
        if (filterDTO.emailAddress() != null) spec = spec.and(PatientSpecification.hasEmail(filterDTO.emailAddress()));
        if (filterDTO.city() != null) spec = spec.and(PatientSpecification.hasCity(filterDTO.city()));
        if (filterDTO.country() != null) spec = spec.and(PatientSpecification.hasCountry(filterDTO.country()));

        var patients = patientRepository.findAll(spec, pageable);
        log.info("Successfully retrieved {} patients on page {}", patients.getTotalElements(), pageable.getPageNumber());
        return patients.map(patientMapper::mapToPatientResponseDTO);
    }


}

