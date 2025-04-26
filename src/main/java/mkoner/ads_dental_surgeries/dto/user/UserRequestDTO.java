package mkoner.ads_dental_surgeries.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

@Schema(description = "DTO used to create a new user with personal details, contact information, and role")
public record UserRequestDTO(

        @NotBlank(message = "first name is required")
        @Size(max = 25)
        @Schema(description = "First name of the user", example = "John")
        String firstName,

        @NotBlank(message = "last name is required")
        @Size(max = 25)
        @Schema(description = "Last name of the user", example = "Doe")
        String lastName,

        @Pattern(regexp = "^\\d{10,15}$", message = "Phone number must be between 10 and 15 digits")
        @Schema(description = "Phone number of the user (10-15 digits)", example = "9876543210")
        String phoneNumber,

        @Email(message = "Invalid email format")
        @NotBlank(message = "Email is required")
        @Schema(description = "Email address of the user", example = "john.doe@example.com")
        String email,

        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{5,}$",
                message = "Password must be at least 5 characters long and contain at least one lowercase letter, one uppercase letter, one digit, and one special character(@$!%*?&)."
        )
        @NotBlank(message = "password is required")
        @Schema(description = "Password for the user account", example = "Passw0rd@123")
        String password,

        @NotBlank(message = "role is required")
        @Schema(description = "Role assigned to the user (e.g., Admin, User)", example = "Admin")
        String role

) {}

