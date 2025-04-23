package mkoner.ads_dental_surgeries.service.impl;

import lombok.RequiredArgsConstructor;
import mkoner.ads_dental_surgeries.dto.dentist.DentistRequestDTO;
import mkoner.ads_dental_surgeries.dto.dentist.DentistResponseDTO;
import mkoner.ads_dental_surgeries.dto.dentist.DentistUpdateDTO;
import mkoner.ads_dental_surgeries.exception.custom_exception.BadRequestException;
import mkoner.ads_dental_surgeries.exception.custom_exception.ResourceNotFoundException;
import mkoner.ads_dental_surgeries.mapper.DentistMapper;
import mkoner.ads_dental_surgeries.model.Dentist;
import mkoner.ads_dental_surgeries.model.Role;
import mkoner.ads_dental_surgeries.repository.DentistRepository;
import mkoner.ads_dental_surgeries.repository.RoleRepository;
import mkoner.ads_dental_surgeries.repository.UserRepository;
import mkoner.ads_dental_surgeries.service.DentistService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DentistServiceImpl implements DentistService {

    private final DentistRepository dentistRepository;
    private final RoleRepository roleRepository;
    private final DentistMapper dentistMapper;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public List<DentistResponseDTO> getAllDentists() {
        return dentistRepository.findAll().stream()
                .map(dentistMapper::mapToDentistResponseDTO).toList();
    }

    public DentistResponseDTO getDentistById(Long id) {
        var dentist = dentistRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Dentist with id " + id + " not found"));
        return dentistMapper.mapToDentistResponseDTO(dentist);
    }

    @Transactional
    public DentistResponseDTO saveDentist(DentistRequestDTO dentistRequestDTO) {
        // Check if email is taken by another user
        userRepository.findByEmailAddress(dentistRequestDTO.email())
                .ifPresent(u -> { throw new BadRequestException("Email is already in use by another user"); });

        // Check if phone number is taken by another user
        userRepository.findByPhoneNumber(dentistRequestDTO.phoneNumber())
                .ifPresent(u -> { throw new BadRequestException("Phone number is already in use by another user"); });

        Dentist dentist = dentistMapper.mapToDentist(dentistRequestDTO);
        dentist.setPassword(passwordEncoder.encode(dentist.getPassword()));
        String roleName = dentist.getRole().getRoleName();
        Role role = roleRepository.findByRoleName(roleName)
                .orElseGet(() -> roleRepository.save(new Role(roleName)));

        dentist.setRole(role);
        return dentistMapper.mapToDentistResponseDTO(dentistRepository.save(dentist));
    }

    public void deleteDentist(Long id) {
        try{
            dentistRepository.deleteById(id);
        }
        catch (DataIntegrityViolationException e) {
            System.out.println(e);
            throw new BadRequestException("Deletion failed due to associated records");
        }
        catch (Exception e){
            System.out.println(e);
            throw new BadRequestException("Deletion failed");
        }
    }

    @Override
    public DentistResponseDTO updateDentist(Long id, DentistUpdateDTO dentistRequestDTO) {
        var existingDentist = dentistRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User with id " + id + " not found"));

        // Check if email is taken by another user
        userRepository.findByEmailAddress(dentistRequestDTO.email())
                .filter(user -> !user.getUserId().equals(id))
                .ifPresent(u -> { throw new BadRequestException("Email is already in use by another user"); });

        // Check if phone number is taken by another user
        userRepository.findByPhoneNumber(dentistRequestDTO.phoneNumber())
                .filter(user -> !user.getUserId().equals(id))
                .ifPresent(u -> { throw new BadRequestException("Phone number is already in use by another user"); });

        existingDentist.setFirstName(dentistRequestDTO.firstName());
        existingDentist.setLastName(dentistRequestDTO.lastName());
        existingDentist.setPhoneNumber(dentistRequestDTO.phoneNumber());
        existingDentist.setEmailAddress(dentistRequestDTO.email());
        existingDentist.setSpecialization(dentistRequestDTO.specialization());
        return dentistMapper.mapToDentistResponseDTO(dentistRepository.save(existingDentist));
    }
}

