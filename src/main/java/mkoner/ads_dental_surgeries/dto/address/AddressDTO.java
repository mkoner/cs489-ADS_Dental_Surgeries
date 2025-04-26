package mkoner.ads_dental_surgeries.dto.address;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Data transfer object representing an address")
public record AddressDTO(

        @NotBlank(message = "country is required for address")
        @Schema(description = "Country name", example = "United States")
        String country,

        @NotBlank(message = "city is required for address")
        @Schema(description = "City name", example = "New York")
        String city,

        @Schema(description = "Postal/ZIP code", example = "10001")
        String zipCode,

        @Schema(description = "Street name and number", example = "123 Main Street")
        String street

) {}


