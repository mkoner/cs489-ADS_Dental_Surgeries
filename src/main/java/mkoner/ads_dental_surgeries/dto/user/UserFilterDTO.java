package mkoner.ads_dental_surgeries.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO used to filter users based on their details and role")
public record UserFilterDTO(

        @Schema(description = "First name of the user", example = "Jane")
        String firstName,

        @Schema(description = "Last name of the user", example = "Doe")
        String lastName,

        @Schema(description = "Phone number of the user", example = "+1-202-555-6789")
        String phoneNumber,

        @Schema(description = "Email address of the user", example = "jane.doe@example.com")
        String emailAddress,

        @Schema(description = "Role of the user (e.g., Admin, User)", example = "Admin")
        String roleName

) {}


