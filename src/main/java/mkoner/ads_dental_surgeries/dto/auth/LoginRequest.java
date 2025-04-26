package mkoner.ads_dental_surgeries.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Request object for user login")
public record LoginRequest(

        @NotBlank(message = "username is required")
        @Schema(description = "email or phone number of the user", example = "admin1@clinic.com")
        String userName,

        @NotBlank(message = "password is required")
        @Schema(description = "User's password", example = "pass")
        String password

) {}

