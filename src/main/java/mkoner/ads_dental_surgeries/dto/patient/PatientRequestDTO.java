package mkoner.ads_dental_surgeries.dto.patient;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import mkoner.ads_dental_surgeries.dto.address.AddressDTO;

import java.time.LocalDate;

@Schema(description = "DTO used to create or register a new patient")
public record PatientRequestDTO(

        @NotBlank(message = "first name is required")
        @Size(max = 25)
        @Schema(description = "First name of the patient", example = "Alice")
        String firstName,

        @NotBlank(message = "last name is required")
        @Size(max = 25)
        @Schema(description = "Last name of the patient", example = "Smith")
        String lastName,

        @Pattern(regexp = "^\\d{10,15}$", message = "Phone number must be between 10 and 15 digits")
        @Schema(description = "Phone number of the patient (10-15 digits)", example = "1234567890")
        String phoneNumber,

        @Email(message = "Invalid email format")
        @NotBlank(message = "Email is required")
        @Schema(description = "Email address of the patient", example = "alice.smith@example.com")
        String email,

        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{5,}$",
                message = "Password must be at least 5 characters long and contain at least one lowercase letter, one uppercase letter, one digit, and one special character(@$!%*?&)."
        )
        @NotBlank(message = "password is required")
        @Schema(description = "Password with at least one lowercase, one uppercase, one digit, and one special character", example = "Password@123")
        String password,

        @NotNull(message = "date of birth is required")
        @Schema(description = "Date of birth of the patient", example = "1985-06-15")
        LocalDate dateOfBirth,

        @NotNull(message = "address is required")
        @Schema(description = "Address of the patient")
        AddressDTO address

) {}
