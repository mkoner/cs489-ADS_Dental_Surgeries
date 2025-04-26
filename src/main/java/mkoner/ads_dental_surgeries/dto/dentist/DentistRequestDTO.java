package mkoner.ads_dental_surgeries.dto.dentist;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO used to create or update a dentist")
public record DentistRequestDTO(

        @NotBlank(message = "first name is required")
        @Size(max = 25)
        @Schema(description = "First name of the dentist", example = "Alice")
        String firstName,

        @NotBlank(message = "last name is required")
        @Size(max = 25)
        @Schema(description = "Last name of the dentist", example = "Johnson")
        String lastName,

        @Pattern(regexp = "^\\d{10,15}$", message = "Phone number must be between 10 and 15 digits")
        @NotBlank(message = "phone number is required")
        @Schema(description = "Phone number of the dentist (digits only, 10 to 15 digits)", example = "1234567890")
        String phoneNumber,

        @Email(message = "Invalid email format")
        @NotBlank(message = "Email is required")
        @Schema(description = "Email address of the dentist", example = "alice.johnson@dentalclinic.com")
        String email,

        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{5,}$",
                message = "Password must be at least 5 characters long and contain at least one lowercase letter, one uppercase letter, one digit, and one special character(@$!%*?&)."
        )
        @NotBlank(message = "password is required")
        @Schema(description = "Password with at least one lowercase, one uppercase, one digit, and one special character", example = "Secure@123")
        String password,

        @Schema(description = "Dentist's specialization, if any", example = "Endodontics")
        String specialization

) {}

