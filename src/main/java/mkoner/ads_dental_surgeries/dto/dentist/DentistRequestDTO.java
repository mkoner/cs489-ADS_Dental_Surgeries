package mkoner.ads_dental_surgeries.dto.dentist;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record DentistRequestDTO(
        @NotBlank(message = "first name is required")
        @Size(max = 25)
        String firstName,
        @NotBlank(message = "last name is required")
        @Size(max = 25)
        String lastName,
        @Pattern(regexp = "^\\d{10,15}$", message = "Phone number must be between 10 and 15 digits")
        @NotBlank(message = "phone number is required")
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
        String specialization
) {
}
