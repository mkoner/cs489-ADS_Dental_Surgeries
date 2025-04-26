package mkoner.ads_dental_surgeries.dto.dentist;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

@Schema(description = "DTO used to update an existing dentist's information")
public record DentistUpdateDTO(

        @NotBlank(message = "first name is required")
        @Size(max = 25)
        @Schema(description = "First name of the dentist", example = "Michael")
        String firstName,

        @NotBlank(message = "last name is required")
        @Size(max = 25)
        @Schema(description = "Last name of the dentist", example = "Lee")
        String lastName,

        @Pattern(regexp = "^\\d{10,15}$", message = "Phone number must be between 10 and 15 digits")
        @Schema(description = "Phone number of the dentist (digits only, 10 to 15 digits)", example = "9876543210")
        String phoneNumber,

        @Email(message = "Invalid email format")
        @NotBlank(message = "Email is required")
        @Schema(description = "Email address of the dentist", example = "michael.lee@dentalclinic.com")
        String email,

        @NotBlank(message = "specialization is required")
        @Schema(description = "Specialization of the dentist", example = "Pediatric Dentistry")
        String specialization

) {}

