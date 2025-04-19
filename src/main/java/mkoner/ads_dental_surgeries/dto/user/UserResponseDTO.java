package mkoner.ads_dental_surgeries.dto.user;

public record UserResponseDTO(
        Long userId,
        String firstName,
        String lastName,
        String phoneNumber,
        String email
) {
}
