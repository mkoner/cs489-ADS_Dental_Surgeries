package mkoner.ads_dental_surgeries.dto.appointment;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import mkoner.ads_dental_surgeries.model.AppointmentStatus;

import java.time.LocalDateTime;

@Schema(description = "DTO used for creating or updating an appointment")
public record AppointmentRequestDTO(

        //@NotNull(message = "Appointment date time is required") // Uncomment if needed for validation
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
        @Schema(description = "Date and time of the appointment in 'yyyy-MM-dd'T'HH:mm' format", example = "2025-05-10T14:30")
        LocalDateTime dateTime,

        @NotNull(message = "Patient id is required")
        @Schema(description = "ID of the patient", example = "101")
        Long patientId,

        @Schema(description = "ID of the dentist", example = "205")
        Long dentistId,

        @Schema(description = "ID of the surgery location", example = "302")
        Long surgeryId,

        @Schema(description = "Status of the appointment", example = "PENDING")
        AppointmentStatus status

) {}

