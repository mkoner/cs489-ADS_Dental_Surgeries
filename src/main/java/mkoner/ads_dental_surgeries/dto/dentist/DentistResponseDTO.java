package mkoner.ads_dental_surgeries.dto.dentist;


public record DentistResponseDTO(
        Long userId,
        String firstName,
        String lastName,
        String phoneNumber,
        String email,
        String specialization
) {
}
