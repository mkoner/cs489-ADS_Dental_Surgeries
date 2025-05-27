package mkoner.ads_dental_surgeries.dto.dentist;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO used to filter dentists based on personal and professional details")
public record DentistFilterDTO(

        @Schema(description = "First name of the dentist", example = "Emily")
        String firstName,

        @Schema(description = "Last name of the dentist", example = "Clark")
        String lastName,

        @Schema(description = "Phone number of the dentist", example = "+1-202-555-0143")
        String phoneNumber,

        @Schema(description = "Email address of the dentist", example = "emily.clark@dentalclinic.com")
        String emailAddress,

        @Schema(description = "Specialization of the dentist", example = "Orthodontics")
        String specialization

) {}


