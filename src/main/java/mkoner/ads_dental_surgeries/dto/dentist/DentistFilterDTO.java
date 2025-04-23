package mkoner.ads_dental_surgeries.dto.dentist;

public record DentistFilterDTO(
        String firstName,
        String lastName,
        String phoneNumber,
        String emailAddress,
        String specialization
) {}

