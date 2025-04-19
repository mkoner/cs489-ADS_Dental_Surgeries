package mkoner.ads_dental_surgeries.mapper;


import mkoner.ads_dental_surgeries.dto.dentist.DentistRequestDTO;
import mkoner.ads_dental_surgeries.dto.dentist.DentistResponseDTO;
import mkoner.ads_dental_surgeries.model.Dentist;
import mkoner.ads_dental_surgeries.model.Role;
import org.springframework.stereotype.Component;

@Component
public class DentistMapper {
    public Dentist mapToDentist(DentistRequestDTO dentistRequestDTO) {
        return new Dentist(
                dentistRequestDTO.firstName(),
                dentistRequestDTO.lastName(),
                dentistRequestDTO.phoneNumber(),
                dentistRequestDTO.email(),
                dentistRequestDTO.password(),
                dentistRequestDTO.specialization(),
                new Role("Dentist")
        );
    }
    public DentistResponseDTO mapToDentistResponseDTO(Dentist dentist) {
        return new DentistResponseDTO(
                dentist.getUserId(),
                dentist.getFirstName(),
                dentist.getLastName(),
                dentist.getPhoneNumber(),
                dentist.getEmailAddress(),
                dentist.getSpecialization()
        );
    }
}
