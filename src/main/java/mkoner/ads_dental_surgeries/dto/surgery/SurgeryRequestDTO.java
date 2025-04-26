package mkoner.ads_dental_surgeries.dto.surgery;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import mkoner.ads_dental_surgeries.dto.address.AddressDTO;

@Schema(description = "DTO used to create a new surgery center with name, contact, and address")
public record SurgeryRequestDTO(

        @NotBlank(message = "Surgery name is required")
        @Schema(description = "Name of the surgery center", example = "City Dental Surgery")
        String name,

        @Pattern(regexp = "^[0-9]{10}$", message = "Phone number must be 10 digits")
        @NotBlank(message = "phone number is required")
        @Schema(description = "Phone number of the surgery center (10 digits)", example = "1234567890")
        String phoneNumber,

        @NotNull(message = "Surgery address is required")
        @Schema(description = "Address of the surgery center", implementation = AddressDTO.class)
        AddressDTO address

) {}

