package mkoner.ads_dental_surgeries.dto.surgery;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO used to filter surgeries based on name, contact details, and location")
public record SurgeryFilterDTO(

        @Schema(description = "Name of the surgery center", example = "Downtown Dental Clinic")
        String name,

        @Schema(description = "Phone number of the surgery center", example = "+1-800-555-1234")
        String phoneNumber,

        @Schema(description = "City where the surgery center is located", example = "San Francisco")
        String city,

        @Schema(description = "Country where the surgery center is located", example = "United States")
        String country

) {}


