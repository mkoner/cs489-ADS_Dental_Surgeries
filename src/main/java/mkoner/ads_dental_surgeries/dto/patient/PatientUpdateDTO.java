package mkoner.ads_dental_surgeries.dto.patient;

import jakarta.validation.constraints.*;
import mkoner.ads_dental_surgeries.dto.address.AddressDTO;

import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO used to update an existing patient's information")
public record PatientUpdateDTO(

        @NotBlank(message = "first name is required")
        @Size(max = 25)
        @Schema(description = "First name of the patient", example = "John")
        String firstName,

        @NotBlank(message = "last name is required")
        @Size(max = 25)
        @Schema(description = "Last name of the patient", example = "Doe")
        String lastName,

        @Pattern(regexp = "^\\d{10,15}$", message = "Phone number must be between 10 and 15 digits")
        @Schema(description = "Phone number of the patient (10-15 digits)", example = "9876543210")
        String phoneNumber,

        @Email(message = "Invalid email format")
        @NotBlank(message = "Email is required")
        @Schema(description = "Email address of the patient", example = "john.doe@example.com")
        String email,

        @NotNull(message = "date of birth is required")
        @Schema(description = "Date of birth of the patient", example = "1990-01-15")
        LocalDate dateOfBirth,

        @NotNull(message = "address is required")
        @Schema(description = "Updated address of the patient")
        AddressDTO address

) {}

