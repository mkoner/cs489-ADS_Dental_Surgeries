package mkoner.ads_dental_surgeries.mapper;

import lombok.RequiredArgsConstructor;
import mkoner.ads_dental_surgeries.dto.patient.PatientRequestDTO;
import mkoner.ads_dental_surgeries.dto.patient.PatientResponseDTO;
import mkoner.ads_dental_surgeries.model.Patient;
import mkoner.ads_dental_surgeries.model.Role;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PatientMapper {
    private final AddressMapper addressMapper;
    public Patient mapToPatient(PatientRequestDTO patientRequestDTO) {
        var patient = new Patient(
                patientRequestDTO.firstName(),
                patientRequestDTO.lastName(),
                patientRequestDTO.phoneNumber(),
                patientRequestDTO.email(),
                patientRequestDTO.password(),
                patientRequestDTO.dateOfBirth(),
                new Role("PATIENT")
        );
        patient.setAddress(addressMapper.mapToAddress(patientRequestDTO.address()));
        return patient;
    }
    public PatientResponseDTO mapToPatientResponseDTO(Patient patient) {
        return new PatientResponseDTO(
                patient.getUserId(),
                patient.getFirstName(),
                patient.getLastName(),
                patient.getPhoneNumber(),
                patient.getEmailAddress(),
                patient.getDateOfBirth(),
                addressMapper.mapToAddressDTO(patient.getAddress())
        );
    }
}
