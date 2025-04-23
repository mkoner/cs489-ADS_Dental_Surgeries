package mkoner.ads_dental_surgeries.dto.patient;

public record PatientFilterDTO(
        String firstName,
        String lastName,
        String phoneNumber,
        String emailAddress,
        String city,
        String country
) {}

