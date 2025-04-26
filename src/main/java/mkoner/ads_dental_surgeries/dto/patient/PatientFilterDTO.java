package mkoner.ads_dental_surgeries.dto.patient;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO used to filter patients based on personal and location details")
public record PatientFilterDTO(

        @Schema(description = "First name of the patient", example = "John")
        String firstName,

        @Schema(description = "Last name of the patient", example = "Doe")
        String lastName,

        @Schema(description = "Phone number of the patient", example = "+1-202-555-1234")
        String phoneNumber,

        @Schema(description = "Email address of the patient", example = "john.doe@example.com")
        String emailAddress,

        @Schema(description = "City where the patient resides", example = "New York")
        String city,

        @Schema(description = "Country where the patient resides", example = "United States")
        String country

) {}


