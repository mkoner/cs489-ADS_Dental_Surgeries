package mkoner.ads_dental_surgeries.service.impl;

import lombok.RequiredArgsConstructor;
import mkoner.ads_dental_surgeries.dto.patient.PatientRequestDTO;
import mkoner.ads_dental_surgeries.dto.patient.PatientResponseDTO;
import mkoner.ads_dental_surgeries.dto.patient.PatientUpdateDTO;
import mkoner.ads_dental_surgeries.exception.BadRequestException;
import mkoner.ads_dental_surgeries.exception.ResourceNotFoundException;
import mkoner.ads_dental_surgeries.mapper.AddressMapper;
import mkoner.ads_dental_surgeries.mapper.PatientMapper;
import mkoner.ads_dental_surgeries.model.Dentist;
import mkoner.ads_dental_surgeries.model.Patient;
import mkoner.ads_dental_surgeries.model.Role;
import mkoner.ads_dental_surgeries.repository.PatientRepository;
import mkoner.ads_dental_surgeries.repository.RoleRepository;
import mkoner.ads_dental_surgeries.repository.UserRepository;
import mkoner.ads_dental_surgeries.service.PatientService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PatientServiceImpl implements PatientService {

    private final PatientRepository patientRepository;
    private final RoleRepository roleRepository;
    private final PatientMapper patientMapper;
    private final UserRepository userRepository;
    private final AddressMapper addressMapper;
    private final PasswordEncoder passwordEncoder;

    public List<PatientResponseDTO> getAllPatients() {
        return patientRepository.findAll().stream()
                .map(patientMapper::mapToPatientResponseDTO).toList();
    }

    public PatientResponseDTO getPatientById(Long id) {
        var patient = patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient with id " + id + " not found"));;
        return patientMapper.mapToPatientResponseDTO(patient);
    }


    @Transactional
    public PatientResponseDTO savePatient(PatientRequestDTO patient) {
        //check if email is used
        userRepository.findByEmailAddress(patient.email())
                .ifPresent(u -> { throw new BadRequestException("Email is already in use by another user"); });

        // Check if phone number is taken by another user
        userRepository.findByPhoneNumber(patient.phoneNumber())
                .ifPresent(u -> { throw new BadRequestException("Phone number is already in use by another user"); });
        Patient newPatient = patientMapper.mapToPatient(patient);
        newPatient.setPassword(passwordEncoder.encode(newPatient.getPassword()));
        String roleName = newPatient.getRole().getRoleName();
        Role role = roleRepository.findByRoleName(roleName)
                .orElseGet(() -> roleRepository.save(new Role(roleName)));

        newPatient.setRole(role);
        return patientMapper.mapToPatientResponseDTO(patientRepository.save(newPatient));
    }

    public void deletePatient(Long id) {
        patientRepository.deleteById(id);
    }

    @Override
    public PatientResponseDTO updatePatient(Long id, PatientUpdateDTO patientRequestDTO) {
        var existingPatient = patientRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User with id " + id + " not found"));

        // Check if email is taken by another user
        userRepository.findByEmailAddress(patientRequestDTO.email())
                .filter(user -> !user.getUserId().equals(id))
                .ifPresent(u -> { throw new BadRequestException("Email is already in use by another user"); });

        // Check if phone number is taken by another user
        userRepository.findByPhoneNumber(patientRequestDTO.phoneNumber())
                .filter(user -> !user.getUserId().equals(id))
                .ifPresent(u -> { throw new BadRequestException("Phone number is already in use by another user"); });

        existingPatient.setFirstName(patientRequestDTO.firstName());
        existingPatient.setLastName(patientRequestDTO.lastName());
        existingPatient.setPhoneNumber(patientRequestDTO.phoneNumber());
        existingPatient.setEmailAddress(patientRequestDTO.email());
        existingPatient.setDateOfBirth(patientRequestDTO.dateOfBirth());
        existingPatient.setAddress(addressMapper.mapToAddress(patientRequestDTO.address()));
        return patientMapper.mapToPatientResponseDTO(patientRepository.save(existingPatient));
    }

}

