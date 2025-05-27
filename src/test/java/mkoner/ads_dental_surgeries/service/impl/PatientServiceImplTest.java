package mkoner.ads_dental_surgeries.service.impl;

import mkoner.ads_dental_surgeries.dto.address.AddressDTO;
import mkoner.ads_dental_surgeries.dto.patient.PatientRequestDTO;
import mkoner.ads_dental_surgeries.dto.patient.PatientResponseDTO;
import mkoner.ads_dental_surgeries.dto.patient.PatientUpdateDTO;
import mkoner.ads_dental_surgeries.exception.custom_exception.BadRequestException;
import mkoner.ads_dental_surgeries.exception.custom_exception.ResourceNotFoundException;
import mkoner.ads_dental_surgeries.mapper.AddressMapper;
import mkoner.ads_dental_surgeries.mapper.PatientMapper;
import mkoner.ads_dental_surgeries.model.Address;
import mkoner.ads_dental_surgeries.model.Patient;
import mkoner.ads_dental_surgeries.model.Role;
import mkoner.ads_dental_surgeries.model.User;
import mkoner.ads_dental_surgeries.repository.PatientRepository;
import mkoner.ads_dental_surgeries.repository.RoleRepository;
import mkoner.ads_dental_surgeries.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PatientServiceImplTest {

    @InjectMocks
    private PatientServiceImpl patientService;

    @Mock
    private PatientRepository patientRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private PatientMapper patientMapper;
    @Mock
    private UserRepository userRepository;
    @Mock
    private AddressMapper addressMapper;
    @Mock
    private PasswordEncoder passwordEncoder;

    private Patient patient;
    private PatientRequestDTO requestDTO;
    private PatientUpdateDTO updateDTO;
    private PatientResponseDTO responseDTO;
    private AddressDTO addressDTO;

    @BeforeEach
    void setUp() {
        patient = new Patient("Alex", "Zokou", "1234567890", "alex@gmail.com",
                "password", LocalDate.of(1990, 1, 1), new Role("PATIENT"));
        patient.setUserId(1L);
        addressDTO = new AddressDTO("City", "Country", "890", "st");
        patient.setAddress(new Address("City", "Country", "890", "st"));
        requestDTO = new PatientRequestDTO(
                "Alex", "Zokou", "1234567890", "alex@gmail.com",
                "password", LocalDate.of(1990, 1, 1), addressDTO
        );

        updateDTO = new PatientUpdateDTO(
                "Alex", "Zokou", "1234567890", "alex@gmail.com",
                LocalDate.of(1990, 1, 1), addressDTO
        );

        responseDTO = new PatientResponseDTO(
                1L, "Alex", "Zokou", "1234567890", "alex@gmail.com",
                 LocalDate.of(1990, 1, 1), addressDTO
        );
    }

    @DisplayName("Should return all patients successfully")
    @Test
    void testGetAllPatients() {
        List<Patient> patients = List.of(patient);
        when(patientRepository.findAll()).thenReturn(patients);
        when(patientMapper.mapToPatientResponseDTO(patient)).thenReturn(responseDTO);

        List<PatientResponseDTO> result = patientService.getAllPatients();

        assertEquals(1, result.size());
        assertEquals("Alex", result.get(0).firstName());
        verify(patientRepository).findAll();
    }

    @DisplayName("Should return patient by ID when found")
    @Test
    void testGetPatientById_Found() {
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(patientMapper.mapToPatientResponseDTO(patient)).thenReturn(responseDTO);

        PatientResponseDTO result = patientService.getPatientById(1L);

        assertEquals("Alex", result.firstName());
    }

    @DisplayName("Should throw exception when patient by ID is not found")
    @Test
    void testGetPatientById_NotFound() {
        when(patientRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> patientService.getPatientById(1L));
    }

    @DisplayName("Should save patient when email and phone are unique")
    @Test
    void testSavePatient_Success() {
        when(userRepository.findByEmailAddress(requestDTO.email())).thenReturn(Optional.empty());
        when(userRepository.findByPhoneNumber(requestDTO.phoneNumber())).thenReturn(Optional.empty());
        when(patientMapper.mapToPatient(requestDTO)).thenReturn(patient);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(roleRepository.findByRoleName("PATIENT")).thenReturn(Optional.of(new Role("PATIENT")));
        when(patientRepository.save(patient)).thenReturn(patient);
        when(patientMapper.mapToPatientResponseDTO(patient)).thenReturn(responseDTO);

        PatientResponseDTO savedPatient = patientService.savePatient(requestDTO);

        assertEquals("Alex", savedPatient.firstName());
    }

    @DisplayName("Save Patient: Should throw BadRequestException exception when email is already in use")
    @Test
    void testSavePatient_EmailExists() {
        when(userRepository.findByEmailAddress(requestDTO.email())).thenReturn(Optional.of(new User()));

        assertThrows(BadRequestException.class, () -> patientService.savePatient(requestDTO));
    }

    @DisplayName("Save Patient: Should throw BadRequestException exception when phone number is already in use")
    @Test
    void testSavePatient_PhoneNumberExists() {
        when(userRepository.findByEmailAddress(requestDTO.email())).thenReturn(Optional.empty());
        when(userRepository.findByPhoneNumber(requestDTO.phoneNumber())).thenReturn(Optional.of(new User()));

        assertThrows(BadRequestException.class, () -> patientService.savePatient(requestDTO));
    }

    @DisplayName("Delete: Should delete patient successfully")
    @Test
    void testDeletePatient_Success() {
        doNothing().when(patientRepository).deleteById(1L);
        assertDoesNotThrow(() -> patientService.deletePatient(1L));
    }

    @DisplayName("Delete: Should throw BadRequestException exception when DataIntegrityViolation occurs on delete")
    @Test
    void testDeletePatient_DataIntegrityViolation() {
        doThrow(new DataIntegrityViolationException("")).when(patientRepository).deleteById(1L);
        assertThrows(BadRequestException.class, () -> patientService.deletePatient(1L));
    }

    @DisplayName("Should update patient successfully when valid")
    @Test
    void testUpdatePatient_Success() {
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(userRepository.findByEmailAddress(updateDTO.email())).thenReturn(Optional.empty());
        when(userRepository.findByPhoneNumber(updateDTO.phoneNumber())).thenReturn(Optional.empty());
        when(addressMapper.mapToAddress(updateDTO.address())).thenReturn(new Address());
        when(patientRepository.save(patient)).thenReturn(patient);
        when(patientMapper.mapToPatientResponseDTO(patient)).thenReturn(responseDTO);

        PatientResponseDTO updated = patientService.updatePatient(1L, updateDTO);

        assertEquals("Alex", updated.firstName());
    }


}