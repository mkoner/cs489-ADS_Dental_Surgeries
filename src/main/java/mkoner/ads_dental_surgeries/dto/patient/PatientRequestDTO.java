package mkoner.ads_dental_surgeries.dto.patient;

import jakarta.validation.constraints.*;
import mkoner.ads_dental_surgeries.dto.address.AddressDTO;
import mkoner.ads_dental_surgeries.model.Address;

import java.time.LocalDate;

public record PatientRequestDTO(
        @NotBlank(message = "first name is required")
        @Size(max = 25)
        String firstName,
        @NotBlank(message = "last name is required")
        @Size(max = 25)
        String lastName,
        @Pattern(regexp = "^\\d{10,15}$", message = "Phone number must be between 10 and 15 digits")
        String phoneNumber,
        @Email(message = "Invalid email format")
        @NotBlank(message = "Email is required")
        String email,
        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{5,}$",
                message = "Password must be at least 5 characters long and contain at least one lowercase letter, one uppercase letter, one digit, and one special character(@$!%*?&)."
        )
        @NotBlank(message = "password is required")
        String password,
        @NotNull(message = "date of birth is required")
        LocalDate dateOfBirth,
        @NotNull(message = "address is required")
        AddressDTO address
) {
}
