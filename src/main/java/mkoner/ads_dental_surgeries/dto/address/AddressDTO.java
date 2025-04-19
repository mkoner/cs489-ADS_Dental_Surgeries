package mkoner.ads_dental_surgeries.dto.address;

import jakarta.validation.constraints.NotBlank;

public record AddressDTO(
        @NotBlank(message = "country is required for address")
        String country,
        @NotBlank(message = "country is required for address")
        String city,
        String zipCode,
        String street
) {
}
