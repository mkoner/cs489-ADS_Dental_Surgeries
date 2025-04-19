package mkoner.ads_dental_surgeries.dto.surgery;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import mkoner.ads_dental_surgeries.dto.address.AddressDTO;
import mkoner.ads_dental_surgeries.model.Address;

public record SurgeryRequestDTO(
        @NotBlank(message = "Surgery name is required")
        String name,
        @Pattern(regexp = "^[0-9]{10}$", message = "Phone number must be 10 digits")
        @NotBlank(message = "phone number is required")
        String phoneNumber,
        @NotNull(message = "Surgery address is required")
        AddressDTO address
) {
}
