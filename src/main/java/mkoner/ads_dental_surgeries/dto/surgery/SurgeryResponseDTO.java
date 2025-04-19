package mkoner.ads_dental_surgeries.dto.surgery;

import mkoner.ads_dental_surgeries.dto.address.AddressDTO;
import mkoner.ads_dental_surgeries.model.Address;

public record SurgeryResponseDTO(
        Long surgeryId,
        String name,
        String phoneNumber,
        AddressDTO address
) {
}
