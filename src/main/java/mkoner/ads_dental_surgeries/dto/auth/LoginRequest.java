package mkoner.ads_dental_surgeries.dto.auth;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank(message = "username is required")
        String userName,
        @NotBlank(message = "password is required")
        String password
) {
}
