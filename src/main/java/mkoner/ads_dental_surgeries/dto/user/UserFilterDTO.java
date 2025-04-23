package mkoner.ads_dental_surgeries.dto.user;

public record UserFilterDTO(
        String firstName,
        String lastName,
        String phoneNumber,
        String emailAddress,
        String roleName
) {}

