package mkoner.ads_dental_surgeries.dto.dentist;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record DentistUpdateDTO(
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
        @NotBlank(message = "specialization is required")
        String specialization
) {
}
