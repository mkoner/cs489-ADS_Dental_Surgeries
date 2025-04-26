package mkoner.ads_dental_surgeries.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO used to update an existing user with personal details, contact information, and role")
public record UserUpdateDTO(

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

        @NotBlank(message = "role is required")
        @Schema(description = "Role assigned to the user (e.g., Admin, User)", example = "Admin")
        String role

) {}

