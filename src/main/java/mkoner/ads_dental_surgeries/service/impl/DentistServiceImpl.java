package mkoner.ads_dental_surgeries.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mkoner.ads_dental_surgeries.dto.dentist.DentistFilterDTO;
import mkoner.ads_dental_surgeries.dto.dentist.DentistRequestDTO;
import mkoner.ads_dental_surgeries.dto.dentist.DentistResponseDTO;
import mkoner.ads_dental_surgeries.dto.dentist.DentistUpdateDTO;
import mkoner.ads_dental_surgeries.exception.custom_exception.BadRequestException;
import mkoner.ads_dental_surgeries.exception.custom_exception.ResourceNotFoundException;
import mkoner.ads_dental_surgeries.filter_specification.DentistSpecification;
import mkoner.ads_dental_surgeries.mapper.DentistMapper;
import mkoner.ads_dental_surgeries.model.Dentist;
import mkoner.ads_dental_surgeries.model.Role;
import mkoner.ads_dental_surgeries.repository.DentistRepository;
import mkoner.ads_dental_surgeries.repository.RoleRepository;
import mkoner.ads_dental_surgeries.repository.UserRepository;
import mkoner.ads_dental_surgeries.service.DentistService;
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
public class DentistServiceImpl implements DentistService {

    private final DentistRepository dentistRepository;
    private final RoleRepository roleRepository;
    private final DentistMapper dentistMapper;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public List<DentistResponseDTO> getAllDentists() {
        log.debug("Fetching all dentists from database");
        List<DentistResponseDTO> dentist =  dentistRepository.findAll().stream()
                .map(dentistMapper::mapToDentistResponseDTO).toList();
        log.info("Fetched all {} dentists from database", dentist.size());
        return dentist;
    }

    public DentistResponseDTO getDentistById(Long id) {
        log.debug("Fetching dentist by id: {}", id);
        var dentist = dentistRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Dentist with id {} not found", id);
                    return new ResourceNotFoundException("Dentist with id " + id + " not found");
                });
        log.info("Fetched dentist by id: {}", id);
        return dentistMapper.mapToDentistResponseDTO(dentist);
    }

    @Transactional
    public DentistResponseDTO saveDentist(DentistRequestDTO dentistRequestDTO) {
        log.debug("Saving dentist: {}", dentistRequestDTO);
        // Check if email is taken by another user
        userRepository.findByEmailAddress(dentistRequestDTO.email())
                .ifPresent(u -> {
                    log.warn("User with email {} already exists", u.getEmailAddress());
                    throw new BadRequestException("Email is already in use by another user");
                });

        // Check if phone number is taken by another user
        userRepository.findByPhoneNumber(dentistRequestDTO.phoneNumber())
                .ifPresent(u -> {
                    log.warn("User with phone number {} already exists", u.getPhoneNumber());
                    throw new BadRequestException("Phone number is already in use by another user");
                });

        Dentist dentist = dentistMapper.mapToDentist(dentistRequestDTO);
        dentist.setPassword(passwordEncoder.encode(dentist.getPassword()));
        String roleName = dentist.getRole().getRoleName();
        Role role = roleRepository.findByRoleName(roleName)
                .orElseGet(() -> roleRepository.save(new Role(roleName)));

        dentist.setRole(role);
        Dentist saved =  dentistRepository.save(dentist);
        log.info("Saved dentist: {}", saved);
        return dentistMapper.mapToDentistResponseDTO(saved);
    }

    public void deleteDentist(Long id) {
        log.info("Attempting to delete dentist with ID {}", id);

        try {
            dentistRepository.deleteById(id);
            log.info("Successfully deleted dentist with ID {}", id);
        } catch (DataIntegrityViolationException e) {
            log.warn("Deletion failed for dentist ID {} due to associated records: {}", id, e.getMessage());
            throw new BadRequestException("Deletion failed due to associated records");
        } catch (Exception e) {
            log.error("Unexpected error occurred while deleting dentist ID {}: {}", id, e.getMessage(), e);
            throw new BadRequestException("Deletion failed");
        }
    }

    @Override
    public DentistResponseDTO updateDentist(Long id, DentistUpdateDTO dentistRequestDTO) {
        log.info("Updating dentist with ID {}", id);
        log.debug("Update request payload: {}", dentistRequestDTO);

        var existingDentist = dentistRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Dentist with ID {} not found", id);
                    return new ResourceNotFoundException("User with id " + id + " not found");
                });

        // Check if email is taken by another user
        userRepository.findByEmailAddress(dentistRequestDTO.email())
                .filter(user -> !user.getUserId().equals(id))
                .ifPresent(user -> {
                    log.warn("Email '{}' is already used by another user with ID {}", dentistRequestDTO.email(), user.getUserId());
                    throw new BadRequestException("Email is already in use by another user");
                });

        // Check if phone number is taken by another user
        userRepository.findByPhoneNumber(dentistRequestDTO.phoneNumber())
                .filter(user -> !user.getUserId().equals(id))
                .ifPresent(user -> {
                    log.warn("Phone number '{}' is already used by another user with ID {}", dentistRequestDTO.phoneNumber(), user.getUserId());
                    throw new BadRequestException("Phone number is already in use by another user");
                });

        existingDentist.setFirstName(dentistRequestDTO.firstName());
        existingDentist.setLastName(dentistRequestDTO.lastName());
        existingDentist.setPhoneNumber(dentistRequestDTO.phoneNumber());
        existingDentist.setEmailAddress(dentistRequestDTO.email());
        existingDentist.setSpecialization(dentistRequestDTO.specialization());

        Dentist updatedDentist = dentistRepository.save(existingDentist);
        log.info("Successfully updated dentist with ID {}", updatedDentist.getUserId());

        return dentistMapper.mapToDentistResponseDTO(updatedDentist);
    }

    public Page<DentistResponseDTO> getFilteredDentists(DentistFilterDTO filterDTO, Pageable pageable) {
        log.debug("Fetching filtered dentists with filter: {}", filterDTO);
        Specification<Dentist> spec = Specification.where(null);

        if (filterDTO.firstName() != null) spec = spec.and(DentistSpecification.hasFirstName(filterDTO.firstName()));
        if (filterDTO.lastName() != null) spec = spec.and(DentistSpecification.hasLastName(filterDTO.lastName()));
        if (filterDTO.phoneNumber() != null) spec = spec.and(DentistSpecification.hasPhoneNumber(filterDTO.phoneNumber()));
        if (filterDTO.emailAddress() != null) spec = spec.and(DentistSpecification.hasEmail(filterDTO.emailAddress()));
        if (filterDTO.specialization() != null) spec = spec.and(DentistSpecification.hasSpecialization(filterDTO.specialization()));

        var dentists = dentistRepository.findAll(spec, pageable);
        log.info("Fetched dentists with filter: got {} on page {}", dentists.getNumberOfElements(), pageable.getPageNumber());
        return dentists.map(dentistMapper::mapToDentistResponseDTO);
    }

}

