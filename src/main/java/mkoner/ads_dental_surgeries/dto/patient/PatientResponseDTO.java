package mkoner.ads_dental_surgeries.dto.patient;

import mkoner.ads_dental_surgeries.dto.address.AddressDTO;
import mkoner.ads_dental_surgeries.model.Address;

import java.time.LocalDate;

public record PatientResponseDTO(
        Long userId,
        String firstName,
        String lastName,
        String phoneNumber,
        String email,
        LocalDate dateOfBirth,
        AddressDTO address
) {
}
